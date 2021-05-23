package edu.dhu.auction.web.controller;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Comment;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.bean.vo.CommentVo;
import edu.dhu.auction.web.bean.vo.LotVo;
import edu.dhu.auction.web.service.LotService;
import edu.dhu.auction.web.util.BeanUtils;
import edu.dhu.auction.web.util.JwtUtils;
import edu.dhu.auction.web.util.ResultEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class LotController {
    @Resource
    private LotService lotService;

    @PostMapping("/lot/add")
    public ResultEntity<Object> addLot(@RequestHeader("token") String token, @RequestBody Lot newLot) {
        Account auth = JwtUtils.getAccount(token);
        Lot lot = lotService.addLot(auth, newLot);
        return ResultEntity.ok(BeanUtils.copy(lot, LotVo.class));
    }

    @GetMapping("/lot/info")
    public ResultEntity<Object> getLotInfo(Long lotId) {
        Lot lot = lotService.getLot(lotId);
        return ResultEntity.ok(BeanUtils.copy(lot, LotVo.class));
    }

    @PostMapping("/lot/comment")
    public ResultEntity<Object> comment(@RequestHeader("token") String token, Long lotId, @RequestBody Comment comment) {
        Account auth = JwtUtils.getAccount(token);
        List<Comment> comments = lotService.addComment(auth, lotId, comment);
        return ResultEntity.ok(BeanUtils.copyList(comments, CommentVo.class));
    }

    @GetMapping("/lot/comment")
    public ResultEntity<Object> getComment(Long lotId) {
        List<Comment> comments = lotService.getComments(lotId);
        return ResultEntity.ok(BeanUtils.copyList(comments, CommentVo.class));
    }

    @PostMapping("/lot/category")
    public ResultEntity<Object> addLotCategory(Long lotId, String category) {
        Lot lot = lotService.addLotCategory(lotId, category);
        return ResultEntity.ok(BeanUtils.copy(lot, LotVo.class));
    }
}
