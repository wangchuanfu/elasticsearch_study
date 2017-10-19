package com.j1.es.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j1.es.pojo.Books;


public class ElasticSearchUtils{
	
	protected static final Logger logger = LoggerFactory.getLogger(ElasticSearchUtils.class);
	/**
	 * es服务器的host
	 */
	private static final String HOST = "localhost";

	/**
	 * es服务器暴露给client的port
	 */
	private static final int PORT = 9300;

	/**
	 * jackson用于序列化操作的mapper
	 */
	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 获得client
	 */
	private static Client getClient() throws Exception {
		Settings settings = Settings.builder().put("cluster.name", "myelasticsearch").build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(HOST), PORT));
		return client;

	}
	/**
	 * 创建索引
	 * 
	 * @throws Exception
	 */
	public static void createIndex(List<Books> bookList) throws Exception {

		Client client = getClient();

		/**
		 * 先判断该索引是否存在,如果存在则删除重新创建
		 */
		if (client.admin().indices().prepareExists("books").get().isExists()) {
			client.admin().indices().prepareDelete("books").get();
		}
		/**
		 * 设置mapping属性
		 */

		String mappingBooks="{ \"books\" : { \"properties\": "
				+ "{ \"id\":"
				+ " { \"type\": \"long\" },"
				+ "\"title\": { \"type\": \"string\",\"analyzer\": \"ik_max_word\" },"
				+ " \"language\": {\"type\": \"string\", \"analyzer\": \"ik_max_word\"},"
				+ " \"author\": {\"type\": \"string\",\"index\": \"not_analyzed\"},"
				+ " \"price\": {\"type\": \"long\",\"index\": \"not_analyzed\"}, "
				+ "\"year\": {\"type\": \"date\",\"index\": \"not_analyzed\"},"
				+ "\"description\": { \"type\": \"string\",\"analyzer\": \"ik_max_word\" }"
				+ "}"
				+ "}"
				+ "}";
		/**
		 * 创建索引,并将索引类型设置为books
		 */
		client.admin().indices().prepareCreate("books").addMapping("books", mappingBooks).get();

		
		// 批量处理request
				BulkRequestBuilder bulkRequest = client.prepareBulk();
				byte[] json;

				for (Books books : bookList) {
					json = mapper.writeValueAsBytes(books);
					bulkRequest.add(new IndexRequest("books", "books", books.getId() + "").source(json));
				}
				
				
				
				// 执行批量处理request
				BulkResponse bulkResponse = bulkRequest.get();

				try {
					// 处理错误信息
					if (bulkResponse.hasFailures()) {
						System.out.println("====================批量创建索引过程中出现错误 下面是错误信息==========================");
						long count = 0L;
						for (BulkItemResponse bulkItemResponse : bulkResponse) {
							System.out.println("发生错误的 索引id为 : " + bulkItemResponse.getId() + " ，错误信息为："
									+ bulkItemResponse.getFailureMessage());
							count++;
						}
						System.out.println(
								"====================批量创建索引过程中出现错误 上面是错误信息 共有: " + count + " 条记录==========================");
						logger.info("创建索引成功");
					}
				} catch (Exception e) {
					logger.error("Cookie Decode Error.", e);
				}
          
				client.close();
	}

	
	/**
	 *查询
	 */
	public static List<Books> searchBooks(Books books) throws Exception {
		Client client = getClient();
		/**
		 * 定义查询条件
		 */
		
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
	
	/**
	 * 新增一个document
	 */
	public static void addDocument(String index,String type,Books books ) throws Exception {
		Client client = getClient();
		byte[] json=mapper.writeValueAsBytes(books);
		
		client.prepareIndex(index, type, books.getId() + "").setSource(json).get();
		client.close();
		System.out.println("========================创建成功=======================");
	}
	
	
	/**
	 * 删除document
	 */
	public static void deleteDocument(String index,String type,Long id ) throws Exception {
		Client client = getClient();
		
		client.prepareDelete(index, type,id+"").get();
		
		client.close();
		System.out.println("=======================删除成功=======================");

		
	}
	
	/**
	 * 更新document
	 */
	
	
	public static void updateDocument(String index,String type,Books books ) throws Exception {
		Client client = getClient();
		
		addDocument(index,type,books);
		
		client.close();
		System.out.println("========================更新成功=======================");
	}
	
	/**
	 * 删除索引
	 */
	public static void deleteIndex(String index) throws Exception {
		Client client = getClient();
		
		DeleteIndexResponse delete=client.admin().indices().prepareDelete(index).execute().actionGet();
		if(delete.isAcknowledged()){
			System.out.println("========================删除成功========================");
		}
		client.close();
		
	}
}
