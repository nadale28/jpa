package com.example.demo.init;

import com.example.demo.address.entity.Address;
import com.example.demo.book.entity.Book;
import com.example.demo.delivery.entity.Delivery;
import com.example.demo.member.entity.Member;
import com.example.demo.order.entity.Order;
import com.example.demo.orderItem.entity.OrderItem;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class initDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit(){
            Member member = new Member();
            member.setUsername("userA");
            member.setAddress(new Address("서울","1","1111"));
            em.persist(member);

            Book book1 = new Book();
            book1.setName("JPA1 BOOK");
            book1.setPrice(10000);
            book1.setStockQuantity(100);
            em.persist(book1);

            Book book2 = new Book();
            book2.setName("JPA2 BOOK");
            book2.setPrice(20000);
            book2.setStockQuantity(100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1,10000,1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2,20000,2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2(){
            Member member = new Member();
            member.setUsername("userB");
            member.setAddress(new Address("진주","2","2222"));
            em.persist(member);

            Book book1 = new Book();
            book1.setName("SPRING1 BOOK");
            book1.setPrice(12000);
            book1.setStockQuantity(100);
            em.persist(book1);

            Book book2 = new Book();
            book2.setName("SPRING2 BOOK");
            book2.setPrice(13000);
            book2.setStockQuantity(100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1,12000,3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2,13000,2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }
    }

}

