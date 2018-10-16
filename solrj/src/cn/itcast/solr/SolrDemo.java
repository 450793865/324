package cn.itcast.solr;

import java.util.List;
import java.util.Map;

import javax.xml.ws.Response;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrDemo {
	
	@Test
	public void testAdd() throws Exception {
		String baseURL="http://localhost:8080/solr/";
		HttpSolrServer solrServer = new HttpSolrServer(baseURL);
		SolrInputDocument doc = new SolrInputDocument();
		doc.setField("id", 3);
		doc.setField("name", "赵云");
		solrServer.add(doc,1000);
	}
	@Test
	public void testDelete() throws Exception {
		//1:连接上SOlr服务器
		String baseURL="http://localhost:8080/solr/";
		HttpSolrServer solrServer = new HttpSolrServer(baseURL);
		solrServer.deleteById("3");
		solrServer.commit();
	}
	@Test
	public void testQuery() throws Exception {
		String baseURL="http://localhost:8080/solr/";
		HttpSolrServer solrServer = new HttpSolrServer(baseURL);
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("q","*:*");
		QueryResponse response = solrServer.query(solrQuery);
		SolrDocumentList docs = response.getResults();
		System.out.println("总条数:" + docs.getNumFound());	
		for (SolrDocument doc : docs) {
			System.out.println("id:" + doc.get("id"));
			System.out.println("title:" + doc.get("title"));
			System.out.println("name:" + doc.get("name"));
		}
	}
	@Test
	public void testQuery1() throws Exception {
		String baseURL="http://localhost:8080/solr/";
		HttpSolrServer solrServer = new HttpSolrServer(baseURL);
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("q", "钻石");
		solrQuery.set("fq", "product_price:{30 TO 80}");
		solrQuery.set("df", "product_keywords");
		solrQuery.addSort("product_price", ORDER.desc);
		solrQuery.set("fl", "id,product_name,product_price");
		solrQuery.setStart(0);
		solrQuery.setRows(10);
		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("product_name");
		solrQuery.setHighlightSimplePre("<font color='red'>");
		solrQuery.setHighlightSimplePost("</font>");
		QueryResponse queryResponse = solrServer.query(solrQuery);
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		System.out.println("共查询到商品数量:" + solrDocumentList.getNumFound());
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println("id:"+solrDocument.get("id"));
			System.out.println("普通name"+solrDocument.get("product_name"));
			Map<String, List<String>> map = highlighting.get(solrDocument.get("id"));
			if (null!= map && map.size()>0) {
				List<String> list = map.get("product_name");
				System.out.println("高亮name"+list.get(0));
			}
			
			System.out.println("product_price"+solrDocument.get("product_price"));
		}
	}
	
}
