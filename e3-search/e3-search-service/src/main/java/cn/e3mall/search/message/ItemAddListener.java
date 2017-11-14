package cn.e3mall.search.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;

/**
 * 监听商品添加消息。接收消息后，将对应添加的商品同步到索引库
 * 
 * @ClassName: ItemAddListener
 * @Description: TODO
 * @author: super
 * @date: 2017年11月13日 下午2:06:50
 */
public class ItemAddListener implements MessageListener {

	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private SolrServer solrServer;
	@Override
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage) message;
			// 从消息中获取商品ID
			String text = textMessage.getText();
			Long itemId = new Long(text);
			//等待
			Thread.sleep(1000);
			// 根据商品ID查询出商品的详细信息
			SearchItem item = itemMapper.getItemById(itemId);
			// 创建文档对象
			SolrInputDocument document = new SolrInputDocument();
			// 向文档对象添加域
			document.addField("id", item.getId());
			document.addField("item_title", item.getTitle());
			document.addField("item_sell_point", item.getSell_point());
			document.addField("item_price", item.getPrice());
			document.addField("item_image", item.getImage());
			document.addField("item_category_name", item.getCategory_name());
			// 将文档写入索引库
			solrServer.add(document);
			// 提交
			solrServer.commit();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
