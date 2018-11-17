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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.indicator.IndicatorManager;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.LayoutContextFactory;
import org.vns.javafx.dock.api.Scope;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;
import org.vns.javafx.dock.api.dragging.view.RectangleFrame;

/**
 *
 * @author Valery Shyshkin
 */
public class SceneViewSkin extends SkinBase<SceneView> {

    private Pane contentPane;
    private ScrollAnimation scrollAnimation;
    private DragIndicator dragIndicator;
    private final Pane treeViewPane; // = new StackPane();
    private ChangeListener rootSceneSizeListener = (v, ov, nv) -> {
        RectangleFrame.hideAll(getSkinnable().getRoot().getScene().getWindow());
    };

    public SceneViewSkin(SceneView control) {
        super(control);

        if (control.getRoot() != null) {
            createSceneGraph(control.getRoot());
            scrollAnimation = new ScrollAnimation(control.getTreeView());
        }

        Dockable d = DockRegistry.makeDockable(control.getTreeView());

        TreeViewExMouseDragHandler dragHandler = new TreeViewExMouseDragHandler(d.getContext());

        d.getContext().getLookup().putUnique(MouseDragHandler.class, dragHandler);
        d.getContext().setLayoutContext(getSkinnable().getLayoutContext());

        treeViewPane = new StackPane();
        contentPane = new StackPane(treeViewPane) {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                TreeItemEx item = (TreeItemEx) getSkinnable().getTreeView().getSelectionModel().getSelectedItem();
                if (item != null) {
                    getSkinnable().getTreeView().getSelectionModel().select(item);
                }
            }
        };

        treeViewPane.getChildren().add(getSkinnable().getTreeView());
        dragIndicator = new DragIndicator(getSkinnable());
        dragIndicator.initIndicatorPane();

        SceneGraphViewLayoutContext targetContext = (SceneGraphViewLayoutContext) DockLayout.of(getSkinnable()).getLayoutContext();

        targetContext.getLookup()
                .putUnique(IndicatorManager.class, new DragIndicatorManager(targetContext, dragIndicator));

        TreeView treeView = control.getTreeView();

        treeView.rootProperty().addListener((v, ov, nv) -> {
            if (nv != null && control.getRoot() == null) {
                control.setRoot((Node) ((TreeItem) nv).getValue());
            } else if (nv == null) {
                control.setRoot(null);
            }
        });

        control.statusParProperty().addListener(this::statusBarChanged);

        targetContext.mousePositionProperty().addListener(this::mousePosChange);

        getChildren().add(contentPane);

        control.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, this::sceneMousePressed);
        control.rootProperty().addListener(this::rootChanged);

    }

    private boolean containsDesignerScope(Set<Scope> scopes) {
        boolean retval = false;
        for (Scope scope : scopes) {
            if ("designer".equals(scope.getId())) {
                retval = true;
                break;
            }
        }
        return retval;

    }

    private void sceneMousePressed(MouseEvent ev) {
        TreeItemEx item = getSkinnable().getTreeItem(ev.getScreenX(), ev.getScreenY());

        if (ev.isSecondaryButtonDown()) {
            secondaryMousePressed(ev, item);
            return;
        }
        NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
        if (nf == null) {
            return;
        }
        if (item == null || item.getValue() == null) {
            nf.hide();
        } else if (item.getValue() instanceof Node) {
            nf.show((Node) item.getValue());
        }

    }

    private void secondaryMousePressed(MouseEvent ev, TreeItemEx item) {

        if (item == null || item.getValue() == null || !(item.getValue() instanceof Node)) {
//            return;
        }

        ContextMenu menu = new ContextMenu();
        MenuItem mi = new MenuItem("Cut");
        setMenuiIemOnAction(mi, new Point2D(ev.getScreenX(), ev.getScreenY()), "cut-item", item);
        menu.getItems().add(mi);

        mi = new MenuItem("Paste");

        setMenuiIemOnAction(mi, new Point2D(ev.getScreenX(), ev.getScreenY()), "paste-cut-item", item);

        menu.getItems().add(mi);
        menu.getItems().add(new SeparatorMenuItem());
        mi = new MenuItem("Send to Back");
        setMenuiIemOnAction(mi, new Point2D(ev.getScreenX(), ev.getScreenY()), "send-to-back", item);
        menu.getItems().add(mi);

        mi = new MenuItem("Bring to Front");
        setMenuiIemOnAction(mi, new Point2D(ev.getScreenX(), ev.getScreenY()), "bring-to-front", item);
        menu.getItems().add(mi);
        getSkinnable().setContextMenu(menu);

    }

    private void setMenuiIemOnAction(MenuItem mi, Point2D point, String txt, TreeItemEx item) {
        Clipboard cb = Clipboard.getSystemClipboard();
        Map<DataFormat, Object> map = new HashMap<>();

        TreeViewEx tv = getSkinnable().getTreeView();

        switch (txt) {
            case "cut-item":
                if (item == null || tv.getRoot() == null) {
                    mi.setDisable(true);
                } else {
                    mi.setDisable(false);
                }
                mi.setOnAction(a -> {
                    if (item == null || item.getValue() == null) {
                        return;
                    }
                    map.put(DataFormat.PLAIN_TEXT, "cut-item");
                    TreeViewEx.clipBoardContent = new Pair("cut-item", item.getValue());
                    cb.setContent(map);

                    if (tv.getRoot() == item) {
                        getSkinnable().setRoot(null);

                    } else {
                        getSkinnable().getLayoutContext().undock(Dockable.of(item.getValue()));
                    }
                });
                break;
            case "paste-cut-item":
                LayoutContext lc = getSkinnable().getLayoutContext();
                if (TreeViewEx.clipBoardContent != null) {
                    Object v = TreeViewEx.clipBoardContent.getValue();
                    Dockable d = Dockable.of(v);

                    if (d == null || !lc.isAcceptable(d) || !lc.isAdmissiblePosition(d, point)) {
                        mi.setDisable(true);
                    } else {
                        mi.setDisable(false);
                    }
                } else {
                    mi.setDisable(true);
                }
                mi.setOnAction(a -> {
                    if (TreeViewEx.clipBoardContent.getValue() == null) {
                        return;
                    }
                    Object obj = TreeViewEx.clipBoardContent.getValue();

                    if (getSkinnable().getRoot() != null && TreeViewEx.clipBoardContent != null) {
                        Object v = TreeViewEx.clipBoardContent.getValue();
                        Dockable d = Dockable.of(v);
                        if (d != null) {
                            lc.dock(point, d);
                        }
                        TreeViewEx.clipBoardContent = null;
                    } else if (getSkinnable().getRoot() == null && (obj instanceof Node)) {
                        getSkinnable().setRoot((Node) obj);
                        TreeViewEx.clipBoardContent = null;
                    }
                });
                //}
                break;
            case "send-to-back":
                if (item == null || tv.getRoot() == null) {
                    mi.setDisable(true);
                } else {
                    mi.setDisable(false);
                }
                mi.setOnAction((ActionEvent e) -> {
                    if (item != null && (item.getValue() instanceof Node)) {
                        ((Node) item.getValue()).toBack();
                    }
                });
                break;
            case "bring-to-front":
                if (item == null || tv.getRoot() == null) {
                    mi.setDisable(true);
                } else {
                    mi.setDisable(false);
                }
                mi.setOnAction(e -> {
                    if (item != null && (item.getValue() instanceof Node)) {
                        ((Node) item.getValue()).toFront();
                    }
                });
                break;
        }//switch
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
        if (oldValue != null && oldValue.getScene() != null) {
            oldValue.getScene().heightProperty().removeListener(rootSceneSizeListener);
            oldValue.getScene().widthProperty().removeListener(rootSceneSizeListener);
        }
        SceneView.removeFramePanes(oldValue);
        if (newValue == null) {
            getSkinnable().getTreeView().setRoot(null);
            return;
        }

        createSceneGraph(newValue);
        scrollAnimation = new ScrollAnimation(getSkinnable().getTreeView());

    }

    private void createSceneGraph(Node node) {
        if (node == null) {
            getSkinnable().getTreeView().setRoot(null);
            return;
        }
        /*        if (node == node.getScene().getRoot()) {
            node.getStyleClass().clear();
            Scene sc = node.getScene();
            StackPane sp = new StackPane();
            sp.setStyle("-fx-background-color: SIENNA; -fx-padding: 20 20 20 20");
            sc.setRoot(sp);
            sp.getChildren().add(node);
            getSkinnable().setRoot(node);
            node.toBack();
            
        }
         */
        SceneView.addFramePanes(node);
        
        LayoutContext lc = new LayoutContextFactory().getContext(getSkinnable().getRoot());
        DockRegistry.makeDockLayout(getSkinnable().getRoot(), lc);
        if (getSkinnable().isDesigner() && !containsDesignerScope(lc.getScopes())) {
            lc.getScopes().add(new Scope("designer"));
        }

        Dockable dockable = DockRegistry.makeDockable(getSkinnable().getRoot());
        dockable.getContext().setDragNode(null);
        if (getSkinnable().isDesigner() && !containsDesignerScope(dockable.getContext().getScopes())) {
            dockable.getContext().getScopes().add(new Scope("designer"));
        }
        if (getSkinnable().getTreeView().getRoot() == null || getSkinnable().getTreeView().getRoot().getValue() != node) {
            TreeItemEx item = new TreeItemBuilder().build(node);
            getSkinnable().getTreeView().setRoot(item);
        }

        Platform.runLater(() -> {
            registerScrollBarEvents();
        });
        if (getSkinnable().getRoot() != null && getSkinnable().getRoot().getScene() != null) {
            getSkinnable().getRoot().getScene().heightProperty().addListener(rootSceneSizeListener);
            getSkinnable().getRoot().getScene().widthProperty().addListener(rootSceneSizeListener);
        }

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

        sb.addEventHandler(MouseEvent.MOUSE_EXITED, ev -> {
            dragIndicator.hideDrawShapes();
            scrollAnimation.stop();
            ev.consume();
        });

        sb.addEventHandler(MouseEvent.MOUSE_MOVED, ev -> {
            dragIndicator.hideDrawShapes();
            if (!scrollAnimation.isRunning()) {
                scrollAnimation.start(ev.getScreenX(), ev.getScreenY());
            }
            ev.consume();
        });

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
