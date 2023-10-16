package com.example.demo.order.service.impl;

import com.example.demo.order.entity.Order;
import com.example.demo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public List<Order> findAll(OrderSearch orderSearch) {
        return orderRepository.findAll();
    }
}
