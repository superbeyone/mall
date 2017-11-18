package cn.e3mall.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;

/**
 * 订单处理服务
 * 
 * @ClassName: OrderServiceImpl
 * @Description: TODO
 * @author: super
 * @date: 2017年11月18日 下午5:02:05
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private JedisClient jedisClient;

	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	@Value("${ORDER_ID_INIT}")
	private String ORDER_ID_INIT;
	@Value("${ORDER_DETAIL_ID_GEN_KEY}")
	private String ORDER_DETAIL_ID_GEN_KEY;
	@Value("${ORDER_DETAIL_ID_INIT}")
	private String ORDER_DETAIL_ID_INIT;
	@Override
	public E3Result createOrder(OrderInfo orderInfo) {

		// 生成订单号,使用redis的incr属性
		if (!jedisClient.exists(ORDER_ID_GEN_KEY)) {
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_INIT);
		}
		String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
		// 补全orderInfo的属性
		orderInfo.setOrderId(orderId);
		// 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭'
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		// 插入订单表
		orderMapper.insert(orderInfo);
		// 向订单明细表插入数据
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		if(!jedisClient.exists(ORDER_DETAIL_ID_GEN_KEY)){
			jedisClient.set(ORDER_DETAIL_ID_GEN_KEY, ORDER_DETAIL_ID_INIT);
		}
		for (TbOrderItem tbOrderItem : orderItems) {
			//生成明细ID
			String orderDetailId = jedisClient.incr(ORDER_DETAIL_ID_GEN_KEY).toString();
			//补全pojo属性
			tbOrderItem.setId(orderDetailId);
			tbOrderItem.setOrderId(orderId);
			//向明细表插入数据
			orderItemMapper.insert(tbOrderItem);
			
		}
		// 向订单物流表插入数据
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		orderShippingMapper.insert(orderShipping);
		// 返回E3Result，其中包含订单号
		return E3Result.ok(orderId);
	}

}
