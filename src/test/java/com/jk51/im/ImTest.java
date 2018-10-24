package com.jk51.im;



import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.communal.im.RLTXManager;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.im.service.iMRecode.response.Evaluatr;
import com.jk51.modules.im.util.RLMessageContent;
import com.jk51.modules.pandian.error.ParseAccessTokenException;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImTest  {

    private static ExecutorService executorService = Executors.newCachedThreadPool();
    @Test
    public void test(){



        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    static class SynchronizedObject{
         public void printString(){
            print("begin");
            if(Thread.currentThread().getName().equals("a")){
                print("a 线程永远suspend了！");
                Thread.currentThread().suspend();
            }
            print("end");
        }
    }

    static class HasSelfPrivateNum{

        int num = 0;
        public synchronized void addI(String username){
            try{

                if(username.equals("a")){
                    num = 100;
                    print("a set over");
                    Thread.sleep(2000);
                }else{
                    num = 200;
                    print("b set over");
                }

                print(username+" num= "+num);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    static class MyThreadA extends Thread{

        private HasSelfPrivateNum numRef;

        public MyThreadA(HasSelfPrivateNum numRef){
            this.numRef = numRef;
        }

        @Override
        public void run(){
            numRef.addI("a");
        }
    }
    static class MyThreadB extends Thread{

        private HasSelfPrivateNum numRef;

        public MyThreadB(HasSelfPrivateNum numRef){
            this.numRef = numRef;
        }

        @Override
        public void run(){
            numRef.addI("b");
        }
    }
    public static void main(String[] args){
        try{
           /* Task publicVar = new Task();
            ThreadA threadA =new ThreadA(publicVar);
            threadA.setName("a");
            threadA.start();
            ThreadB threadB = new ThreadB(publicVar);
            threadB.setName("b");
            threadB.start();*/
           String a = "a";
           String b = "b";
           System.out.println(a == b);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static class Task{
        public void doLongTimeTask(){
            for(int i=0;i<100;i++){
                System.out.println("nosynchronized threadName= "+Thread.currentThread().getName()+" i="+(i+1));
            }
            System.out.println();
            synchronized (this){
                for(int i=0;i<100;i++){
                    System.out.println("synchronized threadName= "+Thread.currentThread().getName()+" i="+(i+1));
                }
            }
        }
    }

    static class Service{
        synchronized void testMethod(){
            if(Thread.currentThread().getName().equals("a")){

                while (true){
                    String random = (Math.random()+"").substring(0,8);
                    if(random.equals("0.123456")){
                        throw new RuntimeException("抛出异常");
                    }
                }
            }else{
                print("thread name = "+Thread.currentThread().getName());
            }
        }
    }

    static class ObjectService{
        public void serviceMethod(){
            try{
                synchronized (this){
                    print("begin time= "+System.currentTimeMillis());
                    Thread.sleep(2000);
                    print("end time= "+System.currentTimeMillis());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class PublicVar{

        String username = "A";
        String password = "AA";

        synchronized public void setValue(String username,String password){
            this.username = username;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.password = password;
            print("set valeu thread = "+Thread.currentThread().getName()+" username = "+username+" password= "+password);

        }

        synchronized public void getValue(){
            print("set valeu thread = "+Thread.currentThread().getName()+" username = "+username+" password= "+password);
        }
    }

    static class  ThreadA extends Thread{

        Task publicVar;
        public ThreadA(Task publicVar){
            this.publicVar = publicVar;
        }

        @Override
        public void run(){
            publicVar.doLongTimeTask();
        }
    }

    static class  ThreadB extends Thread{

        Task publicVar;
        public ThreadB(Task publicVar){
            this.publicVar = publicVar;
        }

        @Override
        public void run(){
            publicVar.doLongTimeTask();
        }
    }


    public static void print(String str){
        System.out.println(str);
    }



    @Test
    public void test11(){
       /* AuthToken aa =   parseAccessToken("eyJ1c2VyX2lkIjo2MDQ1NCwicGhhcm1hY2lzdF9pZCI6Mzc1ODEsInN0b3JlX2lkIjoxMTY4LCJzdG9yZV91c2VyX2lkIjozNzM1LCJzdG9yZV9hZG1pbl9pZCI6MTAxMjkzLCJwaG9uZSI6IjE4NzU1NTUxMTExIiwic2l0ZV9pZCI6MTAwMTkwfQ==");
        aa.getStoreAdminId();*/

       String aa = "11111111111111";
       Integer bb = Integer.parseInt(aa);

    }
    //解析Token
    public AuthToken parseAccessToken(String accessToken){


        String token = EncryptUtils.base64DecodeToString(accessToken.getBytes());
        AuthToken authToken = null;
        try {
            authToken = JacksonUtils.json2pojo(token,AuthToken.class);

        } catch (Exception e) {

            throw new ParseAccessTokenException("解析AccessToken失败");
        }

        return authToken;
    }


}
