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
package org.vns.javafx.dock.api.designer.bean;

import java.util.Arrays;
import static org.vns.javafx.dock.api.designer.bean.NamedItem.AFTER;
import static org.vns.javafx.dock.api.designer.bean.NamedItem.BEFORE;

/**
 *
 * @author Valery
 */
public class ModelUtil {
    protected static void add(Section section,String... props) {
        for (int i = 0; i < props.length; i++) {
            String name;
            String displayName = null;
            if (!props[i].contains(":")) {
                name = props[i];
            } else {
                name = props[i].substring(0, props[i].indexOf(":"));
                displayName = props[i].substring(props[i].indexOf(":") + 1);

            }
            for (PropertyItem pd : section.getItems()) {
                if (pd.getName().equals(name)) {
                    continue;
                }
            }
            PropertyItem pd = new PropertyItem();
            pd.setName(name);
            if (displayName != null) {
                pd.setDisplayName(displayName);
            }
            pd.setSection(section);
            section.getItems().add(pd);
        }
        
    }
    public static void addAfter(Section section,String posPropName, String... props) {
        add(section,AFTER, posPropName, props);
    }

    protected static void addBefore(Section section, String posPropName, String... props) {
        add(section,BEFORE, posPropName, props);
    }
    
    public static void add(Section section,int pos, String posPropName, String... props) {
        
        PropertyItem[] propModels = new PropertyItem[props.length];
        int idx = section.indexByName(posPropName);
        
        for (int i = 0; i < props.length; i++) {

            String name;
            String displayName = null;
            if (!props[i].contains(":")) {
                name = props[i];
            } else {
                name = props[i].substring(0, props[i].indexOf(":"));
                displayName = props[i].substring(props[i].indexOf(":") + 1);

            }

            if (section.getByName(name) != null) {
                continue;
            }
            PropertyItem pd = new PropertyItem();
            pd.setName(name);
            if (displayName != null) {
                pd.setDisplayName(displayName);
            }
            pd.setSection(section);
            propModels[i] = pd;
        }
        if ( idx >= 0 && pos == AFTER) {
            idx++;
            section.getItems().addAll(idx, Arrays.asList(propModels));
        } else if ( idx >= 0 && pos == BEFORE) {
            section.getItems().addAll(idx, Arrays.asList(propModels));
        } else {
            section.getItems().addAll(propModels);
        }
    }    
    
    public static Section addSection(Category category,String sec, String displayName) {
        Section retval = null;
        for (Section s : category.getItems()) {
            if (s.getName().equals(sec)) {
                retval = s;
                break;
            }
        }
        if (retval == null) {
            retval = new Section();
            retval.setName(sec);
            retval.setDisplayName(displayName);
            category.getItems().add(retval);
        }

        return retval;
    }
    public static Section addSectionAfter(Category category,String secPosName, String sec, String displayName) {
        return addSection(category, AFTER, secPosName, sec, displayName);
    }

    public static Section addSectionBefore(Category category,String secPosName, String sec, String displayName) {
        return addSection(category,BEFORE, secPosName, sec, displayName);
    } 
    public static Section addSection(Category category,int pos, String secPosName, String sec, String displayName) {
        int idx = category.getItems().size();
        for (int i = 0; i < category.getItems().size(); i++) {
            if (category.getItems().get(i).getName().equals(secPosName)) {
                idx = i;
                break;
            }
        }
        Section retval = null;
        for (Section s : category.getItems()) {
            if (s.getName().equals(sec)) {
                retval = s;
                retval.setName(sec);
                retval.setDisplayName(displayName);
                break;
            }
        }
        if (retval == null) {
            retval = new Section();
            retval.setName(sec);
            retval.setDisplayName(displayName);
            if (idx >= 0 && pos == BEFORE) {
                category.getItems().add(idx, retval);
            } else if (idx >= 0 && pos == AFTER) {
                category.getItems().add(idx++, retval);
            } else {
                category.getItems().add(retval);
            }

        }
        return retval;

    }    
}
