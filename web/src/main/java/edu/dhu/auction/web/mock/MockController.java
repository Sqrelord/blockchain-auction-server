package edu.dhu.auction.web.mock;

import edu.dhu.auction.web.bean.Category;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.util.ResultEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class MockController {
    @Resource
    private MockService mockService;

    @PostMapping("/mock/category")
    public ResultEntity<Object> mockCategory(@RequestBody List<Category> categories) {
        List<Category> categoryList = mockService.addCategory(categories);
        return ResultEntity.ok(categoryList);
    }

    @PostMapping("/mock/lot")
    public ResultEntity<Object> mockLot(@RequestBody List<Lot> lots) {
        mockService.addLot(lots);
        return ResultEntity.ok();
    }
}
