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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

/**
 *
 * @author Valery Shyshkin
 */
public class DefaultFraming implements NodeFraming {

    private ShapeFraming shapeFraming;
    private boolean applyCss;

    public DefaultFraming() {
        this(false);
    }

    public DefaultFraming(ShapeFraming shapeFraming) {
        this(shapeFraming, false);
    }

    public DefaultFraming(boolean applyCss) {
        this.applyCss = applyCss;
    }

    public DefaultFraming(ShapeFraming shapeFraming, boolean applyCss) {
        this.shapeFraming = shapeFraming;
        this.applyCss = applyCss;
    }

    private final ReadOnlyObjectWrapper<Node> nodeWrapper = new ReadOnlyObjectWrapper<>();

    //
    // NodeFraming Implementation
    //
    private ChangeListener<Node> nodeParentListener;
    private ChangeListener<Scene> nodeSceneListener;
    private ChangeListener<Window> nodeWindowListener;

    @Override
    public final void show(Node node) {
        if (!isAcceptable(node)) {
            return;
        }
        if (getNode() != null) {
            finalizeNode();
            hide();
        }
        setNode(node);
        //
        // Check when null to avoid proplem when the method isAcceptable is overidden
        //
        if (node != null) {
            initializeNode();
        }
        //
        // Check when null to avoid proplem when the method isAcceptable is overidden
        //
        ShapeFraming pane = (ShapeFraming) node.getScene().getRoot().lookup("." + ShapeFraming.ID);
        if (shapeFraming != null && pane == null) {
            ((Pane) node.getScene().getRoot()).getChildren().add(shapeFraming);
        } else if (shapeFraming == null && pane != null) {
            shapeFraming = pane;
        } else if (shapeFraming == null && pane == null) {
            shapeFraming = new ShapeFraming();

            ((Pane) node.getScene().getRoot()).getChildren().add(shapeFraming);
            if (!applyCss) {
                shapeFraming.setDefaultStyles();
            }
            ShapeFraming.SideCircles sc = new ShapeFraming.SideCircles();
            sc.setRadius(1.5);

            if (!applyCss) {
                sc.setDefaultStyles();
            } else {
                sc.addStyleClass("side-shape");
            }
            shapeFraming.setSideShapes(sc);
        }

        shapeFraming.bind(node);
        shapeFraming.setVisible(true);
    }

    @Override
    public final void hide() {
        if (getNode() == null) {
            return;
        }
        shapeFraming.setVisible(false);
        finalizeNode();

    }

/*    public boolean isShowing(Node node) {
        return isShowing() && (getNode() == node);
    }

    public boolean isShowing() {
        return getNode() != null;
    }
*/
    //
    //
    //

    public ReadOnlyObjectProperty<Node> nodeProperty() {
        return nodeWrapper.getReadOnlyProperty();
    }

    public Node getNode() {
        return nodeWrapper.getReadOnlyProperty().getValue();
    }

    protected void setNode(Node node) {
        nodeWrapper.setValue(node);
    }

    protected boolean isAcceptable(Node node) {
        boolean retval = true;

        if (node == null || node.getParent() == null || node.getScene() == null || node.getScene().getWindow() == null) {
            retval = false;
        } else if (getNode() != null && getNode() == node) {
            //retval = false;
        }

        return retval;
    }

    private void initializeNode() {
        nodeParentListener = (ov, oldValue, newvalue) -> {
            hide();
        };
        nodeSceneListener = (ov, oldValue, newvalue) -> {
            hide();
        };
        nodeWindowListener = (ov, oldValue, newvalue) -> {
            hide();
        };

        getNode().parentProperty().addListener(nodeParentListener);
        getNode().sceneProperty().addListener(nodeSceneListener);
        getNode().getScene().windowProperty().addListener(nodeWindowListener);

    }

    protected void finalizeNode() {
        getNode().parentProperty().removeListener(nodeParentListener);
        getNode().sceneProperty().removeListener(nodeSceneListener);
        if (getNode().getScene() != null) {
            getNode().getScene().windowProperty().removeListener(nodeWindowListener);
        }

    }

}
