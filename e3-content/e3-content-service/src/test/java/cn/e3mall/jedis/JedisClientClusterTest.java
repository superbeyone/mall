package cn.e3mall.jedis;


import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.common.jedis.JedisClientCluster;

public class JedisClientClusterTest {

	@Test
	public void testJedisClientCluster(){
		
		/*ApplicationContext application = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		JedisClientCluster jedisClientCluster = application.getBean(JedisClientCluster.class);
		jedisClientCluster.set("sd", "sdp");
		String string = jedisClientCluster.get("sd");
		System.out.println(string);*/
		
	}
}
