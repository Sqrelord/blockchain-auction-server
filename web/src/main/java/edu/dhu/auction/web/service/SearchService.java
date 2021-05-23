package edu.dhu.auction.web.service;

import edu.dhu.auction.web.bean.Category;
import edu.dhu.auction.web.bean.Lot;

import java.util.List;

public interface SearchService {
    List<Category> addCategory(Category category);

    List<Category> getCategories();

    List<Lot> getSearchResult(String type, String value, Integer page, String status, String sort);

    List<Lot> getSearchResultByKeyword(String keyword, Integer page, String status, String sort);

    List<Lot> getSearchResultByCategory(Long categoryId, Integer page, String status, String sort);

    List<Lot> getWaterfallList(Integer page);
}
