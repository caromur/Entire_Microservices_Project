package ait.a00231910.microservices.dto;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ait.a00231910.microservices.dto.ProductDTO;
import ait.a00231910.microservices.entities.Seller;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Seller details")
public class SellerDTO {
	
	@ApiModelProperty(notes="Unique seller id")
	private Long id;
	
	@ApiModelProperty(notes="Seller name")
	@Size(min=3, message="Name must be at least 3 characters")
	private String name;
	@ApiModelProperty(notes="Must be a valid email format")
	@Email(message="Must be a valid email address format")
	private String email;
	@ApiModelProperty(notes="Number should be between 10 and 20 digits")
	@Size(min=10, max=20, message="Number should be between 10 and 20 digits")
	private String number;
	@ApiModelProperty(notes="Password must be at least 7 characters")
	@Size(min=7, message="Password must be at least 7 characters")
	private String password;
	
	@Transient
	private List<ProductDTO> products;
	
	public SellerDTO()
	{
		
	}
	
	public SellerDTO(Seller seller)
	{
		this.id = seller.getId();
		this.name = seller.getName();
		this.email = seller.getEmail();
		this.number = seller.getNumber();
		this.products = seller.getProducts();
		this.password = seller.getPassword();
	}
	
	public SellerDTO(Long id, String name, String email, String number, List<ProductDTO> products) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.number = number;
		this.products = products;
	}
	
	public SellerDTO(Long id, String name, String email, String number, List<ProductDTO> products, String password) {
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

	public List<ProductDTO> getProducts() {
		return products;
	}

	public void setProducts(List<ProductDTO> products) {
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
