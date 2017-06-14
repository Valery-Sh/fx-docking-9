/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api.editor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;

/**
 *
 * @author Valery
 */
public abstract class GraphicPlaceholderBuilder extends DefaultTreeItemBuilder implements TreeItemBuilder.PlaceholderBuilder {

    private final int placeholderId;

    public GraphicPlaceholderBuilder(int placeHolderId) {
        this.placeholderId = placeHolderId;
    }

    //protected abstract void setGraphic(Object treeItemObject, Node node);

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = createItem(obj);
        return retval;
    }

    @Override
    protected HBox createDefaultContent(Object obj) {
        Label label = new Label();
        HBox hb = new HBox(label);
        label.getStyleClass().add("labeled-insert-graphic");
        if (obj != null) {

            Label glb = new Label(obj.getClass().getSimpleName());

            glb.getStyleClass().add("tree-item-node-" + obj.getClass().getSimpleName().toLowerCase());
            hb.getChildren().add(glb);
            if (obj instanceof Labeled) {
                glb.setText(glb.getText() + " " + ((Labeled) obj).getText());
            }
        }
        return hb;
    }
    public static class GraphicChangeListener implements ChangeListener<Node> {

        private TreeItemEx treeItem;
        private final int placeholderId;

        public GraphicChangeListener(int placeholderId, TreeItemEx treeItem) {
            this.treeItem = treeItem;
            this.placeholderId = placeholderId;
        }

        @Override
        public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
            if ("graphicLb".equals(((Node) treeItem.getObject()).getId())) {
                System.err.println("CCCCCCCCCCCCCC CHANGED");
            }

            if (oldValue != null && newValue == null) {
                treeItem.getChildren().clear();
            } else if (oldValue == null && newValue != null) {
                //TreeItemEx ph = createPlaceholder(0,newValue);
                System.err.println("CHANGED treeItem.getObject=" + treeItem.getObject());
                //TreeItemEx ph = ((PlaceholderBuilderFactory) treeItem.getBuilder()).getPlaceholderBuilder(placeholderId).createPlaceholder(newValue);
                Object o1 = treeItem.getObject();
                o1 = ((TreeItemEx) treeItem.getParent()).getObject();

                TreeItemEx ph = treeItem.createPlaceholder(placeholderId, newValue);
                System.err.println("CCC " + ph.getObject());
                //System.err.println("   ph.getObject=" + ph.getObject());
                //System.err.println("---------------------------------------");

                treeItem.getChildren().add(ph);
                //((Labeled)  treeItem.getObject()).setGraphic(newValue);
            } else if (oldValue != null && newValue != null) {
                TreeItemEx t = treeItem.treeItemOf(newValue);
                if (t != null) {
                    TreeViewEx.updateOnMove(t);
                }
                ((Labeled) treeItem.getObject()).setGraphic(newValue);
            }
        }

    }

}//GraphicPlaceholderBuilder
