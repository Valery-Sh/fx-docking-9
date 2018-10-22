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

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Nastia
 */
public class TitledPaneItem extends TitledPane {

    private boolean layoutDone = false;

    private int level;

    private TitledPaneItem parentItem;
    
    private ObservableList<TitledPaneItem> items = FXCollections.observableArrayList();

    public TitledPaneItem() {
        this("");
    }

    public TitledPaneItem(String title) {
        this(title, new StackPane());
    }

    protected TitledPaneItem(StackPane sp) {
        this("", sp);
    }

    protected TitledPaneItem(String title, StackPane sp) {
        super(title, sp);
        sp.setPrefHeight(0);
        init();
    }

    private void init() {
        items.addListener(this::itemsChanged);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public TitledPaneItem getParentItem() {
        return parentItem;
    }

    protected void setParentItem(TitledPaneItem parentItem) {
        this.parentItem = parentItem;
    }

    public ObservableList<TitledPaneItem> getItems() {
        return items;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        StackPane p = (StackPane) lookup(".content");
        p.setStyle("-fx-padding: 0");
        StackPane c = (StackPane) getContent();
        
        //c.setPadding(new Insets(getLevel() * 10,0,0,0));
        //System.err.println("layoutChildrenm level = " + getLevel());
        //if (getItems().isEmpty() && c != null && p != null && !layoutDone) {
        if (getItems().isEmpty() && !layoutDone) {            
            //System.err.println("LAYOUT");
/*            p.setPrefHeight(0);
            p.setMinHeight(0);
            p.setMaxHeight(0);
*/
            c.setPrefHeight(0);
            c.setMinHeight(0);
            c.setMaxHeight(0);
            
//                p.getChildren().clear();
            layoutDone = true;
        } else if (! getItems().isEmpty() && !layoutDone) {
            c.setPrefHeight(-1);
            c.setMinHeight(-1);
            c.setMaxHeight(-1);
            layoutDone = true;
            //System.err.println("LEVEL = " + getLevel());
        }
    }

    private void itemsChanged(ListChangeListener.Change<? extends TitledPaneItem> change) {
        while (change.next()) {
            if (change.wasPermutated()) {
                // Handle permutations
                handlePermutated(change);
            } else if (change.wasUpdated()) {
                // Handle updates
                handleUpdated(change);
            } else if (change.wasReplaced()) {
                // Handle replacements
                handleRemoved(change);
                handleAdded(change);
            } else {
                if (change.wasRemoved()) {
                    // Handle removals
                    handleRemoved(change);
                } else if (change.wasAdded()) {
                    // Handle additions
                    handleAdded(change);
                }
            }
        }
    }//itemschanged

    private void handlePermutated(ListChangeListener.Change<? extends TitledPaneItem> change) {
        System.out.println("Change Type: Permutated");
        //System.out.println("Permutated Range: " + getRangeText(change));
        int start = change.getFrom();
        int end = change.getTo();
        for (int oldIndex = start; oldIndex < end; oldIndex++) {
            int newIndex = change.getPermutation(oldIndex);
            System.out.println("index[" + oldIndex + "] moved to "
                    + "index[" + newIndex + "]");
        }
    }

    private void handleUpdated(ListChangeListener.Change<? extends TitledPaneItem> change) {
        System.out.println("Change Type: Updated");
        change.getList().subList(change.getFrom(), change.getTo());
    }

    private void handleRemoved(ListChangeListener.Change<? extends TitledPaneItem> change) {
        System.out.println("Change Type: removed");
        int removedSize = change.getRemovedSize();
        List<? extends TitledPaneItem> subList = change.getRemoved();
    }

    private void handleAdded(ListChangeListener.Change<? extends TitledPaneItem> change) {
        System.out.println("Change Type: Added");
        int addedSize = change.getAddedSize();
        List<? extends TitledPaneItem> subList = change.getAddedSubList();
        ((StackPane)getContent()).getChildren().addAll(subList);
        subList.forEach(it -> {
            it.setParentItem(this);
            it.setLevel(it.getParentItem().getLevel() + 1);
            System.err.println("it.getLevel = " + it.getLevel());
            it.getContent().setStyle("-fx-padding: 0 0 0 " + it.getLevel()*30 );

        });
                
        layoutDone = false;

    }

    private Pane createContent() {
        return new VBox();
    }
}
