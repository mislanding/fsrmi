package com.brul.fsrmi.server.impl;


import java.io.ObjectOutputStream;

/**
 * @author LYY
 * @date 2020年9月29日
 * 注册中心(单例)
 */
public class Register{
    public static Register register = null;
    public static Register getInstance(){
        if(register==null){
            register = new Register();
        }
        return register;
    }

}
