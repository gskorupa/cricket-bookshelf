package com.mycompany.bookshelf;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

/**
 *
 * @author greg
 */
public interface BookshelfHttpAdapterIface {
    
    public void handle(HttpExchange exchange) throws IOException;
    
}
