package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService{

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	@Override
	public List<EasyUITreeNode> getContentList(long parentId) {
		// 根据parentId查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> catList = contentCategoryMapper.selectByExample(example);
		//转换成EasyUITreeNode的列表
		List<EasyUITreeNode> nodeList = new ArrayList<>();				
		for (TbContentCategory tbContentCategory : catList) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			nodeList.add(node);
			
		}
		return nodeList;
	}
	
	@Override
	public E3Result addContentCategroy(long parentId, String name) {
		// 创建Tb_content_category表的pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		//设置pojo属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//默认排序为1
		contentCategory.setSortOrder(1);
		//正常为1，删除为2
		contentCategory.setStatus(1);
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		contentCategoryMapper.insert(contentCategory);
		//判断父节点是否为parent节点
		//根据parentId查询父节点
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()){
			parent.setIsParent(true);
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		return E3Result.ok(contentCategory);
	}

	/**
	 * 修改分类类别名称
	 */
	@Override
	public E3Result updateContentCategory(long id, String name) {
		TbContentCategory primaryKey = contentCategoryMapper.selectByPrimaryKey(id);
		primaryKey.setName(name);
		contentCategoryMapper.updateByPrimaryKey(primaryKey);
		return E3Result.ok();
	}

	/**
	 * 删除分类类别
	 */
	@Override
	public E3Result deleteContentCategory(long id) {
		TbContentCategory primaryKey = contentCategoryMapper.selectByPrimaryKey(id);
		//获得父Id
		Long parentId = primaryKey.getParentId();
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//获得所要删除的节点的父节点所被引用的次数
		int count = contentCategoryMapper.countByExample(example);
		//若该节点不是父节点
		if(!primaryKey.getIsParent()){
			if(count>0){
				parent.setIsParent(true);
			}else{
				parent.setIsParent(false);
			}
			contentCategoryMapper.deleteByPrimaryKey(id);
			return E3Result.ok();
		}		
			return null;
	}

	


}
