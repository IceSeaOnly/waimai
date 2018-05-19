package site.binghai.store.service;

import org.springframework.stereotype.Service;
import site.binghai.store.entity.RefereeRecord;

/**
 * Created by IceSea on 2018/5/19.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class RefereeRecordService extends BaseService<RefereeRecord> {
    public RefereeRecord findByUserId(Long userId) {
        RefereeRecord record = new RefereeRecord();
        record.setUserId(userId);
        record.setPaid(Boolean.FALSE);
        return queryOne(record);
    }
}
