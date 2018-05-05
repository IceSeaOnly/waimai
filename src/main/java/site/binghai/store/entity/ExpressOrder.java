package site.binghai.store.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 寄快递订单
 * Created by IceSea on 2018/4/27.
 * GitHub: https://github.com/IceSeaOnly
 */
@Entity
@Data
public class ExpressOrder extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String userName;
    private Long userId;
    private Long unifiedId; // 统一订单号
    private Boolean hasPay; // 是否已经支付
    private Boolean canceled; // 是否已取消
    private Boolean priceConfirmed; // 管理员设置了价格
    private Integer type; // 0 寄件,1 取件

    /**
     * 如果是取件，则from是收件人，to是配送收件人
     * 如果是寄件，则from是寄件人，to是收件人
     * */
    @Column(name = "_from")
    private String from;
    @Column(name = "_fromPhone")
    private String fromPhone;
    @Column(name = "_to")
    private String to;
    @Column(name = "_toPhone")
    private String toPhone;
    @Column(name = "_toWhere")
    private String toWhere; //寄件时有效
    private String whatIs; // 里面是什么

    @Column(columnDefinition = "TEXT")
    private String sms; // 取件时使用的短信内容
    private String bookPeriod;

    private String exName; // 快递名
    private String exNo; // 快递单号

}
