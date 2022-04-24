package ait.a00231910.microservices.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import ait.a00231910.microservices.dao.SellerRepository;
import ait.a00231910.microservices.dto.Product;
import ait.a00231910.microservices.dto.Seller;
import ait.a00231910.microservices.dto.TheAccessToken;
import ait.a00231910.microservices.feignclients.ProjectFeignClient;
import ait.a00231910.microservices.utils.SleepUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Service
@RequestMapping("/api")
@Api(value="", tags="Seller Manager")
@Tag(name="Seller Manager", description="Service to carry out Seller functionality")
public class SellerService {
	
	Logger log = LoggerFactory.getLogger(SellerService.class);

	@Autowired
	SellerRepository sellerRepo;
	
	@Autowired
	ProjectFeignClient projectClient;
	
	@Value("${seller-manager.helloProperty}")
	private String helloInstance;
	
	List<Seller> cachedSellers = new ArrayList<>();
	
	@GetMapping("/ping")
	@ApiOperation(value="Return Hello World String from instance",
	notes="Simple API to check that the instance is up and running.")
	public String returnHello()
	{
		log.info("Ping method called");
		return "Hello World from " + helloInstance;
	}
	
	@ApiOperation(value="Method used for testing hello world through gateway. TESTING ONLY.",
			notes="Was used for testing rest templates and calling services.")
	@GetMapping("/helloProxied")
	public String returnHelloProxied()
	{
		log.info("HelloProxied method called");
		String uri = "http://localhost:8080/api/ping";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTAxODI4MzYsInVzZXJfbmFtZSI6IkFkYW0gQ2Fyb2xhbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwianRpIjoiZWExYzgyZjAtNWFiOC00MjY4LThiNjgtZTNiYjc4OWExYTNkIiwiY2xpZW50X2lkIjoic2hvcC1tYW5hZ2VyLWNsaWVudCIsInNjb3BlIjpbIndlYmNsaWVudCJdfQ.c1ZsHY8vT3RhCnDHxCr-gDBvKxAWtIqz2NSgmlp6sE0");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(
				  uri, entity , String.class);
		System.out.println("THE RESULT IS: " + response.getBody().toString());
		return response.getBody().toString();
	}
	
	@ApiOperation(value="Method intended to use jackson for extracting token. TESTING ONLY. ",
			notes="Was going to be used to add create bearer tokens for each seller.")
	@GetMapping("/bearerProxied")
	public String getAccessToken()
	{
		log.info("GetAccessToken method called");
		ObjectMapper mapper = new ObjectMapper();
		String uri = "http://localhost:9999/oauth/token";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic c2hvcC1tYW5hZ2VyLWNsaWVudDpzZWNyZXRzaG9wcGFzc3dvcmQ=");
		RestTemplate restTemplate = new RestTemplate();
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "password");
		map.add("scope", "webclient");
		map.add("username", "Adam Carolan");
		map.add("password", "pa55word");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity( uri, request , String.class );
		
		System.out.println("THE REQUEST: " + request.toString());
		
		String jsonString = response.getBody().toString();
		TheAccessToken token = null;
		try {
			token = mapper.readValue(jsonString, TheAccessToken.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		headers = new HttpHeaders();
		headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTAxODcyNjYsInVzZXJfbmFtZSI6IkFkYW0gQ2Fyb2xhbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwianRpIjoiNGFkYTg3ZmYtZmM1MC00MmE0LThjMzgtZDE0NDMwYTdkZWI4IiwiY2xpZW50X2lkIjoic2hvcC1tYW5hZ2VyLWNsaWVudCIsInNjb3BlIjpbIndlYmNsaWVudCJdfQ.9NyuxddHP1uR0xq4GKA_RiEjXeZCM6kSdS7gyFvfNDk");
				
		System.out.println("THE RESULT IS: " + token.getAccess_token());
		return response.getBody();
	}
	
	
	
	
	// Method that gets a seller by Id while simulating a delay specified in milliseconds
	@HystrixCommand(
			fallbackMethod="getIdFallbackMethod",
			commandProperties= {
					@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="500"),
					@HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="3")
			})
	@GetMapping("/seller-delayed/{delayMs}")
	@ApiOperation(value="Method that gets a seller by Id while simulating a delay specified in milliseconds",
	notes="Takes in an integer representing the number of milliseconds for the delay.")
	List<Seller> getSellersDelayed(@PathVariable("delayMs") int delayMs) {
		log.info("GetSellerDelayed method called");
		SleepUtils.sleep(delayMs);
		return getAllSellerEntities();
	}
	
	public List<Seller> getIdFallbackMethod(int delayMs)
	{
		return cachedSellers;
	}

	// Get list of sellers - Admin role
	@GetMapping("/sellers")
	@ApiOperation(value="Get list of sellers",
	notes="Admin role only.")
	List<Seller> getAllSellerEntities() {
		log.info("sellers method called");
		Iterable<Seller> sellerIter = sellerRepo.findAll();
		List<Seller> sellers = new ArrayList<>();
		for(Seller seller : sellerIter)
		{
			List<Product> products = projectClient.getAllProductEntitiesById(seller.getId());
			seller.setProducts(products);
			sellers.add(seller);
		}
		cachedSellers = sellers;
		return sellers;
	}
	
	// Get seller by ID - Admin role
	@GetMapping("/seller/{id}")
	@ApiOperation(value="Get seller by ID",
	notes="Admin role required.")
	ResponseEntity getSellerById(@PathVariable("id") Long id) {
		log.info("GetSellerById method called");
		Optional<Seller> seller = sellerRepo.findById(id);
		if (seller.isPresent()) {
			List<Product> products = projectClient.getAllProductEntitiesById(id);
			seller.get().setProducts(products);
			return ResponseEntity.status(HttpStatus.OK).body(seller);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with an id of: " + id + " not found");
		}
	}
	
	@GetMapping("/seller/username/{name}")
	@ApiOperation(value="Method that gets a seller by username",
	notes="Requires Admin or Seller role")
	ResponseEntity getSellerByName(@PathVariable("name") String name) {
		log.info("GetSellerByUsername method called");
		Optional<Seller> seller = sellerRepo.findByName(name);
		if (seller.isPresent()) {
			List<Product> products = projectClient.getAllProductEntitiesById(seller.get().getId());
			seller.get().setProducts(products);
			return ResponseEntity.status(HttpStatus.OK).body(seller);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with a username of: " + name + " not found");
		}
	}
	
	@PostMapping("/sellers")
	@ApiOperation(value="Create a seller",
	notes="Open to public")
	ResponseEntity createSeller(@RequestBody Seller seller) {
		log.info("CreateSeller method called");
		if(seller.getPassword() == null || seller.getPassword().equals(" "))
		{
			seller.setPassword("password");
		}
		Optional<Seller> savedSeller = sellerRepo.findByName(seller.getName());
		if(savedSeller.isPresent())
		{
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
		}
		sellerRepo.save(seller);
		
		return ResponseEntity.status(HttpStatus.OK).body(seller);
	}
	
	@PostMapping("/seller/{username}/products")
	@ApiOperation(value="Add a product for a seller based on username",
	notes="Requires Admin or Seller role")
	ResponseEntity addProduct(@RequestHeader("Authorization") String authorization, @PathVariable("username") String username, @RequestBody Product product) {
		log.info("AddProduct method called");
		Optional<Seller> optSeller = sellerRepo.findByName(username);
		if(optSeller.isPresent())
		{
			Long sellerId = optSeller.get().getId();
			product.setSellerId(sellerId);
			System.out.println("Authorization: " + authorization);
			projectClient.createProduct(authorization, product);
			return ResponseEntity.status(HttpStatus.OK).body("Done");
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
	}
	
	@DeleteMapping("/seller/{username}/product/{id}")
	@ApiOperation(value="Delete a product for a seller based on username and product id",
	notes="Requires an Admin or Seller role.")
	ResponseEntity removeProduct(@RequestHeader("Authorization") String authorization, @PathVariable("username") String username, @PathVariable("id") Long id) {
		log.info("RemoveProduct method called");
		Optional<Seller> optSeller = sellerRepo.findByName(username);
		Optional<Product> optProduct = projectClient.getProductById(id);
		if(optSeller.isPresent())
		{
			Long sellerId = optSeller.get().getId();
			String result = "";
			if(optProduct.isPresent())
			{
				if(sellerId == optProduct.get().getSellerId())
				{
					return projectClient.deleteProductById(authorization, id, sellerId);
				}
				else
				{
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User cannot delete that product");
				}
			}
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is not present");
	}
	
	@DeleteMapping("/seller/product/{id}")
	@ApiOperation(value="Delete a product for a seller based on a product id",
	notes="Requires an Admin role.")
	ResponseEntity removeProductAdmin(@RequestHeader("Authorization") String authorization, @PathVariable("id") Long id) {
		log.info("RemoveProductAdmin method called");
		Optional<Product> optProduct = projectClient.getProductById(id);
		if(optProduct.isPresent())
		{
			return projectClient.deleteProductById(authorization, id, 0L);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product is not present");
	}
	
	@PutMapping("/seller/{username}/product/{id}")
	@ApiOperation(value="Update a product based on username and product id.",
	notes="Requires a Seller or Admin role.")
	ResponseEntity putProduct(@RequestHeader("Authorization") String authorization, @PathVariable("username") String username, @PathVariable("id") Long id, @RequestBody Product product) {
		log.info("PutProduct method called");
		Optional<Seller> optSeller = sellerRepo.findByName(username);
		if(optSeller.isPresent())
		{
			Long sellerId = optSeller.get().getId();
			product.setSellerId(sellerId);
			ResponseEntity<String> result = projectClient.updateProductById(authorization, id, product);
			return result;
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is not present");
	}

	@PutMapping("/seller/{id}")
	@ApiOperation(value="Update a seller based on seller id.",
	notes="Requires an Admin role.")
	ResponseEntity updateSellerById(@PathVariable("id") Long id, @RequestBody Seller seller) {
		log.info("UpdateSellerById method called");
		seller.setId(id);
		Optional<Seller> savedSeller = sellerRepo.findById(id);
		if (savedSeller.isPresent()) {
			if (seller.getNumber() == null) {
				seller.setNumber(savedSeller.get().getNumber());
			}
			if (seller.getName() == null) {
				seller.setName(savedSeller.get().getName());
			}
			if (seller.getEmail() == null) {
				seller.setEmail(savedSeller.get().getEmail());
			}
			if(seller.getPassword() == null || seller.getPassword().equals(""))
			{
				seller.setPassword(savedSeller.get().getPassword());
			}
			List<Product> products = projectClient.getAllProductEntitiesById(seller.getId());
			seller.setProducts(products);
			sellerRepo.save(seller);
			return ResponseEntity.status(HttpStatus.OK).body(seller);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with an id of: " + seller.getId() + " not found");
		}
	}
	
	@PutMapping("/seller/username/{name}")
	@ApiOperation(value="Update a seller based on username.",
	notes="Requires an Admin or Seller role.")
	ResponseEntity updateSellerByName(@PathVariable("name") String name, @RequestBody Seller seller) {
		log.info("UpdateSellerByName method called");
		Optional<Seller> savedSeller = sellerRepo.findByName(name);
		if (savedSeller.isPresent()) {
			seller.setId(savedSeller.get().getId());
			if (seller.getNumber() == null) {
				seller.setNumber(savedSeller.get().getNumber());
			}
			if (seller.getName() == null) {
				seller.setName(savedSeller.get().getName());
			}
			if (seller.getEmail() == null) {
				seller.setEmail(savedSeller.get().getEmail());
			}
			if(seller.getPassword() == null || seller.getPassword().equals(""))
			{
				seller.setPassword(savedSeller.get().getPassword());
			}
			List<Product> products = projectClient.getAllProductEntitiesById(seller.getId());
			seller.setProducts(products);
			sellerRepo.save(seller);
			return ResponseEntity.status(HttpStatus.OK).body(seller);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with a username of: " + name + " not found");
		}
	}

	@DeleteMapping("/seller/{id}")
	@ApiOperation(value="Delete a seller based on seller id.",
	notes="Requires an Admin role.")
	ResponseEntity<String> deleteSellerById(@PathVariable("id") Long id) {
		log.info("DeleteSellerById method called");
		Optional<Seller> savedSeller = sellerRepo.findById(id);
		if (savedSeller.isPresent()) {
			sellerRepo.delete(savedSeller.get());
			return ResponseEntity.status(HttpStatus.OK).body(savedSeller.get().toString() + " has been deleted");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with an id of: " + id + " not found");
		}
	}
	
	@DeleteMapping("/seller/username/{name}")
	@ApiOperation(value="Delete a seller based on username.",
	notes="Requires an Admin or Seller role.")
	ResponseEntity<String> deleteSellerByUsernameAndId(@PathVariable("name") String name) {
		log.info("DeleteSellerByUsernameAndId method called");
		Optional<Seller> savedSeller = sellerRepo.findByName(name);
		if (savedSeller.isPresent()) {
			sellerRepo.delete(savedSeller.get());
			return ResponseEntity.status(HttpStatus.OK).body(savedSeller.get().toString() + " has been deleted");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with a username of: " + name + " not found");
		}
	}
}
