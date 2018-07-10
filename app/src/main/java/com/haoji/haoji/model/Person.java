package com.haoji.haoji.model;
public class Person implements java.io.Serializable{
	private String name;
	private String phone;
	private String email;
	private String address;
	private String id;
	public Person(String name, String phone) {
		super();
		this.name = name;
		this.phone = phone;
	}	
	public Person() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getID() {
		return id;
	}
	public void setID(String id) {
		this.id = id;
	}
	
}