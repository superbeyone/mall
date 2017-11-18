package cn.e3mall.order.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

/**
 * 用户登录拦截器
 * 
 * @ClassName: LoginInterceptor
 * @Description: TODO
 * @author: super
 * @date: 2017年11月18日 下午4:12:59
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private TokenService tokenService;
	@Autowired
	private CartService cartService;

	@Value("${SSO_URL}")
	private String SSO_URL;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 从cookie中取token
		String token = CookieUtils.getCookieValue(request, "token");
		// 判断token是否存在
		if (StringUtils.isBlank(token)) {
			// 如果token不存在，未登录状态，跳转到sso系统登录页面，待用户登录成功后须返回此页面
			response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
			return false;
		}
		// 如果token存在，需要调用sso系统的服务，根据token取用户的信息
		E3Result e3Result = tokenService.getUserByToken(token);
		if (e3Result.getStatus() != 200) {
			// 如果取不到，用户登录已经过期，跳转到sso系统登录页面，待用户登录成功后须返回此页面
			response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
			return false;
		}
		// 如果取到，证明用户是登录状态，需要把用户信息写入到request中
		TbUser user = (TbUser) e3Result.getData();
		request.setAttribute("user", user);
		// 判断cookie中是否有购物车数据，如果有，需要与Redis合并
		String json = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isNotBlank(json)) {
			// 合并购物车
			List<TbItem> itemList = JsonUtils.jsonToList(json, TbItem.class);
			cartService.mergeCart(user.getId(), itemList);
		}
		// 放行
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
