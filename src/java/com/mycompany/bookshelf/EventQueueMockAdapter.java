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

import com.gskorupa.cricket.Adapter;
import com.gskorupa.cricket.Event;
import java.util.Properties;

/**
 *
 * @author greg
 */
public class EventQueueMockAdapter implements EventQueueAdapterIface, Adapter{
    
    /**
    * Read properties with names started with "DataStorageAdapterIface-"
    */
    public void loadProperties(Properties properties){
    }
    
    public String getContext(){
        return null;
    }

    public int push(Event event){
        return EventQueueAdapterIface.OK;
    }

    public Event pull(String eventType){
        Event event=new Event(eventType, "MOCK", "this is mock event");
        return event;
    }

}
