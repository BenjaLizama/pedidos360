package com.pedidos360.orders.client;

import com.pedidos360.orders.dto.request.StockOperationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "catalog-service", url = "${external.catalog-service.url}")
public interface CatalogClient {

    @PostMapping("/validate-stock")
    void validateStock(@RequestBody StockOperationRequest request);

    @PutMapping("/decrease-stock")
    void decreaseStock(@RequestBody StockOperationRequest request);
}
