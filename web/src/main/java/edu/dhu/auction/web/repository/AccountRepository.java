package edu.dhu.auction.web.repository;

import edu.dhu.auction.web.bean.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Account SET loginTime = ?2 WHERE id = ?1")
    void updateLoginTime(Long id, LocalDateTime localDateTime);

    Account findByUsername(String username);

}
