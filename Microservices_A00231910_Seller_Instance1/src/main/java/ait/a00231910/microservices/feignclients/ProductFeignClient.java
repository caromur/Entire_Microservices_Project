package ait.a00231910.microservices.feignclients;

import ait.a00231910.microservices.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("zuul-api-gateway")
//@FeignClient("localhost:8989")
public interface ProductFeignClient
{
    @GetMapping("/product-manager/product-entities/{id}")
    List<Product> getAllProductEntitiesById(@PathVariable("id") Long id);

//    @GetMapping("/service-two/servicetwo-entities/{id}")
//    ServiceTwoEntity getServiceTwoEntitiesById(@PathVariable Long id);
}