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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.designer.DesignerLookup;
import org.vns.javafx.dock.api.designer.EditorUtil;
import org.vns.javafx.dock.api.designer.SceneGraphView;

/**
 *
 * @author Valery Shyshkin
 */
public class DefaultFraming implements NodeFraming {

    private ShapeFraming shapeFraming;
    private ParentFraming parentFraming;

    private final ObservableList<String> styleClass = FXCollections.observableArrayList();
    private final ObjectProperty<String> style = new SimpleObjectProperty<>();

    private boolean applyCss;

    private final ReadOnlyObjectWrapper<Node> nodeWrapper = new ReadOnlyObjectWrapper<>();

    private ChangeListener<Node> nodeParentListener;
    private ChangeListener<Scene> nodeSceneListener;
    private ChangeListener<Window> nodeWindowListener;

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

    public void setDefaultStyle() {
        setStyle("-fx-stroke-type: outside; -fx-stroke: rgb(255, 148, 40); -fx-stroke-width: 1; -fx-fill: white");
    }

    public ObservableList<String> getStyleClass() {
        return styleClass;
    }

    public ObjectProperty<String> styleProperty() {
        return style;
    }

    public String getStyle() {
        return style.get();
    }

    public void setStyle(String style) {
        this.style.set(style);
    }

    //
    // 
    //
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
        ShapeFraming shape = lookupShapeFraming();
        if (shapeFraming != null && shape == null) {
            ((Pane) node.getScene().getRoot()).getChildren().add(shapeFraming);
        } else if (shapeFraming == null && shape != null) {
            shapeFraming = shape;
        } else if (shapeFraming == null && shape == null) {
            shapeFraming = new ShapeFraming();
            shapeFraming.setId(ShapeFraming.ID);

            ((Pane) node.getScene().getRoot()).getChildren().add(shapeFraming);
            if (! applyCss) {
                if ( getStyle() != null ) {
                    shapeFraming.setStyle(getStyle());
                } else {
                    shapeFraming.setDefaultStyles();
                }
            } else {
                if ( getStyle() != null ) {
                    shapeFraming.setStyle(getStyle());
                } 
                if ( ! getStyleClass().isEmpty()) {
                    getStyleClass().forEach(s -> {
                        shapeFraming.getStyleClass().add(s);
                    });
                }
            }
            System.err.println("node.getScene().getRoot()); = " + node.getScene().getRoot());
            createSideShapes();
        }

        shapeFraming.bind(node);
        shapeFraming.setVisible(true);

        createParentFraming();

        Selection sel = DockRegistry.lookup(Selection.class);
        if (sel != null) {
            sel.notifySelected(node);
        }
    }

    protected ShapeFraming lookupShapeFraming() {
        return (ShapeFraming) getNode().getScene().getRoot().lookup("." + ShapeFraming.ID);
    }

    protected void setId() {
        shapeFraming.setId(ShapeFraming.ID);
    }

    protected void createParentFraming() {
        SceneGraphView sgv = DesignerLookup.lookup(SceneGraphView.class);
        if (sgv == null) {
            return;
        }
        TreeItem item = EditorUtil.findTreeItemByObject(sgv.getTreeView(), getNode());
        if (item != null && item.getParent() != null && item.getParent().getValue() != null && (item.getParent().getValue() instanceof Node)) {
            Node parent = (Node) item.getParent().getValue();
            parentFraming = new ParentFraming();

            parentFraming.show(parent);
            //parentFraming.getShapeFraming().setStyle("-fx-stroke-type: outside; -fx-stroke: rgb(255, 148, 40); -fx-stroke-width: 6; -fx-fill: transparent; -fx-opacity: 0.7");
            parentFraming.getShapeFraming().setStyle("-fx-stroke-type: outside; -fx-stroke: rgb(255, 201, 14); -fx-stroke-width: 6; -fx-fill: transparent; -fx-opacity: 0.8");            

        }

    }
    //GOLDENROD NAVAJOWHITE SANDYBROWN PERU WHEAT             
    protected void createSideShapes() {
        
        ShapeFraming.SideCircles sc = new ShapeFraming.SideCircles();
        sc.setRadius(1.5);

        if (!applyCss) {
            sc.setDefaultStyle();
        } else {
            sc.getStyleClass().add("side-shape");
        }
        shapeFraming.setSideShapes(sc);
    }

    public ShapeFraming getShapeFraming() {
        return shapeFraming;
    }

    @Override
    public final void hide() {
        if (getNode() == null) {
            return;
        }
        shapeFraming.setVisible(false);
        finalizeNode();
        if (parentFraming != null) {
            parentFraming.hide();
        }

    }

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

    public static class ParentFraming extends DefaultFraming {

        public ParentFraming() {
            this(false);
        }

        public ParentFraming(boolean applyCss) {
            super(applyCss);
        }

        @Override
        protected void createSideShapes() {

        }

        protected void createParentFraming() {

        }

        @Override
        protected ShapeFraming lookupShapeFraming() {
            return (ShapeFraming) getNode().getScene().getRoot().lookup(".PARENT-" + ShapeFraming.ID);
        }

        @Override
        protected void setId() {
            getShapeFraming().setId("PARENT-" + ShapeFraming.ID);
        }

        protected boolean isAcceptable(Node node) {
            return true;
        }
    }
}
