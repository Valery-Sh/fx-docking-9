package org.vns.javafx.dock.api.designer;

import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TargetContext;
import static org.vns.javafx.dock.api.designer.TreeItemBuilder.CELL_UUID;
import org.vns.javafx.dock.api.dragging.DragType;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "rootNode")
public class SceneGraphView extends Control implements DockTarget {

    private SceneGraphViewTargetContext targetContext;

    private DragType dragType = DragType.SIMPLE;

    public static final int LAST = 0;
    public static final int FIRST = 2;

    public static double ANCHOR_OFFSET = 4;

    private final TreeViewEx treeView;
    private Node rootNode;

    private final Pane treeViewPane = new StackPane();

    //
    // ContentPane is a subclass of VBox
    // 
    private final ContentPane contentPane = new ContentPane(treeViewPane);

    private DragIndicator dragIndicator;
    private Region statusBar;

    private final ObservableList<TreeCell> visibleCells = FXCollections.observableArrayList();

    private ScrollAnimation scrollAnimation;

    public SceneGraphView() {
        this.treeView = new TreeViewEx<>(this);
        init();
    }

    public SceneGraphView(Node rootNode) {
        this.treeView = new TreeViewEx<>(this);
        this.rootNode = rootNode;
        init();
    }

    private void init() {
        dragIndicator = new DragIndicator(this);
        treeViewPane.getChildren().add(treeView);

        sceneProperty().addListener(this::sceneChanged);
        customizeCell();
        dragIndicator.initIndicatorPane();
        scrollAnimation = new ScrollAnimation((TreeViewEx) treeView);
        treeView.addEventHandler(DragEvent.ANY, new TreeViewDragEventHandler(this));

    }

    public ObservableList<TreeCell> getVisibleCells() {
        return visibleCells;
    }

    public DragType getDragType() {
        return dragType;
    }

    public void setDragType(DragType dragType) {
        this.dragType = dragType;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
        rootChanged();
    }

    public Region getStatusBar() {
        return statusBar;
    }

    public void setStatusBar(Region statusBar) {
        if (statusBar == null && this.statusBar != null) {
            contentPane.getChildren().remove(this.statusBar);
        }
        this.statusBar = statusBar;
        if (statusBar != null) {
            contentPane.getChildren().add(0, statusBar);
        }
    }

    protected DragIndicator getDragIndicator() {
        return dragIndicator;
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    protected void contentChanged(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        if (oldValue != null) {
            contentPane.getChildren().remove(oldValue);
        } else if (newValue != null) {
            contentPane.getChildren().clear();
        }
    }

    protected ScrollAnimation getScrollAnimation() {
        return scrollAnimation;
    }

    protected Pane getTreeViewPane() {
        return treeViewPane;
    }

    public TreeViewEx getTreeView() {
        return treeView;
    }

    /*!!!23.01public void childrenModification(TreeItem.TreeModificationEvent<ItemValue> ev) {
        if (ev.wasAdded()) {
            for (TreeItem<ItemValue> item : ev.getAddedChildren()) {
                //item.getValue().getBuilder().registerChangeHandler(item);
                //System.err.println("Event:  added item obj = " + item.getValue().getTreeItemObject());
            }
        }
        if (ev.wasRemoved()) {
            for (TreeItem<ItemValue> item : ev.getRemovedChildren()) {
                //!!!23.01 TreeViewEx.updateOnMove((TreeItemEx) item);
            }
        }
    }
     */
    protected TreeItemEx createSceneGraph(Node node) {
        //TreeItemBuilder builder = new TreeItemBuilder();
        TreeItemEx item = new TreeItemBuilder().build(node);
        //NodeDescriptor nc = NodeDescriptorRegistry.getInstance().getDescriptor(node);

        //!!!23.01item.addEventHandler(TreeItem.<ItemValue>childrenModificationEvent(),
        //        this::childrenModification);
        return item;
    }

    private void sceneChanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        if (newValue != null) {
            newValue.windowProperty().addListener(this::windowChanged);
        }
    }

    private void windowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        if (newValue != null) {
            newValue.addEventFilter(WindowEvent.WINDOW_SHOWN, this::windowShown);
        }
    }

    private void windowShown(WindowEvent ev) {
        rootChanged();
    }

    protected void rootChanged() {
        if (rootNode == null) {
            return;
        }
        TreeItemEx it = createSceneGraph(rootNode);
        it.setExpanded(true);
        treeView.setRoot(it);
        Platform.runLater(() -> {
            registerScrollBarEvents();
        });
    }

    protected void registerScrollBarEvents() {
        ScrollBar sb = ((TreeViewEx) treeView).getVScrollBar();

        sb.addEventHandler(DragEvent.DRAG_EXITED, ev -> {
            dragIndicator.hideDrawShapes();
            scrollAnimation.stop();
            ev.consume();
        });
        sb.addEventHandler(DragEvent.DRAG_OVER, ev -> {
            dragIndicator.hideDrawShapes();
            ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            if (!scrollAnimation.isRunning()) {
                scrollAnimation.start(ev.getScreenX(), ev.getScreenY());
            }
            ev.consume();
        });

        sb.addEventHandler(DragEvent.DRAG_ENTERED, ev -> {
            dragIndicator.hideDrawShapes();

            ev.consume();
            scrollAnimation.start(ev.getScreenX(), ev.getScreenY());

        });

    }

    protected void customizeCell() {
        TreeView<Object> t = treeView;
        t.setCellFactory((TreeView<Object> tv) -> {
            TreeCell cell = new TreeCell() {
                @Override
                public void updateItem(Object value, boolean empty) {
                    super.updateItem(value, empty);

                    if (empty) {
                        setText(null);
                        setGraphic(null);
                        if (this.getUserData() != null) {
                            Object[] o = (Object[]) this.getUserData();
                            if (o[0] != null) {
                                this.removeEventHandler(DragEvent.DRAG_OVER, (TreeItemCellDragEventHandler) o[0]);
                            }
                        }
                        this.setOnDragDetected(null);
                        this.setOnDragDropped(null);
                        this.setOnDragDone(null);
                        DockRegistry.getInstance().unregisterDefault(this);
                        getVisibleCells().remove(this);

                    } else {
                        this.setGraphic(((TreeItemEx) this.getTreeItem()).getCellGraphic());
                        if (value != null && (value instanceof Node)) {
                            setId(((Node) value).getId());
                        }
                        TreeItemCellDragEventHandler h = new TreeItemCellDragEventHandler(SceneGraphView.this, this);

                        this.addEventHandler(DragEvent.ANY, h);
                        this.setUserData(new Object[]{h, null});

                        registerDragDetected(this);
                        //registerMouseDragged(this);

                        registerDragDropped(this);
                        registerDragDone(this);

                        if (!getVisibleCells().contains(this)) {
                            getVisibleCells().add(this);
                        }
                    }
                }
            };
            return cell;
        });
    }

    public TreeViewEx getTreeView(double x, double y) {
        TreeViewEx retval = null;
        if (DockUtil.contains(getTreeView(), x, y)) {
            return getTreeView();
        }
        return retval;
    }

    public TreeItemEx getTreeItem(double x, double y) {
        TreeItemEx retval = null;
        for (TreeCell cell : getVisibleCells()) {
            if (DockUtil.contains(cell, x, y)) {
                retval = (TreeItemEx) cell.getTreeItem();
                break;
            }
        }
        return retval;
    }

    public TreeItemEx getTreeItem(Point2D p) {
        return getTreeItem(p.getX(), p.getY());
    }

    protected void registerDragDetected(TreeCell cell) {
        if (getDragType().equals(DragType.DRAG_AND_DROP)) {
            cell.setOnDragDetected(ev -> {
                Dragboard dragboard = treeView.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                DragGesture dg = new DragTreeViewGesture(treeView, (TreeItemEx) cell.getTreeItem());
                treeView.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
                ClipboardContent content = new ClipboardContent();
                content.putUrl(CELL_UUID);
                dragboard.setContent(content);
                treeView.getSelectionModel().clearSelection();
                ev.consume();
            });
        } else {
            if (cell.getTreeItem().getValue() != null && (cell.getTreeItem().getValue() instanceof Node)) {
                Node node = (Node) cell.getTreeItem().getValue();
                DockRegistry.getInstance().registerDefault(cell);
                Dockable dockable = DockRegistry.dockable(cell);
                dockable.getDockableContext().setTargetContext(getTargetContext());
                //DockRegistry.dockable(cell).getDockableContext().setDragNode(cell);
            }
        }
    }

    /*    protected void registerMouseDragged(TreeCell cell) {
        if (getDragType().equals(DragType.DRAG_AND_DROP)) {
        } else {
        }
    }
     */
    protected void registerDragDone(TreeCell cell) {
        cell.setOnDragDone(ev -> {
            dragIndicator.hideDrawShapes();
            ev.consume();
        });
    }

    protected void registerDragDropped(TreeCell cell) {
        cell.setOnDragDropped((DragEvent ev) -> {
            TreeItemEx targetItem = getTargetTreeItem(ev, (TreeItemEx) ((TreeCell) ev.getGestureTarget()).getTreeItem());
            //
            // Try transfer data to the place
            //
            if (targetItem != null && !ev.isDropCompleted()) {
                //ItemValue targetValue = targetItem.getValue();
                TreeItem place = ((TreeCell) ev.getGestureTarget()).getTreeItem();
                new TreeItemBuilder().accept(treeView, (TreeItemEx) targetItem, (TreeItemEx) place, (Node) ev.getGestureSource());

                ev.setDropCompleted(true);
            } else {
                ev.setDropCompleted(false);
            }
            ev.consume();
        });
    }

    protected TreeItemEx getTargetTreeItem(DragEvent ev, TreeItemEx item) {
        return dragIndicator.getTargetTreeItem(ev.getScreenX(), ev.getScreenY(), item);
    }

    @Override
    protected double computePrefHeight(double h) {
        return contentPane.computePrefHeight(h);
    }

    @Override
    protected double computePrefWidth(double w) {
        return contentPane.computePrefWidth(w);
    }

    @Override
    protected double computeMinHeight(double h) {
        return contentPane.computeMinHeight(h);
    }

    @Override
    protected double computeMinWidth(double w) {
        return contentPane.computeMinWidth(w);
    }

    @Override
    protected double computeMaxHeight(double h) {
        return contentPane.computeMaxHeight(h);
    }

    @Override
    protected double computeMaxWidth(double w) {
        return contentPane.computeMaxWidth(w);
    }

    @Override
    public Node target() {
        return this;
    }

    @Override
    public TargetContext getTargetContext() {
        if (targetContext == null) {
            targetContext = new SceneGraphViewTargetContext(this);
        }
        return targetContext;
    }

    public static class TreeItemCellDragEventHandler extends DragEventHandler {

        public TreeItemCellDragEventHandler(SceneGraphView editor, TreeCell targetCell) {
            super(editor, targetCell);
        }

        @Override
        public void handle(DragEvent ev) {

            ((TreeViewEx) getSceneGraphView().getTreeView()).notifyDragEvent(ev);

            if (ev.getEventType() == DragEvent.DRAG_OVER) {
                //System.err.println("TreeViewEx by Point = " + getSceneGraphView().getTreeView(ev.getScreenX(), ev.getScreenY()));                
                ((TreeViewEx) getSceneGraphView().getTreeView()).notifyDragAccepted(false);
                TreeView tv = getSceneGraphView().getTreeView();
                getSceneGraphView().getDragIndicator().hideDrawShapes();
                if (!isAdmissiblePosition(ev)) {
                    ev.consume();
                } else {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    ((TreeViewEx) getSceneGraphView().getTreeView()).notifyDragEvent(ev);
                    ((TreeViewEx) getSceneGraphView().getTreeView()).notifyDragAccepted(true);
                    drawIndicator(ev);
                    ev.consume();
                }
            }
        }

    }//MouseEventHandler

    public static class TreeViewDragEventHandler extends DragEventHandler {

        public TreeViewDragEventHandler(SceneGraphView editor) {
            super(editor, null);
        }

        @Override
        public TreeItemEx getTreeCellItem() {
            int sz = getSceneGraphView().getTreeView().getExpandedItemCount();
            return (TreeItemEx) getSceneGraphView().getTreeView().getTreeItem(sz - 1);
        }

        @Override
        protected TreeItemEx getTargetTreeItem(DragEvent ev) { //, TreeItem place) {
            return (TreeItemEx) getSceneGraphView().getTreeView().getRoot();
        }

        @Override
        public void handle(DragEvent ev) {
            getSceneGraphView().getDragIndicator().hideDrawShapes();
            VirtualScrollBar sb = ((TreeViewEx) getSceneGraphView().getTreeView()).getVScrollBar();
            if (sb != null) {
                Bounds sbBounds = sb.localToScreen(sb.getBoundsInLocal());
                if (sbBounds != null && sbBounds.contains(ev.getScreenX(), ev.getScreenY())) {
                    return;
                }
            }
            if (ev.getEventType() == DragEvent.DRAG_OVER) {
//                System.err.println("TreeViewDragEventHandler DRAG OVER");
                if (isAdmissiblePosition(ev)) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    drawIndicator(ev);
                }
                ev.consume();
            } else if (ev.getEventType() == DragEvent.DRAG_DROPPED) {
//                System.err.println("TreeViewDragEventHandler DRAG DROPPED");                
                getSceneGraphView().getDragIndicator().hideDrawShapes();
                TreeItemEx targetItem = (TreeItemEx) getSceneGraphView().getTreeView().getRoot();
                //ItemValue targetValue = targetItem.getValue();
                //
                // Transfer the data to the place
                //
                if (isAdmissiblePosition(ev)) {
                    TreeItem place = getTreeCellItem();
                    new TreeItemBuilder().accept(getSceneGraphView().getTreeView(), (TreeItemEx) targetItem, (TreeItemEx) place, (Node) ev.getGestureSource());

                    ev.setDropCompleted(true);

                } else {
                    ev.setDropCompleted(false);
                }

            } else if (ev.getEventType() == DragEvent.DRAG_DONE) {
//                System.err.println("TreeViewDragEventHandler DRAG DONE");                
                getSceneGraphView().getDragIndicator().hideDrawShapes();
            }
            ev.consume();
        }
    }//TreeViewDragEventHandler

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SceneGraphViewNodeSkin(this);
    }

    public static class SceneGraphViewNodeSkin extends SkinBase<SceneGraphView> {

        public SceneGraphViewNodeSkin(SceneGraphView control) {
            super(control);
            if (!getChildren().isEmpty()) {
                getChildren().clear();
            }
            getChildren().add(control.contentPane);
            /*            if (control.getTitleBar() != null && ! control.getDelegate().getChildren().contains(control.getTitleBar())) {
                control.getDelegate().getChildren().add(control.getTitleBar());
            }
             */
            //control.contentPane.getChildren().add(control.getContent());
        }

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

}// SceneGraphView
