package com.j1.es.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.j1.es.pojo.Books;

public class ElasticSearchTest {

	@Test
	public void testCreatIndexBooks() throws Exception {
		List<Books> booksList = new ArrayList<Books>();
		/**
		 * 添加数据
		 */

		SimpleDateFormat df = new SimpleDateFormat("yyyy");// 设置日期格式
		String format = df.format(new Date());

		booksList.add(new Books(1L, "Java编程思想", "java", "Bruce Eckel", 70L, "Java学习必读经典,殿堂级著作！赢得了全球程序员的"));

		booksList.add(new Books(2L, "Java程序性能优化", "java", "葛一鸣", 60L, "让你的Java程序更快、更稳定。深入剖析软件设计层面、代码层面、JVM虚拟机层面的优化方法"));

		booksList.add(new Books(3L, "Python科学计算", "python", "张若愚", 50L,
				"零基础学python,光盘中作者独家整合开发winPython运行环境，涵盖了Python各个扩展库"));

		booksList.add(new Books(4L, "Python基础教程", "python", "张若愚", 40L, "经典的Python入门教程，层次鲜明，结构严谨，内容翔实"));

		booksList.add(new Books(5L, "JavaScript高级程序设计", "javascript", "Nicholas C.Zakas", 30L, "JavaScript技术经典名著"));

		ElasticSearchUtils.createIndex(booksList);

	}

	

	/**
	 * 测试新增document
	 */
	@Test
	public void testAddDoc() throws Exception {
		Books books = new Books(6L, "Java编程思想", "java", "Bruce Eckel", 6L, "Java学习必读经典,殿堂级著作！赢得了全球程序员的");
		ElasticSearchUtils.addDocument("books", "books", books);

	}

	/**
	 * 测试更新document
	 */
	@Test
	public void testUpdateDoc() throws Exception {
		Books books = new Books(6L, "Spring源码实战", "java", "Bruce Eckel", 6L, "Java学习必读经典,殿堂级著作！赢得了全球程序员的");
		ElasticSearchUtils.addDocument("books", "books", books);

	}

	/**
	 * 删除索引
	 */

	@Test
	public void deleteIndex() throws Exception {
		ElasticSearchUtils.deleteIndex("books");

	}

	/**
	 * 测试删除doc
	 * 
	 * @throws UnknownHostException
	 * @throws JsonProcessingException
	 */
	@Test
	public void testDelDoc() throws Exception {
		ElasticSearchUtils.deleteDocument("books", "books", 5L);
	}
	
	
	/**
	 * 测试高亮显示
	 */
	@Test
	public void searchHighLight() throws Exception {
		 ElasticSearchUtils.searchHighLight("java");
	}
	/**
	 * 测试查询
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchBooks() throws Exception {

		Books books = new Books();
		books.setTitle("java");
		List<Books> bookListResult = ElasticSearchUtils.searchBooks(books);
		/**
		 * restfulApi:
		 *
		 * GET /books/books/_search { "query": { "bool" : { "must" : [ { "match"
		 * : { "title" : { "query" : "java", "operator" : "OR", "prefix_length"
		 * : 0, "max_expansions" : 50, "fuzzy_transpositions" : true, "lenient"
		 * : false, "zero_terms_query" : "NONE", "boost" : 1.0 } } } ],
		 * "disable_coord" : false, "adjust_pure_negative" : true, "boost" : 1.0
		 * } } }
		 */
		for (Books book : bookListResult) {
			System.out.println(book);
		}
	}

}
