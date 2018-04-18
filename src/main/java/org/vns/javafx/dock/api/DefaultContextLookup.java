/*
 * Copyright 2017 Your Organisation.
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

package org.vns.javafx.dock.api;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;

/**
 *
 * @author Valery
 */
public class DefaultContextLookup implements ContextLookup {

    public final ObservableMap<Class, List<Object>> lookup = FXCollections.observableHashMap();

    @Override
    public <T> T lookup(Class<T> clazz) {
        T retval = null;
        if (lookup.get(clazz) != null && !lookup.get(clazz).isEmpty()) {
            retval = (T) lookup.get(clazz).get(0);
        }
        return retval;
    }

    @Override
    public <T> List<? extends T> lookupAll(Class<T> clazz) {
        List retval = FXCollections.observableArrayList();
        if (lookup.get(clazz) != null) {
            retval = lookup.get(clazz);
        }
        return retval;
    }

    @Override
    public <T> void add(T obj) {
        if (lookup.containsKey(obj.getClass())) {
            if (!lookup.get(obj.getClass()).contains(obj)) {
                lookup.get(obj.getClass()).add(obj);
            }
            return;
        }
        ObservableList<Class> ifs = FXCollections.observableArrayList();
        ifs.addAll(obj.getClass().getInterfaces());
        ObservableList<Class> classes = FXCollections.observableArrayList();
        Class sc = obj.getClass();
        while (!Object.class.equals(sc)) {
            FilteredList<Class> fl = ifs.filtered(c -> {
                return !ifs.contains(c);
            });
            ifs.addAll(fl);
            if (!classes.contains(sc)) {
                classes.add(sc);
                classes.addAll(sc.getInterfaces());

            }
            sc = sc.getSuperclass();
        }
        classes.addAll(ifs);
        classes.forEach(c -> {
            if (!lookup.containsKey(c)) {
                List<Object> list = FXCollections.observableArrayList();
                list.add(obj);
                lookup.put(c, list);
            } else {
                if (!lookup.get(c).contains(obj)) {
                    lookup.get(c).add(obj);
                }
            }

        });
    }

    public <T> void put(Class key, T obj) {
        if (lookup.containsKey(key)) {
            if (!lookup.get(key).contains(obj)) {
                lookup.get(key).add(obj);
            }
        } else {
            List<Object> list = FXCollections.observableArrayList();
            list.add(obj);
            lookup.put(key, list);
        }

    }
//    @Override

    @Override
    public <T> void remove(T obj) {
        List<Class> toDelete = new ArrayList<>();
        lookup.keySet().forEach(clazz -> {
            List<Object> list = lookup.get(clazz);
            if (list.contains(obj)) {
                list.remove(obj);
                if (list.isEmpty()) {
                    toDelete.add(clazz);
                }
            }
        });
        toDelete.forEach(clazz -> {
            lookup.remove(clazz);
        });
    }

    @Override
    public <T> void remove(Class key, T obj) {
        if (lookup.containsKey(key)) {
            if (lookup.get(key).contains(obj)) {
                lookup.get(key).remove(obj);
            }
        }
    }

    @Override
    public <T> void clear(Class key) {
        if (lookup.containsKey(key)) {
            lookup.get(key).clear();
        }
    }

    @Override
    public <T> void putUnique(Class key, T obj) {
        if (lookup.containsKey(key)) {
            lookup.get(key).clear();
        }
        if ( obj != null ) {
            put(key,obj);
        }
    }

}
