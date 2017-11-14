package cn.e3mall.activemq;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageConsumer {

	@Test
	public void receiveMessage() throws Exception {
		// 初始化Spring容器
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext-activemq.xml");
		//等待
		System.in.read();
	}
}
