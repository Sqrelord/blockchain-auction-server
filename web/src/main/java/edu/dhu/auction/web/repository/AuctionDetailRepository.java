package edu.dhu.auction.web.repository;

import edu.dhu.auction.web.bean.AuctionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionDetailRepository extends JpaRepository<AuctionDetail, Long> {
    List<AuctionDetail> findAllByLot_IdOrderByBidTimeDesc(Long id);
}
