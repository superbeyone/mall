package cn.e3mall.solrj;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrCloud {
	
	@Test
	public void testAddDocument() throws Exception{
		
		//创建一个集群的连接，应该是CloudSolrServer对象
		CloudSolrServer solrServer = new CloudSolrServer("192.168.25.122:2181,192.168.25.122:2182,192.168.25.122:2183");
		//zkHost zookeeper的地址列表
		//设置一个defaultCollection属性
		solrServer.setDefaultCollection("collection3");
		//创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		//向文档中添加属性
		document.setField("id", "solrCloud02");
		document.setField("item_title", "测试用例2");
		document.setField("item_price", 120);
		//把文件写入索引库
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
	
	@Test
	public void testQuery() throws Exception{
		//创建CloudSolrServer对象
		CloudSolrServer solrServer = new CloudSolrServer("192.168.25.122:2181,192.168.25.122:2182,192.168.25.122:2183");
		solrServer.setDefaultCollection("collection3");
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		QueryResponse queryResponse = solrServer.query(query);
		SolrDocumentList results = queryResponse.getResults();
		System.out.println("总记录数："+results.getNumFound());
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("title"));
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_title"));
		}
		
		solrServer.commit();
	}
}
