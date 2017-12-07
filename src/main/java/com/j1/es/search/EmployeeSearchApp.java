package com.j1.es.search;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.search.SearchRequestBuilder;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.j1.es.pojo.Books;

/**
 * 员工搜索
 * 
 * @author wangchuanfu 
 * （1）搜索职位中包含technique的员工 
 * （2）同时要求age在30到40岁之间 
 * （3）分页查询，查找第一页
 *
 */
public class EmployeeSearchApp {
	


	private static final int PORT = 9300;
	private static final String HOST = "localhost";

	public static void main(String[] args) throws Exception {
		/**
		 * 创建client
		 */
		Settings settings = Settings.builder().put("cluster.name", "myelasticsearch").build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(HOST), PORT));

		/**
		 * 构造数据
		 */
		prepareData(client);
		/**
		 * 搜索查询
		 */
		executeSearch(client);
		HighlightedFieldSearch(client);

		client.close();

	}

	private static void prepareData(TransportClient client) throws Exception {
		client.prepareIndex("company", "employee", "1") 
		.setSource(XContentFactory.jsonBuilder()
				.startObject()
					.field("name", "jack")
					.field("age", 27)
					.field("position", "technique software")
					.field("country", "china")
					.field("join_date", "2017-01-01")
					.field("salary", 10000)
				.endObject())
		.get();

		client.prepareIndex("company", "employee", "2")
				.setSource(XContentFactory.jsonBuilder().startObject().field("name", "marry").field("age", 35)
						.field("position", "technique manager").field("country", "china")
						.field("join_date", "2017-01-01").field("salary", 12000).endObject())
				.get();

		client.prepareIndex("company", "employee", "3")
				.setSource(XContentFactory.jsonBuilder().startObject().field("name", "tom").field("age", 32)
						.field("position", "senior technique software").field("country", "china")
						.field("join_date", "2016-01-01").field("salary", 11000).endObject())
				.get();

		client.prepareIndex("company", "employee", "4")
				.setSource(XContentFactory.jsonBuilder().startObject().field("name", "jen").field("age", 25)
						.field("position", "junior finance").field("country", "usa").field("join_date", "2016-01-01")
						.field("salary", 7000).endObject())
				.get();

		client.prepareIndex("company", "employee", "5")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
						.field("name", "mike").field("age", 37)
						.field("position", "finance manager")
						.field("country", "usa").field("join_date", "2015-01-01")
						.field("salary", 15000)
						.endObject())
				.get();
	}

	private static void executeSearch(TransportClient client) {
		SearchResponse searchResponse=client.prepareSearch("company")
				.setTypes("employee")
				   .setQuery( QueryBuilders.matchQuery("position", "technique"))
		             .setPostFilter(QueryBuilders.rangeQuery("age")
		               .from(30).to(40))
		                  .setFrom(0).setSize(1).get();
		
		SearchHit[] searchHits=searchResponse.getHits().getHits();
		for (int i = 0; i < searchHits.length; i++) {
			System.out.println(searchHits[i].getSourceAsString());
		}

	}
	/**
	 *查询
	
	public static List<Books> searchBooks(Books books) throws Exception {
		Client client = getClient();
		/**
		 * 定义查询条件
		
		
		QueryBuilder qb = new BoolQueryBuilder().must(QueryBuilders.matchQuery("title", books.getTitle()));
		SearchResponse response =client.prepareSearch("books").setTypes("books").setQuery(qb).execute().actionGet();
		SearchHit[] hits =response.getHits().getHits();
		List<Books> list=new ArrayList<Books>();
		for(SearchHit hit: hits){
			
			Books book=mapper.readValue(hit.getSourceAsString(), Books.class);
			list.add(book);
		}
		client.close();
		return list;
	}
*/

	private static void HighlightedFieldSearch(TransportClient client) {
		
	       
		 QueryBuilder matchQuery = QueryBuilders.matchQuery("title", "编程");
	        HighlightBuilder hiBuilder=new HighlightBuilder();
	        hiBuilder.preTags("<font style='color:red'>");
	        hiBuilder.postTags("<font");
	        hiBuilder.field("title");
	        // 搜索数据
	        SearchResponse response = client.prepareSearch("blog")
	                .setQuery(matchQuery)
	                .highlighter(hiBuilder)
	                .execute().actionGet();
	        //获取查询结果集
	        SearchHits searchHits = response.getHits();
	        System.out.println("共搜到:"+searchHits.getTotalHits()+"条结果!");
	        //遍历结果
	        for(SearchHit hit:searchHits){
	            System.out.println("String方式打印文档搜索内容:");
	            System.out.println(hit.getSourceAsString());
	            System.out.println("Map方式打印高亮内容");
	            System.out.println(hit.getHighlightFields());

	            System.out.println("遍历高亮集合，打印高亮片段:");
	            Text[] text = hit.getHighlightFields().get("title").getFragments();
	            for (Text str : text) {
	                System.out.println(str.string());
	            }
	        }
	    }
		
	}

