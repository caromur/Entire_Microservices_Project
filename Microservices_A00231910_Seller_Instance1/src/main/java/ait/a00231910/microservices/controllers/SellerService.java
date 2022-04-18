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

@RestController
@Service
public class SellerService {
	
	Logger log = LoggerFactory.getLogger(SellerService.class);

	@Autowired
	SellerRepository sellerRepo;
	
	@Autowired
	ProjectFeignClient projectClient;
	
	@Value("${seller-manager.helloProperty}")
	private String helloInstance;
	
	@RequestMapping("/")
	public String returnHello()
	{
		return "Hello World from " + helloInstance;
	}
	
	@RequestMapping("/helloProxied")
	public String returnHelloProxied()
	{
		String uri = "http://localhost:8080/";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTAxODI4MzYsInVzZXJfbmFtZSI6IkFkYW0gQ2Fyb2xhbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwianRpIjoiZWExYzgyZjAtNWFiOC00MjY4LThiNjgtZTNiYjc4OWExYTNkIiwiY2xpZW50X2lkIjoic2hvcC1tYW5hZ2VyLWNsaWVudCIsInNjb3BlIjpbIndlYmNsaWVudCJdfQ.c1ZsHY8vT3RhCnDHxCr-gDBvKxAWtIqz2NSgmlp6sE0");
		RestTemplate restTemplate = new RestTemplate();
		//String result = restTemplate.getForObject(uri, String.class);
		ResponseEntity<String> response = restTemplate.postForEntity(
				  uri, entity , String.class);
		System.out.println("THE RESULT IS: " + response.getBody().toString());
		return response.getBody().toString();
	}
	
	@PostMapping("/bearerProxied")
	public String getAccessToken()
	{
		ObjectMapper mapper = new ObjectMapper();
		String uri = "http://localhost:9999/oauth/token";
		HttpHeaders headers = new HttpHeaders();
//		HttpEntity<String> entity = new HttpEntity<String>(headers);
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
		
		//String result = restTemplate.getForObject(uri, String.class);
//		ResponseEntity<String> response = restTemplate.postForEntity(
//				  uri, entity , String.class);
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
		
		//updateSellerById(1L, new Seller(1L, "Adam Carolan", "adam@email.com", "0890000000", null, token.getAccess_token()));
		
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
	@GetMapping("/seller-delayed/{id}/{delayMs}")
	ResponseEntity getSellerByIdDelayed(@PathVariable("id") Long id, @PathVariable("delayMs") int delayMs) {
		SleepUtils.sleep(delayMs);
		return getSellerById(id);
	}
	
	public ResponseEntity getIdFallbackMethod(Long id, int delayMs)
	{
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallback method called as microservice is under heavy load");
	}

	// Get list of sellers - Admin role
	@GetMapping("/sellers")
	List<Seller> getAllSellerEntities() {
		log.info("seller-entities method called");
		Iterable<Seller> sellerIter = sellerRepo.findAll();
		List<Seller> sellers = new ArrayList<>();
		for(Seller seller : sellerIter)
		{
			List<Product> products = projectClient.getAllProductEntitiesById(seller.getId());
			seller.setProducts(products);
			sellers.add(seller);
		}
		return sellers;
	}
	
//	@GetMapping("/seller/{username}")
//	Long getSellerId(@PathVariable("username") String username) {
//		log.info("getSellerId method called");
//		Optional<Seller> seller = sellerRepo.findByName(username);
//		if(seller.isPresent())
//		{
//			return seller.get().getId();
//		}
//		return -1L;
//	}
	
	// Get seller by ID - Seller role
	@GetMapping("/seller/{id}")
	ResponseEntity getSellerById(@PathVariable("id") Long id) {
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
	ResponseEntity getSellerByName(@PathVariable("name") String name) {
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
	ResponseEntity createSeller(@RequestBody Seller seller) {
		if(seller.getPassword() == null || seller.getPassword().equals(" "))
		{
			seller.setPassword("password");
		}
		Optional<Seller> savedSeller = sellerRepo.findByName(seller.getName());
		if(savedSeller.isPresent())
		{
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
		}
//		projectClient.add(seller.getName(), "{noop}" + seller.getPassword());
		sellerRepo.save(seller);
		
		return ResponseEntity.status(HttpStatus.OK).body(seller);
	}
	
	@PostMapping("/seller/{username}/products")
	ResponseEntity addProduct(@PathVariable("username") String username, @RequestBody Product product) {
		Optional<Seller> optSeller = sellerRepo.findByName(username);
		if(optSeller.isPresent())
		{
			Long sellerId = optSeller.get().getId();
			product.setSellerId(sellerId);
			projectClient.createProduct(product);
			return ResponseEntity.status(HttpStatus.OK).body("Done");
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
	}
	
	@DeleteMapping("/seller/{username}/product/{id}")
	ResponseEntity removeProduct(@PathVariable("username") String username, @PathVariable("id") Long id) {
		Optional<Seller> optSeller = sellerRepo.findByName(username);
		if(optSeller.isPresent())
		{
			Long sellerId = optSeller.get().getId();
			//Iterable<Seller> products = productRepo.findAll();
			projectClient.deleteProductById(id);
//			product.setSellerId(sellerId);
//			projectClient.createProduct(product);
			return ResponseEntity.status(HttpStatus.OK).body("Deleted product with id: " + id);
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is not present");
	}

	@PutMapping("/seller/{id}")
	ResponseEntity updateSellerById(@PathVariable("id") Long id, @RequestBody Seller seller) {
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
//			projectClient.add(seller.getName(), seller.getPassword());
//			projectClient.remove(savedSeller.get().getName());
			List<Product> products = projectClient.getAllProductEntitiesById(seller.getId());
			seller.setProducts(products);
			sellerRepo.save(seller);
			return ResponseEntity.status(HttpStatus.OK).body(seller);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with an id of: " + id + " not found");
		}
	}
	
	@PutMapping("/seller/username/{name}")
	ResponseEntity updateSellerByName(@PathVariable("name") String name, @RequestBody Seller seller) {
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
//			projectClient.add(seller.getName(), seller.getPassword());
//			projectClient.remove(savedSeller.get().getName());
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
	ResponseEntity<String> deleteSellerById(@PathVariable("id") Long id) {
		Optional<Seller> savedSeller = sellerRepo.findById(id);
		if (savedSeller.isPresent()) {
			sellerRepo.delete(savedSeller.get());
//			projectClient.remove(savedSeller.get().getName());
			return ResponseEntity.status(HttpStatus.OK).body(savedSeller.get().toString() + " has been deleted");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with an id of: " + id + " not found");
		}
	}
	
	@DeleteMapping("/seller/username/{name}")
	ResponseEntity<String> deleteSellerById(@PathVariable("name") String name) {
		Optional<Seller> savedSeller = sellerRepo.findByName(name);
		if (savedSeller.isPresent()) {
			sellerRepo.delete(savedSeller.get());
//			projectClient.remove(savedSeller.get().getName());
			return ResponseEntity.status(HttpStatus.OK).body(savedSeller.get().toString() + " has been deleted");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with a username of: " + name + " not found");
		}
	}
}
