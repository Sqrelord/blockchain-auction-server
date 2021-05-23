package edu.dhu.auction.web.repository;

import edu.dhu.auction.web.bean.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long>, JpaSpecificationExecutor<Lot> {
    Optional<Lot> findByAssetIdentifier(UUID uuid);

    @Query(nativeQuery = true, value = "select * from lot where auction_end_time > now() order by rand() limit ?, 8")
    List<Lot> findAllRandom(Integer page);
}
