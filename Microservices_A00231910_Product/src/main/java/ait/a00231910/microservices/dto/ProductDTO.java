package ait.a00231910.microservices.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import ait.a00231910.microservices.entity.Product;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Product details")
public class ProductDTO {
	
	private Long id;
	
	@ApiModelProperty(notes="Product name must be at least 3 characters long")
	private String name;
	@ApiModelProperty(notes="Product description must be at least 5 characters long")
	private String description;
	@ApiModelProperty(notes="Product price must be greater than 0")
	private Double price;
	@ApiModelProperty(notes="Seller ID - Seller that owns the product - cannot be null")
	private Long sellerId;
	
	public ProductDTO()
	{
		
	}
	
	public ProductDTO(Product product)
	{
		this.id = product.getId();
		this.name = product.getName();
		this.description = product.getDescription();
		this.price = product.getPrice();
		this.sellerId = product.getSellerId();
	}
	
	public ProductDTO(Long id, String name, String description, double price, Long sellerId) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.sellerId = sellerId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price
				+ ", sellerId=" + sellerId + "]";
	}

}
