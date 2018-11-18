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
package org.vns.javafx.designer;

import org.vns.javafx.dock.api.Selection;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;

/**
 *
 * @author Valery
 */
public class DesignerSelection extends Selection {

    //private NodeResizer resizer;
    private NodeFraming resizer;

    public DesignerSelection() {
        init();
    }

    private void init() {
        selectedProperty().addListener(this::selectedChanged);
    }

    @Override
    public void setSelected(Object toSelect) {
        System.err.println("DesignerSelection: setLelected = " + toSelect);
        if (toSelect instanceof Node) {
            NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
            if (nf != null) {
                nf.show((Node) toSelect);
            }
        }
        notifySelected(toSelect);
        //this.selected.set(toSelect);
    }

    protected void selectedChanged(ObservableValue ov, Object oldValue, Object newValue) {

        NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
//        System.err.println("1 Designerselection: selectedChanged nf = " + nf);
        if (newValue == null) {
            if (nf != null) {
                nf.hide();
//                resizer = null;
            }
            return;
        }

        if (nf != null) {
            nf.hide();
        }
        if (newValue instanceof Node) {
            System.err.println("DesignerSelection: selectedChanged = " + newValue);

            nf.show((Node) newValue);
        }
//        System.err.println("2 Designerselection: selectedChanged newValue = " + newValue);

        notifySelected(newValue);
    }

    @Override
    public void notifySelected(Object value) {
//        System.err.println("notifySelected");
        SceneView sgv = DesignerLookup.lookup(SceneView.class);
        if (sgv != null) {
            TreeItemEx item;
            if (sgv.getTreeView().getRoot().getValue() == value) {
                item = (TreeItemEx) sgv.getTreeView().getRoot();
            } else {
                item = EditorUtil.findTreeItemByObject(sgv.getTreeView(), value);
            }
            if (item != null) {
//                System.err.println("DesignerSelection: item.value=" + item.getValue());
//                sgv.getTreeView().getSelectionModel().selectFirst();
                sgv.getTreeView().getSelectionModel().select(item);
                //DesignerLookup.lookup(SceneGraphView.class).getTreeView().requestFocus();
            }
        }
    }

}
