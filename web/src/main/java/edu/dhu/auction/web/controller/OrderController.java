package edu.dhu.auction.web.controller;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Order;
import edu.dhu.auction.web.bean.Order.OrderStatus;
import edu.dhu.auction.web.bean.vo.OrderDetailVo;
import edu.dhu.auction.web.bean.vo.OrderVo;
import edu.dhu.auction.web.service.OrderService;
import edu.dhu.auction.web.util.BeanUtils;
import edu.dhu.auction.web.util.JwtUtils;
import edu.dhu.auction.web.util.ResultEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
public class OrderController {
    @Resource
    private OrderService orderService;

    @PostMapping("/order/generate")
    public ResultEntity<Object> generateOrder(String lotId) {
        Order order = orderService.generateOrder(UUID.fromString(lotId));
        return ResultEntity.ok(BeanUtils.copy(order, OrderVo.class));
    }

    @GetMapping("/order/list")
    public ResultEntity<Object> getOrderList(@RequestHeader("token") String token) {
        Account auth = JwtUtils.getAccount(token);
        List<Order> orderList = orderService.getAllOrders(auth);
        return ResultEntity.ok(BeanUtils.copyList(orderList, OrderVo.class));
    }

    @GetMapping("/order/info")
    public ResultEntity<Object> getOrderInfo(@RequestHeader("token") String token, Long orderId) {
        Account auth = JwtUtils.getAccount(token);
        Order order = orderService.getOrderInfo(auth, orderId);
        return ResultEntity.ok(BeanUtils.copy(order, OrderDetailVo.class));
    }

    @PostMapping("/order/status")
    public ResultEntity<Object> nextStatus(@RequestHeader("token") String token, Long orderId, OrderStatus status) {
        Account auth = JwtUtils.getAccount(token);
        Order order = orderService.changeStatus(auth, orderId, status);
        return ResultEntity.ok(BeanUtils.copy(order, OrderDetailVo.class));
    }
}
