package edu.dhu.auction.web.repository;

import edu.dhu.auction.web.bean.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Resource;
import java.util.List;

@Resource
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByLot_Id(Long id);
}
