package site.binghai.store.service;

import org.springframework.stereotype.Service;
import site.binghai.store.entity.BookPeriod;
import site.binghai.store.enums.BookingTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class BookPeriodService extends BaseService<BookPeriod> {

    public List<BookPeriod> getPeriods(Long regionId, BookingTypeEnum typeEnum) {
        return findAll(9999).stream()
                .filter(v -> v.getRegionId().equals(regionId) && v.getBookingType().equals(typeEnum.getCode()))
                .collect(Collectors.toList());

    }
}
