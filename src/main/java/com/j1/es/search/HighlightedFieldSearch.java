package com.j1.es.search;

import java.net.InetAddress;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
public class HighlightedFieldSearch {
	private static final int PORT = 9300;
	private static final String HOST = "localhost";
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) throws Exception {
		/**
		 * 创建client
		 */
		Settings settings = Settings.builder().put("cluster.name", "myelasticsearch").build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(HOST), PORT));
		
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


