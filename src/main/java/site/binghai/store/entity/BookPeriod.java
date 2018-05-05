package site.binghai.store.entity;

import lombok.Data;
import site.binghai.store.enums.BookingTypeEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 * 预约时间段
 */
@Entity
@Data
public class BookPeriod extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long regionId;
    /**
     * {@link  BookingTypeEnum }
     */
    private Integer bookingType;
}
