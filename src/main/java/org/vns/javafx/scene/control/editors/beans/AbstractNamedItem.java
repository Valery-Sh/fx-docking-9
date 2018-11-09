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
package org.vns.javafx.scene.control.editors.beans;

import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Valery Shyshkin
 */
public class AbstractNamedItem implements NamedItem {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();

    protected AbstractNamedItem() {
        init();
    }
    protected AbstractNamedItem(String name, String displayName) {
        this();
        this.name.set(name);
        this.displayName.set(displayName);
    }
    protected AbstractNamedItem(String name) {
        this();
        this.name.set(name);
    }

    private void init() {
        name.addListener((v, ov, nv) -> {
            if (getDisplayName() == null && nv != null || nv != null && nv.equals(getDisplayName())) {
                setDisplayName(NamedItem.toDisplayName(nv));
            }
        });
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public void setName(String id) {
        this.name.set(id);
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName.get();
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }

}
