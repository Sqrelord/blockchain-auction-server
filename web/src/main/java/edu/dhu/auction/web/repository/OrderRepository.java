package edu.dhu.auction.web.repository;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOwner(Account account);
}
