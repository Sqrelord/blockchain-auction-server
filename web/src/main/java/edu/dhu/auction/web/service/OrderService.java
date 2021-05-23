package edu.dhu.auction.web.service;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Order;
import edu.dhu.auction.web.bean.Order.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order generateOrder(UUID lotId);

    List<Order> getAllOrders(Account account);

    Order getOrderInfo(Account account, Long orderId);

    Order changeStatus(Account account, Long orderId, OrderStatus status);
}
