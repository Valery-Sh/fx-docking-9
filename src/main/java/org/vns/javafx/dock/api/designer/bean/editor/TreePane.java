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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Skin;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.vns.javafx.dock.api.designer.PropertyEditorPane;

/**
 *
 * @author Nastia
 */
public class TreePane<E> extends TreePaneItem<E> {

    //private ObjectProperty<TreePaneItem<E>> root = new SimpleObjectProperty();
    private final ToggleGroup toggleGroup;
    private final ObservableMap<ButtonBase,E> values = FXCollections.observableHashMap();
    
            
    public TreePane() {
        this(null);
    }

    public TreePane(String name) {
        super(name);
        toggleGroup = new ToggleGroup();
/*        toggleGroup.getToggles().addListener( (ListChangeListener.Change<? extends Toggle> change) -> {
            System.err.println("TOGLE GROUP CHANGE");
            if ( ! change.getList().contains(toggleGroup.getSelectedToggle()) ) {
                getExternalValuePane().getChildren().clear();   
            }
        });
*/        
        toggleGroup.selectedToggleProperty().addListener((v,ov,nv) -> {
            if ( getExternalValuePane() == null ) {
                return;
            }
            if ( nv == null ) {
                //getValuePane().getChildren().clear();   
                getExternalValuePane().getChildren().clear();   
                return;
            }
            if ( nv.isSelected() ) {
                //getValuePane().getChildren().clear();
                getExternalValuePane().getChildren().clear();
                TreePaneItem item = itemOf((ToggleButton)nv);
                if ( item != null && item.getValue() != null ) {
                    PropertyEditorPane pane = (PropertyEditorPane) item.getValue();
//                    if ( getBoundProperty() != null && pane.getBean() != getBoundProperty().getValue() ) {
//                        pane.setBean(getBoundProperty().getValue());
//                    }
//                    getValuePane().getChildren().add(pane);
                    getExternalValuePane().getChildren().add(pane);
                    
                }
            }
        });
        getStyleClass().add("tree-pane");
        init();
    }

    private void init() {

    }
    private TreePaneItem itemOf(Node node) {
        
        if ( node instanceof TreePaneItem ) {
            return (TreePaneItem) node;
        }
        Node retval = node;
        while(retval != null && ! (retval instanceof TreePaneItem) ) {
            retval = retval.getParent();
        }
        return (TreePaneItem) retval;
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
    
    public ToggleGroup getToggleGroup() {
        return toggleGroup;
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
            control.getToggleGroup().getToggles().clear();
            items.forEach(it -> {
                control.getToggleGroup().getToggles().add((Toggle)it.getTextButton());
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
