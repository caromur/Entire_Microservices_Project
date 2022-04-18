package ait.a00231910.microservices.dto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Seller {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	private String name;
	private String email;
	private String number;
	private String password;
	
	@Transient
	private List<Product> products;
	
	public Seller()
	{
		
	}
	
	public Seller(Long id, String name, String email, String number, List<Product> products) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.number = number;
		this.products = products;
	}
	
	public Seller(Long id, String name, String email, String number, List<Product> products, String password) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.number = number;
		this.products = products;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Seller [id=" + id + ", name=" + name + ", email=" + email + ", number=" + number + ", products="
				+ products + "]";
	}

}
