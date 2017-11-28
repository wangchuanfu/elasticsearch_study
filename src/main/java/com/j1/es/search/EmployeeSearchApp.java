package com.j1.es.search;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

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
		//executeSearch(client);
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
		/**
		 * GET /company/_search
			{
			  "query": {
			    "bool": {
			      "must": [
			        {
			          "match": {
			            "position": "technique"
			          }
			        }
			      ],
			      "filter": {
			        "range": {
			          "age": {
			            "gte": 30,
			            "lte": 40
			          }
			        }
			      }
			    }
			  },
			  
			  "from": 0,
			  "size": 1
			}
		 */
		SearchHit[] searchHits=searchResponse.getHits().getHits();
		for (int i = 0; i < searchHits.length; i++) {
			System.out.println(searchHits[i].getSourceAsString());
		}

	}

}
