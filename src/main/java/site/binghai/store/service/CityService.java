package site.binghai.store.service;

import org.springframework.stereotype.Service;
import site.binghai.store.entity.City;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by IceSea on 2018/5/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class CityService extends BaseService<City> {

    public List<City> getByProvince(String province){
        return findAll(9999).stream()
                .filter(v->v.getProvince().equals(province))
                .collect(Collectors.toList());
    }

    /**
     * 每个省份各一条
     * */
    public List<City> getAllProvince(){
        Set<String> maps = new HashSet<>();
        return findAll(9999).stream()
                .filter(v->{
                    if(maps.contains(v.getProvince())){
                        return false;
                    }
                    maps.add(v.getProvince());
                    return true;
                }).collect(Collectors.toList());
    }
}
