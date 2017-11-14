package cn.e3mall.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {

	@Test
	public void testJedis()throws Exception{
		//创建一个jedis连接，连接地址，端口号
		Jedis jedis = new Jedis("192.168.25.122",6379);
		//存储对象
		jedis.set("ss", "beyone");
		String string = jedis.get("ss");
		System.out.println(string);
		jedis.close();
		
	}
	@Test
	public void testJedisPool()throws Exception{
		//创建一个jedisPool连接池，连接地址，端口号
		 JedisPool jedisPool = new JedisPool("192.168.25.122",6379);
		Jedis resource = jedisPool.getResource();
		resource.set("sb", "sb");
		String string = resource.get("sb");
		System.out.println(string);
		jedisPool.close();
		
	}
	@Test
	public void testJedisCluster()throws Exception{
		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort("192.168.25.122", 7001));
		nodes.add(new HostAndPort("192.168.25.122", 7002));
		nodes.add(new HostAndPort("192.168.25.122", 7003));
		nodes.add(new HostAndPort("192.168.25.122", 7004));
		nodes.add(new HostAndPort("192.168.25.122", 7005));
		nodes.add(new HostAndPort("192.168.25.122", 7006));
		JedisCluster jedisCluster = new JedisCluster(nodes);
		jedisCluster.set("sp", "spp");
		String string = jedisCluster.get("sp");
		System.out.println(string);
		jedisCluster.close();
		}
}
