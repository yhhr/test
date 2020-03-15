package com.huamai.test;

public class Test1 {


    public static void main(String[] args) {
        Boolean b= new Boolean(false);
        new Test1().test(b);
        System.out.println(b);
    }
    public void test(boolean a){
        a = true;
    }
}
