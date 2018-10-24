package com.jk51.uitls;

import org.junit.Test;

/**
 * Created by Administrator on 2018/5/10.
 */
public class TestA {

    @Test
    public void test1(){
        Boolean flag = false;
        test2(flag);
        System.out.println(flag);
    }
    void test2(Boolean flag){
        flag = true;
    }





    @Test
    public void test3(){
        Integer a = 1;
        test4(a);
        System.out.println(a);
    }
    void test4(Integer a){
        a++;
    }


    @Test
    public void test5(){
        aaa a = new aaa();
        test6(a);
        System.out.println(a.a);
    }
    void test6(aaa a){
        a.a = 2;
    }
}
class aaa{
    Integer a = 1;
}
