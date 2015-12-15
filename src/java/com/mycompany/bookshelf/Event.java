/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookshelf;

/**
 *
 * @author Grzegorz Skorupa <g.skorupa at gmail.com>
 */
public class Event {
    
    public static final String BOOK_NEW = "NEW";
    public static final String BOOK_MODIFY = "MODIFY";
    public static final String NEW_DEL = "DELETE";
    public static final String NEW_SEARCH = "SEARCH";
    
    private String type;
    private long timestamp;
    private String origin;
    private Object data;
    
    public Event(String type, String origin, long timestamp, Object data){
        this.type=type;
        this.origin=origin;
        this.timestamp=timestamp;
        this.data=data;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }
    
}
