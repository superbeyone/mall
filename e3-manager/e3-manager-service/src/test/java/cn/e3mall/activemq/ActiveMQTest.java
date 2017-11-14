package cn.e3mall.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

/**
 * 点到点发送消息 
 * @ClassName: ActiveMQTest
 * @Description: TODO
 * @author: super
 * @date: 2017年11月12日 下午8:44:11
 */
public class ActiveMQTest {

	@Test
	public void testQueueProducer() throws Exception {
		// 1.创建一个连接工厂对象，需要指定服务的IP及端口
		ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.25.122:61616");
		// 2.使用工厂对象创建一个connection对象
		Connection connection = factory.createConnection();
		// 3.开启连接，调用Connection对象的start方法
		connection.start();
		// 4.创建一个Session对象
		// 第一个参数:是否开启事务,如果开启事务，第二个参数没有意义
		// 第二个参数:应答模式;一般是自动应答或手动应答，一般是自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5.使用Session对象创建一个Destination对象，两种形式Queue，Topic现在使用Queue
		Queue queue = session.createQueue("test-queue");
		// 6.使用Session对象创建一个Producer对象
		MessageProducer producer = session.createProducer(queue);
		// 7.创建一个Message消息对象，可以使用TextMessage

		// ActiveMQTextMessage message = new ActiveMQTextMessage();
		// message.setText("hello MQ");

		TextMessage message = session.createTextMessage("hello MQ");
		// 8.发送消息
		producer.send(message);
		// 9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}

	@Test
	public void testQueueConsumer() throws Exception{
		//1.创建工厂对象
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.122:61616");
		//2.创建connection连接对象
		Connection connection = connectionFactory.createConnection();
		//3.开启连接
		connection.start();
		//4.使用Connection创建Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.创建一个Destination对象
		Queue queue = session.createQueue("spring-queue");
		//6.使用Session对象创建一个消费者对象
		MessageConsumer consumer = session.createConsumer(queue);
		//7.接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				//打印结果
				TextMessage textMessage = (TextMessage) message;
				String text;
				try {
					text = textMessage.getText();
					System.out.println(text);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//等待接收消息
		System.in.read();
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
	
	@Test
	public void testTopicProducer() throws Exception {
		// 1.创建一个连接工厂对象，需要指定服务的IP及端口
		ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.25.122:61616");
		// 2.使用工厂对象创建一个connection对象
		Connection connection = factory.createConnection();
		// 3.开启连接，调用Connection对象的start方法
		connection.start();
		// 4.创建一个Session对象
		// 第一个参数:是否开启事务,如果开启事务，第二个参数没有意义
		// 第二个参数:应答模式;一般是自动应答或手动应答，一般是自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5.使用Session对象创建一个Destination对象，两种形式Queue，Topic现在使用Queue
		Topic topic = session.createTopic("test-topic");
		// 6.使用Session对象创建一个Producer对象
		MessageProducer producer = session.createProducer(topic);
		// 7.创建一个Message消息对象，可以使用TextMessage

		// ActiveMQTextMessage message = new ActiveMQTextMessage();
		// message.setText("hello MQ");

		TextMessage message = session.createTextMessage("hello TopicMQ");
		// 8.发送消息
		producer.send(message);
		// 9.关闭资源
		producer.close();
		session.close();
		connection.close();
	}
	
	@Test
	public void testTopicConsumer() throws Exception{
		//1.创建工厂对象
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.122:61616");
		//2.创建connection连接对象
		Connection connection = connectionFactory.createConnection();
		//3.开启连接
		connection.start();
		//4.使用Connection创建Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.创建一个Destination对象
		Topic topic = session.createTopic("test-topic");
		//6.使用Session对象创建一个消费者对象
		MessageConsumer consumer = session.createConsumer(topic);
		//7.接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				//打印结果
				TextMessage textMessage = (TextMessage) message;
				String text;
				try {
					text = textMessage.getText();
					System.out.println(text);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//等待接收消息
		System.in.read();
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
	
}
