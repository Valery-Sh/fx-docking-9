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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Pair;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.indicator.IndicatorManager;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.LayoutContextFactory;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;
import org.vns.javafx.dock.api.dragging.view.RectangleFrame;

/**
 *
 * @author Valery Shyshkin
 */
public class SceneGraphViewSkin extends SkinBase<SceneGraphView> {

    private Pane contentPane;
    private ScrollAnimation scrollAnimation;
    private DragIndicator dragIndicator;
    private final Pane treeViewPane; // = new StackPane();
    private ChangeListener rootSceneSizeListener = (v, ov, nv) -> {
        RectangleFrame.hideAll(getSkinnable().getRoot().getScene().getWindow());
    };

    public SceneGraphViewSkin(SceneGraphView control) {
        super(control);
        Dockable d = DockRegistry.makeDockable(getSkinnable().getTreeView());

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

        if (control.getRoot() != null) {
            LayoutContext lc = new LayoutContextFactory().getContext(getSkinnable().getRoot());
            DockRegistry.makeDockLayout(control.getRoot(), lc);
            Dockable dockable = DockRegistry.makeDockable(control.getRoot());
            dockable.getContext().setDragNode(null);

            createSceneGraph(control.getRoot());
            scrollAnimation = new ScrollAnimation(control.getTreeView());
        } else {
        }
        control.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, this::sceneMousePressed);
        control.rootProperty().addListener(this::rootChanged);

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
            return;
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
        menu.getItems().add(mi);
        mi = new MenuItem("Bring to Front");
        menu.getItems().add(mi);
        getSkinnable().getTreeView().setContextMenu(menu);

        //menu.show(getSkinnable().getTreeView(), ev.getSceneX(), ev.getSceneY());

    }

    private void setMenuiIemOnAction(MenuItem mi, Point2D point, String txt, TreeItemEx item) {
        Clipboard cb = Clipboard.getSystemClipboard();
        Map<DataFormat, Object> map = new HashMap<>();
        switch (txt) {
            case "cut-item":
                mi.setOnAction(a -> {
                    map.put(DataFormat.PLAIN_TEXT, "cut-item");
                    TreeViewEx.clipBoardContent = new Pair("cut-item", item.getValue());
                    cb.setContent(map);
                    System.err.println("Dockable.of(item.getValue()) = " + item.getValue());
                    System.err.println("   ---   Dockable.of(item.getValue()) = " + Dockable.of(item.getValue()));
                    System.err.println("   ---   layoutContext = " + Dockable.of(item.getValue()).getContext().getLayoutContext());
                    
                    TreeViewEx tv = getSkinnable().getTreeView();
                    getSkinnable().getLayoutContext().undock(Dockable.of(item.getValue()));
                    if ( tv.getRoot() == item ) {
                        System.err.println("CUT SET ROOT NULL");
                        getSkinnable().setRoot(null);
                    } else {
                        
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
                    }
                    mi.setOnAction(a -> {
                        if (TreeViewEx.clipBoardContent != null) {
                            Object o = TreeViewEx.clipBoardContent.getValue();
                            lc.dock(point, d);
                            TreeViewEx.clipBoardContent = null;
                        }
                    });
                }
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
        if (newValue == null) {
            return;
        }
        /*        if (newValue instanceof Region) {
            Region r = (Region) newValue;
            Border b = r.getBorder();
            if (b == null) {
                BorderStroke bs = new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.EMPTY);
                b = new Border(bs);
            }

            //r.setBorder(b);
        }
         */
        createSceneGraph(newValue);
        scrollAnimation = new ScrollAnimation(getSkinnable().getTreeView());

    }

    private TreeItemEx createSceneGraph(Node node) {
        if (node == null) {
            return null;
        }
        TreeItemEx item = new TreeItemBuilder().build(node);
        getSkinnable().getTreeView().setRoot(item);
        Platform.runLater(() -> {
            registerScrollBarEvents();
        });
        if (getSkinnable().getRoot() != null && getSkinnable().getRoot().getScene() != null) {
            getSkinnable().getRoot().getScene().heightProperty().addListener(rootSceneSizeListener);
            getSkinnable().getRoot().getScene().widthProperty().addListener(rootSceneSizeListener);
        }

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
