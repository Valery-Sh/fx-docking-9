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
package org.vns.javafx.dock.api.designer;

import org.vns.javafx.dock.api.Selection;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.dragging.view.NodeResizer;

/**
 *
 * @author Valery
 */
public class DesignerSelection extends Selection {

    private NodeResizer resizer;

    public DesignerSelection() {
        init();
    }

    private void init() {
        selectedProperty().addListener(this::selectedChanged);
    }

    protected void selectedChanged(ObservableValue ov, Object oldValue, Object newValue) {
        if (newValue == null) {
            if (resizer != null) {
                resizer.hide();
                resizer = null;
            }
            return;
        }
        if (resizer != null && resizer.getNode() == newValue) {
            //selectTreeItem(newValue);
            //return;
        }
        if (resizer != null) {
            resizer.hide();
            resizer = null;
        }
        if (newValue != null && (newValue instanceof Node)) {
            resizer = new NodeResizer((Region) newValue);
            //resizer.setWindowType(NodeResizer.WindowType.STAGE);
            resizer.setWindowType(NodeResizer.WindowType.POPUPCONTROL);
            resizer.show();
        }
        //selectTreeItem(newValue);
    }


    @Override
    public void notifySelected(Object value) {
//        Platform.runLater(() -> {
        SceneGraphView sgv = DesignerLookup.lookup(SceneGraphView.class);
        if (sgv != null) {
            TreeItemEx item;
            if (sgv.getTreeView().getRoot().getValue() == value) {
                item = (TreeItemEx) sgv.getTreeView().getRoot();
            } else {
                item = EditorUtil.findTreeItemByObject(sgv.getTreeView(), value);
            }
//            System.err.println("DesignerSelection: item = " + item);
            
            if (item != null) {
//                System.err.println("DesignerSelection: item.value = " + item.getValue());
                Platform.runLater(() -> {
                    //sgv.getTreeView().getSelectionModel().select(item);
                });
                sgv.getTreeView().getSelectionModel().select(item);
            }
        }
    }

}
