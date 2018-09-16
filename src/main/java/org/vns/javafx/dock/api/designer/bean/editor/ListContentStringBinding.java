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
package org.vns.javafx.dock.api.designer.bean.editor;

import java.lang.ref.WeakReference;
import javafx.beans.WeakListener;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

/**
 *
 * @author Valery
 */
public class ListContentStringBinding<E> implements ListChangeListener<E>, ChangeListener<String>, WeakListener {

    private final WeakReference<ObservableList<E>> listRef;
    private final WeakReference<StringProperty> stringRef;

    private final StringConverter<E> converter;
    private final String separator;

    private boolean updating = false;
    private boolean bound = false;

    public ListContentStringBinding(StringProperty string, ObservableList<E> list, String separator, StringConverter<E> converter) {
        
        listRef = new WeakReference<>(list);
        stringRef = new WeakReference<>(string);
        this.separator = separator;
        this.converter = converter;
    }

    @Override
    public void onChanged(Change<? extends E> change) {
        if (!updating) {
            final ObservableList<E> ls = listRef.get();
            final StringProperty sp = stringRef.get();

            if (ls == null || sp == null) {
                if (ls != null) {
                    ls.removeListener(this);
                }
                if (sp != null) {
                    sp.removeListener(this);
                }
                return;
            }

            try {
                updating = true;
                StringBuilder sb = new StringBuilder();
                for (E obj : ls) {
                    sb.append(converter.toString(obj));
                    sb.append(separator);
                }
                if (!ls.isEmpty()) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sp.set(sb.toString());
            } finally {
                updating = false;
            }
        }
    }

    @Override
    public boolean wasGarbageCollected() {
        return (listRef.get() == null) || (stringRef.get() == null);
    }

    @Override
    public int hashCode() {
        final ObservableList<E> ls = listRef.get();
        final StringProperty sp = stringRef.get();
        final int hc1 = (ls == null) ? 0 : ls.hashCode();
        final int hc2 = (sp == null) ? 0 : sp.hashCode();
        return hc1 * hc2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final Object ls = listRef.get();
        final Object sp = stringRef.get();
        if ((ls == null) || (sp == null)) {
            return false;
        }

        if (obj instanceof ListContentStringBinding) {
            final ListContentStringBinding otherBinding = (ListContentStringBinding) obj;
            final Object o1 = otherBinding.listRef.get();
            final Object o2 = otherBinding.stringRef.get();
            if ((o1 == null) || (o2 == null)) {
                return false;
            }

            if ((ls == o1) && (sp == o2)) {
                return true;
            }
            if ((ls == o2) && (sp == o1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!updating) {
                    
            final ObservableList<E> ls = listRef.get();
            final StringProperty sp = stringRef.get();

            if (ls == null || sp == null) {
                if (ls != null) {
                    ls.removeListener(this);
                }
                if (sp != null) {
                    sp.removeListener(this);
                }
                return;
            }

            try {
                updating = true;
                String[] items = StringTextField.split(newValue, separator);
                if ( converter instanceof SubstitutionConverter) {
                    if ( ((SubstitutionConverter)converter).isEmptyListSubstitution(newValue) ) {
                        ls.clear();
                        return;
                    } else if ( ((SubstitutionConverter)converter).isSingleEmptyItemSubstitution(newValue) ) {
                        ls.clear();
                        ls.add(converter.fromString(""));
                        return;
                    } else if ( ((SubstitutionConverter)converter).isNullSubstitution(newValue) ) {
                        ls.clear();
                        ls.add(null);
                        return;
                    }
                }
                
                ls.clear();
                if ( newValue != null && newValue.isEmpty() ) {
                    return;
                }
                for (String item : items) {
                    ls.add(converter.fromString(item));
                }
            } finally {
                updating = false;
            }

        }
    }

    public void bind() {
        StringBuilder sb = new StringBuilder();
        for (E obj : listRef.get()) {
            sb.append(converter.toString(obj));
            sb.append(separator);
        }
        if (!listRef.get().isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        stringRef.get().set(sb.toString());
        listRef.get().addListener(this);
        //stringRef.get().addListener(this);
        bound = true;
    }

    public void bindBidirectional() {
        StringBuilder sb = new StringBuilder();
        for (E obj : listRef.get()) {
            sb.append(converter.toString(obj));
            sb.append(separator);
        }
        if (!listRef.get().isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        stringRef.get().set(sb.toString());
        listRef.get().addListener(this);
        stringRef.get().addListener(this);
        bound = true;
    }
    
    public void unbind() {
        listRef.get().removeListener(this);
        stringRef.get().removeListener(this);
        bound = false;
    }

    public boolean isBound() {
        return bound;
    }
    
}
