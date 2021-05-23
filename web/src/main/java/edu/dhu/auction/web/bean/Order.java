package edu.dhu.auction.web.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS")
public class Order implements Serializable {

    private static final long serialVersionUID = 2735628143741248375L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal payment;
    private LocalDateTime createTime = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NULL;
    @ManyToOne
    private Address address;
    @ManyToOne
    private Account owner;
    @OneToOne(fetch = FetchType.LAZY)
    private Lot lot;

    public enum OrderStatus {
        /*无状态*/
        NULL,
        /*订单未确认*/
        UNCONFIRMED,
        /*订单已确认*/
        CONFIRMED,
        /*已发货*/
        SENT,
        /*已签收*/
        SIGNED
    }

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }
}
