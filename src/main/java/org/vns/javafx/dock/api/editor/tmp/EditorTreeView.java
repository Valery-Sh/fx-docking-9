package org.vns.javafx.dock.api.editor.tmp;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import static org.vns.javafx.dock.api.demo.TestTreeView.MousePosInfo.screenTreeViewBounds;
import static org.vns.javafx.dock.api.editor.tmp.TreeItemBuilder.CELL_UUID;
import static org.vns.javafx.dock.api.editor.tmp.TreeItemBuilder.NODE_UUID;

/**
 *
 * @author Valery
 */
public class EditorTreeView {

    private TreeViewDragEventHandler mouseDragHandler;

    public static double ANCHOR_OFFSET = 4;

    private final TreeView<AnchorPane> treeView;
    private Pane rootPane = new Pane();
    private Line vertLine = new Line();
    private Line horLine = new Line();
    private Rectangle itemRect = new Rectangle();

    public EditorTreeView() {
        this.treeView = new TreeView<>();
        init();
    }

    public TreeItem createItem(Node node) {
        TreeItem item = null;
        if (TreeItemRegistry.getInstance().exists(node)) {
            item = TreeItemRegistry.getInstance().getBuilder(node).build(node);
        }
        return item;
    }

    public TreeView<AnchorPane> getTreeView() {
        return treeView;
    }

    private void init() {
        customizeCell();
        mouseDragHandler = new TreeViewDragEventHandler(this);
        treeView.addEventHandler(DragEvent.ANY, mouseDragHandler);
        rootPane.getChildren().addAll(horLine, vertLine, itemRect);
        rootPane.setStyle("-fx-background-color: green; -fx-padding: 0 3 0 3; -fx-insets: 0; -fx-border-width: 0");
        vertLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");
        horLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");
        //itemRect.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2; -fx-background-color: transparent;-fx-fill: transparent");
        itemRect.setStyle("-fx-stroke: red;-fx-stroke-width: 2; -fx-background-color: transparent;-fx-fill: transparent");
        vertLine.setMouseTransparent(true);
        horLine.setMouseTransparent(true);
        itemRect.setMouseTransparent(true);

    }

    public TreeCell getCell(TreeItem<AnchorPane> item) {
        return (TreeCell) item.getValue().getProperties().get("cell");
    }

    protected void customizeCell() {
        treeView.setCellFactory((TreeView<AnchorPane> tv) -> {
            TreeCell<AnchorPane> cell = new TreeCell<AnchorPane>() {
                @Override
                public void updateItem(AnchorPane item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        this.setGraphic(item);

                        TreeItemEx treeItem = ((TreeItemEx) this.getTreeItem());
                        if (treeItem != null && treeItem.getCell() != null) {
                            TreeItemCellDragEventHandler h = new TreeItemCellDragEventHandler(EditorTreeView.this, (TreeItemEx) this.getTreeItem());
                            this.addEventHandler(DragEvent.ANY, h);
                            treeItem.setEventHandler(h);
                        } 
                        if (treeItem != null) {
                            ((TreeItemEx) this.getTreeItem()).setCell(this);
                            registerDragDetected((TreeItemEx) this.getTreeItem());
                        }
                    }

                }
            };
            return cell;
        });
    }

    protected void registerDragDetected(TreeItemEx treeItem) {
        treeItem.getCell().setOnDragDetected(ev -> {
            Dragboard dragboard = treeItem.getCell().startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putUrl(CELL_UUID);
            dragboard.setContent(content);
            ev.consume();
        });
    }

    public Pane getRootPane() {
        return rootPane;
    }

    protected void drawLines(double x, double y) {

        Bounds tvBounds = EditorUtil.screenTreeViewBounds(treeView);
        if (!tvBounds.contains(x, y)) {
            return;
        }
        TreeItem treeItem = null;

        for (int i = 0; i < treeView.getExpandedItemCount(); i++) {
            TreeItem<AnchorPane> item = treeView.getTreeItem(i);
            AnchorPane ap = item.getValue();
            Bounds apBnd = ap.localToScreen(ap.getBoundsInLocal());
            Bounds bnd = new BoundingBox(tvBounds.getMinX(), apBnd.getMinY(), tvBounds.getWidth(), apBnd.getHeight());
            if (bnd.contains(x, y)) {
                treeItem = item;
                break;
            }
        }
        if (treeItem == null && treeView.getExpandedItemCount() == 1) {
            treeItem = treeView.getTreeItem(treeView.getExpandedItemCount() - 1);
            drawRectangle(treeItem);
            return;
        } else if (treeItem == null) {
            treeItem = treeView.getTreeItem(treeView.getExpandedItemCount() - 1);
            drawLines(treeView.getRoot(), treeItem);
        }
    }

    public TreeItem getTreeItem(double x, double y) {
        Bounds tvBnd = screenTreeViewBounds(treeView);

        if (!tvBnd.contains(x, y)) {
            return null;
        }

        int i = 0;
        TreeItem<AnchorPane> retval = null;

        while (treeView.getTreeItem(i) != null) {
            TreeItem<AnchorPane> item = treeView.getTreeItem(i);
            AnchorPane ap = item.getValue();
            Bounds apBnd = ap.localToScreen(ap.getBoundsInLocal());
            Bounds bnd = new BoundingBox(tvBnd.getMinX(), apBnd.getMinY(), tvBnd.getWidth(), apBnd.getHeight());
            if (bnd.contains(x, y)) {
                retval = item;
                break;
            }
            i++;
        }// while
        return retval;

    }

    public void drawRectangle(TreeItem item) {
        Bounds lb = EditorUtil.screeTreeItemBounds(item);
        lb = rootPane.screenToLocal(lb);
        itemRect.setX(lb.getMinX());
        itemRect.setY(lb.getMinY());
        itemRect.setWidth(lb.getWidth());
        itemRect.setHeight(lb.getHeight());
        System.err.println("DRAW RECT");
        itemRect.toFront();
    }

    protected void drawLines(TreeItem from, TreeItem to) {

    }

    public void treeItemDragOver(DragEvent ev, TreeItemEx item) {
        System.err.println("treeItemDragOver");
        if (item != null) {
            itemRect.setVisible(true);
            drawRectangle(item);
        } else {
            itemRect.setVisible(false);
            itemRect.toBack();
        }
    }

    public static class TreeViewDragEventHandler implements EventHandler<DragEvent> {

        private EditorTreeView editor;
        private Point2D point = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);

        public TreeViewDragEventHandler(EditorTreeView editor) {
            this.editor = editor;
        }

        @Override
        public void handle(DragEvent ev) {
            if (ev.getEventType() == DragEvent.DRAG_OVER) {

                Dragboard dragboard = ev.getDragboard();
                if (dragboard.hasUrl()) {
//                if (dragboard.hasUrl() && dragboard.getUrl().equals(NODE_UUID) || dragboard.getUrl().equals(CELL_UUID)) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(point)) {
                        System.err.println("TreeTreeView DRAG_OVER");
                        point = pt;
                        TreeItemEx item = (TreeItemEx) editor.getTreeItem(ev.getScreenX(), ev.getScreenY());
                        System.err.println("TreeTreeView DRAG_OVER item=" + item);
                        if (item != null) {
                            editor.itemRect.setVisible(true);
                            editor.drawRectangle(item);
                        } else {
                            editor.itemRect.setVisible(false);
                            editor.itemRect.toBack();
                        }

                    } else {

                    }

                }
//                ev.consume();
            } else if (ev.getEventType() == DragEvent.DRAG_ENTERED_TARGET) {
                //ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
//                ev.consume();
            } else {
//                ev.consume();
            }
        }

    }//MouseEventHandler

    public static class TreeItemCellDragEventHandler implements EventHandler<DragEvent> {

        private final EditorTreeView editor;
        private final TreeItemEx targetTreeItem;

        private Point2D point = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);

        public TreeItemCellDragEventHandler(EditorTreeView editor, TreeItemEx targetTreeItem) {
            this.editor = editor;
            this.targetTreeItem = targetTreeItem;
        }

        public Object getDragTarget(DragEvent ev) {
/*            TreeItem item = editor.getTreeItem(ev.getScreenX(), ev.getScreenY());
            System.err.println("item == treeItemTarget = " + (item == targetTreeItem));
            //return ((TreeItemEx)item).getNode();
            ((TreeItemEx) item).getNode();
            System.err.println("item.node == treeItemTarget.node = " + (((TreeItemEx) item).getNode() == targetTreeItem.getNode()));
            //return ((TreeItemEx)item).getNode();
*/            
            return targetTreeItem.getNode();
        }

        protected Object getDragSource(DragEvent ev) {
            Object o = ev.getGestureSource();
            if (o == null) {
                return null;
            }
            if (!(o instanceof TreeCell)) {
                return o;
            }
            Object retval = null;
            TreeItem item = ((TreeCell) o).getTreeItem();
            if (item instanceof TreeItemEx) {
                retval = ((TreeItemEx) item).getNode();
            }
            return retval;
        }

        @Override
        public void handle(DragEvent ev) {
            if (ev.getEventType() == DragEvent.DRAG_OVER) {

                Dragboard dragboard = ev.getDragboard();
                Object dragSource = getDragSource(ev);
                //    System.err.println("dragSource = " + dragSource);
                //    System.err.println("dragTarget = " + dragTarget);
                //TreeItemBuilder builder = getDragTarget();
                //builder = TreeItemRegistry.getInstance().getBuilder(targetTreeItem.getNode());
                TreeItemBuilder builder = TreeItemRegistry.getInstance().getBuilder(getDragTarget(ev));
//                System.err.println("BUILDER = " + builder);
                //System.err.println("isDragTarget() = " + builder.isDragTarget());
                //System.err.println("isAcceptable() = " + builder.isAcceptable(dragSource));                
//                if (dragboard.hasUrl() && dragboard.getUrl().equals(NODE_UUID) || dragboard.getUrl().equals(CELL_UUID) ) {

                if (dragboard.hasUrl() && dragboard.getUrl().equals(NODE_UUID) || dragboard.getUrl().equals(CELL_UUID)
                        && builder != null
                        && builder.isDragTarget()
                        && builder.isAcceptable(dragSource)) {
//                    System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");

                    //&& TreeItemRegistry.getInstance().getBuilder(ev.getGestureSource()).isAcceptable(ev.getGestureSource()) ) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(point)) {

                        /*                        System.err.println("TreeItemBuilder DRAG_OVER ev.getGestureSource() " + ev.getGestureSource() );
                        point = pt;
                        //TreeItemEx item = (TreeItemEx) editor.getTreeItem(ev.getScreenX(), ev.getScreenY());
                        System.err.println("ev.getGestureTarget() = " + ev.getGestureTarget());
                        System.err.println("ev.getTarget() = " + ev.getTarget());
                        System.err.println("ev.getAcceptingObject() = " + ev.getAcceptingObject());
                        System.err.println("-------------------------------------------------------------");
                         */
                        if (ev.getAcceptingObject() != null && (ev.getAcceptingObject() instanceof TreeCell)) {
                            TreeCell cell = (TreeCell) ev.getAcceptingObject();
                            if (cell != null) {
                                editor.treeItemDragOver(ev, (TreeItemEx) cell.getTreeItem());
                            }
                        }
                    }
                }
            }
            ev.consume();
        }
    }//MouseEventHandler

}
