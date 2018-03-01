package org.vns.javafx.dock.api.designer;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.dragging.DragType;
import org.vns.javafx.dock.api.DockLayout;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "root")
public class SceneGraphView extends Control implements DockLayout {

    private SceneGraphViewTargetContext targetContext;

    private DragType dragType = DragType.SIMPLE;

    public static final int LAST = 0;
    public static final int FIRST = 2;

    public static double ANCHOR_OFFSET = 4;

    private final TreeViewEx treeView;

    private ObjectProperty<Node> root = new SimpleObjectProperty<>();

    //private final Pane treeViewPane = new StackPane();
    //
    // ContentPane is a subclass of VBox
    // 
    //private DragIndicator dragIndicator;
    private ObjectProperty<Node> statusBar = new SimpleObjectProperty<>();

    private final ObservableList<TreeCell> visibleCells = FXCollections.observableArrayList();

    public SceneGraphView() {
        this.treeView = new TreeViewEx<>(this);
        init();
    }

    public SceneGraphView(Node rootNode) {
        this.treeView = new TreeViewEx<>(this);
        root.set(rootNode);
        init();
    }

    private void init() {
        customizeCell();
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

    public ObjectProperty<Node> rootProperty() {
        return root;
    }

    public Node getRoot() {
        return root.get();
    }

    public void setRoot(Node rootNode) {
        this.root.set(rootNode);
    }

    public ObjectProperty<Node> statusParProperty() {
        return statusBar;
    }

    public Node getStatusBar() {
        return statusBar.get();
    }

    public void setStatusBar(Region statusBar) {
        this.statusBar.set(statusBar);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public TreeViewEx getTreeView() {
        return treeView;
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
                        /*                        if (this.getUserData() != null) {
                            Object[] o = (Object[]) this.getUserData();
                            if (o[0] != null) {
                                this.removeEventHandler(DragEvent.DRAG_OVER, (TreeItemCellDragEventHandler) o[0]);
                            }
                        }
                        this.setOnDragDetected(null);
                        this.setOnDragDropped(null);
                        this.setOnDragDone(null);
                        DockRegistry.getInstance().unregisterDockable(this);
                        getVisibleCells().remove(this);
                         */
                        getVisibleCells().remove(this);
                    } else {
                        this.setGraphic(((TreeItemEx) this.getTreeItem()).getCellGraphic());
                        if (value != null && (value instanceof Node)) {
                            setId(((Node) value).getId());
                        }
                        /*                        TreeItemCellDragEventHandler h = new TreeItemCellDragEventHandler(SceneGraphView.this, this);

                        this.addEventHandler(DragEvent.ANY, h);
                        this.setUserData(new Object[]{h, null});

                        registerDragDetected(this);
                        //registerMouseDragged(this);

                        registerDragDropped(this);
                        registerDragDone(this);
                         */
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
//        System.err.println("visCells = " + getVisibleCells().size());
//        System.err.println("x = " + x + "; y = " + y );
        for (TreeCell cell : getVisibleCells()) {
            
//            System.err.println("cellX = " + cell.localToScreen(0, 0));
            if (DockUtil.contains(cell, x, y)) {
                retval = (TreeItemEx) cell.getTreeItem();
//                System.err.println("retval = " + retval);
                break;
            }
        }
        return retval;
    }

    public TreeItemEx getTreeItem(Point2D p) {
        return getTreeItem(p.getX(), p.getY());
    }

    /*    protected void registerDragDetected(TreeCell cell) {
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
                DockRegistry.makeDockable(cell);
                Dockable dockable = DockRegistry.dockable(cell);
                dockable.getContext().setTargetContext(getLayoutContext());
            }
        }
    }

    protected void registerDragDone(TreeCell cell) {
        cell.setOnDragDone(ev -> {
//            dragIndicator.hideDrawShapes();
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
     */

 /*    @Override
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
     */
    @Override
    public Node layoutNode() {
        return this;
    }

    @Override
    public LayoutContext getLayoutContext() {
        if (targetContext == null) {
            targetContext = new SceneGraphViewTargetContext(this);
        }
        return targetContext;
    }

    /*    public static class TreeItemCellDragEventHandler extends DragEventHandler {

        public TreeItemCellDragEventHandler(SceneGraphView editor, TreeCell targetCell) {
            super(editor, targetCell);
        }

        @Override
        public void handle(DragEvent ev) {

            ((TreeViewEx) getSceneGraphView().getTreeView()).notifyDragEvent(ev);

            if (ev.getEventType() == DragEvent.DRAG_OVER) {
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
                if (isAdmissiblePosition(ev)) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    drawIndicator(ev);
                }
                ev.consume();
            } else if (ev.getEventType() == DragEvent.DRAG_DROPPED) {
                getSceneGraphView().getDragIndicator().hideDrawShapes();
                TreeItemEx targetItem = (TreeItemEx) getSceneGraphView().getTreeView().getRoot();
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
                getSceneGraphView().getDragIndicator().hideDrawShapes();
            }
            ev.consume();
        }
    }//TreeViewDragEventHandler
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new SceneGraphViewSkin(this);
    }

}// SceneGraphView
