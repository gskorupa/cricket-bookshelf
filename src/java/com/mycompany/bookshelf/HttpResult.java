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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 *
 * @author greg
 */
public class HttpResult implements Result {

    private List<BookData> books;
    private int code;
    private String message;

    public void setData(Object books) {
        this.books = (List) books;
    }

    public Object getData() {
        return books;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
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

    public String toJsonString() {

        JSONObject bookObj;
        JSONArray jsa = new JSONArray();
        for (BookData book : books) {
            bookObj = new JSONObject();
            bookObj.put("uid", book.getID());
            bookObj.put("author", book.getAuthor());
            bookObj.put("title", book.getTitle());
            bookObj.put("publisher", book.getPublisher());
            bookObj.put("publish-date", book.getPublishDate());
            jsa.put(bookObj);
        }
        JSONObject obj = new JSONObject();
        obj.put("code", getCode());
        obj.put("message", getMessage());
        obj.put("books", jsa);
        return obj.toString() + "\n";
    }

    public String toXmlString() {
        return null;
    }

    public String toCsvString() {
        return null;
    }

}
