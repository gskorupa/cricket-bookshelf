/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookshelf;

import com.gskorupa.cricket.Result;
import java.util.List;

/**
 *
 * @author greg
 */
public class HttpResult implements Result{
    
    private List<BookData> books;
    private int code;
    
    public void setData(Object books){
        this.books=(List)books;
    }
    
    public Object getData(){
        return books;
    }
    
    public void setCode(int code){
        this.code=code;
    }
    
    public int getCode(){
        return code;
    }
    
}
