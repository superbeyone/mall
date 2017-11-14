package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;

/**
 * 展示内容列表
 * @author super
 *
 */
public interface ContentCategoryService {
	
	List<EasyUITreeNode> getContentList(long parentId);
	
	E3Result addContentCategroy(long parentId,String name);
	
	E3Result updateContentCategory(long id,String name);
	
	E3Result deleteContentCategory(long id);
	
}
