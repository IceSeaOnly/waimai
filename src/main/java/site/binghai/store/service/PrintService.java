package site.binghai.store.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import site.binghai.store.entity.PrintData;
import site.binghai.store.entity.RegionConfig;
import site.binghai.store.tools.BaseBean;

/**
 * Created by IceSea on 2018/5/15.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class PrintService extends BaseBean {

    public static final String URL = "http://api.feieyun.cn/Api/Open/";//不需要修改

    public String print(PrintData printData, RegionConfig config) {

        //通过POST请求，发送打印信息到服务器
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(30000)//读取超时
                .setConnectTimeout(30000)//连接超时
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpPost post = new HttpPost(URL);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("user", config.getPrinterUser()));
        String STIME = String.valueOf(System.currentTimeMillis() / 1000);
        nvps.add(new BasicNameValuePair("stime", STIME));
        nvps.add(new BasicNameValuePair("sig", signature(config.getPrinterUser(), config.getPrinterKey(), STIME)));
        nvps.add(new BasicNameValuePair("apiname", "Open_printMsg"));//固定值,不需要修改
        nvps.add(new BasicNameValuePair("sn", config.getPrinterSN()));
        nvps.add(new BasicNameValuePair("content", printData.toString()));
        nvps.add(new BasicNameValuePair("times", "1"));

        CloseableHttpResponse response = null;
        String result = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            response = httpClient.execute(post);
            int statecode = response.getStatusLine().getStatusCode();
            if (statecode == 200) {
                HttpEntity httpentity = response.getEntity();
                if (httpentity != null) {
                    result = EntityUtils.toString(httpentity);
                    logger.info("Print Log: config:{},data:{} => {}", config, printData, result);
                }
            }
        } catch (Exception e) {
            logger.info("PrintError: config:{},data:{}", config, printData, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                post.abort();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    //生成签名字符串
    private static String signature(String USER, String UKEY, String STIME) {
        String s = DigestUtils.sha1Hex(USER + UKEY + STIME);
        return s;
    }
}
