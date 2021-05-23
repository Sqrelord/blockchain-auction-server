package edu.dhu.auction.web.repository;

import edu.dhu.auction.web.bean.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByAccount_Id(Long id);
}
