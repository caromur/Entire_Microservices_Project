package ait.a00231910.microservices.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import ait.a00231910.microservices.dao.SellerRepository;
import ait.a00231910.microservices.dto.Product;
import ait.a00231910.microservices.dto.Seller;
import ait.a00231910.microservices.feignclients.ProductFeignClient;
import ait.a00231910.microservices.utils.SleepUtils;

@RestController
@Service
public class SellerService {

	@Autowired
	SellerRepository sellerRepo;
	
	@Autowired
	ProductFeignClient productClient;
	
	@Value("${seller-manager.helloProperty}")
	private String helloInstance;
	
	@RequestMapping("/")
	public String returnHello()
	{
		return "Hello World from " + helloInstance;
	}
	
	
	@GetMapping("/seller/{id}")
	ResponseEntity getSellerById(@PathVariable("id") Long id) {
		Optional<Seller> seller = sellerRepo.findById(id);
		if (seller.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(seller);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with an id of: " + id + " not found");
		}
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

	@GetMapping("/sellers")
	Iterable<Seller> getAllSellers() {
		return sellerRepo.findAll();
	}

	@GetMapping("/seller-entities")
	List<Seller> getAllSellerEntities() {
		Iterable<Seller> sellerIter = sellerRepo.findAll();
		List<Seller> sellers = new ArrayList<>();
		for(Seller seller : sellerIter)
		{
			List<Product> products = productClient.getAllProductEntitiesById(seller.getId());
			seller.setProducts(products);
			sellers.add(seller);
		}
		return sellers;
	}
	
	@PostMapping("/sellers")
	ResponseEntity<Seller> createSeller(@RequestBody Seller seller) {
		sellerRepo.save(seller);
		return ResponseEntity.status(HttpStatus.OK).body(seller);
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

			sellerRepo.save(seller);
			return ResponseEntity.status(HttpStatus.OK).body(seller);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with an id of: " + id + " not found");
		}
	}

	@DeleteMapping("/seller/{id}")
	ResponseEntity<String> deleteSellerById(@PathVariable("id") Long id) {
		Optional<Seller> savedSeller = sellerRepo.findById(id);
		if (savedSeller.isPresent()) {
			sellerRepo.delete(savedSeller.get());
			return ResponseEntity.status(HttpStatus.OK).body(savedSeller.get().toString() + " has been deleted");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Seller with an id of: " + id + " not found");
		}
	}
}
