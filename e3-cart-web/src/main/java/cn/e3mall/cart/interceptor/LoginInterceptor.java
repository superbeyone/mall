package cn.e3mall.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

/**
 * 登录拦截器
 * 
 * @ClassName: LoginInterceptor
 * @Description: TODO
 * @author: super
 * @date: 2017年11月17日 下午8:20:02
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private TokenService tokenService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 前处理，执行handler之前执行此方法
		// 从cookie中取token
		String token = CookieUtils.getCookieValue(request, "token");
		// 如果没有取到，未登录状态，直接放行
		if (StringUtils.isBlank(token)) {
			return true;
		}
		// 如果取到token，需要调用sso系统服务，根据token取用户信息
		E3Result result = tokenService.getUserByToken(token);
		// 如果没有取到用户信息，证明登录过期，直接放行
		if (result.getStatus() != 200) {
			return true;
		}
		// 取到用户信息，登录状态
		TbUser user = (TbUser) result.getData();
		// 把用户信息放到request中，只需要在Controller中判断request中是否包含user信息，放行
		request.setAttribute("user", user);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView arg3)
			throws Exception {
		// 执行handler之后，返回ModelAndView之前

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// 完成处理，返回ModelAndView之后，主要用于处理异常

	}

}
