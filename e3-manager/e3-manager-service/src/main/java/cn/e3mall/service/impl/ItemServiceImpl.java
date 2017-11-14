package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemCatMapper;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;

/**
 * 商品管理service
 * 
 * @author super
 *
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;

	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	@Value("${REDIS_ITEM_CACHE_TIME}")
	private int REDIS_ITEM_CACHE_TIME;

	@Override
	public TbItem getItemById(Long itemId) {
		// 查询缓存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":BASE");
			if (StringUtils.isNotBlank(json)) {
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 缓存中没有，查询数据库
		// itemMapper.selectByPrimaryKey(itemId);
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(itemId);
		List<TbItem> list = itemMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			// 将查询结果添加到缓存中
			try {
				jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":BASE", JsonUtils.objectToJson(list.get(0)));
				// 设置过期时间
				jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":BASE", REDIS_ITEM_CACHE_TIME);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list.get(0);

		}
		return null;
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		// 设置分页信息
		PageHelper.startPage(page, rows);
		// 执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		// 创建一个返回结果的对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);

		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		// 取总记录数
		long total = pageInfo.getTotal();
		result.setTotal(total);
		// 取分页结果

		return result;
	}

	@Override
	public E3Result addItem(TbItem item, String desc) {
		// 生成商品ID
		final long itemId = IDUtils.genItemId();
		// 补全item属性
		item.setId(itemId);
		// 1-正常 2-下架 3-删除
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		// 向商品表中插入数据
		itemMapper.insert(item);
		// 创建一个商品描述的pojo
		TbItemDesc itemDesc = new TbItemDesc();
		// 补全商品描述属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		// 向商品描述表插入数据
		itemDescMapper.insert(itemDesc);
		// 发送商品添加消息
		jmsTemplate.send(topicDestination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});
		// 返回结果
		return E3Result.ok();
	}

	// 修改商品信息
	@Override
	public E3Result updateItem(TbItem item, String desc) {
		System.out.println("desc===" + desc);
		Long id = item.getId();
		item.setId(id);
		item.setUpdated(new Date());
		item.setCreated(item.getCreated());
		item.setNum(item.getNum());
		item.setImage(item.getImage());
		item.setPrice(item.getPrice());
		item.setSellPoint(item.getSellPoint());
		item.setStatus(item.getStatus());
		item.setTitle(item.getTitle());
		item.setBarcode(item.getBarcode());
		item.setSellPoint(item.getSellPoint());
		itemMapper.updateByPrimaryKeySelective(item);
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
		itemDesc.setItemId(id);
		itemDesc.setItemDesc(itemDesc.getItemDesc());
		itemDesc.setCreated(itemDesc.getCreated());
		itemDesc.setUpdated(new Date());
		itemDescMapper.updateByPrimaryKeySelective(itemDesc);
		return E3Result.ok();
	}

	@Override
	public E3Result getItemDescById(long id) {
		TbItem tbItem = itemMapper.selectByPrimaryKey(id);
		Long cid = tbItem.getCid();
		TbItemCat itemCatDesc = itemCatMapper.selectByPrimaryKey(cid);
		String name = itemCatDesc.getName();
		tbItem.setCid(Long.parseLong(name));
		return E3Result.ok(tbItem);
	}

	@Override
	public E3Result paramItemQueryById(long id) {

		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(id);

		return E3Result.ok(tbItemDesc);
	}

	@Override
	public E3Result deleteItem(long[] ids) {
		for (long id : ids) {
			itemDescMapper.deleteByPrimaryKey(id);
			itemMapper.deleteByPrimaryKey(id);
		}
		return E3Result.ok();
	}

	@Override
	public TbItemDesc getItemDescribeById(long itemId) {
		// 查询缓存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":DESC");
			if (StringUtils.isNotBlank(json)) {
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return tbItemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 缓存中没有，查询数据库
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		// 将查询结果添加到缓存中
		try {
			jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemDesc));
			// 设置过期时间
			jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":DESC", REDIS_ITEM_CACHE_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}
}
