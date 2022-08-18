package com.cyh.caprice.capriceservice.pojo;

import lombok.Data;

/**
 * @Author: cuiyuhao
 * @Description:
 * @Data: Created in 15:03 2021/10/9
 **/
@Data
public class SendWechatLog {
    private int id;//主键
    private String projectName;//项目名称
    private String openId;//用户openid
    private String requestTime;//发送时间
    private String requestState;//接收状态
    private String requestInformation;//接受信息
    private String title;//标题
    private String orderId;//订单id
    private String callbackUrl;//标题
}
