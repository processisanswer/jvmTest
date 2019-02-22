package com.jvm;

import java.io.File;

/**
 * Created by zheng gongming on 2019/2/22.
 * 数据、指令、控制
 */
public class HelloWorld {
    private final  int i = 0;
    private static int k=0;
    //成员变量
    private Object obj=new Object();
    private int sss=0;

    public void methodOne(int i){
        int j=0;
        int sum=i+j;
        Object abc=obj;
        long start=System.currentTimeMillis();
        methodTwo();
        return;
    }
    public void methodTwo(){
        File file=new File("");
    }
    public static void methodThree(){
        methodThree();
    }

    public static void main(String[] args) {
        methodThree();
    }

    @Override
    protected void finalize() throws Throwable{
        super.finalize();
    }
}
