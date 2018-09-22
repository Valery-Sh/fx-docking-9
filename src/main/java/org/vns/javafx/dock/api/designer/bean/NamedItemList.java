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

import javafx.collections.ObservableList;

/**
 *
 * @author Valery
 */
public interface NamedItemList<E extends NamedItem> extends NamedItem{

    ObservableList<E> getItems();

    default void addItemsAfter(E after, E... scs) {
        int idx = getItems().indexOf(after);
        idx = idx < 0 ? getItems().size() : idx + 1;

        for (E sec : scs) {
            int size = getItems().size();
            addItem(idx, sec);
            if (size != getItems().size()) {
                idx++;
            }
        }

    }

    default void addItemsBefore(E before, E... items) {
        int idx = getItems().indexOf(before);
        idx = idx < 0 ? getItems().size() : idx;

        for (E item : items) {
            int size = getItems().size();
            addItem(idx, item);
            if (size != getItems().size()) {
                idx++;
            }
        }

    }

    default E addItem(int idx, E item) {
        E retval = null;
        for (E it : getItems()) {
            if (it.getName().equals(item.getName())) {
                retval = it;
                break;
            }
        }
        if (retval == null) {
            getItems().add(idx, item);
            retval = item;
            /*            ObservableList<NamedItem> props = FXCollections.observableArrayList();
            props.addAll(sec.getItems());
            sec.getItems().clear();
            sec.merge(props);
             */
            mergeChilds(item);

        } else {
            retval.setDisplayName(item.getDisplayName());
        }
        return retval;
    }

    default void addItems(E... items) {
        addItems(getItems().size(), items);
    }

    default void addItems(int idx, E... items) {

        E retval;
//        int size = sections.size();
        for (E item : items) {
            retval = null;
            for (E it : getItems()) {
                if (it.getName().equals(item.getName())) {
                    retval = it;
                    break;
                }
            }
            if (retval == null) {
                getItems().add(idx++, item);
                retval = item;
                /*                ObservableList<PropertyItem> props = FXCollections.observableArrayList();
                props.addAll(sec.getItems());
                sec.getItems().clear();
                 */
                 mergeChilds(item);
            } else {
                retval.setDisplayName(item.getDisplayName());
            }
        }
        //return retval;
    }

    void mergeChilds(E item);


}
