package edu.dhu.auction.web.service.impl;

import edu.dhu.auction.web.bean.Category;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.repository.CategoryRepository;
import edu.dhu.auction.web.repository.LotRepository;
import edu.dhu.auction.web.service.SearchService;
import edu.dhu.auction.web.util.AssertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private CategoryRepository categoryRepository;
    @Resource
    private LotRepository lotRepository;

    @Override
    public List<Category> addCategory(Category category) {
        categoryRepository.save(category);
        return getCategories();
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Lot> getSearchResult(String type, String value, Integer page, String status, String sort) {
        if ("keyword".equals(type)) {
            return getSearchResultByKeyword(value, page, status, sort);
        } else if ("category".equals(type)) {
            return getSearchResultByCategory(Long.valueOf(value), page, status, sort);
        } else {
            throw new AssertException("查询类型错误:{}", type);
        }
    }

    @Override
    public List<Lot> getSearchResultByCategory(Long categoryId, Integer page, String status, String sort) {
        Pageable pageable = PageRequest.of(page, 8);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new AssertException("该类别不存在:{}", categoryId));
        Specification<Lot> lotSpecification = (lot, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate.getExpressions().add(criteriaBuilder.equal(lot.get("category"), category));
            return getSearchPredicate(status, sort, lot, query, criteriaBuilder, predicate);
        };
        Page<Lot> lots = lotRepository.findAll(lotSpecification, pageable);
        return lots.getContent();
    }

    @Override
    public List<Lot> getSearchResultByKeyword(String keyword, Integer page, String status, String sort) {
        Pageable pageable = PageRequest.of(page, 8);
        Specification<Lot> lotSpecification = (lot, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate.getExpressions().add(criteriaBuilder.like(lot.get("name"), MessageFormat.format("%{0}%", keyword)));
            return getSearchPredicate(status, sort, lot, query, criteriaBuilder, predicate);
        };
        Page<Lot> lots = lotRepository.findAll(lotSpecification, pageable);
        return lots.getContent();
    }

    @Override
    public List<Lot> getWaterfallList(Integer page) {
        return lotRepository.findAllRandom(page);
    }

    private Predicate getSearchPredicate(String status, String sort, Root<Lot> lot, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Predicate predicate) {
        if ("active".equals(status)) {
            predicate.getExpressions().add(criteriaBuilder.greaterThan(lot.get("auctionEndTime"), LocalDateTime.now()));
        } else if ("finish".equals(status)) {
            predicate.getExpressions().add(criteriaBuilder.lessThan(lot.get("auctionEndTime"), LocalDateTime.now()));
        }

        if ("highest".equals(sort)) {
            query.groupBy(lot.get("id")).orderBy(
                    criteriaBuilder.desc(criteriaBuilder.max(lot.get("highestPrice"))),
                    criteriaBuilder.desc(criteriaBuilder.max(lot.get("basePrice")))
            );
        } else if ("lowest".equals(sort)) {
            query.groupBy(lot.get("id")).orderBy(
                    criteriaBuilder.desc(criteriaBuilder.min(lot.get("highestPrice"))),
                    criteriaBuilder.desc(criteriaBuilder.min(lot.get("basePrice")))
            );
        } else if ("most".equals(sort)) {
            query.orderBy(criteriaBuilder.desc(criteriaBuilder.size(lot.get("auctionDetails"))));
        } else if ("least".equals(sort)) {
            query.orderBy(criteriaBuilder.asc(criteriaBuilder.size(lot.get("auctionDetails"))));
        }
        return predicate;
    }
}
