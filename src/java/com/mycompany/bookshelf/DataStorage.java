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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author greg
 */
public class DataStorage {

    public static int OK = 0;

    private static DataStorage instance = null;
    private HashMap<String, BookData> books;

    public DataStorage() {
        books = new HashMap();
    }

    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    public BookData addBook(BookData data) {
        BookData bd = data;
        data.setID(generateId());
        books.put(bd.getID(), bd);
        return bd;
    }

    public BookData getBook(String id) {
        return books.get(id);
    }

    public int modifyBook(BookData data) {
        books.put(data.getID(), data);
        return OK;
    }

    public int removeBook(String id) {
        books.remove(id);
        return OK;
    }

    public List<BookData> search(BookData queryData) {
        ArrayList<BookData> list = new ArrayList();
        return list;
    }

    private synchronized String generateId() {
        long ts = System.currentTimeMillis();
        return "" + ts;
    }

}
