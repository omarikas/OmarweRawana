package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "checkoutservice", url = "http://host.docker.internal:8083")
public interface TransactionServiceClient {

    @PostMapping("/transactions/checkout")
    void checkout(@RequestParam("userId") Long userId,
                  @RequestBody List<String> productIds);


    @PostMapping("/transactions/cancel")
    void cancel(@RequestParam Long userId);


}
