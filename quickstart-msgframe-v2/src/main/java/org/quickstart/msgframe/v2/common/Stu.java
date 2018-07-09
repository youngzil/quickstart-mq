/**
 * 项目名称：quickstart-msgframe 
 * 文件名：Stu.java
 * 版本信息：
 * 日期：2017年2月18日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.msgframe.v2.common;

import java.io.Serializable;

/**
 * Stu
 * 
 * @author：yangzl@asiainfo.com
 * @2017年2月18日 上午10:29:56
 * @version 1.0
 */
public class Stu implements Serializable {

    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
