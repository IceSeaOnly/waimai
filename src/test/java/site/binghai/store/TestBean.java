package site.binghai.store;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import site.binghai.store.entity.TradeItem;
import site.binghai.store.tools.HttpUtils;
import site.binghai.store.tools.IoUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by IceSea on 2018/4/8.
 * GitHub: https://github.com/IceSeaOnly
 */
public class TestBean {
    @Test
    public void beanTest() throws Exception {
        Map map = new HashMap();
        map.put("id",1);
        map.put("categoryId",11);
        map.put("price",15);
        map.put("saleCount",16);
        map.put("name","ThisIsName");
        map.put("detail","ThisIsDetail");
        map.put("img","ThisIsImg");

        JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(map));
        TradeItem tradeItem = obj.toJavaObject(TradeItem.class);

        map.put("saleCount",17);

        JSONObject item = JSONObject.parseObject(JSONObject.toJSONString(tradeItem));
        item.putAll(map);
        TradeItem newOne = item.toJavaObject(TradeItem.class);
    }

    @Test
    public void drive(){
        Long start  = 370218040482525L;
        Long end = 370218040482589L;
        String[] id = {"370718040884162","370718040884165","370718040884166","370218040482525","370218040482526","370218040482527","370218040482528","370218040482586","370218040482587","370218040482588","370218040482589"};
        StringBuilder sb = new StringBuilder();
        for (String s:id) {
            for (int i = 1; i < 20; i++) {
                JSONObject rs;
                try {
                     rs = HttpUtils.sendJSONGet("http://sd.122.gov.cn/m/examplan/getStudentInfo?page="+i+"&xh="+s,null);
                }catch (Exception e){
                    continue;
                }
                if(rs.getInteger("code") == 200 && rs.get("data") != null){
                    JSONArray arr = rs.getJSONObject("data").getJSONArray("content");
                    for (int j = 0; j < arr.size(); j++) {
                        JSONObject o = arr.getJSONObject(j);
                        System.out.println(String.format("%5s %18s %s ~ %s @ %s -> %s",
                                o.getString("xm"),
                                o.getString("sfzmhm"),
                                o.getString("pxsj"),
                                o.getString("zt").equals("1")?"成功":"失败",
                                s,
                                i));
                    }
                }
            }

        }

//        IoUtils.WriteCH("km2.txt",sb.toString());
    }

    @Test
    public void name() throws Exception {
        System.out.println(HttpUtils.sendGet("http://sd.122.gov.cn/m/examplan/getStudentInfo?page=0&xh=370218040482586",null));
    }

    @Test
    public void uuid() throws Exception {
        System.out.println(UUID.randomUUID().toString().length());
        System.out.println(UUID.randomUUID().toString().substring(9).length());
    }

    @Test
    public void citySql(){
        try {
            Scanner scanner = new Scanner(new FileInputStream("FeHelper-20180502222058.json"));
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()){
                sb.append(scanner.nextLine().trim());
            }
            scanner.close();
            JSONArray array = JSONObject.parseObject(sb.toString()).getJSONArray("provinces");
            for (int i = 0; i < array.size(); i++) {
                String provinceName = array.getJSONObject(i).getString("provinceName");
                JSONArray citys = array.getJSONObject(i).getJSONArray("citys");
                for (int j = 0; j < citys.size(); j++) {
                    System.out.println(String.format("insert into city(province,city) values('%s','%s');",provinceName,citys.getJSONObject(j).getString("citysName")));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
