package com.above.config.shiro;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.authc.UsernamePasswordToken;

import java.io.Serializable;
/**
 *@Decription: 用户手机登录
 *@params:
 *@return:
 *@Author:hxj
 *@Date:2022/1/10 11:48
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPhoneToken extends UsernamePasswordToken implements Serializable {

    private static final long serialVersionUID = 6293390033867929958L;

    /**
     * 返回的状态
     */
    private static final transient String STATUS = "ok";

    /**
     *  手机号码
     */
    private String phone;

    /**
     * 获取存入的值
     * @return 手机号或者用户名
     */
    @Override
    public Object getPrincipal(){
        if(phone == null){
            return getUsername();
        }else{
            return phone;
        }
    }

    @Override
    public Object getCredentials(){
        if(phone == null){
            return getPassword();
        }else{
            return UserPhoneToken.STATUS;
        }
    }

    public UserPhoneToken(String phoneNum) {
        this.phone = phoneNum;
    }

    public UserPhoneToken(final String userName, final String password) {
        super(userName, password);
    }

    @Override
    public String toString() {
        return "PhoneToken [PhoneNum=" + phone + "]";
    }

}
