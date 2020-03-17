package com.deepexi.sdk;


import com.deepexi.sdk.core.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author HuangTao
 * @version 1.0
 * @date 2020-02-14 23:00
 */

@ApiModel
public class User implements Serializable {
    @ApiModelProperty(value = "用户id")
    private Integer id;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "用户地址")
    private String address;
    @ApiModelProperty(value = "日期" ,dataType = "date")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    //getter/setter


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static void main(String[] args) {
        JSON json = new JSON();
        String tt = "{\"id\":123,\"username\":\"tewt\",\"address\":\"ererer\",\"date\":'2010-03-10 1:39:35'}";
        User user = json.deserialize(tt,User.class);
        String s = json.serialize(null);
        System.out.println(s);
    }
}
