package com.above.vo.user;

import lombok.Data;

/**
 * @Description: 微信登录参数
 * @Author: LZH
 * @Date: 2022/2/22 15:40
 */
@Data
public class WeChatVo {

    private String code ;
    private String iv ;
    private String encryptedData ;

}
