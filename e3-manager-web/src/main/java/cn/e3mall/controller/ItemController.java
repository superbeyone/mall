package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;

@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;

	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable long itemId) {
		TbItem tbItem = itemService.getItemById(itemId);
		return tbItem;
	}

	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {

		EasyUIDataGridResult result = itemService.getItemList(page, rows);
		return result;
	}

	@RequestMapping(value = "/item/save", method = RequestMethod.POST)
	@ResponseBody
	public E3Result addItem(TbItem item, String desc) {
		E3Result result = itemService.addItem(item, desc);
		return result;
	}

	// 打开编辑商品页面
	@RequestMapping(value = "/rest/page/item-edit")
	public String itemEdit() {

		return "item-edit";
	}

	// 修改商品信息
	@RequestMapping(value = "/rest/item/update")
	@ResponseBody
	public E3Result updateItem(TbItem item, String desc) {
		E3Result result = itemService.updateItem(item, desc);
		return result;
	}

	// 加载商品描述
	@RequestMapping(value = "/rest/item/query/item/desc/{id}")
	@ResponseBody
	public E3Result queryItemDesc(long id) {
		E3Result result = itemService.getItemDescById(id);
		return result;
	}

	// 加载商品规格
	@RequestMapping(value = "/rest/item/param/item/query/{id}")
	@ResponseBody
	public E3Result paramItemQuery(long id) {
		E3Result result = itemService.paramItemQueryById(id);
		return result;
	}

	// 商品删除
	@RequestMapping(value = "/rest/item/delete")
	public E3Result deleteItem(@RequestParam("id") long[] ids) {
		E3Result result = itemService.deleteItem(ids);
		return result;
	}
}
