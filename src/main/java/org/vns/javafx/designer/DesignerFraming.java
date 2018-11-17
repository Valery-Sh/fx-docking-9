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

import org.vns.javafx.dock.api.dragging.view.*;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Selection;

/**
 *
 * @author Olga
 */
public class DesignerFraming extends ResizeNodeFraming {

    public static final String DESIGNER_RESIZE_RECT_ID = "DESIGNER-RESIZE-RECT-" + RectangleFrame.ID;
    public static final String DESIGNER_PARENT_RECT_ID = "DESIGNER-PARENT-RECT-" + RectangleFrame.ID;

    private RectangularFraming parentFraming;

    @Override
    protected void finalizeOnHide(Node node) {
        super.finalizeOnHide(node);

        if (parentFraming != null) {
            parentFraming.hide();
        }

    }

    public DesignerFraming() {
        super(DESIGNER_RESIZE_RECT_ID, false);

    }

    public DesignerFraming(boolean applyCss) {
        super(DESIGNER_RESIZE_RECT_ID, applyCss);
    }

    public DesignerFraming(String rectId) {
        super(rectId, false);

    }

    public DesignerFraming(String rectId, boolean applyCss) {
        super(rectId, applyCss);
    }

    @Override
    public void setDefaultStyle() {
        setStyle("-fx-stroke-type: outside; -fx-stroke: rgb(255, 148, 40); -fx-stroke-width: 1; -fx-fill: white");
    }

    @Override
    protected void initializeOnShow(Node node) {
        super.initializeOnShow(node);
        createParentFraming();

        Selection sel = DockRegistry.lookup(Selection.class);
        if (sel != null) {
            sel.notifySelected(node);
        }
    }

    protected void createParentFraming() {
        SceneView sgv = DesignerLookup.lookup(SceneView.class);
        if (sgv == null) {
            return;
        }
        TreeItem item = EditorUtil.findTreeItemByObject(sgv.getTreeView(), getNode());
        if (item != null && item.getParent() != null && item.getParent().getValue() != null && (item.getParent().getValue() instanceof Node)) {
            Node parent = (Node) item.getParent().getValue();
            if (parent != parent.getScene().getRoot()) { //&&& 12.02
                 parentFraming = new RectangularFraming(DESIGNER_PARENT_RECT_ID);
                 parentFraming.show(parent);
                //parentFraming.getShapeFraming().setStyle("-fx-stroke-type: outside; -fx-stroke: rgb(255, 148, 40); -fx-stroke-width: 6; -fx-fill: transparent; -fx-opacity: 0.7");
                parentFraming.getRectangleFrame().setStyle("-fx-stroke-type: outside; -fx-stroke: rgb(255, 201, 14); -fx-stroke-width: 6; -fx-fill: transparent; -fx-opacity: 0.8");
            }
        }

    }

}
