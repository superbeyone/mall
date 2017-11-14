package cn.e3mall.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**全局异常处理器
 * @ClassName: GlobalExceptionResolver
 * @Description: TODO
 * @author: super
 * @date: 2017年11月12日 下午10:13:04
 */
public class GlobalExceptionResolver implements HandlerExceptionResolver {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		//打印控制台
		ex.printStackTrace();
		//写日志
		logger.debug("debug测试数据--------。。。。。。。。。。。。");
		logger.info("Info信息：系统故障");
		logger.error("系统发生异常",ex);
		//发邮件
		//使用jmail发送邮件
		//显示错误页面
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error/exception");
		return modelAndView;
	}

}
