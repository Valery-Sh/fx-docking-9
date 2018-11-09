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
package org.vns.javafx.designer;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author Olga
 */
public class SceneGraphData {
    
    private final ObservableMap<String,Object> variables = FXCollections.observableHashMap();

    private SceneGraphData() {
    }
    
    public static final SceneGraphData getInstance() {
        return Singleton.INSTANCE;
    }

    public ObservableMap<String, Object> getVariables() {
        return variables;
    }
    
    public String nameBySimpleClassName(String className) {
        String retval = className.substring(0,1).toLowerCase() + className.substring(1);
        int idx = 1;
        
        ObservableList<Integer> list = FXCollections.observableArrayList();
        for ( String k : variables.keySet()) {
            if ( ! k.startsWith(retval ) || k.equals(retval)) {
                continue;
            }
            String rmd = k.substring(retval.length());
            if ( rmd.startsWith("0")) {
                continue;
            }
            char[] crmd = rmd.toCharArray();
            boolean nice = true;
            for ( char c : crmd) {
                if (!Character.isDigit(c)) {
                    nice = false;
                    break;
                }
            }
            if ( nice ) {
                list.add(Integer.parseInt(rmd));
            }
        }
        if ( list.isEmpty() ) {
            idx = 1;
        } else if (list.size() == 1) {
            idx = list.get(0) + 1;
        } else {
           FXCollections.sort(list);
           for ( int i=0; i < list.size();i++) {
               if ( i == list.size() - 1 ) {
                   idx = list.get(i) + 1;
                   break;
               }
               if ( list.get(i+1) - list.get(i) > 1 ) {
                   idx = list.get(i) + 1;
                   break;
               }
           }
        }
        return retval + idx;
    }

    public String nameByObject(Object obj) {
        if (obj == null) {
            throw new NullPointerException("The parameter value cannot be null");
        }
        return nameBySimpleClassName(obj.getClass().getSimpleName());
    }
    
    
    private static final class Singleton {
        private static final SceneGraphData INSTANCE = new SceneGraphData();
    }
}
