package cn.e3mall.solrj;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ {

	@Test
	public void addDocument() throws Exception {
		// 创建一个SolrServer对象，创建一个连接，参数为solr服务的url
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.122:8080/solr/collection1");
		// 创建一个文档对象SolrInputDocument
		SolrInputDocument solrInputDocument = new SolrInputDocument();
		// 向文档对象中添加域，其中必须要有一个id域，并且所有的域名称都需在schema.xml中定义
		solrInputDocument.addField("id", "doc01");
		solrInputDocument.addField("item_title", "测试用例1");
		solrInputDocument.addField("item_price", 23);

		// 将文档写入索引库
		solrServer.add(solrInputDocument);
		// 提交
		solrServer.commit();
	}

	@Test
	public void deleteDocument() throws Exception {
		// 创建一个SolrServer对象，创建一个连接，参数为solr服务的url
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.122:8080/solr/collection1");
		solrServer.deleteById("doc01");
		solrServer.commit();
	}

	@Test
	public void queryIndex() throws Exception {
		// 创建一个SolrServer对象，创建一个连接，参数为solr服务的url
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.122:8080/solr/collection1");
		// 创建一个SolrQuery查询对象
		SolrQuery solrQuery = new SolrQuery();
		// 设置查询条件
		solrQuery.set("q", "*:*");
		// 执行查询，QueryResponse对象
		QueryResponse queryResponse = solrServer.query(solrQuery);
		// 获得文档列表，取查询结果的总记录数
		SolrDocumentList documentList = queryResponse.getResults();
		System.out.println("总记录数：" + documentList.getNumFound());
		// 遍历整个文档列表,获取文档域中的内容
		for (SolrDocument solrDocument : documentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.getFieldValue("item_title"));
		}

	}

	@Test
	public void queryIndexHighLight() throws Exception {
		// 创建一个SolrServer对象，创建一个连接，参数为solr服务的url
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.122:8080/solr/collection1");
		// 创建一个SolrQuery查询对象
		SolrQuery solrQuery = new SolrQuery();
		// 设置查询条件
		solrQuery.setQuery("手机");
		solrQuery.setStart(0);
		solrQuery.setRows(30);
		solrQuery.set("df", "item_title");
		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<em>");
		solrQuery.setHighlightSimplePost("</em>");
		// 执行查询，QueryResponse对象
		QueryResponse queryResponse = solrServer.query(solrQuery);
		// 获得文档列表，取查询结果的总记录数
		SolrDocumentList documentList = queryResponse.getResults();
		System.out.println("总记录数：" + documentList.getNumFound());
		// 遍历整个文档列表,获取文档域中的内容
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		for (SolrDocument solrDocument : documentList) {
			System.out.println(solrDocument.get("id"));
			
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			String title = "";
			if (list != null && list.size() > 0) {
				title = list.get(0);
			}else{
				title= (String) solrDocument.getFieldValue("item_title");
			}
			System.out.println(title);
		}

	}

}
