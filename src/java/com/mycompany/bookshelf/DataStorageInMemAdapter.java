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

import java.util.HashMap;
import org.cricketmsf.Adapter;
import org.cricketmsf.Event;
import org.cricketmsf.out.OutboundAdapter;
import java.util.List;
import java.util.logging.Logger;
import org.cricketmsf.Kernel;

/**
 *
 * @author greg
 */
public class DataStorageInMemAdapter extends OutboundAdapter implements DataStorageAdapterIface, Adapter {

    private static final Logger logger = Logger.getLogger(com.mycompany.bookshelf.DataStorageInMemAdapter.class.getName());

    private boolean showCounter = false;

    /**
     * Read properties with names started with "DataStorageAdapterIface-"
     */
    public void loadProperties(HashMap<String,String> properties) {
        String metricProp = properties.get("counter");
        showCounter = (metricProp != null && metricProp.equalsIgnoreCase("true"));
        System.out.println("counter=" + showCounter);
    }

    public BookData addBook(BookData data) {
        Kernel.getInstance().handleEvent(
            new Event(this.getClass().getSimpleName(), 
                    Event.CATEGORY_LOG, 
                    Event.LOG_INFO,
                    "",
                    "BOOK_NEW")
        );
        return InMemDataStorage.getInstance().addBook(data);
    }

    public BookData getBook(String id) {
        Kernel.getInstance().handleEvent(
            new Event(this.getClass().getSimpleName(), 
                    Event.CATEGORY_LOG, 
                    Event.LOG_INFO,
                    "",
                    "BOOK_SEARCH")
        );
        return InMemDataStorage.getInstance().getBook(id);
    }

    public int modifyBook(BookData data) {
        return InMemDataStorage.getInstance().modifyBook(data);
    }

    public int removeBook(String id) {
        return InMemDataStorage.getInstance().removeBook(id);
    }

    public List<BookData> search(BookData queryData) {
        List<BookData> result = InMemDataStorage.getInstance().search(queryData);
        if (showCounter) {
            System.out.println("found " + result + " books");
        }
        return result;
    }

}
