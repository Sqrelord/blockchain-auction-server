package edu.dhu.auction.web.service.impl;

import edu.dhu.auction.web.bean.*;
import edu.dhu.auction.web.bean.Order.OrderStatus;
import edu.dhu.auction.web.common.rocketmq.RocketMQProducer;
import edu.dhu.auction.web.repository.AccountRepository;
import edu.dhu.auction.web.repository.LotRepository;
import edu.dhu.auction.web.repository.OrderRepository;
import edu.dhu.auction.web.service.OrderService;
import edu.dhu.auction.web.util.AssertException;
import edu.dhu.auction.web.util.AssertUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LogManager.getLogger(OrderServiceImpl.class);

    @Resource
    private AccountRepository accountRepository;
    @Resource
    private LotRepository lotRepository;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private RocketMQProducer producer;

    @Transactional
    @Scheduled(cron = "0 0/5 * * * ?")
    public void checkAndGenerateBidEndTime() {
        log.info("开始执行定时任务 - checkAndGenerateBidEndTime");
        List<Lot> endAuction = lotRepository.findAll((Specification<Lot>) (lot, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.lessThan(lot.get("auctionEndTime"), LocalDateTime.now()),
                        criteriaBuilder.equal(lot.get("isActive"), true)
                ));
        if (!endAuction.isEmpty()) {
            List<Order> orderList = endAuction.stream()
                    .filter(lot -> !lot.getAuctionDetails().isEmpty())
                    .map(lot -> {
                        AuctionDetail highestBid = lot.getAuctionDetails().stream()
                                .max(Comparator.comparing(AuctionDetail::getAmount)).orElseThrow(() -> new AssertException("该拍卖品没有人出价:{}", lot.getId()));
                        Address defaultAddress = highestBid.getBidder().getAddresses().stream()
                                .filter(Address::getIsDefault).findFirst().orElseThrow(() -> new AssertException("该账号无默认地址:{}", highestBid.getBidder().getUsername()));
                        Order order = new Order();
                        order.setPayment(highestBid.getAmount());
                        order.setStatus(OrderStatus.UNCONFIRMED);
                        order.setAddress(defaultAddress);
                        order.setOwner(highestBid.getBidder());
                        order.setLot(lot);
                        lot.setOrder(order);
                        lot.setActive(false);
                        return order;
                    }).collect(Collectors.toList());
            orderRepository.saveAll(orderList);
            endAuction.forEach(lot -> {
                lot.setActive(false);
                if (!lot.getAuctionDetails().isEmpty()) {
                    producer.sendNewOrderMessage(lot);
                }
            });
            lotRepository.saveAll(endAuction);
        }
        log.info("定时任务执行完毕 - checkAndGenerateBidEndTime");
    }

    @Override
    public Order generateOrder(UUID lotId) {
        Lot lot = lotRepository.findByAssetIdentifier(lotId).orElseThrow(() -> AssertException.lotNotExist(lotId));
        Optional<AuctionDetail> optional = lot.getAuctionDetails().stream().max(Comparator.comparing(AuctionDetail::getAmount));
        if (optional.isPresent()) {
            AuctionDetail highestBid = optional.get();
            Address defaultAddress = highestBid.getBidder().getAddresses().stream()
                    .filter(Address::getIsDefault).findFirst().orElseThrow(() -> new AssertException("该账号无默认地址:{}", highestBid.getBidder().getUsername()));
            Order order = new Order();
            order.setPayment(highestBid.getAmount().add(lot.getCommission()));
            order.setStatus(OrderStatus.UNCONFIRMED);
            order.setAddress(defaultAddress);
            order.setOwner(highestBid.getBidder());
            order.setLot(lot);
            lot.setOrder(order);
            lot.setActive(false);
            producer.sendNewOrderMessage(lot);
            return orderRepository.save(order);
        } else {
            lot.setActive(false);
            return lotRepository.save(lot).getOrder();
        }
    }

    @Override
    public List<Order> getAllOrders(Account account) {
        Account owner = accountRepository.findById(account.getId()).orElseThrow(() -> AssertException.accountNotExist(account.getUsername()));
        return orderRepository.findAllByOwner(owner);
    }

    @Override
    public Order getOrderInfo(Account account, Long orderId) {
        Account owner = accountRepository.findById(account.getId()).orElseThrow(() -> AssertException.accountNotExist(account.getUsername()));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AssertException("该订单不存在:{}", orderId));
        AssertUtils.isTrue(Objects.equals(owner, order.getOwner()), "请求用户与订单持有人不匹配:{} -> {}", owner.getUsername(), orderId);
        return order;
    }

    @Override
    public Order changeStatus(Account account, Long orderId, OrderStatus status) {
        Account owner = accountRepository.findById(account.getId()).orElseThrow(() -> AssertException.accountNotExist(account.getUsername()));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AssertException("该订单不存在:{}", orderId));
        AssertUtils.isTrue(Objects.equals(owner, order.getOwner()), "请求用户与订单持有人不匹配:{} -> {}", owner.getUsername(), orderId);
        OrderStatus nextStatus = getNextStatus(status);
        order.setStatus(nextStatus);
        return orderRepository.save(order);
    }

    private OrderStatus getNextStatus(OrderStatus status) {
        if (status.equals(OrderStatus.UNCONFIRMED)) {
            return OrderStatus.CONFIRMED;
        } else if (status.equals(OrderStatus.CONFIRMED)) {
            return OrderStatus.SENT;
        } else if (status.equals(OrderStatus.SENT)) {
            return OrderStatus.SIGNED;
        } else {
            return OrderStatus.NULL;
        }
    }
}
