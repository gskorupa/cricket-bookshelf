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
import org.cricketmsf.Kernel;
import org.cricketmsf.out.OutboundAdapter;

/**
 *
 * @author greg
 */
public class EventQueueMockAdapter extends OutboundAdapter implements EventQueueAdapterIface, Adapter {

    /**
     * Read properties with names started with "DataStorageAdapterIface-"
     */
    public void loadProperties(HashMap<String,String> properties) {
    }

    public int push(Event event) {
        //we can log usage of this method sending a new event
        Kernel.getInstance().handleEvent(
            new Event(this.getClass().getSimpleName(), 
                    Event.CATEGORY_LOG, 
                    Event.LOG_INFO,
                    "",
                    "event " + event.getCategory() +"."+event.getType() + " pushed")
        );
        //the method does nothing
        return EventQueueAdapterIface.OK;
    }

    public Event pull(String eventCategory) {
        return new Event(this.getClass().getSimpleName(), 
                    eventCategory, 
                    "MOCK",
                    "",
                    "this is mock event"
        );
    }

}
