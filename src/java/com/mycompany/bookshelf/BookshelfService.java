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

import com.gskorupa.cricket.AdapterHook;
import com.gskorupa.cricket.ArgumentParser;
import com.gskorupa.cricket.in.HttpAdapter;
import com.gskorupa.cricket.Httpd;
import com.gskorupa.cricket.RequestObject;
import java.util.logging.Logger;
import com.gskorupa.cricket.Service;
import static java.lang.Thread.MIN_PRIORITY;
import java.util.ArrayList;
import java.util.Map;

/**
 * SimpleService
 *
 * @author greg
 */
public class BookshelfService extends Service {

    // emergency logger
    private static final Logger logger = Logger.getLogger(com.mycompany.bookshelf.BookshelfService.class.getName());

    // adapters
    DataStorageAdapterIface storageAdapter = null;
    EventQueueAdapterIface eventsAdapter = null;
    BookshelfHttpAdapterIface httpAdapter = null;

    public BookshelfService() {

        fields = new Object[3];
        fields[0] = storageAdapter;
        fields[1] = eventsAdapter;
        fields[2] = httpAdapter;
        adapters = new Class[3];
        adapters[0] = DataStorageAdapterIface.class;
        adapters[1] = EventQueueAdapterIface.class;
        adapters[2] = BookshelfHttpAdapterIface.class;
    }

    public void getAdapters() {
        storageAdapter = (DataStorageAdapterIface) super.fields[0];
        eventsAdapter = (EventQueueAdapterIface) super.fields[1];
        httpAdapter = (BookshelfHttpAdapterIface) super.fields[2];
    }

    //
    public String sayHello() {
        return "Hi! I'm " + this.getClass().getSimpleName();
    }

    @AdapterHook(handlerClassName = "BookshelfHttpAdapterIface", requestMethod = "POST")
    public Object addBook(RequestObject request) {
        HttpResult result = new HttpResult();

        //create data object based on request parmeters
        BookData book = new BookData();
        book.setAuthor((String) request.parameters.get("author"));
        book.setTitle((String) request.parameters.get("title"));
        book.setPublisher((String) request.parameters.get("publisher"));
        book.setPublishDate((String) request.parameters.get("publish-date"));

        //store data object and add registered book data to the result
        ArrayList books=new ArrayList();
        books.add(storageAdapter.addBook(book));
        result.setData(books);

        //publish event
        boolean eventPropagated
                = EventQueueAdapterIface.OK == eventsAdapter.push(
                        new Event(
                                Event.BOOK_NEW,
                                this.getClass().getSimpleName(),
                                System.currentTimeMillis(),
                                null)
                );

        //set result code which will be sent in http response
        result.setCode(HttpAdapter.SC_CREATED);

        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final BookshelfService service;
        Map<String, String> arguments = ArgumentParser.getArguments(args);

        if (arguments.containsKey("error")) {
            System.out.println(arguments.get("error"));
            System.exit(-1);
        }
        if (arguments.containsKey("help")) {
            BookshelfService s = new BookshelfService(); //creating instance this way is valid only for displaing help!
            System.out.println(s.getHelp());
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
                if (service.isHttpHandlerLoaded()) {
                    System.out.println("Starting http server ...");
                    Runtime.getRuntime().addShutdownHook(
                            new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(200);
                                //some cleaning up code could be added here ... if required
                                System.out.println("\nShutdown ...");
                                service.getHttpd().server.stop(MIN_PRIORITY);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    service.setHttpd(new Httpd(service));
                    service.getHttpd().run();
                    System.out.println("Started. Press Ctrl-C to stop");
                    while (true) {
                        Thread.sleep(100);
                    }
                } else {
                    System.out.println("Couldn't find any http request hook method. Exiting ...");
                    System.exit(MIN_PRIORITY);
                }
            } else {
                // say hello
                System.out.println(service.sayHello());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
