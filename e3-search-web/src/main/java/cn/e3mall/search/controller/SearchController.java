package cn.e3mall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;

/**
 * @ClassName: SearchController
 * @Description: <b style="color:red">商品搜索Controller</b>
 * @author: super
 * @date: 2017年11月11日 下午10:16:24
 */
@Controller
public class SearchController {
	@Autowired
	private SearchService searchService;
	@Value("${SEARCH_RESULT_ROWS}")
	private Integer SEARCH_RESULT_ROWS;
	
	@RequestMapping(value = "/search")
	public String searchItemList(String keyword, @RequestParam(defaultValue = "1") Integer page,Model model) throws Exception {
		//转码
		keyword = new String(keyword.getBytes("iso-8859-1"), "utf-8");
		//查询商品列表
		SearchResult result = searchService.search(keyword, page, SEARCH_RESULT_ROWS);
		model.addAttribute("query", keyword);
		model.addAttribute("totalPages", result.getTotalPages());
		model.addAttribute("page", page);
		model.addAttribute("recourdCount", result.getRecordCount());
		model.addAttribute("itemList", result.getItemList());
		//返回逻辑视图
		return "search";
	}

}
