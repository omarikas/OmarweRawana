package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "productsearch", url = "http://host.docker.internal:8082")
public interface ProductServiceClient {

    @GetMapping("/products/exists")
    boolean isValidProduct(@RequestParam("productId") String productId);

    @PostMapping("/products/batch")
    ResponseEntity<List<Map<String, Object>>> getProductsByIds(@RequestBody List<String> ids);

}
