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
import org.cricketmsf.Event;
import org.cricketmsf.EventHook;
import org.cricketmsf.HttpAdapterHook;
import org.cricketmsf.Kernel;
import org.cricketmsf.RequestObject;
import org.cricketmsf.in.http.EchoHttpAdapterIface;
import org.cricketmsf.in.http.HtmlGenAdapterIface;
import org.cricketmsf.in.http.HttpAdapter;
import org.cricketmsf.in.scheduler.SchedulerIface;
import org.cricketmsf.out.db.KeyValueCacheAdapterIface;
import org.cricketmsf.out.html.HtmlReaderAdapterIface;
import org.cricketmsf.out.log.LoggerAdapterIface;

/**
 * SimpleService
 *
 * @author greg
 */
public class BookshelfService extends Kernel {

    //adapters
    LoggerAdapterIface logAdapter = null;
    EchoHttpAdapterIface httpAdapter = null;
    KeyValueCacheAdapterIface cache = null;
    SchedulerIface scheduler = null;
    HtmlGenAdapterIface htmlAdapter = null;
    HtmlReaderAdapterIface htmlReaderAdapter = null;
    //
    DataStorageAdapterIface storageAdapter = null;
    EventQueueAdapterIface eventsAdapter = null;
    BookshelfHttpAdapterIface bookshelfHttpAdapter = null;
    

    @Override
    public void getAdapters() {
        logAdapter = (LoggerAdapterIface) getRegistered("LoggerAdapterIface");
        httpAdapter = (EchoHttpAdapterIface) getRegistered("EchoHttpAdapterIface");
        cache = (KeyValueCacheAdapterIface) getRegistered("KeyValueCacheAdapterIface");
        scheduler = (SchedulerIface) getRegistered("SchedulerIface");
        htmlAdapter = (HtmlGenAdapterIface) getRegistered("HtmlGenAdapterIface");
        htmlReaderAdapter = (HtmlReaderAdapterIface) getRegistered("HtmlReaderAdapterIface");
        //
        storageAdapter = (DataStorageAdapterIface) getRegistered("DataStorageAdapterIface");
        eventsAdapter = (EventQueueAdapterIface) getRegistered("EventQueueAdapterIface");
        bookshelfHttpAdapter = (BookshelfHttpAdapterIface) getRegistered("BookshelfHttpAdapterIface");
    }

    @Override
    public void runOnce() {
        super.runOnce();
        System.out.println("Hi! I'm " + this.getClass().getSimpleName());
    }

    @EventHook(eventCategory = "LOG")
    public void logEvent(org.cricketmsf.Event event) {
        logAdapter.log(event);
    }

    @EventHook(eventCategory = "*")
    public void processEvent(org.cricketmsf.Event event) {
        eventsAdapter.push(event);
    }

    @HttpAdapterHook(handlerClassName = "BookshelfHttpAdapterIface", requestMethod = "GET")
    public Object getBooks(Event requestEvent) {
        String uid = ((RequestObject)requestEvent.getPayload()).pathExt;
        if (uid.isEmpty()) {
            BookData book = new BookData();
            //TODO: parameters
            return search(book);
        } else {
            return get(uid);

        }
    }

    private HttpResult get(String uid) {
        HttpResult result = new HttpResult();
        ArrayList books = new ArrayList();
        BookData book = storageAdapter.getBook(uid);
        if (book != null) {
            books.add(book);
            result.setCode(HttpAdapter.SC_OK);
        } else {
            result.setCode(HttpAdapter.SC_NOT_FOUND);
        }
        result.setData(books);
        return result;
    }

    private HttpResult search(BookData book) {
        HttpResult result = new HttpResult();
        ArrayList books = new ArrayList(storageAdapter.search(book));
        result.setCode(HttpAdapter.SC_OK);
        result.setData(books);
        return result;
    }

    @HttpAdapterHook(handlerClassName = "BookshelfHttpAdapterIface", requestMethod = "POST")
    public Object addBook(Event requestEvent) {
        HttpResult result = new HttpResult();
        RequestObject request = (RequestObject)requestEvent.getPayload();
        //create data object based on request parmeters
        BookData book = new BookData();
        book.setAuthor((String) request.parameters.get("author"));
        book.setTitle((String) request.parameters.get("title"));
        book.setPublisher((String) request.parameters.get("publisher"));
        book.setPublishDate((String) request.parameters.get("publish-date"));

        //store data object and add registered book data to the result
        //TODO: storage error?
        ArrayList books = new ArrayList();
        books.add(storageAdapter.addBook(book));
        result.setData(books);

        //set result code which will be sent in http response
        result.setCode(HttpAdapter.SC_CREATED);

        return result;
    }

    @HttpAdapterHook(handlerClassName = "BookshelfHttpAdapterIface", requestMethod = "PUT")
    public Object modifyBook(Event requestEvent) {
        HttpResult result = new HttpResult();
        RequestObject request = (RequestObject)requestEvent.getPayload();
        //get UID
        String uid = request.pathExt;

        //create data object based on request parmeters
        BookData book = new BookData();
        book.setID(uid);
        book.setAuthor((String) request.parameters.get("author"));
        book.setTitle((String) request.parameters.get("title"));
        book.setPublisher((String) request.parameters.get("publisher"));
        book.setPublishDate((String) request.parameters.get("publish-date"));

        //store data object and add registered book data to the result
        ArrayList books = new ArrayList();
        int success = storageAdapter.modifyBook(book);
        if (success == DataStorageAdapterIface.OK) {
            books.add(storageAdapter.getBook(uid));
            result.setData(books);
            result.setCode(HttpAdapter.SC_OK);
        } else {
            result.setData(books);
            result.setCode(HttpAdapter.SC_NOT_FOUND);
        }

        return result;
    }

    @HttpAdapterHook(handlerClassName = "BookshelfHttpAdapterIface", requestMethod = "DELETE")
    public Object removeBook(Event requestEvent) {
        HttpResult result = new HttpResult();
        RequestObject request = (RequestObject)requestEvent.getPayload();
        String uid = request.pathExt;
        int success = storageAdapter.removeBook(uid);
        switch (success) {
            case DataStorageAdapterIface.OK:
                result.setCode(HttpAdapter.SC_OK);
                break;
            case DataStorageAdapterIface.NOT_FOUND:
                result.setCode(HttpAdapter.SC_NOT_FOUND);
                break;
            default:
                result.setCode(HttpAdapter.SC_INTERNAL_SERVER_ERROR);
        }
        return result;
    }

}
