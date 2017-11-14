package cn.e3mall.item.listener;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class HtmlGenListener implements MessageListener {

	@Autowired
	private ItemService itemService;
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;

	@Value("${HTML_GEN_PATH}")
	private String HTML_GEN_PATH;

	@Override
	public void onMessage(Message message) {
		try {
			// 创建一个模板，参考jsp
			// 从消息中获取商品ID
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			Long itemId = new Long(text);
			// 等待事务提交
			Thread.sleep(1000);
			// 根据ID查询商品基本信息，和商品描述信息
			TbItem tbItem = itemService.getItemById(itemId);
			Item item = new Item(tbItem);
			TbItemDesc itemDesc = itemService.getItemDescribeById(itemId);
			// 创建一个数据集，把商品数据封装
			Map data = new HashMap<>();
			data.put("item", item);
			data.put("itemDesc", itemDesc);
			// 加载模板对象
			Configuration configuration = freeMarkerConfig.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");

			// 创建一个输出流,指定文件名
			Writer writer = new FileWriter(HTML_GEN_PATH + itemId + ".html");
			// 生成静态页面
			template.process(data, writer);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
