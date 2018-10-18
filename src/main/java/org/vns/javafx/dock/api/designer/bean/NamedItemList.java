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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static org.vns.javafx.dock.api.designer.bean.NamedItem.AFTER;
import static org.vns.javafx.dock.api.designer.bean.NamedItem.BEFORE;
import static org.vns.javafx.dock.api.designer.bean.NamedItem.NOT_INSERT;

/**
 *
 * @author Valery
 */
public interface NamedItemList<E extends NamedItem> extends NamedItem {

    ObservableList<E> getItems();

    default void addItemsAfter(E after, E... scs) {
        int idx = getItems().indexOf(after);
        idx = idx < 0 ? getItems().size() : idx + 1;

        for (E sec : scs) {
            int size = getItems().size();
            addOrUpdateItem(idx, sec);
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
            addOrUpdateItem(idx, item);
            if (size != getItems().size()) {
                idx++;
            }
        }

    }

    default E addOrUpdateItem(int idx, E item) {
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

            if (item instanceof NamedItemList) {
                ObservableList<NamedItem> save = FXCollections.observableArrayList();
                NamedItemList list = (NamedItemList) item;
                save.addAll(list.getItems());
                list.getItems().clear();
                list.merge(save);
            }

            //mergeChilds(item);
        } else {
            retval.setDisplayName(item.getDisplayName());
            if (retval instanceof NamedItemList) {
                NamedItemList list = (NamedItemList) retval;
                list.merge(((NamedItemList) item).getItems());
            }
        }
        return retval;
    }

    default void merge(ObservableList<E> items) {
        for (E item : items) {
            updateBy(item);
        }
    }

    default void addOrUpdateItems(E... items) {
        addOrUpdateItems(getItems().size(), items);
    }

    default void addOrUpdateItems(int idx, E... items) {

        E toupdateItem;

        for (E item : items) {
            toupdateItem = null;
            for (E it : getItems()) {
                if (it.getName().equals(item.getName())) {
                    toupdateItem = it;
                    break;
                }
            }
            if (toupdateItem == null) {
                getItems().add(idx++, item);
                toupdateItem = item;

                if (item instanceof NamedItemList) {
                    ObservableList<NamedItem> props = FXCollections.observableArrayList();
                    NamedItemList list = (NamedItemList) item;
                    props.addAll(list.getItems());
                    list.getItems().clear();
                    //
                    // We must apply insert items
                    //
                    list.merge(props);
                }
            } else {
                toupdateItem.setDisplayName(item.getDisplayName());
                if (toupdateItem instanceof NamedItemList) {
                    NamedItemList list = (NamedItemList) toupdateItem;
                    list.merge(((NamedItemList) item).getItems());
                }
            }
        }
        //return retval;
    }

    //void mergeChilds(E item);
    default int indexByName(String searchName) {
        if (searchName == null) {
            return -1;
        }

        int retval = -1;

        E sec = getByName(searchName);
        if (sec != null) {
            retval = getItems().indexOf(sec);
        }
        return retval;
    }

    default boolean containsInList(String searchName, List<E> list) {
        if (searchName == null) {
            return false;
        }
        boolean retval = false;
        for (E item : list) {
            if (searchName.equals(item.getName())) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    default E getByName(String searchName) {
        if (searchName == null) {
            return null;
        }
        E retval = null;
        for (E sec : getItems()) {
            if (sec.getName().equals(searchName)) {
                retval = sec;
                break;
            }
        }
        return retval;
    }

    default void updateBy(E item) {
        int idx = indexByName(item.getName());
        int pos = NOT_INSERT;
        List<E> list = new ArrayList<>();
        if (item instanceof InsertBeforeItem) {
            idx = indexByName(item.getName());

            if (idx < 0 && item.getName() != null) {
                return;
            }

            list = ((InsertBeforeItem) item).getInsertList();
            //
            // insertList cannot contain an item with the same name as the item's name
            //
            if (containsInList(item.getName(), list)) {
                return;
            }

            updateByInsert(list);
            //
            // We must recalculate the idx index as one or more items may be removed
            //
            idx = indexByName(item.getName());
            if (idx < 0 && item.getName() != null) {
                return;
            } else if (idx < 0) {
                idx = 0;
            }
            pos = BEFORE;
        } else if (item instanceof InsertAfterItem) {
            idx = indexByName(item.getName());

            if (idx < 0 && item.getName() != null) {
                return;
            }

            list = ((InsertAfterItem) item).getInsertList();
            //
            // insertList cannot contain an item with the same name as the item's name
            //
            if (containsInList(item.getName(), list)) {
                return;
            }

            updateByInsert(list);
            //
            // We must recalculate the idx index as one or more items may be removed
            //
            idx = indexByName(item.getName());

            if (idx < 0 && item.getName() != null) {
                return;
            } else if (idx < 0) {
                idx = getItems().size();
            } else {
                idx++;
            }
            pos = AFTER;
        } else {
            list.add(item);
        }

        if (idx < 0 || pos == NOT_INSERT) {
            for (E c : list) {
                addOrUpdateItem(getItems().size(), item);
            }
        } else if (pos == BEFORE) {
            int size = getItems().size();
            for (E c : list) {
                addOrUpdateItem(idx, c);
                if (getItems().size() > size) {
                    idx++;
                }
                size = getItems().size();
            }
        } else if (pos == AFTER) {
            int size = getItems().size();
            for (E c : list) {
                addOrUpdateItem(idx, c);
                if (getItems().size() > size) {
                    ++idx;
                }
                size = getItems().size();
            }
        }
    }

    /**
     * For each element of the specified list, a search is performed in the
     * collection of this object by the value of the property named
     * {@code name}. If the search is successful, the element of the specified
     * list is replaced with the found element of the object collection. Thus,
     * an insert operation can be used to change the position of an item in an
     * object's collection.
     *
     * @param list the list of items defined by an insert operation.
     */
    default void updateByInsert(List<E> list) {
        Map<E, E> replace = new HashMap<>();
        for (E listItem : list) {
            int idx = indexByName(listItem.getName());
            if (idx < 0) {
                continue;
            }
            replace.put(listItem, getItems().get(idx));
        }

        replace.forEach((k, v) -> {
            list.set(list.indexOf(k), v);
            getItems().remove(v);
        });
    }

    /*    default void updateByInsert__(List<E> list) {
        Map<E,E> replase = new HashMap<>();
        for (E listItem : list) {
            int idx = indexByName(listItem.getName());
            if (idx < 0) {
                continue;
            }
            E remove = getItems().get(idx);
            if (listItem instanceof NamedItemList) {
                ((NamedItemList) listItem).getItems().clear();
                ((NamedItemList) listItem).getItems().addAll(((NamedItemList) remove).getItems());
            }
            getItems().remove(remove);
        }
    }
     */
}
