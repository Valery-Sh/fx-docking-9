package org.vns.javafx.dock.api.editor.tmp;

import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
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

    protected void customizeCell() {
        treeView.setCellFactory((TreeView<AnchorPane> tv) -> {
            TreeCell<AnchorPane> cell;
            cell = new TreeCell<AnchorPane>() {
                @Override
                public void updateItem(AnchorPane item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        this.setGraphic(item);

                        TreeItemCellDragEventHandler h = new TreeItemCellDragEventHandler(EditorTreeView.this, this);
                        this.addEventHandler(DragEvent.ANY, h);
                        registerDragDetected(this);
                        registerDragDropped(this);
                        registerDragDone(this);
                    }

                }
            };
            return cell;
        });
    }

    protected void registerDragDetected(TreeCell cell) {
        cell.setOnDragDetected(ev -> {
            Dragboard dragboard = cell.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putUrl(CELL_UUID);
            dragboard.setContent(content);
            treeView.getSelectionModel().clearSelection();
            ev.consume();
        });
    }

    protected void registerDragDone(TreeCell cell) {
        cell.setOnDragDone(ev -> {
            hideDrawShapes();
            ev.consume();
        });
    }

    protected void registerDragDropped(TreeCell cell) {
        cell.setOnDragDropped((DragEvent e) -> {
            // Transfer the data to the target
            Dragboard dragboard = e.getDragboard();
            if (dragboard.hasString()) {
                e.setDropCompleted(true);
            } else {
                e.setDropCompleted(false);
            }

            e.consume();
        }
        );
    }

    public Pane getRootPane() {
        return rootPane;
    }

    public void hideDrawShapes() {
        itemRect.setVisible(false);
        vertLine.setVisible(false);
        horLine.setVisible(false);
        itemRect.toBack();
        vertLine.toBack();
        horLine.toBack();

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
        hideDrawShapes();
        Bounds lb = EditorUtil.screenTreeItemBounds(item);
        lb = rootPane.screenToLocal(lb);
        itemRect.setX(lb.getMinX());
        itemRect.setY(lb.getMinY());
        itemRect.setWidth(lb.getWidth());
        itemRect.setHeight(lb.getHeight());
        itemRect.toFront();
        itemRect.setVisible(true);
    }

    protected void drawLines(TreeItem from, TreeItem to) {
        //TreeItem from = treeView.getTreeItem(1);
        //TreeItem to = treeView.getTreeItem(2);

        treeView.setPadding(Insets.EMPTY);
        Insets pins = treeView.getPadding();

        AnchorPane ap = (AnchorPane) from.getValue();

        Pane p = getRootPane();
        Bounds bnd = EditorUtil.screenNonValueLevelBounds(treeView, from);
        int level = treeView.getTreeItemLevel(from);

        double gap = EditorUtil.getRootStartGap(treeView);

        Bounds arrowBnd = EditorUtil.screenArrowBounds(from);
        double startY = bnd.getMinY() + bnd.getHeight() + pins.getBottom();
        if (arrowBnd.getHeight() != 0) {
            startY = arrowBnd.getMinY() + arrowBnd.getHeight();
        }

        Bounds rootBounds = EditorUtil.screenNonValueLevelBounds(treeView, treeView.getRoot());

        double startX = rootBounds.getMinX() + rootBounds.getWidth() + gap * level;

        if (arrowBnd.getWidth() != 0) {
            startX = arrowBnd.getMinX() + arrowBnd.getWidth() / 2;
        }

        vertLine.setStartX(p.screenToLocal(startX, startY).getX());
        vertLine.setStartY(p.screenToLocal(startX, startY).getY());

        vertLine.setEndX(vertLine.getStartX());

        hideDrawShapes();
        vertLine.toFront();
        vertLine.setVisible(true);
        //
        // --- Horizontal line ----------
        //
        Bounds lb = EditorUtil.screenTreeItemBounds(to);
        lb = rootPane.screenToLocal(lb);
        horLine.setStartX(lb.getMinX());
        horLine.setStartY(lb.getMinY() + lb.getHeight());
        horLine.setEndY(horLine.getStartY());
        horLine.setEndX(horLine.getStartX() + lb.getWidth());

        vertLine.setEndY(horLine.getStartY());

        horLine.toFront();
        horLine.setVisible(true);
    }
    private static final double LEVEL_SPACE = 10;

    public Bounds[] getLevelBounds(TreeItem item) {
        int level = treeView.getTreeItemLevel(item);
        Bounds[] bounds = new Bounds[level + 3];
        TreeItem rootItem = treeView.getRoot();
        //Bounds rootBounds = EditorUtil.screenNonValueBounds(treeView, rootItem);
        Bounds rootBounds = EditorUtil.screenNonValueBounds(rootItem);
        //System.err.println("rootBounds=" + rootBounds);
        //Bounds itemBounds = EditorUtil.screenNonValueBounds(treeView, item);        
        Bounds itemBounds = EditorUtil.screenNonValueBounds(item);
        Bounds valueBounds = EditorUtil.screenValueBounds(item);
        double xOffset;// = 0;
        double width;// = 0;
        double cellOffset = EditorUtil.getRootStartGap(treeView);

        for (int i = 0; i <= level; i++) {
            if (i == 0) {
                xOffset = 0;
                width = rootBounds.getWidth() / 2;
            } else {
                xOffset = rootBounds.getWidth() / 2 + cellOffset * (i - 1);
                width = cellOffset;
            }
            bounds[i] = new BoundingBox(itemBounds.getMinX() + xOffset, itemBounds.getMinY() + itemBounds.getHeight() - LEVEL_SPACE, width, LEVEL_SPACE);
            if (i == level && level > 0) {
                bounds[i + 1] = new BoundingBox(itemBounds.getMinX() + xOffset, itemBounds.getMinY() + itemBounds.getHeight() - LEVEL_SPACE, itemBounds.getWidth() - cellOffset, LEVEL_SPACE);
                bounds[i + 2] = new BoundingBox(valueBounds.getMinX(), itemBounds.getMinY() + itemBounds.getHeight() - ANCHOR_OFFSET, valueBounds.getWidth(), ANCHOR_OFFSET + 1);
            } else if (i == level && level == 0) {
                bounds[i + 1] = new BoundingBox(itemBounds.getMinX() + width, itemBounds.getMinY() + itemBounds.getHeight() - LEVEL_SPACE, width + cellOffset, LEVEL_SPACE);
                bounds[i + 2] = new BoundingBox(valueBounds.getMinX(), itemBounds.getMinY() + itemBounds.getHeight() - ANCHOR_OFFSET, valueBounds.getWidth(), ANCHOR_OFFSET + 1);
            }
        }
        return bounds;
    }

    public void treeItemDragOver(DragEvent ev, TreeItemEx item) {
        hideDrawShapes();
        if (item != null) {
            Bounds[] bounds = getLevelBounds(item);
            int n = -1;
            System.err.println("========================== bounds.len=" + bounds.length);
            System.err.println("screenX=" + ev.getScreenX() + "; ev.getScreenY=" + ev.getScreenY());
            System.err.println("--------------------------");
            for (int i = 0; i < bounds.length; i++) {
                System.err.println("i=" + i + ") " + bounds[i]);
                if (bounds[i].contains(ev.getScreenX(), ev.getScreenY())) {
                    n = i;
                    break;
                }
            }
            System.err.println("n = " + n);
            System.err.println("==========================");
            int level = treeView.getTreeItemLevel(item);
            if (n < 0) {
                itemRect.setVisible(true);
                drawRectangle(item);
            } else {
                if (n == level || n == level + 1 || n == level + 2) {
                    drawLines(item, item);
                } else {
                    TreeItem it = item;
                    while (true) {
                        if (treeView.getTreeItemLevel(it) == n) {
                            break;
                        }
                        it = it.getParent();
                    }
                    if ((item.getParent() == it
                            || item.getParent().getChildren().indexOf(item) == item.getParent().getChildren().size() - 1)
                            && item.getChildren().isEmpty()) {
                        drawLines(it, item);
                        System.err.println("LAST LINE");
                    }
                }
            }

        }
    }

    public void drawLines(DragEvent ev, TreeItemEx item) {
        hideDrawShapes();
        if (item == null) {
            return;
        }
        Bounds[] bounds = getLevelBounds(item);
        int n = -1;
        System.err.println("========================== bounds.len=" + bounds.length);
        System.err.println("screenX=" + ev.getScreenX() + "; ev.getScreenY=" + ev.getScreenY());
        System.err.println("--------------------------");
        for (int i = 0; i < bounds.length; i++) {
            System.err.println("i=" + i + ") " + bounds[i]);
            if (bounds[i].contains(ev.getScreenX(), ev.getScreenY())) {
                n = i;
                break;
            }
        }
        System.err.println("n = " + n);
        System.err.println("==========================");
        int level = treeView.getTreeItemLevel(item);
        if (n >= 0) {
            if (n == level || n == level + 1 || n == level + 2) {
                drawLines(item, item);
            } else {
                TreeItem it = item;
                while (true) {
                    if (treeView.getTreeItemLevel(it) == n) {
                        break;
                    }
                    it = it.getParent();
                }
                if ((item.getParent() == it
                        || item.getParent().getChildren().indexOf(item) == item.getParent().getChildren().size() - 1)
                        && item.getChildren().isEmpty()) {
                    drawLines(it, item);
                    System.err.println("LAST LINE");
                }
            }
        }
    }

    public boolean canDrawLines(DragEvent ev) {
        boolean retval = false;

        TreeItem item = getTreeItem(ev.getScreenX(), ev.getScreenY());
        if (item == null || !(item instanceof TreeItemEx)) {
            return false;
        }

        Bounds[] bounds = getLevelBounds(item);
        int n = -1;
        for (int i = 0; i < bounds.length; i++) {
            if (bounds[i].contains(ev.getScreenX(), ev.getScreenY())) {
                n = i;
                break;
            }
        }
        int level = treeView.getTreeItemLevel(item);
        if (n >= 0) {
            if (n == level || n == level + 1 || n == level + 2) {
                retval = true;
            } else {
                TreeItem it = item;
                while (true) {
                    if (treeView.getTreeItemLevel(it) == n) {
                        break;
                    }
                    it = it.getParent();
                }
                if ((item.getParent() == it
                        || item.getParent().getChildren().indexOf(item) == item.getParent().getChildren().size() - 1)
                        && item.getChildren().isEmpty()) {
                    retval = true;
                }
            }
        }
        return retval;
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
                if (dragboard.hasUrl()
                        && (dragboard.getUrl().equals(NODE_UUID)
                        || dragboard.getUrl().equals(CELL_UUID))) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(point)) {
//                        System.err.println("TreeTreeView DRAG_OVER");
                        point = pt;
                        TreeItem item = editor.getTreeItem(ev.getScreenX(), ev.getScreenY());
//                        System.err.println("TreeTreeView DRAG_OVER item=" + item);
                        int n = editor.getTreeView().getExpandedItemCount();
                        if (n > 0) {
                            item = (TreeItemEx) editor.getTreeView().getTreeItem(n - 1); // last item
                            TreeItem rootItem = editor.getTreeView().getRoot();
                            editor.drawLines(rootItem, item);
                        }
                    }

                }
            } else if (ev.getEventType() == DragEvent.DRAG_DROPPED) {
                Dragboard dragboard = ev.getDragboard();
                if (dragboard.hasString()) {
                    ev.setDropCompleted(true);
                } else {
                    ev.setDropCompleted(false);
                }
                ev.consume();

            } else if (ev.getEventType() == DragEvent.DRAG_DONE) {
                // Check how the data transfer happened. If it was moved, clear the text in the source.
                TransferMode modeUsed = ev.getTransferMode();
                if (modeUsed == TransferMode.MOVE) {
                    //sourceFld.setText("");
                }
                editor.hideDrawShapes();
                ev.consume();
            }
        }
    }//TreeViewDragEventHandler

    public static class TreeItemCellDragEventHandler implements EventHandler<DragEvent> {

        private final EditorTreeView editor;
        //private  TreeItemEx targetTreeItem;
        private final TreeCell targetCell;

        private Point2D point = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);

        public TreeItemCellDragEventHandler(EditorTreeView editor, TreeCell targetCell) {
            this.editor = editor;
            this.targetCell = targetCell;
        }

        public Object getDragTarget(DragEvent ev) {
            return ((TreeItemEx) targetCell.getTreeItem()).getNode();
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
                TreeItemBuilder builder = TreeItemRegistry.getInstance().getBuilder(getDragTarget(ev));
                editor.hideDrawShapes();
                if ((dragboard.hasUrl() && dragboard.getUrl().equals(NODE_UUID) || dragboard.getUrl().equals(CELL_UUID))
                        && builder != null
                        && builder.isDragTarget()
                        && builder.isAcceptable(dragSource)) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);

                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(point)) {
                        if (ev.getAcceptingObject() != null && (ev.getAcceptingObject() instanceof TreeCell)) {
                            TreeCell cell = (TreeCell) ev.getAcceptingObject();
                            if (cell != null) {
                                editor.treeItemDragOver(ev, (TreeItemEx) cell.getTreeItem());
                            }
                        }
                    }
                } else if (dragboard.hasUrl()
                        && (dragboard.getUrl().equals(NODE_UUID)
                        || dragboard.getUrl().equals(CELL_UUID))
                        && builder != null
                        && editor.canDrawLines(ev)) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(point)) {
                        if (ev.getAcceptingObject() != null && (ev.getAcceptingObject() instanceof TreeCell)) {
                            TreeCell cell = (TreeCell) ev.getAcceptingObject();
                            if (cell != null) {
                                editor.drawLines(ev, (TreeItemEx) cell.getTreeItem());
                            }
                        }
                    }
                }
            }
            ev.consume();
        }
    }//MouseEventHandler

}
