
package com.mycompany.bookshelf;

import com.gskorupa.cricket.Adapter;
import com.gskorupa.cricket.HttpAdapter;
import com.sun.net.httpserver.HttpHandler;


import java.util.Properties;

/**
 *
 * @author greg
 */
public class BookshelfHttpAdapter extends HttpAdapter implements BookshelfHttpAdapterIface, Adapter, HttpHandler {
    
    public void loadProperties(Properties properties) {
        setContext(properties.getProperty("BookshelfHttpAdapterIface-context"));
        System.out.println("context=" + getContext());
        getServiceHooks(); 
    }
}
