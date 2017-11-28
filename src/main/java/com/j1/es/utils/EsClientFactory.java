//package com.j1.es.utils;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.ImmutableSettings;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.common.transport.TransportAddress;
//
//import com.huayuan.search.core.common.EsBasicConfig;
//import com.huayuan.search.core.exception.ClientModelDuplicateException;
//
//public class EsClientFactory {
//	/**
//	 * LOG
//	 */
//	protected final Log LOG = LogFactory.getLog(EsClientFactory.class);
//	
//	/**
//	 * ES-TCP客戶端
//	 */
//	private TransportClient transportClient;
//	
//	/**
//	 * 每次获得一个新建的TransportClient接口，该接口基于TCP协议进行客户端与ES服务器之间的通讯。
//	 * TransportClient底层使用多线程复用机制，因此可作为应用级别启动，在应用运行期间无需重复创建。
//	 * 配合对象池使用。
//	 * 
//	 * @return TransportClient返回一个TransportClient实例，该实例基于TCP协议进行客户端与ES服务器之间的通讯。
//	 */
//	public static TransportClient getNewInstance() throws Exception
//	{
//		try
//		{
//			EsClientFactory factory = new EsClientFactory();
//			return factory.getClient();
//		}
//		catch(Exception e)
//		{
//			throw new Exception("###EsClientFactory创建错误！###");
//		}
//	}
//	
//	public void close() throws Exception
//	{
//		if(null != transportClient)
//		{
//			transportClient.close();
//			transportClient = null;
//		}
//	}
//	
//	public TransportClient getClient() throws Exception
//	{
//		if(null == transportClient)
//			return internalCreateTransportClient();
//		return transportClient;
//	}
//	
//	/**
//	 * 创建ES-TCP客户端
//	 */
//	protected TransportClient internalCreateTransportClient() throws Exception
//	{
//		final TransportClient client;
//
//		Settings settings = initialSettings();
//
//		if (null != settings)
//		{
//			client = new TransportClient(settings);
//		}
//		else
//		{
//			client = new TransportClient();
//		}
//
//		TransportAddress[] transAddrArr = initialAddress();
//
//		if (null != transAddrArr)
//		{
//			client.addTransportAddresses(transAddrArr);
//		}
//		else
//		{
//			LOG.error("###必须配置服务器节点，否则无法正常启动搜索功能！###");
//			close();
//			System.exit(3);
//		}
//		
//		transportClient = client;
//		return transportClient;
//	}
//
//	/**
//	 * 初始设置
//	 * 
//	 * @return
//	 */
//	private Settings initialSettings()
//	{
//		try
//		{
//			ImmutableSettings.Builder builder = ImmutableSettings
//					.settingsBuilder();
//			// 集群名
//			builder.put("cluster.name",
//					EsBasicConfig.getStrProp("esclient.cluster.name"));
//			// 客户端去嗅探整个集群的状态
//			builder.put("client.transport.sniff", EsBasicConfig.getBoolProp(
//					"esclient.client.transport.sniff", false));
//			// 连接前是否忽略集群名验证
//			builder.put("client.transport.ignore_cluster_name", EsBasicConfig
//					.getBoolProp(
//							"esclient.client.transport.ignore_cluster_name",
//							false));
//			// 设置超时时限
//			builder.put("client.transport.ping_timeout", EsBasicConfig
//					.getIntProp("esclient.client.transport.ping_timeout"));
//			// 设置已连接node的连接测试的时间间隔
//			builder.put(
//					"client.transport.nodes_sampler_interval",
//					EsBasicConfig
//							.getIntProp("esclient.client.transport.nodes_sampler_interval"));
//			return builder.build();
//		}
//		catch (Exception e)
//		{
//			LOG.error(e);
//		}
//		return null;
//	}
//
//	/**
//	 * 设置集群服务器IP,Port
//	 * 
//	 * @return
//	 */
//	private TransportAddress[] initialAddress()
//	{
//		TransportAddress[] transAddrArr = null;
//		try
//		{
//			String defaultPort = EsBasicConfig
//					.getStrProp("esclient.default.server.port");
//			String[] addrArr = getServerAddressArr();
//			transAddrArr = new InetSocketTransportAddress[addrArr.length];
//			for (int i = 0; i < addrArr.length; i++)
//			{
//				String hostname = addrArr[i];
//				// 允许配置文件中存在多余的逗号
//				if (hostname == null || "".equals(hostname))
//					continue;
//				// {192.168.1.2:9301} ==> {192.168.1.2},{9301}
//				String[] hostnameArr = hostname.split(":");
//				// 允许配置文件中存在多余的冒号
//				if (hostnameArr == null || "".equals(hostnameArr[0]))
//					continue;
//				String addr = hostnameArr[0];
//				Integer port = 9300;
//				if (hostnameArr.length > 1 && hostnameArr[1] != null)
//					port = Integer.valueOf(hostnameArr[1]);
//				else if (defaultPort != null)
//					port = Integer.valueOf(defaultPort);
//				transAddrArr[i] = new InetSocketTransportAddress(addr, port);
//			}
//		}
//		catch (Exception e)
//		{
//			LOG.error(e, e);
//		}
//		return transAddrArr;
//	}
//
//	/**
//	 * 获得服务器地址数组
//	 * 
//	 * @return
//	 */
//	private String[] getServerAddressArr()
//	{
//		return EsBasicConfig.getSplitStr("esclient.server.address", ",");
//	}
//
//}
