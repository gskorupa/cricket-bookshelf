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
import org.cricketmsf.in.http.HttpAdapter;

/**
 *
 * @author greg
 */
public class BookshelfHttpAdapter extends HttpAdapter implements BookshelfHttpAdapterIface, Adapter{
    
    public void loadProperties(HashMap<String,String> properties) {
        setContext(properties.get("context"));
        System.out.println("context=" + getContext());
    }
}
