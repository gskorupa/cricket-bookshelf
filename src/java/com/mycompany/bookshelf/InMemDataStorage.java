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
import java.util.Map;

/**
 *
 * @author greg
 */
public class InMemDataStorage {

    public static int OK = 0;

    private static InMemDataStorage instance = null;
    private HashMap<String, BookData> books;

    public InMemDataStorage() {
        books = new HashMap();
    }

    public static InMemDataStorage getInstance() {
        if (instance == null) {
            instance = new InMemDataStorage();
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

    /**
    * Returns a list of books matching example data.
    * <p>
    * This is simple implementation comparing titles only.
    * Should be extended by adding, for instance, regexp.
    * 
    * @param    exampleData an example book data used as the search criteria
    * @return               list of books matching search criteria
    * @see BookData
    */
    public List<BookData> search(BookData exampleData) {
        ArrayList<BookData> list = new ArrayList();
        BookData book;
        boolean match=false;
        for (Map.Entry<String, BookData> entry : books.entrySet()) {
		book=entry.getValue();
                match=true;
                if(null!=exampleData.getTitle()){
                    match = match && book.getTitle().equalsIgnoreCase(exampleData.getTitle());
                }
                if(match){
                    list.add(book);
                }
	}
        return list;
    }
    
    /**
     * Returns a new unique ID based on current system time (milliseconds).
     * 
     * @return  new unique identifier
     */
    private synchronized String generateId() {
        long ts = System.currentTimeMillis();
        return "" + ts;
    }

}
