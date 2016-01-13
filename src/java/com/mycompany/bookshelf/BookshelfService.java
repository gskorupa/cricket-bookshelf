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

import com.gskorupa.cricket.ArgumentParser;
import com.gskorupa.cricket.Event;
import com.gskorupa.cricket.EventHook;
import com.gskorupa.cricket.HttpAdapterHook;
import com.gskorupa.cricket.in.HttpAdapter;
import com.gskorupa.cricket.Kernel;
import com.gskorupa.cricket.RequestObject;
import com.gskorupa.cricket.out.LoggerAdapterIface;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * SimpleService
 *
 * @author greg
 */
public class BookshelfService extends Kernel {

    // emergency logger
    private static final Logger logger = Logger.getLogger(com.mycompany.bookshelf.BookshelfService.class.getName());

    // adapters
    DataStorageAdapterIface storageAdapter = null;
    EventQueueAdapterIface eventsAdapter = null;
    BookshelfHttpAdapterIface httpAdapter = null;
    LoggerAdapterIface logAdapter = null;

    public BookshelfService() {

        adapters = new Object[4];
        adapters[0] = logAdapter;
        adapters[1] = storageAdapter;
        adapters[2] = eventsAdapter;
        adapters[3] = httpAdapter;

        adapterClasses = new Class[4];
        adapterClasses[0] = LoggerAdapterIface.class;
        adapterClasses[1] = DataStorageAdapterIface.class;
        adapterClasses[2] = EventQueueAdapterIface.class;
        adapterClasses[3] = BookshelfHttpAdapterIface.class;
    }

    @Override
    public void getAdapters() {
        logAdapter = (LoggerAdapterIface) super.adapters[0];
        storageAdapter = (DataStorageAdapterIface) super.adapters[1];
        eventsAdapter = (EventQueueAdapterIface) super.adapters[2];
        httpAdapter = (BookshelfHttpAdapterIface) super.adapters[3];
    }

    @Override
    public void runOnce() {
        //write to logs
        Event ev = new Event(
                this.getClass().getSimpleName(),
                Event.CATEGORY_LOG, // equals "LOG"
                Event.LOG_INFO, // equals "INFO"
                null);
        logEvent(ev);
        //alternatively:
        //logAdapter.log(ev);
        System.out.println("Hi! I'm " + this.getClass().getSimpleName());
    }

    @EventHook(eventCategory = "LOG")
    public void logEvent(com.gskorupa.cricket.Event event) {
        logAdapter.log(event);
    }

    @EventHook(eventCategory = "*")
    public void processEvent(com.gskorupa.cricket.Event event) {
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final BookshelfService service;
        ArgumentParser arguments = new ArgumentParser(args);
        if (arguments.isProblem()) {
            if (arguments.containsKey("error")) {
                System.out.println(arguments.get("error"));
            }
            System.out.println(new BookshelfService().getHelp());
            System.exit(-1);
        }
        try {
            if (arguments.containsKey("config")) {
                service = (BookshelfService) BookshelfService.getInstance(BookshelfService.class, arguments.get("config"));
            } else {
                service = (BookshelfService) BookshelfService.getInstanceUsingResources(BookshelfService.class);
            }
            service.getAdapters();

            if (arguments.containsKey("run")) {
                service.start();
            } else {
                service.runOnce();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
