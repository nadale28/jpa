package com.example.demo.order.service.impl;

import com.example.demo.order.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    @PersistenceContext
    private final EntityManager em;

    public void save(Order order) {em.persist(order);}

    public Order findOne(Long id) {return em.find(Order.class, id);}

    public List<Order> findAll(OrderSearch orderSearch){

        // 동적쿼리라면...
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;
        if(orderSearch.getOrderStatus() != null){
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000);
        if(orderSearch.getOrderStatus() != null){
            query = query.setParameter("status",orderSearch.getOrderStatus());
        }

        return em.createQuery("select o from Order o join o.member m" +
                " where o.status = :status " +
                " and m.username like :name ",Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

}
