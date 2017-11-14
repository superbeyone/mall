/**  

 * Copyright © 2017 superbeyone. All rights reserved.

 *

 * @Title: SearchServiceImpl.java

 * @Prject: e3-search-service

 * @Package: cn.e3mall.search.service.impl

 * @Description: TODO

 * @author: super  

 * @date: 2017年11月11日 下午9:46:37

 * @version: V1.0  

 */
package cn.e3mall.search.service.impl;

import java.io.Serializable;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.service.SearchService;

/**
 * @ClassName: SearchServiceImpl
 * @Description: TODO
 * @author: super
 * @date: 2017年11月11日 下午9:46:37
 */
@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;
	@Override
	public SearchResult search(String keyword, int page, int rows) throws Exception {
		// 创建一个SolrQuery对象
		SolrQuery solrQuery = new SolrQuery();
		// 设置查询条件
		solrQuery.setQuery(keyword);
		// 设置分页条件
		if (page <= 0)
			page = 1;
		solrQuery.setStart((page - 1) * rows);
		solrQuery.setRows(rows);
		// 设置默认搜索域
		solrQuery.set("df", "item_title");
		// 开启高亮显示
		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<em style='color:red'>");
		solrQuery.setHighlightSimplePost("</em>");
		// 调用dao执行查询
		SearchResult searchResult = searchDao.search(solrQuery);
		// 计算总页数
		long recordCount = searchResult.getRecordCount();
		int totalPage = (int) ((recordCount % rows) == 0 ? (recordCount / rows):(recordCount / rows)+1);   
		//添加到返回结果
		searchResult.setTotalPages(totalPage);
		// 返回结果
		return searchResult;

	}

}
