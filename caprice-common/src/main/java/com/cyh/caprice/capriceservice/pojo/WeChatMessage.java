package com.cyh.caprice.capriceservice.pojo;

import lombok.Data;

/**
 * @Author: cuiyuhao
 * @Description: 订单实体类
 * @Data: Created in 2021/10/8-16:49
 **/
@Data
public class WeChatMessage {
    //用户openId
    private String touser;
    //项目名称
    private String projectName;
    //标题
    private String title;
    //备注
    private String remark;
    //跳转链接
    private String url;
    //回调函数
    private String callbackUrl;
    //订单Id
    private String orderId;
    //模板Id
    private String templateId;
    //数组(包含 类型、姓名、时间等)
    private String[] fillData;

}
