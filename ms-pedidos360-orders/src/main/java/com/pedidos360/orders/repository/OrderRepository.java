package com.pedidos360.orders.repository;

import com.pedidos360.orders.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findAllByClientIdOrderByCreatedAtDesc(String clientId);
}
