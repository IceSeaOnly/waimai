package site.binghai.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.store.entity.BookPeriod;
import site.binghai.store.enums.BookingTypeEnum;
import site.binghai.store.service.BookPeriodService;

import java.util.Map;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@RestController
@RequestMapping("/admin/book/")
public class BookPeriodController extends BaseController {
    @Autowired
    private BookPeriodService bookPeriodService;

    @RequestMapping("list")
    public Object list(@RequestParam Integer type) {
        return success(bookPeriodService.getPeriods(BookingTypeEnum.valueOf(type)), "SUCCESS");
    }

    @PostMapping("add")
    public Object add(@RequestBody Map map) {
        BookPeriod bookPeriod = bookPeriodService.newInstance(map);
        bookPeriodService.save(bookPeriod);
        return success();
    }

    @GetMapping("delete")
    public Object delete(@RequestParam Long id) {
        bookPeriodService.delete(id);
        return success();
    }


}
