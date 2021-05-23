package edu.dhu.auction.web.service;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.Comment;
import edu.dhu.auction.web.bean.Lot;

import java.util.List;

public interface LotService {
    Lot addLot(Account account, Lot newLot);

    Lot getLot(Long lotId);

    List<Comment> addComment(Account account, Long lotId, Comment comment);

    List<Comment> getComments(Long lotId);

    Lot addLotCategory(Long lotId, String categoryName);
}
