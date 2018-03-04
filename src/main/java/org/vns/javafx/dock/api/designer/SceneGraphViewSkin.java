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

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.indicator.IndicatorManager;
import org.vns.javafx.dock.api.DockLayout;

/**
 *
 * @author Valery
 */
public class SceneGraphViewSkin extends SkinBase<SceneGraphView> {

    //private ContentPane contentPane;
    private Pane contentPane;
    private ScrollAnimation scrollAnimation;
    private DragIndicator dragIndicator;
    private final Pane treeViewPane; // = new StackPane();

    public SceneGraphViewSkin(SceneGraphView control) {
        super(control);
        Dockable d = DockRegistry.makeDockable(getSkinnable().getTreeView());

        TreeViewExMouseDragHandler dragHandler = new TreeViewExMouseDragHandler(d.getContext());

        d.getContext().getLookup().putUnique(MouseDragHandler.class, dragHandler);
        d.getContext().setLayoutContext(getSkinnable().getLayoutContext());
        treeViewPane = new StackPane();
        if (!getChildren().isEmpty()) {
            getChildren().clear();
        }
        //StackPane.setAlignment(getSkinnable().getTreeView(), Pos.CENTER);
        contentPane = new StackPane(treeViewPane) {

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                //getSkinnable().getParent().layout();
                //getSkinnable().getParent().requestLayout();
                //requestParentLayout();
                if (getSkinnable().getTreeView().getSkin() != null) {
                }

            }
        };

//        contentPane.setStyle("-fx-border-color:red; -fx-border-width: 1; -fx-background-color: yellow");
        treeViewPane.setStyle("-fx-border-color: red; -fx-border-width: 1");
//        getSkinnable().getTreeView().setPrefHeight(1000);
        treeViewPane.getChildren().add(getSkinnable().getTreeView());
        dragIndicator = new DragIndicator(getSkinnable());
        dragIndicator.initIndicatorPane();
//                lookup.putUnique(IndicatorManager.class, new DragIndicatorManager(this);
        SceneGraphViewTargetContext targetContext = (SceneGraphViewTargetContext) DockLayout.of(getSkinnable()).getLayoutContext();

        targetContext
                .getLookup()
                .putUnique(IndicatorManager.class, new DragIndicatorManager(targetContext, dragIndicator));
        getSkinnable().statusParProperty().addListener(this::statusBarChanged);

        targetContext.mousePositionProperty().addListener(this::mousePosChange);

        getChildren().add(contentPane);
        //getChildren().add(treeViewPane);

        if (getSkinnable().getRoot() != null) {
            createSceneGraph(getSkinnable().getRoot());
            scrollAnimation = new ScrollAnimation(control.getTreeView());
            getSkinnable().getRoot().getScene().setOnMouseClicked( e -> {
            Selection sel = DesignerLookup.lookup(Selection.class);
            sel.setSelected(getSkinnable().getRoot().getScene());
        });

        }
        getSkinnable().rootProperty().addListener(this::rootChanged);

    }

    protected void statusBarChanged(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        if (oldValue == null) {
            contentPane.getChildren().remove(oldValue);
        }
        if (newValue != null) {
            contentPane.getChildren().add(0, newValue);
        }
    }

    protected void rootChanged(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        if (newValue == null) {
            return;
        }
        //((Stage)newValue.getScene().getWindow()).requestFocus();
        System.err.println("SceneGraphViewSkin: SKIN ROOT CHANGED root = " + getSkinnable().getRoot());
        System.err.println("SceneGraphViewSkin: SKIN ROOT CHANGED root.getParent() = " + getSkinnable().getRoot().getParent());
        ((Stage) newValue.getScene().getWindow()).requestFocus();
        if (newValue instanceof Region) {
            Region r = (Region) newValue;
            Border b = r.getBorder();
            if (b == null) {
                BorderStroke bs = new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.EMPTY);
                b = new Border(bs);
            }

            r.setBorder(b);
        }

        createSceneGraph(newValue);
        scrollAnimation = new ScrollAnimation(getSkinnable().getTreeView());
        //newValue.getStyle();
        //newValue.setStyle("-fx-border-width: 2; -fx-border-color: aqua");
        //Stage s = (Stage)newValue.getScene().getWindow();
        getSkinnable().requestFocus();
        ((Stage) newValue.getScene().getWindow()).requestFocus();
        newValue.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            System.err.println("SCENE CLICKED");
            Selection sel = DesignerLookup.lookup(Selection.class);
            sel.setSelected(newValue.getScene());
//            e.consume();
        });
        //s.setWidth(s.getWidth() + 1);
        Platform.runLater(() -> {
            //r.setBorder(null);
        });

    }

    protected TreeItemEx createSceneGraph(Node node) {
        TreeItemEx item = new TreeItemBuilder().build(node);
        item.setExpanded(true);
        getSkinnable().getTreeView().setRoot(item);
        Platform.runLater(() -> {
            registerScrollBarEvents();
        });
        return item;
    }

    protected void mousePosChange(ObservableValue<? extends Point2D> observable, Point2D oldValue, Point2D newValue) {

        if (getSkinnable().getTreeView().getRoot() == null) {
            return;
        }
        ScrollBar sb = getSkinnable().getTreeView().getVScrollBar();
        if (newValue == null) {
            scrollAnimation.stop();
        }
        if (!sb.contains(sb.screenToLocal(newValue))) {
            scrollAnimation.stop();
        }
        if (sb.contains(sb.screenToLocal(newValue))) {
            scrollAnimation.start(newValue.getX(), newValue.getY());
        }
    }

    protected void registerScrollBarEvents() {
        ScrollBar sb = getSkinnable().getTreeView().getVScrollBar();

//        sb.addEventHandler(DragEvent.DRAG_EXITED, ev -> {
        sb.addEventHandler(MouseEvent.MOUSE_EXITED, ev -> {
            dragIndicator.hideDrawShapes();
            scrollAnimation.stop();
            ev.consume();
        });
        //sb.addEventHandler(DragEvent.DRAG_OVER, ev -> {
        sb.addEventHandler(MouseEvent.MOUSE_MOVED, ev -> {
            dragIndicator.hideDrawShapes();
            //ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            if (!scrollAnimation.isRunning()) {
                scrollAnimation.start(ev.getScreenX(), ev.getScreenY());
            }
            ev.consume();
        });

        //sb.addEventHandler(DragEvent.DRAG_ENTERED, ev -> {
        sb.addEventHandler(MouseEvent.MOUSE_ENTERED, ev -> {
            dragIndicator.hideDrawShapes();
            ev.consume();
            scrollAnimation.start(ev.getScreenX(), ev.getScreenY());
        });
    }

    public static class ContentPane extends VBox {

        public ContentPane() {

        }

        public ContentPane(Node... items) {
            super(items);
        }

        @Override
        protected double computePrefHeight(double h) {
            return super.computePrefHeight(h);
        }

        @Override
        protected double computePrefWidth(double w) {
            return super.computePrefWidth(w);
        }

        @Override
        protected double computeMinHeight(double h) {
            return super.computeMinHeight(h);
        }

        @Override
        protected double computeMinWidth(double w) {
            return super.computeMinWidth(w);
        }

        @Override
        protected double computeMaxHeight(double h) {
            return super.computeMaxHeight(h);
        }

        @Override
        protected double computeMaxWidth(double w) {
            return super.computeMaxWidth(w);
        }
    }

}
