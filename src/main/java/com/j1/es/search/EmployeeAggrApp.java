package com.j1.es.search;


import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * 员工聚合分析应用程序
 * @author Administrator
 *
（1）首先按照country国家来进行分组
（2）然后在每个country分组内，再按照入职年限进行分组
（3）最后计算每个分组内的平均薪资

GET /company/employee/_search
{
  "size": 0,
  "aggs": {
    "group_by_country": {
      "terms": {
        "field": "country"
      },
      "aggs": {
        "group_by_join_date": {
          "date_histogram": {
            "field": "join_date",
            "interval": "year"
          },
          "aggs": {
            "avg_salary": {
              "avg": {
                "field": "salary"
              }
            }
          }
        }
      }
    }
  }
}
 */
public class EmployeeAggrApp {
	@SuppressWarnings("unchecked")
	public static void main(String[] args)  throws Exception{
		
		/**
		 * 过去client
		 */
		Settings settings=Settings.builder().put("cluster.name", "myelasticsearch").build();
		
		@SuppressWarnings("resource")
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300)); 
		SearchResponse response=client.prepareSearch("company") 
		.addAggregation(AggregationBuilders.terms("group_by_country").field("country")
				.subAggregation(AggregationBuilders
						.dateHistogram("group_by_join_date")
						.field("join_date")
						.dateHistogramInterval(DateHistogramInterval.YEAR)
						.subAggregation(AggregationBuilders.avg("avg_salary").field("salary")))
		)
		.execute().actionGet();

		Map<String, Aggregation> aggrMap=response.getAggregations().asMap();
		
		StringTerms groupByCountry=(StringTerms) aggrMap.get("group_by_country");
		Iterator <Bucket>groupByCountryBucketIterator=groupByCountry.getBuckets().iterator();
		while(groupByCountryBucketIterator.hasNext()){
			Bucket groupByCountryBucket=groupByCountryBucketIterator.next();
			System.out.println(groupByCountryBucket.getKey() + ":" + groupByCountryBucket.getDocCount()); 
			
			Histogram groupByJoinDate=(org.elasticsearch.search.aggregations.bucket.histogram.Histogram) groupByCountryBucket.getAggregations().asMap().get("group_by_join_date");
	
			Iterator<org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket> groupByJoinDateBucketIterator =(Iterator<org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket>) groupByJoinDate.getBuckets().iterator();
		while(groupByJoinDateBucketIterator.hasNext()){
			org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket groupByJoinDateBucket = groupByJoinDateBucketIterator.next();
			System.out.println(groupByJoinDateBucket.getKey() + ":" +groupByJoinDateBucket.getDocCount()); 
			Avg avg=(Avg) groupByJoinDateBucket.getAggregations().asMap().get("avg_salary");
			System.out.println(avg.getValue());
		  }
		}
		
		client.close();
	}
	/**
	 * china:3
			2016-01-01T00:00:00.000Z:1
			11000.0
			2017-01-01T00:00:00.000Z:2
			11000.0
	  usa:2
			2015-01-01T00:00:00.000Z:1
			15000.0
			2016-01-01T00:00:00.000Z:1
			7000.0
	 */

	/**
	 * "doc_count": 3,
          "group_by_join_date": {
            "buckets": [
              {
                "key_as_string": "2016-01-01T00:00:00.000Z",
                "key": 1451606400000,
                "doc_count": 1,
                "avg_salary": {
                  "value": 11000
                }
              },
              {
                "key_as_string": "2017-01-01T00:00:00.000Z",
                "key": 1483228800000,
                "doc_count": 2,
                "avg_salary": {
                  "value": 11000
                }
              }
            ]
          }
        },
        {
          "key": "usa",
          "doc_count": 2,
          "group_by_join_date": {
            "buckets": [
              {
                "key_as_string": "2015-01-01T00:00:00.000Z",
                "key": 1420070400000,
                "doc_count": 1,
                "avg_salary": {
                  "value": 15000
                }
              },
              {
                "key_as_string": "2016-01-01T00:00:00.000Z",
                "key": 1451606400000,
                "doc_count": 1,
                "avg_salary": {
                  "value": 7000
                }
              }
            ]


	 */
}
