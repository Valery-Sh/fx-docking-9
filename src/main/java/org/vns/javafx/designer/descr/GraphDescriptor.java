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
package org.vns.javafx.designer.descr;

import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

/**
 * The container of objects of type {@link NodeDescriptor}. 
 * Used as a root node in an {@code fxml} file.
 * 
 * @author Valery Shyshkin
 */
@DefaultProperty("descriptors")
public class GraphDescriptor extends Control {
    private final ObservableList<NodeDescriptor> descriptors = FXCollections.observableArrayList();
    
    /**
     * Returns a list of all registered objects of type {@link NodeDescriptor}
     * @return a list of all registered objects of type {@link NodeDescriptor}
     */
    public ObservableList<NodeDescriptor> getDescriptors() {
        return descriptors;
    }
    
}
