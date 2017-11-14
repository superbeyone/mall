package cn.e3mall.search.service;

import cn.e3mall.common.pojo.SearchResult;

/**
 * @ClassName: SearchService
 * @Description: TODO
 * @author: super
 * @date: 2017年11月11日 下午9:45:35
 */
public interface SearchService {
	SearchResult search(String keyword, int page, int rows) throws Exception;
}
