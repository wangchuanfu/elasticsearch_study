package com.j1.es.search;

import java.net.InetAddress;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class EmployeeCRUDApp {
	private static final int PORT=9300;
	private static final String HOST="localhost";
	@SuppressWarnings("unchecked")
	public static void main(String[] args)  throws Exception{
		//创建Client
		Settings settings=Settings.builder().put("cluster.name", "myelasticsearch").build();

		@SuppressWarnings("resource")
		TransportClient client=new PreBuiltTransportClient(settings).addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(HOST),PORT));
		//创建document
		//createDoc(client);
		//
		//getEmployee(client);
		//更新document
	//	updateDoc(client);
		deleteDoc(client);
		client.close();
	}
	/**
	 * 创建document
	 * @param client
	 */
	private static void createDoc(TransportClient client)  throws Exception{
		IndexResponse inresponse=client.prepareIndex("company", "employee","1")
				.setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("name", "jack")
				.field("age", 27)
				.field("position", "technique")
				.field("country", "china")
				.field("join_date", "2017-01-01")
				.field("salary", 10000)
				.endObject())
		        .get();
			System.out.println(inresponse.getResult());
		
	}
	
	/**
	 * 查询document
	 */
	
	private static void getEmployee(TransportClient client) throws Exception {
	
		GetResponse getResponse =client.prepareGet("company", "employee","1").get();
		System.out.println(getResponse.getSourceAsString());
	}
	/**
	 * 更新document
	 */
	private static void updateDoc(TransportClient client)  throws Exception{
		UpdateResponse updateResponse =client.prepareUpdate("company", "employee","1")
		      .setDoc(XContentFactory.jsonBuilder().startObject()
				.field("position", "technique manager").endObject()).get();
		
		System.out.println(updateResponse .getResult());
	}
	
	/**
	 * 删除document
	 */
	private static void deleteDoc(TransportClient client)  throws Exception{
		DeleteResponse  deleteResponse=client.prepareDelete("company", "employee","1").get();
		System.out.println(deleteResponse.getResult());  

	}
}
