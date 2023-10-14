package com.example.demo.order.service.impl;

import com.example.demo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderServiceRepository orderServiceRepository;

}
