/*
 * Copyright 2015 Grzegorz Skorupa <g.skorupa at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycompany.bookshelf;

import com.gskorupa.cricket.in.Result;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 *
 * @author greg
 */
public class HttpResult implements Result{
    
    private List<BookData> books;
    private int code;
    private String message;
    
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

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String toJsonString(){
        String jst=
                new JSONStringer()
                        .object()
                            .key("code")
                            .value(getCode())
                            .key("message")
                            .value(getMessage())
                            //.key("data")
                            //.value(new JSONObject(data))
                        .endObject()
                        .toString()
                +"\n";
        return jst;
    }

    public String toXmlString() {
        return null;
    }

    public String toCsvString() {
        return null;
    }
    
}
