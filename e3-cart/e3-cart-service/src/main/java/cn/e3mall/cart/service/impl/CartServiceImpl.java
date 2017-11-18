package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;

/**
 * 购物车处理服务
 * 
 * @ClassName: CartServiceImpl
 * @Description: TODO
 * @author: super
 * @date: 2017年11月18日 下午1:31:58
 */
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbItemMapper itemMapper;

	@Value("${REDIS_CART_PRE}")
	private String REDIS_CART_PRE;

	@Override
	public E3Result addCart(long userId, long itemId, int num) {
		// 向redis中添加购物车
		// 数据类型是hash key：用户id ,field 商品id,value:商品信息
		// 判断商品是否存在
		Boolean hexists = jedisClient.hexists(REDIS_CART_PRE + ":" + userId, itemId + "");
		// 如果存在，数量相加
		if (hexists) {
			String json = jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
			TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
			tbItem.setNum(tbItem.getNum() + num);
			jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
			return E3Result.ok();
		}
		// 如果不存在，根据商品id取商品信息
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		// 设置购物车数量
		tbItem.setNum(num);
		// 设置图片
		String image = tbItem.getImage();
		if (StringUtils.isNotBlank(image)) {
			String string = image.split(",")[0];
			tbItem.setImage(string);
		}
		// 添加到购物车列表
		jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		// 返回成功
		return E3Result.ok();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @Title: mergeCart
	 * 
	 * @Description: 合并购物车列表
	 * 
	 * @param userId
	 * 
	 * @param itemList
	 * 
	 * @return
	 * 
	 * @see cn.e3mall.cart.service.CartService#mergeCart(long, java.util.List)
	 */
	@Override
	public E3Result mergeCart(long userId, List<TbItem> itemList) {
		// 遍历商品列表
		// 将列表添加到购物车
		// 判断购物车中是否有此商品
		// 如果有，数量相加
		// 如果没有，添加新商品
		for (TbItem tbItem : itemList) {
			addCart(userId, tbItem.getId(), tbItem.getNum());
		}
		return E3Result.ok();
	}

	@Override
	public List<TbItem> getCartList(long userId) {
		// 根据用户ID查询购物车列表
		List<String> hvals = jedisClient.hvals(REDIS_CART_PRE + ":" + userId);
		List<TbItem> itemList = new ArrayList<>();
		for (String val : hvals) {
			// 创建一个TbItem对象
			TbItem item = JsonUtils.jsonToPojo(val, TbItem.class);
			// 添加到列表
			itemList.add(item);

		}
		return itemList;
	}

	@Override
	public E3Result updateCartNum(long userId, long itemId, int num) {
		// 从redis中取商品对象
		String json = jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
		if (StringUtils.isNotBlank(json)) {
			// 将json转成pojo
			TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
			tbItem.setNum(num);
			// 写入redis
			jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		}
		return E3Result.ok();
	}

	@Override
	public E3Result deleteCartItem(long userId, long itemId) {
		// 删除购物车商品
		jedisClient.hdel(REDIS_CART_PRE + ":" + userId, itemId + "");
		return E3Result.ok();
	}

	@Override
	public E3Result clearCart(long userId) {
		jedisClient.del(REDIS_CART_PRE + ":" + userId);
		return E3Result.ok();
	}
}
