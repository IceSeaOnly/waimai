package site.binghai.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.store.entity.FruitTakeOut;
import site.binghai.store.service.dao.FruitTakeOutDao;

import java.util.List;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class FruitTakeOutService extends BaseService<FruitTakeOut> {
    @Autowired
    private FruitTakeOutDao dao;

    @Override
    protected JpaRepository<FruitTakeOut, Long> getDao() {
        return dao;
    }

    public List<FruitTakeOut> list(Integer page, Integer pageSize) {
        return dao.findAllByOrderByCreatedDesc(new PageRequest(page,pageSize));
    }
}
