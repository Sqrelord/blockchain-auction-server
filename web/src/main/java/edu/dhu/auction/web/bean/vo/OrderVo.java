package edu.dhu.auction.web.bean.vo;

import edu.dhu.auction.web.bean.Order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderVo {
    private Long id;
    private BigDecimal payment;
    private LocalDateTime createTime;
    private OrderStatus status;
    private OrderLotVo lot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderLotVo getLot() {
        return lot;
    }

    public void setLot(OrderLotVo lot) {
        this.lot = lot;
    }
}
