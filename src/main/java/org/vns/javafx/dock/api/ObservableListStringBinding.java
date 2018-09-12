/*
 * Copyright 2018 Your Organisation.
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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Valery
 */
public class ObservableListStringBinding<T> extends StringBinding{
    
    private final ObservableList<T> list;
    private final String separator;
    private InvalidationListener invalidationListener = o -> {invalidate(); };
    
    public ObservableListStringBinding(ObservableList<T> list, String separator) {
        this.list = list;
        this.separator = separator;
        list.addListener(invalidationListener);
        
    }
    @Override
     public ObservableList getDependencies() {
         return FXCollections.singletonObservableList(list);
     }
    @Override
    protected String computeValue() {
        StringBuilder sb = new StringBuilder();
        for ( T item : list ) {
            if ( item == null ) {
                sb.append("");
            } else {
                sb.append(item.toString());
            }
            sb.append(separator);
        }
        if ( ! list.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    @Override
    public void dispose() {
        list.removeListener(invalidationListener);
    }

}
