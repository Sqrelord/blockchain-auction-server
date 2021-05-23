package edu.dhu.auction.web.controller;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.AuctionDetail;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.bean.vo.AccountVo;
import edu.dhu.auction.web.bean.vo.AuctionDetailVo;
import edu.dhu.auction.web.bean.vo.LotSearchVo;
import edu.dhu.auction.web.bean.vo.LotVo;
import edu.dhu.auction.web.service.AuctionService;
import edu.dhu.auction.web.util.BeanUtils;
import edu.dhu.auction.web.util.JwtUtils;
import edu.dhu.auction.web.util.ResultEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@RestController
public class AuctionController {
    @Resource
    private AuctionService auctionService;

    @GetMapping("/auction/participate")
    public ResultEntity<Object> getParticipate(Long lotId) {
        Set<Account> participates = auctionService.getParticipates(lotId);
        return ResultEntity.ok(BeanUtils.copySet(participates, AccountVo.class));
    }

    @PostMapping("/auction/participate")
    public ResultEntity<Object> participate(@RequestHeader("token") String token, Long lotId) {
        Account auth = JwtUtils.getAccount(token);
        Lot lot = auctionService.participate(auth, lotId);
        return ResultEntity.ok(BeanUtils.copy(lot, LotVo.class));
    }

    @PostMapping("/auction/bid")
    public ResultEntity<Object> bid(@RequestHeader("token") String token, Long lotId, @RequestBody AuctionDetail auctionDetail) {
        Account auth = JwtUtils.getAccount(token);
        List<AuctionDetail> auctionDetails = auctionService.bid(auth, lotId, auctionDetail);
        return ResultEntity.ok(BeanUtils.copyList(auctionDetails, AuctionDetailVo.class));
    }

    @GetMapping("/auction/bid")
    public ResultEntity<Object> getBidInfo(Long lotId) {
        List<AuctionDetail> auctionDetails = auctionService.getAuctionDetails(lotId);
        return ResultEntity.ok(BeanUtils.copyList(auctionDetails, AuctionDetailVo.class));
    }

    @GetMapping("/auction/list")
    public ResultEntity<Object> getJoinAuction(@RequestHeader("token") String token) {
        Account auth = JwtUtils.getAccount(token);
        Set<Lot> joinAuction = auctionService.getJoinAuction(auth);
        return ResultEntity.ok(BeanUtils.copySet(joinAuction, LotSearchVo.class));
    }
}
