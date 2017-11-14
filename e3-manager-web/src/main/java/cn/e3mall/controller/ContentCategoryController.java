package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;

/**
 * 内容分类管理Controller
 * @author super
 *
 */
@Controller
public class ContentCategoryController {
	@Autowired
	ContentCategoryService contentCategoryService;
	
	@RequestMapping(value="/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCatList(@RequestParam(name="id",defaultValue="0") long parentId){
		List<EasyUITreeNode> list = contentCategoryService.getContentList(parentId);
		return list;
		
	}
	
	@RequestMapping(value="/content/category/create",method=RequestMethod.POST)
	@ResponseBody
	public E3Result createContentCategory(long parentId,String name){
		//调用服务添加节点
		E3Result e3Result = contentCategoryService.addContentCategroy(parentId, name);
		return e3Result;
	}
	
	@RequestMapping(value="/content/category/update",method=RequestMethod.POST)
	@ResponseBody
	public E3Result updateContentCategory(long id,String name){
		E3Result e3Result = contentCategoryService.updateContentCategory(id, name);
		return e3Result;
	}
	
	@RequestMapping(value="/content/category/delete",method=RequestMethod.POST)
	@ResponseBody
	public E3Result deleteContentCategory(long id){
		E3Result e3Result = contentCategoryService.deleteContentCategory(id);
		return e3Result;
	}
}
