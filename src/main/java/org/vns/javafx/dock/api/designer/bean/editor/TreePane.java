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

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Nastia
 */
public class TreePane<E> extends TreePaneItem<E> {

    //private ObjectProperty<TreePaneItem<E>> root = new SimpleObjectProperty();
    private final ToggleGroup togleGroup;
    private final ObservableMap<ButtonBase,E> values = FXCollections.observableHashMap();
    
            
    public TreePane() {
        this(null);
    }

    public TreePane(String name) {
        super(name);
        togleGroup = new ToggleGroup();
        getStyleClass().add("tree-pane");
        init();
    }

    private void init() {

    }

    @Override
    public List<TreePaneItem<E>> getAllItems() {
        List list = new ArrayList<>();
        list.add(this);
        for (TreePaneItem it : (List<TreePaneItem>) this.getChildItems()) {
            list.add(it);
            addToList(list, it);
        }

        return list;
    }

    private void addToList(List list, TreePaneItem item) {
        for ( TreePaneItem it : (List<TreePaneItem>)item.getChildItems()) {
            list.add(it);
            addToList(list, it);
        }
    }
    public ObservableMap<ButtonBase,E> getValues() {
        return values;
    }
    
    public ToggleGroup getTogleGroup() {
        return togleGroup;
    }

/*    public ObjectProperty<TreePaneItem<E>> rootProperty() {
        return root;
    }

    public TreePaneItem<E> getRoot() {
        return root.get();
    }

    public void setRoot(TreePaneItem<E> root) {
        this.root.set(root);
    }
*/
/*    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }
*/
    @Override
    public Skin<?> createDefaultSkin() {
        return new TreePaneSkin(this);
    }

    public static class TreePaneSkin<E> extends TreePaneItemSkin {

        //StackPane layout;

        public TreePaneSkin(TreePane<E> control) {
            super(control);
            List<TreePaneItem<E>> items = control.getAllItems();
            System.err.println("SIZE = " + items.size());
            control.getTogleGroup().getToggles().clear();
            items.forEach(it -> {
                control.getTogleGroup().getToggles().add((Toggle)it.getTextButton());
            });

  /*          layout = new StackPane();

            if (control.getRoot() != null) {
                layout.getChildren().add(control.getRoot());
            }
            control.rootProperty().addListener((v, ov, nv) -> {
                if (ov != null) {
                    layout.getChildren().remove(ov);
                }
                if (nv != null) {
                    layout.getChildren().add(nv);
                }
            });
            List<TreePaneItem<E>> items = control.getAllItems();
            items.forEach(it -> {
                control.getTogleGroup().getToggles().add((Toggle)it.getTextButton());
            });
            getChildren().add(layout);
*/
        }

    }///TreePaneSkin
}//TreePane
