package com.atguigu.core.exception;

/**
 * @author eternity
 * @create 2019-11-12 19:31
 */
public class GmallException extends RuntimeException{

    public  GmallException(){

    }

    public  GmallException(String message){
        super(message);
    }
}
