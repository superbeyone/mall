package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;

/**
 * 购物车处理Controller
 * 
 * @ClassName: CartController
 * @Description: TODO
 * @author: super
 * @date: 2017年11月16日 下午9:13:16
 */
@Controller
public class CartController {

	@Autowired
	private ItemService itemService;
	@Autowired
	private CartService cartService;

	@Value("${CART_COOKIE_EXPIRE}")
	private int CART_COOKIE_EXPIRE;

	@RequestMapping(value = "/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue = "1") int num,
			HttpServletRequest request, HttpServletResponse response) {
		// 判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("user");
		// 如果是登录状态，将购物车信息保存到redis中
		if (user != null) {
			// 保存到服务端
			cartService.addCart(user.getId(), itemId, num);
			// 返回逻辑视图
			return "cartSuccess";
		}
		// 如果未登录，信息写入cookie中
		// 从cookie中取购物车列表
		List<TbItem> cartList = getCartListByCookie(request);
		// 定义判断标识
		boolean flag = false;
		// 判断商品在购物车里是否存在
		for (TbItem tbItem : cartList) {
			if (itemId.longValue() == tbItem.getId()) {
				flag = true;
				// 如果已经存在，找到商品，商品数量增加
				tbItem.setNum(tbItem.getNum() + num);
				// 跳出循环
				break;
			}
		}
		// 如果不存在，根据商品ID查询出商品对象TbItem
		if (!flag) {
			TbItem tbItem = itemService.getItemById(itemId);
			// 设置商品数量
			tbItem.setNum(num);
			// 设置图片
			String image = tbItem.getImage();
			if (StringUtils.isNotBlank(image)) {
				tbItem.setImage(image.split(",")[0]);
			}
			cartList = new ArrayList<>();
			// 添加商品对象于购物车中
			cartList.add(tbItem);
			// 写入Cookie
			CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), CART_COOKIE_EXPIRE,
					true);
		}

		// 返回添加成功页面

		return "cartSuccess";
	}

	/**
	 * 从购物车中取Cookie列表的处理
	 * 
	 * @Title: getCartListByCookie
	 * @Description: TODO
	 * @param request
	 * @return
	 * @return: List<TbItem>
	 */
	private List<TbItem> getCartListByCookie(HttpServletRequest request) {
		String json = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isBlank(json)) {
			return new ArrayList<TbItem>();
		}
		// 将json转换成一个商品列表
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}

	/**
	 * 展示购物车列表
	 * 
	 * @Title: showCart
	 * @Description: TODO
	 * @param request
	 * @return
	 * @return: String
	 */
	@RequestMapping(value = "/cart/cart")
	public String showCart(HttpServletRequest request,HttpServletResponse response) {
		// 从cookie中取购物车列表
		List<TbItem> cartList = getCartListByCookie(request);
		// 判断用户是否为登录状态
		TbUser user = (TbUser) request.getAttribute("user");
		// 如果是登录状态，
		if (user != null) {
			// 从cookie中取购物车列表
			// 如果不为空，将cookie中的购物车信息与redis中的购物车信息合并
			if (cartList != null && cartList.size() > 0) {
				cartService.mergeCart(user.getId(), cartList);
			}
			// 把Cookie中的购物车列表删除
			CookieUtils.deleteCookie(request, response, "cart");
			//从服务端去购物车列表
			cartList = cartService.getCartList(user.getId());
		}

		// 未登录状态
		if (cartList != null && cartList.size() > 0) {
			// 将列表传递到页面
			request.setAttribute("cartList", cartList);
		}
		// 返回逻辑视图
		return "cart";
	}

	@RequestMapping(value = "/cart/update/num/{itemId}/{num}", method = RequestMethod.POST)
	public E3Result updateCartNum(@PathVariable Long itemId, @PathVariable int num, HttpServletRequest request,
			HttpServletResponse response) {
		//判断是否为登录状态
		TbUser user = (TbUser) request.getAttribute("user");
		if(user != null){
			cartService.updateCartNum(user.getId(), itemId, num);
			return E3Result.ok();
		}
		
		List<TbItem> cartList = getCartListByCookie(request);
		// 遍历购物车列表
		for (TbItem tbItem : cartList) {
			if (tbItem.getId() == itemId.longValue()) {
				// 更新数量
				tbItem.setNum(num);
				break;
			}
		}
		// 将购物车重新写回Cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), CART_COOKIE_EXPIRE, true);
		request.setAttribute("cartList", cartList);
		return E3Result.ok();
	}

	// 删除购物车的内容
	@RequestMapping(value = "/cart/delete/{itemId}")
	public String deleteCartByItemId(@PathVariable Long itemId, HttpServletRequest request,
			HttpServletResponse response) {
		//判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("user");
		if(user != null){
			cartService.deleteCartItem(user.getId(), itemId);
			return "redirect:/cart/cart.html";
		}
		List<TbItem> cartList = getCartListByCookie(request);
		// 查找到要删除的对象
		for (TbItem tbItem : cartList) {
			if (itemId.longValue() == tbItem.getId()) {
				// 进行删除
				cartList.remove(tbItem);
				// 跳出循环
				break;
			}
		}
		// 将购物车列表重新写入Cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), CART_COOKIE_EXPIRE, true);
		request.setAttribute("cartList", cartList);
		return "redirect:/cart/cart.html";
	}
}
