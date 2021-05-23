package edu.dhu.auction.web.controller;

import edu.dhu.auction.web.bean.Category;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.bean.vo.CategoryVo;
import edu.dhu.auction.web.bean.vo.LotSearchVo;
import edu.dhu.auction.web.service.SearchService;
import edu.dhu.auction.web.util.BeanUtils;
import edu.dhu.auction.web.util.ResultEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SearchController {
    @Resource
    private SearchService searchService;

    @PostMapping("/category/add")
    public ResultEntity<Object> addCategory(@RequestBody Category category) {
        List<Category> categories = searchService.addCategory(category);
        return ResultEntity.ok(BeanUtils.copyList(categories, CategoryVo.class));
    }

    @GetMapping("/category/list")
    public ResultEntity<Object> getCategories() {
        List<Category> categories = searchService.getCategories();
        return ResultEntity.ok(BeanUtils.copyList(categories, CategoryVo.class));
    }

    @GetMapping("lot/search")
    public ResultEntity<Object> getSearchResult(@RequestParam(defaultValue = "keyword") String type,
                                                @RequestParam(defaultValue = "") String value,
                                                @RequestParam(defaultValue = "0") Integer page,
                                                @RequestParam(defaultValue = "default") String status,
                                                @RequestParam(defaultValue = "") String sort) {
        List<Lot> lots = searchService.getSearchResult(type, value, page, status, sort);
        return ResultEntity.ok(BeanUtils.copyList(lots, LotSearchVo.class));
    }

    @GetMapping("/home/waterfall")
    public ResultEntity<Object> getHomePageWaterfall(@RequestParam(defaultValue = "0") Integer page) {
        List<Lot> lots = searchService.getWaterfallList(page);
        return ResultEntity.ok(BeanUtils.copyList(lots, LotSearchVo.class));
    }
}
