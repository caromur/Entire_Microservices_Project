package ait.a00231910.microservices.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ait.a00231910.microservices.dao.ProductRepository;
import ait.a00231910.microservices.dto.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Service
@RequestMapping("/api")
@Api(value="", tags="Product Manager")
@Tag(name="Product Manager", description="Service to carry out Product functionality")
public class ProductService {
	
	Logger log = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	ProductRepository productRepo;
	
	@Value("${product-manager.helloProperty}")
	private String helloInstance;
	
	@GetMapping("/ping")
	@ApiOperation(value="Return Hello World String from instance",
	notes="Simple API to check that the instance is up and running.")
	public String returnHello()
	{
		log.info("Ping method called");
		return "Hello World from " + helloInstance;
	}
	
	@GetMapping("/product/{id}")
	@ApiOperation(value="Return a product based on an id",
	notes="Publicly available.")
	ResponseEntity getProductById(@PathVariable("id") Long id) {
		log.info("product/{id} method called");
		Optional<Product> product = productRepo.findById(id);
		if (product.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(product);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Product with an id of: " + id + " not found");
		}
	}
	
	@GetMapping("/products")
	@ApiOperation(value="Return list of all products.",
	notes="Publicly available.")
	List<Product> getAllProductEntities() {
		log.info("products method called");
		Iterable<Product> productIter = productRepo.findAll();
		List<Product> products = new ArrayList<>();
		for(Product product : productIter)
		{
			products.add(product);
		}
		return products;
	}
	
	@GetMapping("/products/{id}")
	@ApiOperation(value="Return list of products for a particular user.",
	notes="Restricted to Admin and Seller roles.")
	List<Product> getAllProductEntitiesById(@PathVariable("id") Long id) {
		log.info("products/{id} method called");
		List<Product> products = productRepo.findBySellerId(id);
		return products;
	}

	@PostMapping("/products")
	@ApiOperation(value="Create a product.",
	notes="Restricted to Seller and Admin roles.")
	ResponseEntity<Product> createProduct(@RequestBody Product product) {
		log.info("CreateProduct method called");
		productRepo.save(product);
		return ResponseEntity.status(HttpStatus.OK).body(product);
	}

	@PutMapping("/product/{id}")
	@ApiOperation(value="Update a product.",
	notes="Restricted to Seller and Admin roles.")
	ResponseEntity updateProductById(@PathVariable("id") Long id, @RequestBody Product product) {
		log.info("UpdateProductById method called");
		product.setId(id);
		Optional<Product> savedProduct = productRepo.findById(id);
		if (savedProduct.isPresent()) {
			if (product.getPrice() == null) {
				product.setPrice(savedProduct.get().getPrice());
			}
			if (product.getName() == null) {
				product.setName(savedProduct.get().getName());
			}
			if (product.getDescription() == null) {
				product.setDescription(savedProduct.get().getDescription());
			}
			if(product.getSellerId() == savedProduct.get().getSellerId())
			{
				productRepo.save(product);
				return ResponseEntity.status(HttpStatus.OK).body(product);
			}
			else
			{
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Product does not belong to this user");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Product with an id of: " + id + " not found");
		}
	}

	@DeleteMapping("/product/{id}")
	@ApiOperation(value="Delete a product.",
	notes="Restricted to Seller and Admin roles.")
	ResponseEntity<String> deleteProductById(@PathVariable("id") Long id, Long sellerId) {
		log.info("DeleteProductById method called");
		Optional<Product> savedProduct = productRepo.findById(id);
		if (savedProduct.isPresent()) {
			productRepo.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body(savedProduct.get().toString() + " has been deleted");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Product with an id of: " + id + " not found");
		}
	}
}
