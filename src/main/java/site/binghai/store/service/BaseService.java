package site.binghai.store.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import site.binghai.store.entity.Admin;
import site.binghai.store.entity.BaseEntity;
import site.binghai.store.entity.TradeItem;
import site.binghai.store.tools.BaseBean;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IceSea on 2018/4/4.
 * GitHub: https://github.com/IceSeaOnly
 */
public abstract class BaseService<T extends BaseEntity> extends BaseBean {
    @Autowired
    private EntityManager entityManager;
    private SimpleJpaRepository<T, Long> daoHolder;

    public T newInstance(Map map) {
        JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(map));
        return obj.toJavaObject(getTypeArguement());
    }

    protected JpaRepository<T, Long> getDao() {
        if (daoHolder != null) {
            return daoHolder;
        }
        daoHolder = new SimpleJpaRepository(getTypeArguement(), entityManager);
        return daoHolder;
    }

    /**
     * 获取T的实际类型
     * */
    protected Class<T> getTypeArguement() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

    @Transactional
    public T save(T t) {
        return getDao().save(t);
    }

    @Transactional
    public T update(T t) {
        if (t.getId() >= 0) {
            return save(t);
        }
        return t;
    }

    public T findById(Long id) {
        return getDao().findById(id).orElseGet(null);
    }

    @Transactional
    public void delete(Long id) {
        getDao().deleteById(id);
    }

    @Transactional
    public T deleteIfExist(Long id) {
        T t = findById(id);
        if (t == null) return null;
        delete(id);
        return t;
    }

    @Transactional
    public boolean deleteAll(String confirm) {
        if (confirm.equals("confirm")) {
            getDao().deleteAll();
            return true;
        }
        return false;
    }

    public List<T> findByIds(List<Long> ids) {
        return getDao().findAllById(ids);
    }

    public List<T> findAll(int limit) {
        return getDao().findAll(new PageRequest(0, limit)).getContent();
    }

    public List<T> findAll(int page, int pageSize) {
        return getDao().findAll(new PageRequest(page, pageSize)).getContent();
    }

    public long count() {
        return getDao().count();
    }

    @Transactional
    public void batchSave(List<T> batch) {
        getDao().saveAll(batch);
    }

    /**
     * 使用map更新entity中除id外的其他字段
     */
    public T updateParams(T t, Map map) {
        Long id = t.getId();
        JSONObject item = JSONObject.parseObject(JSONObject.toJSONString(t));
        item.putAll(map);
        item.put("id", id);
        return item.toJavaObject(getTypeArguement());
    }

    @Transactional
    public T updateAndSave(Admin admin, Map map) throws Exception {
        Long id = getLong(map, "id");
        if (id == null) {
            throw new Exception("id must be present!");
        }
        T old = findById(id);
        if (old == null) {
            throw new Exception("item not exist!");
        }
        T new_ = updateParams(old, map);
        logger.warn("{} update {} from {} to {}", admin, getTypeArguement().getSimpleName(), old, new_);
        return update(new_);
    }
}

