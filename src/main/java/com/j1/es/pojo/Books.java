package com.j1.es.pojo;

import java.util.Date;

public class Books {

	/**
	 * 


{"index":{ "_index": "books", "_type": "IT", "_id": "5" }}
{
"id":"5",
"title":"JavaScript高级程序设计",
"language":"javascript",
"author":"Nicholas C.Zakas",
"price":66.40,
"year":2012,
"description":"JavaScript技术经典名著"}
	 */
	
	
	private Long id;
	private String title;
	private String  language;
	private String author;
	private Long price;
	private Date year;
	private String  description;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
	public Date getYear() {
		return year;
	}
	public void setYear(Date year) {
		this.year = year;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	/**
	 *构造函数
	 *private Long id;
	private String title;
	private String  language;
	private String author;
	private Long price;
	private Date year;
	private String  description;
	 */
	   public Books(Long id,  String title, String language, String author,  long price, String  description) {
       	 super();  
	        this.id = id;  
	        this.title = title;  
	        this.language = language;  
	        this.author = author;  
	        this.price = price;  
	      
	        this.description = description;  
	       
	}
	@Override
	public String toString() {
		return "Books [id=" + id + ", title=" + title + ", language=" + language + ", author=" + author + ", price="
				+ price + ", description=" + description + "]";
	}
	public Books() {  
        super();  
    }  
	
	
}
