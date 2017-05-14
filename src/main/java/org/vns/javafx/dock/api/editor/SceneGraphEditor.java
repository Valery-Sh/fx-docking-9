package org.vns.javafx.dock.api.editor;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import static org.vns.javafx.dock.api.editor.tmp.TreeItemBuilder.CELL_UUID;
import static org.vns.javafx.dock.api.editor.tmp.TreeItemBuilder.NODE_UUID;

/**
 *
 * @author Valery
 */
public class SceneGraphEditor {

    private static final double LEVEL_SPACE = 10;

    public static final int LAST = 0;
    public static final int FIRST = 2;

    private TreeViewDragEventHandler mouseDragHandler;

    public static double ANCHOR_OFFSET = 4;

    private final TreeView<ItemValue> treeView;
    private final Pane editorPane = new Pane();
    private final Line vertLine = new Line();
    private final Line horLine = new Line();
    private final Rectangle itemRect = new Rectangle();
    private final Node rootNode;

    public SceneGraphEditor(Node rootNode) {
        this.treeView = new TreeView<>();
        this.rootNode = rootNode;
        init();
    }

    private void init() {
        getEditorPane().getChildren().add(treeView);
        addStageListeners();
        customizeCell();
        mouseDragHandler = new TreeViewDragEventHandler(this);
        treeView.addEventHandler(DragEvent.ANY, mouseDragHandler);
        
        editorPane.getChildren().addAll(horLine, vertLine, itemRect);
        editorPane.setStyle("-fx-background-color: green; -fx-padding: 0 3 0 3; -fx-insets: 0; -fx-border-width: 0");
        vertLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");
        horLine.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2");
        //itemRect.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2; -fx-background-color: transparent;-fx-fill: transparent");
        itemRect.setStyle("-fx-stroke: RGB(255,148,40);-fx-stroke-width: 2; -fx-background-color: transparent;-fx-fill: transparent");
        vertLine.setMouseTransparent(true);
        horLine.setMouseTransparent(true);
        itemRect.setMouseTransparent(true);
        
       if (treeView.getScene() != null && treeView.getScene().getWindow() != null && treeView.getScene().getWindow().isShowing() ) {
                windowShown(null);        
       }
    }

    public Pane getEditorPane() {
        return editorPane;
    }

    public TreeView<ItemValue> getTreeView() {
        return treeView;
    }

    protected TreeItem createItem(Node node) {
        TreeItem item = null;
        if (TreeItemRegistry.getInstance().exists(node)) {
            item = TreeItemRegistry.getInstance().getBuilder(node).build(node);
        }
        return item;
    }

    protected void addStageListeners() {

        if (treeView.getScene() != null && treeView.getScene().getWindow() != null) {
            if (treeView.getScene().getWindow().isShowing()) {
                windowShown(null);
            } else {
                treeView.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_SHOWN, this::windowShown);
            }
        } else if (treeView.getScene() == null) {
            treeView.sceneProperty().addListener(this::sceneChanged);
        } else if (treeView.getScene() != null) {
            treeView.getScene().windowProperty().addListener(this::windowChanged);
        }
    }

    protected void sceneChanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        if (newValue != null) {
            newValue.windowProperty().addListener(this::windowChanged);
        }
    }

    protected void windowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        if (newValue != null) {
            newValue.addEventFilter(WindowEvent.WINDOW_SHOWN, this::windowShown);
        }
    }

    protected void windowShown(WindowEvent ev) {
        TreeItem it = createItem(rootNode);
        it.setExpanded(true);
        treeView.setRoot(it);
    }

    protected void customizeCell() {
        treeView.setCellFactory((TreeView<ItemValue> tv) -> {
            TreeCell<ItemValue> cell = new TreeCell<ItemValue>() {
                @Override
                public void updateItem(ItemValue value, boolean empty) {
                    super.updateItem(value, empty);

                    if (empty || value == null) {
                        setText(null);
                        setGraphic(null);
                        if (this.getUserData() != null) {
                            this.removeEventHandler(DragEvent.ANY, (TreeItemCellDragEventHandler) this.getUserData());
                        }
                        this.setOnDragDetected(null);
                        this.setOnDragDropped(null);
                        this.setOnDragDone(null);
                    } else {
                        this.setGraphic(value.getCellGraphic());
                        TreeItemCellDragEventHandler h = new TreeItemCellDragEventHandler(SceneGraphEditor.this, this);
                        this.addEventHandler(DragEvent.ANY, h);
                        this.setUserData(h);
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
            DragGesture dg = new DragTreeCellGesture(cell);
            cell.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
            ClipboardContent content = new ClipboardContent();
            content.putUrl(CELL_UUID);
            dragboard.setContent(content);
            treeView.getSelectionModel().clearSelection();
            ev.consume();
        });
    }

    protected void registerDragDone(TreeCell cell) {
        cell.setOnDragDone(ev -> {
            ItemValue sourceValue = ((TreeCell<ItemValue>) ev.getGestureSource()).getTreeItem().getValue();
            hideDrawShapes();
            ev.consume();
        });
    }

    protected void registerDragDropped(TreeCell cell) {
        cell.setOnDragDropped((DragEvent ev) -> {
            ItemValue sourceValue = ((TreeCell<ItemValue>) ev.getGestureSource()).getTreeItem().getValue();
            /*TreeItem<ItemValue> targetItem = treeView.getRoot();
            if ( (ev.getGestureTarget() instanceof TreeCell) ) {
                targetItem = getTargetTreeItem(ev, ((TreeCell) ev.getGestureTarget()).getTreeItem());                    
            }
             */
            TreeItem<ItemValue> targetItem = getTargetTreeItem(ev, ((TreeCell) ev.getGestureTarget()).getTreeItem());
            ItemValue targetValue = targetItem.getValue();
            //
            // Transfer the data to the target
            //
            Dragboard dragboard = ev.getDragboard();
            if (dragboard.hasUrl()) {
                TreeItem target = ((TreeCell) ev.getGestureTarget()).getTreeItem();

                TreeItemBuilder tib = targetValue.getBuilder();
                targetValue.getBuilder().accept(treeView, targetItem, target, (Node) ev.getGestureSource());
                ev.setDropCompleted(true);

            } else {
                ev.setDropCompleted(false);
            }

            ev.consume();
        });
    }

    protected TreeItem<ItemValue> findTreeItem(Object sourceGesture) {
        return findTreeItem(getTreeView().getRoot(), sourceGesture);
    }

    protected TreeItem<ItemValue> findTreeItem(TreeItem<ItemValue> item, Object sourceGesture) {
        TreeItem retval = null;
        for (TreeItem<ItemValue> it : item.getChildren()) {
            if (it.getValue().getTreeItemObject() == sourceGesture) {
                retval = it;
                break;
            }
            retval = findTreeItem(it, sourceGesture);
            if (retval != null) {
                break;
            }
        }
        return retval;
    }

    protected void hideDrawShapes() {
        itemRect.setVisible(false);
        vertLine.setVisible(false);
        horLine.setVisible(false);
        itemRect.toBack();
        vertLine.toBack();
        horLine.toBack();

    }

    protected void drawRectangle(TreeItem item) {
        hideDrawShapes();
        Bounds lb = EditorUtil.screenTreeItemBounds(item);
        lb = editorPane.screenToLocal(lb);
        itemRect.setX(lb.getMinX());
        itemRect.setY(lb.getMinY());
        itemRect.setWidth(lb.getWidth());
        itemRect.setHeight(lb.getHeight());
        itemRect.toFront();
        itemRect.setVisible(true);
    }

    protected void drawLines(TreeItem<ItemValue> from, TreeItem<ItemValue> to) {
        treeView.setPadding(Insets.EMPTY);
        Insets pins = treeView.getPadding();

        AnchorPane ap = (AnchorPane) from.getValue().getCellGraphic();

        Pane p = getEditorPane();

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
        lb = editorPane.screenToLocal(lb);
        horLine.setStartX(lb.getMinX());
        horLine.setStartY(lb.getMinY() + lb.getHeight());
        horLine.setEndY(horLine.getStartY());
        horLine.setEndX(horLine.getStartX() + lb.getWidth());

        vertLine.setEndY(horLine.getStartY());

        horLine.toFront();
        horLine.setVisible(true);

    }

    public Bounds[] levelBoundsOf(TreeItem item) {
        int level = treeView.getTreeItemLevel(item);
        Bounds[] bounds = new Bounds[level + 3];
        TreeItem rootItem = treeView.getRoot();
        //Bounds rootBounds = EditorUtil.screenNonValueBounds(treeView, rootItem);

        Bounds rootBounds = EditorUtil.screenNonValueBounds(rootItem);

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
                bounds[i + 2] = new BoundingBox(valueBounds.getMinX(), itemBounds.getMinY() + itemBounds.getHeight() - ANCHOR_OFFSET - 2, valueBounds.getWidth(), ANCHOR_OFFSET + 1);
            } else if (i == level && level == 0) {
                bounds[i + 1] = new BoundingBox(itemBounds.getMinX() + width, itemBounds.getMinY() + itemBounds.getHeight() - LEVEL_SPACE, width + cellOffset, LEVEL_SPACE);
                bounds[i + 2] = new BoundingBox(valueBounds.getMinX(), itemBounds.getMinY() + itemBounds.getHeight() - ANCHOR_OFFSET, valueBounds.getWidth(), ANCHOR_OFFSET + 1);
            }
        }
        return bounds;
    }

    protected TreeItem<ItemValue> getTargetTreeItem(DragEvent ev, TreeItem item) {
        TreeItem<ItemValue> retval = null;
        hideDrawShapes();

        if (item != null) {
            Bounds[] bounds = levelBoundsOf(item);
            int n = -1;
            for (int i = 0; i < bounds.length; i++) {
                if (bounds[i].contains(ev.getScreenX(), ev.getScreenY())) {
                    n = i;
                    break;
                }
            }
            int level = treeView.getTreeItemLevel(item);
            if (n < 0) {
                retval = item;
                ((ItemValue) retval.getValue()).setDragDropQualifier(LAST);
                //drawRectangle(item);
            } else if (item.isLeaf()) {
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
                    //if (level == n || n +1 == level || n+2 == level) {
                    //drawLines(item.getParent(), item);
                    retval = item.getParent();
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        //drawLines(parentAtLevel(item, n), item);
                        retval = parentAtLevel(item, n);
                    } else {
                        //drawLines(item.getParent(), item);
                        retval = item.getParent();
                    }
                }
            } else if (!item.isExpanded()) {
                // not leaf and not expanded     
                if (n == level || n == level + 1 || n == level + 2) {
                    //if ( acceptable ) {
                    //    drawLines(item, item);
                    //}
                } else if (n == level - 1) {
                    //drawLines(item.getParent(), item);
                    retval = item.getParent();
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        //drawLines(parentAtLevel(item, n), item);
                        retval = parentAtLevel(item, n);
                    } else {
                        //drawLines(item.getParent(), item);
                        retval = item.getParent();
                    }
                }
            } else {
                //drawLines(item, item);
                retval = item;
                ((ItemValue) item.getValue()).setDragDropQualifier(FIRST);
            }
        }
        return retval;
    }

    protected TreeItem parentAtLevel(TreeItem item, int level) {
        TreeItem it = item;
        while (it != null) {
            if (treeView.getTreeItemLevel(it) == level) {
                break;
            }
            it = it.getParent();
        }
        return it;
    }

    protected void treeItemDragOver(DragEvent ev, TreeItemEx item, boolean acceptable) {
        hideDrawShapes();
        if (item != null) {
            Bounds[] bounds = levelBoundsOf(item);
            int n = -1;
            for (int i = 0; i < bounds.length; i++) {
                if (bounds[i].contains(ev.getScreenX(), ev.getScreenY())) {
                    n = i;
                    break;
                }
            }
            int level = treeView.getTreeItemLevel(item);
            if (n < 0 && !acceptable) {
                return;
            } else if (n < 0) {
                itemRect.setVisible(true);
                drawRectangle(item);
            } else if (item.isLeaf()) {
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
                    drawLines(item.getParent(), item);
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        drawLines(parentAtLevel(item, n), item);
                    } else {
                        drawLines(item.getParent(), item);
                    }
                }
            } else if (!item.isExpanded()) {
                // not leaf and not expanded     
                if (n == level || n == level + 1 || n == level + 2) {
                    if (acceptable) {
                        drawLines(item, item);
                    }
                } else if (n == level - 1) {
                    drawLines(item.getParent(), item);
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        drawLines(parentAtLevel(item, n), item);
                    } else {
                        drawLines(item.getParent(), item);
                    }
                }
            } else {
                drawLines(item, item);
            }
        }
    }

    public static class DragEventHandler implements EventHandler<DragEvent> {

        private final SceneGraphEditor editor;
        private final TreeCell targetCell;

        private Point2D point = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);

        protected DragEventHandler(SceneGraphEditor editor, TreeCell targetCell) {
            this.editor = editor;
            this.targetCell = targetCell;
        }

        public Point2D getPoint() {
            return point;
        }

        public void setPoint(Point2D point) {
            this.point = point;
        }

        public SceneGraphEditor getEditor() {
            return editor;
        }

        public TreeCell getTargetCell() {
            return targetCell;
        }

        public TreeItem getTargetTreeItem() {
            return targetCell.getTreeItem();
        }

        protected Object getDragTarget(DragEvent ev) {
            return ((ItemValue) getTargetTreeItem().getValue()).getTreeItemObject();
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
                retval = ((ItemValue) item.getValue()).getTreeItemObject();
            }
            return retval;
        }

        protected boolean isAcceptable(DragEvent ev, TreeItemBuilder builder) {
            Dragboard dragboard = ev.getDragboard();
            Object dragSource = getDragSource(ev);
            TreeItem it = getTargetTreeItem();
            Object o = ((ItemValue) it.getValue()).getTreeItemObject();

            boolean b = (dragboard.hasUrl() && (dragboard.getUrl().equals(NODE_UUID) || dragboard.getUrl().equals(CELL_UUID)))
                    && builder != null
                    && builder.isDragTarget()
                    && builder.isAcceptable(getTargetTreeItem(), dragSource);
            return (dragboard.hasUrl() && (dragboard.getUrl().equals(NODE_UUID) || dragboard.getUrl().equals(CELL_UUID)))
                    && builder != null
                    && builder.isDragTarget()
                    && builder.isAcceptable(getTargetTreeItem(), dragSource);
        }

        protected boolean isDragPlace(DragEvent ev, TreeItemBuilder builder) {
            Dragboard dragboard = ev.getDragboard();
            Object dragSource = getDragSource(ev);
            return (dragSource != null && dragboard.hasUrl()
                    && (dragboard.getUrl().equals(NODE_UUID)
                    || dragboard.getUrl().equals(CELL_UUID))
                    && builder != null);
            //&& getEditor().getTargetTreeItem(ev, item);
        }

        @Override
        public void handle(DragEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static class TreeItemCellDragEventHandler extends DragEventHandler {

        public TreeItemCellDragEventHandler(SceneGraphEditor editor, TreeCell targetCell) {
            super(editor, targetCell);
        }

        @Override
        public void handle(DragEvent ev) {
            if (ev.getEventType() == DragEvent.DRAG_OVER) {
                TreeItemBuilder builder = TreeItemRegistry.getInstance().getBuilder(getDragTarget(ev));
                getEditor().hideDrawShapes();
                if (isAcceptable(ev, builder)) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(getPoint())) {
                        if (ev.getAcceptingObject() != null && (ev.getAcceptingObject() instanceof TreeCell)) {
                            TreeCell cell = (TreeCell) ev.getAcceptingObject();
                            if (cell != null) {
                                getEditor().treeItemDragOver(ev, (TreeItemEx) cell.getTreeItem(), true);
                            }
                        }
                    }
                    ev.consume();
                } else if (isDragPlace(ev, builder)) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(getPoint())) {
                        if (ev.getAcceptingObject() != null && (ev.getAcceptingObject() instanceof TreeCell)) {
                            TreeCell cell = (TreeCell) ev.getAcceptingObject();
                            if (cell != null) {
                                //editor.drawLines(ev, (TreeItemEx) cell.getTreeItem());
                                getEditor().treeItemDragOver(ev, (TreeItemEx) cell.getTreeItem(), false);
                            }
                        }
                    }
                    ev.consume();
                }
            }

        }
    }//MouseEventHandler

    public static class TreeViewDragEventHandler extends DragEventHandler {

        public TreeViewDragEventHandler(SceneGraphEditor editor) {
            super(editor, null);
        }

        @Override
        protected Object getDragTarget(DragEvent ev) {
            return ((ItemValue) getTargetTreeItem().getValue()).getTreeItemObject();
        }

        @Override
        public TreeItem getTargetTreeItem() {
            int sz = getEditor().getTreeView().getExpandedItemCount();
            return getEditor().getTreeView().getTreeItem(sz - 1);
        }

        @Override
        public void handle(DragEvent ev) {
            if (ev.getEventType() == DragEvent.DRAG_OVER) {
                TreeItemBuilder builder = TreeItemRegistry.getInstance().getBuilder(getDragTarget(ev));
                getEditor().hideDrawShapes();
                System.err.println("TREEVIEW isDragPalce()=" + isDragPlace(ev, builder));
                if (isDragPlace(ev, builder)) {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    if (!pt.equals(getPoint())) {
                        //setPoint(pt);
                        TreeItem item = getTargetTreeItem();
                        if (item != null) {

                            TreeItem<ItemValue> it = getEditor().getTreeView().getRoot();
                            builder = TreeItemRegistry.getInstance().getBuilder(it.getValue().getTreeItemObject());

                            if (builder.isAcceptable(it, getDragSource(ev))) {
                                getEditor().drawLines(getEditor().getTreeView().getRoot(), item);
                            }
                        }
                    }

                }

            } else if (ev.getEventType() == DragEvent.DRAG_DROPPED) {
                getEditor().hideDrawShapes();
                //TreeItem<ItemValue> targetItem = getEditor().getTargetTreeItem(ev, getEditor().getTreeView().getRoot());
                TreeItem<ItemValue> targetItem = getEditor().getTreeView().getRoot();
                ItemValue targetValue = targetItem.getValue();
                //
                // Transfer the data to the target
                //
                Dragboard dragboard = ev.getDragboard();
                if (dragboard.hasUrl()) {
                    TreeItem target = getEditor().getTreeView().getRoot();
                    TreeItemBuilder tib = targetValue.getBuilder();
                    targetValue.getBuilder().accept(getEditor().getTreeView(), targetItem, target, (Node) ev.getGestureSource());
                    ev.setDropCompleted(true);

                } else {
                    ev.setDropCompleted(false);
                }

                ev.consume();

            } else if (ev.getEventType() == DragEvent.DRAG_DONE) {
                // Check how the data transfer happened. If it was moved, clear the text in the source.
                //TransferMode modeUsed = ev.getTransferMode();
                getEditor().hideDrawShapes();
                ev.consume();
            }
            ev.consume();
        }
    }//TreeViewDragEventHandler
}// SceneGraphEditor
