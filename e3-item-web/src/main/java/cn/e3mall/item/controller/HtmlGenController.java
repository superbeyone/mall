package cn.e3mall.item.controller;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 生成静态页面测试用例
 * @ClassName: HtmlGenController
 * @Description: TODO
 * @author: super
 * @date: 2017年11月13日 下午5:29:56
 */
@Controller
public class HtmlGenController {

	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	
	@RequestMapping("/gen")
	@ResponseBody
	public String genHtml()throws Exception{
		Configuration configuration = freeMarkerConfig.getConfiguration();
		//加载模板对象
		Template template = configuration.getTemplate("hello.ftl");
		//创建数据集
		Map data = new HashMap();
		data.put("hello", "superbeyone");
		//指定文件输出的路径及文件名
		FileWriter fileWriter = new FileWriter("D:/sb.html");
		
		//输出文件
		template.process(data, fileWriter);
		//关闭流
		fileWriter.close();
		return "free";
	}
	
}
