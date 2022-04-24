package ait.a00231910.microservices.feignclients;

import ait.a00231910.microservices.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient("zuul-api-gateway")
//@FeignClient("localhost:8989")
public interface ProjectFeignClient
{
    @GetMapping("/product-manager/api/products/{id}")
    List<Product> getAllProductEntitiesById(@PathVariable("id") Long id);
    
    @RequestMapping("/authentication-service/user/add/{username}/{password}")
    String add(@PathVariable("username") String username, @PathVariable("password") String password);
    
    @RequestMapping("/authentication-service/user/delete/{username}")
    String remove(@PathVariable("username") String username);
    
    @PostMapping("product-manager/api/products")
    ResponseEntity<Product> createProduct(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @RequestBody Product product);
    
    @DeleteMapping("product-manager/api/product/{id}")
    ResponseEntity<String> deleteProductById(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable("id") Long id, Long sellerId);

    @GetMapping("product-manager/api/product/{id}")
    Optional<Product> getProductById(@PathVariable("id") Long id);
    
    @PutMapping("product-manager/api/product/{id}")
    ResponseEntity<String> updateProductById(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable("id") Long id, @RequestBody Product product);

}