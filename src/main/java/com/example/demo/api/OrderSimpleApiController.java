package com.example.demo.api;

import com.example.demo.address.entity.Address;
import com.example.demo.order.entity.Order;
import com.example.demo.order.entity.OrderStatus;
import com.example.demo.order.service.OrderService;
import com.example.demo.order.service.impl.OrderRepository;
import com.example.demo.order.service.impl.OrderSearch;
import com.example.demo.order.simpleQuery.OrderSimpleQueryDto;
import com.example.demo.order.simpleQuery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 * Order -> OrderItem (OneToMany)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * 아래 코드의 문제
     * 1.무한 루프에 빠진다.
     * Order에 있는 member를 불러오고 Member는 orders를 불러오는 무한루프...
     * 해결하려면? members나 orders 둘 중 하나를 @JsonIgnore 해줘야한다.
     * 2.LAZY로 인한 문제
     * Order에 있는 Member에 LAZY가 걸려있다.
     * 그렇기 떄문에 Proxy 객체를 사용하게 되는데
     * 문제는 이걸 JSON으로 반환할때 프록시 객체를 어떻게 JSON으로 반환해요! 라는 에러
     * 해결하려면? Hibernate5Module 설치 후 Bean으로 등록...  DemoApplication 소스 확인
     *  또는 for문을 돌려버리기
     *  3. n+1 문제
     *  주문목록이 두개일때 쿼리가 몇 번 실행될까?
     *  주문목록 1번 + 회원 2번 + 배송 2번 총 5번...
     *  그냥 EAGER를 쓰는게 맞나? order가 어디어디 연관이 있을까?
     *  EAGER를 쓰면 조회할 때 제어하기가 너무 힘들다.
     *  무슨 쿼리가 나갈지 예측이 너무 어려움. 위험
     *
     * @return
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderService.findAll(new OrderSearch());
        for(Order order : all) {
            order.getMember().getUsername();
        }
        return all;
    }


    /**
     * fetch join
     * fetch join을 사용하여 위의 문제를 해결!
     * 근데 JPQL의 단점... 동적쿼리가 불편한다.
     * 이건 어떻게 해결할것인가
     * @return
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(1,10);
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }


    /**
     * for문을 돌리지 않고 쿼리에서 바로 Dto에 매핑한다.
     * 하지만 재사용성이 떨어진다.
     * 특정 Dto에 종속되므로
     * @return
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getUsername();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
