package com.example.demo.order.service;

import com.example.demo.order.entity.Order;
import com.example.demo.order.service.impl.OrderSearch;

import java.util.List;

public interface OrderService {
    public List<Order> findAll(OrderSearch orderSearch);
}
