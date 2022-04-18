package ait.a00231910.microservices.feignclients;

import ait.a00231910.microservices.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("zuul-api-gateway")
//@FeignClient("localhost:8989")
public interface ProjectFeignClient
{
    @GetMapping("/product-manager/products/{id}")
    List<Product> getAllProductEntitiesById(@PathVariable("id") Long id);
    
    @RequestMapping("/authentication-service/user/add/{username}/{password}")
    String add(@PathVariable("username") String username, @PathVariable("password") String password);
    
    @RequestMapping("/authentication-service/user/delete/{username}")
    String remove(@PathVariable("username") String username);
    
    @PostMapping("product-manager/products")
    ResponseEntity<Product> createProduct(@RequestBody Product product);
    
    @DeleteMapping("product-manager/product/{id}")
    ResponseEntity<String> deleteProductById(@PathVariable("id") Long id);

//    @GetMapping("/service-two/servicetwo-entities/{id}")
//    ServiceTwoEntity getServiceTwoEntitiesById(@PathVariable Long id);
}