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
package org.vns.javafx.dock.api.dragging.view;

import javafx.scene.Node;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Selection;

/**
 *
 * @author Olga
 */
public class ResizeNodeFraming extends RectangularFraming {

    public static final String RESIZE_RECT_ID = "DEFAULT-RESIZE-RECT-" + RectangleFrame.ID;
    //private RectangleNodeFraming parentFraming;

    private boolean applyCss;

    public ResizeNodeFraming() {
        super(RESIZE_RECT_ID, false);

    }

    public ResizeNodeFraming(boolean applyCss) {
        super(RESIZE_RECT_ID, applyCss);
    }

    public ResizeNodeFraming(String rectId) {
        super(rectId, false);

    }

    public ResizeNodeFraming(String rectId, boolean applyCss) {
        super(rectId, applyCss);
    }

    @Override
    public void setDefaultStyle() {
        setStyle("-fx-stroke-type: outside; -fx-stroke: rgb(255, 148, 40); -fx-stroke-width: 1; -fx-fill: white");
    }

    @Override
    protected void initializeOnShow(Node node) {
        super.initializeOnShow(node);
        System.err.println("ResizeNodeFraming: initializeOnShow node = " + node);
        createSideShapes();

//        createParentFraming();
        Selection sel = DockRegistry.lookup(Selection.class);
        if (sel != null) {
            sel.notifySelected(node);
        }

    }

    protected void createSideShapes() {
        if (getRectangleFrame().getSideShapes() == null) {
            System.err.println("ResizeNodeFraming createSideShapes getRectangleFrame().getBoundNode = " + getRectangleFrame().getBoundNode());
            RectangleFrame.SideCircles sc = new RectangleFrame.SideCircles();
            
            sc.setRadius(2);

            if (!applyCss) {
                sc.setDefaultStyle();
            } else {
                sc.getStyleClass().add("side-shape");
            }
            getRectangleFrame().setSideShapes(sc);
        } else {
            //getRectangleFrame().getSideShapes().setVisible(true);
        }
    }

}
