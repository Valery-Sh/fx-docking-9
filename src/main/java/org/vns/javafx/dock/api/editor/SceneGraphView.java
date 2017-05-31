package org.vns.javafx.dock.api.editor;

import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
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
import javafx.util.Duration;
import org.vns.javafx.dock.api.Dockable;
import static org.vns.javafx.dock.api.editor.TreeItemBuilder.CELL_UUID;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "rootNode")
public class SceneGraphView extends Control {

//    private static final double LEVEL_SPACE = 15;

    public static final int LAST = 0;
    public static final int FIRST = 2;

    private TreeViewDragEventHandler dragDropHandler;

    public static double ANCHOR_OFFSET = 4;

    private final TreeView<ItemValue> treeView;
    private Node rootNode;

    private final Pane treeViewPane = new StackPane();
    //private final Pane indicatorPane = new Pane();
    //
    // is a subclass of VBox
    // 
    private final ContentPane contentPane = new ContentPane(treeViewPane);

//    private final Line vertLine = new Line();
//    private final Line horLine = new Line();
//    private final Rectangle itemRect = new Rectangle();
    private DragIndicator dragIndicator;
    private Region statusBar;

    private double parentOffset = 10;
    private Bounds disclosureBounds;
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
        getTreeViewPane().getChildren().add(treeView);
        //addStageListeners();
        sceneProperty().addListener(this::sceneChanged);
        customizeCell();
        dragIndicator.initIndicatorPane();
/*        indicatorPane.setMouseTransparent(true);
        treeViewPane.getChildren().add(indicatorPane);
        indicatorPane.toFront();
*/        
        contentPane.setStyle("-fx-border-width: 3;-fx-border-color: blue; -fx-background-color: red");
        scrollAnimation = new ScrollAnimation((TreeViewEx) treeView);
        //
        // 
        //
        dragDropHandler = new TreeViewDragEventHandler(this);
        //
        //
        //
        treeView.addEventHandler(DragEvent.ANY, dragDropHandler);

//        indicatorPane.getChildren().addAll(horLine, vertLine, itemRect);
        //
        // TO DELETE
        treeViewPane.setStyle("-fx-background-color: green;-fx-insets: 0;-fx-border-width: 0;  ");
        /*-fx-padding: 0 3 0 3; */
        //END TO DELETE    

/*        vertLine.getStyleClass().add("tree-view-indicator");
        vertLine.getStyleClass().add("vert-line");
        horLine.getStyleClass().add("hor-line");
        horLine.getStyleClass().add("tree-view-indicator");
        itemRect.getStyleClass().add("tree-view-indicator");
        itemRect.getStyleClass().add("rect");

        vertLine.setMouseTransparent(true);
        horLine.setMouseTransparent(true);
        itemRect.setMouseTransparent(true);
*/        
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

/*    protected Pane getIndicatorPane() {
        return indicatorPane;
    }
*/
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

    protected TreeView<ItemValue> getTreeView() {
        return treeView;
    }

    protected TreeItem createSceneGraph(Node node) {
        TreeItem item = null;
        if (TreeItemRegistry.getInstance().exists(node)) {
            item = TreeItemRegistry.getInstance().getBuilder(node).build(node);
        }
        return item;
    }

    /*    protected void addStageListeners() {
        sceneProperty().addListener(this::sceneChanged);
        if ( true ) {
            return;
        }
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
     */
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
        TreeItem<ItemValue> it = createSceneGraph(rootNode);
        it.setExpanded(true);
        treeView.setRoot(it);
        Platform.runLater(() -> {
//            Node arrow = ((Pane) ((TreeCell) it.getValue().getCellGraphic().getParent()).getDisclosureNode()).getChildren().get(0);
            dragIndicator.getItemParentOffset(it);
            dragIndicator.getDisclosureBounds();
            registerScrollBarEvents();
        });
    }

    protected void registerScrollBarEvents() {
        ScrollBar sb = ((TreeViewEx) treeView).getScrollBar();

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
            //treeView.getScene().setCursor(Cursor.HAND);
            scrollAnimation.start(ev.getScreenX(), ev.getScreenY());

        });

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
                        //setStyle(null);
                        if (this.getUserData() != null) {
                            Object[] o = (Object[]) this.getUserData();
                            if (o[0] != null) {
                                this.removeEventHandler(DragEvent.ANY, (TreeItemCellDragEventHandler) o[0]);
                            }
                            /*                            if (o[1] != null) {
                                this.removeEventHandler(NodeDragEvent.NODE_DRAG, (TreeCellNodeDragEventHandler) o[1]);
                            }
                             */
                        }
                        this.setOnDragDetected(null);
                        this.setOnDragDropped(null);
                        this.setOnDragDone(null);
                    } else {
                        this.setGraphic(value.getCellGraphic());
                        TreeItemCellDragEventHandler h = new TreeItemCellDragEventHandler(SceneGraphView.this, this);
                        this.addEventHandler(DragEvent.ANY, h);
                        this.setUserData(new Object[]{h, null});

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
            Platform.runLater(() -> {
                dragIndicator.getItemParentOffset(cell.getTreeItem());
            });

        });
    }

    protected void registerDragDone(TreeCell cell) {
        cell.setOnDragDone(ev -> {
            //ItemValue sourceValue = ((TreeCell<ItemValue>) ev.getGestureSource()).getTreeItem().getValue();
            dragIndicator.hideDrawShapes();
            ev.consume();
        });
    }

    protected void registerDragDropped(TreeCell cell) {
        //System.err.println("DROPPED");
        cell.setOnDragDropped((DragEvent ev) -> {
            System.err.println("");
            TreeItem<ItemValue> targetItem = getTargetTreeItem(ev, ((TreeCell) ev.getGestureTarget()).getTreeItem());
            //
            // Try transfer data to the place
            //
            //Dragboard dragboard = ev.getDragboard();
/*            boolean admissible = ((ItemValue)cell.getTreeItem()
               .getValue()).getBuilder()
               .isAdmissiblePosition(treeView, targetItem, cell.getTreeItem(), DragEventHandler.getGestureSourceObject(ev));
*/            
            if (targetItem != null ) {
                ItemValue targetValue = targetItem.getValue();
                TreeItem place = ((TreeCell) ev.getGestureTarget()).getTreeItem();
                //System.err.println("DROPPED BEFORE ACCEPT " + targetValue + "; gestureSource = " + ev.getGestureSource());
                targetValue.getBuilder().accept(treeView, targetItem, place, (Node) ev.getGestureSource());

                ev.setDropCompleted(true);
            } else {
                ev.setDropCompleted(false);
            }
            ev.consume();

        });
    }

    /*    protected TreeItem<ItemValue> findFirstVisibleTreeItem(double x, double y) {
        //treeView.getExpandedItemCount()
        TreeItem<ItemValue> retval = null;
        for (int i = 0; i < treeView.getExpandedItemCount(); i++) {
            if (isVisible(treeView.getTreeItem(i))) {
                Node cell = treeView.getTreeItem(i).getValue().getCellGraphic().getParent();

                if (cell.contains(cell.screenToLocal(x, y))) {
                    retval = treeView.getTreeItem(i);
                    break;

                }
            }
        }
        return retval;
    }
     */
 /*    protected TreeItem<ItemValue> findLastVisibleTreeItem(double x, double y) {
        //treeView.getExpandedItemCount()
        TreeItem<ItemValue> retval = null;
        for (int i = treeView.getExpandedItemCount() - 1; i >= 0; i--) {
            if (isVisible(treeView.getTreeItem(i))) {
                Node cell = treeView.getTreeItem(i).getValue().getCellGraphic().getParent();

                if (cell.contains(cell.screenToLocal(x, y))) {
                    retval = treeView.getTreeItem(i);
                    break;

                }
            }
        }
        return retval;
    }
     */
 /*    protected TreeItem<ItemValue> findTreeItem(Object sourceGesture) {
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
     */
 /*    public void expandAllItems(TreeItem item) {
        item.setExpanded(true);
        List<TreeItem> l = item.getChildren();
        for (TreeItem it : l) {
            expandAllItems(it);
        }
    }
     */
 /*    protected void expand(TreeItem item ) {
        item.setExpanded(true);
        List<TreeItem> l = item.getChildren();
        for ( TreeItem it : l ) {
            it.setExpanded(true);
            expand(it);
        }
    }
     */

    /*    
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
        //System.err.println("LB = " + lb);
        if (lb == null) {
            return;
        }

        lb = treeViewPane.screenToLocal(lb);
        //System.err.println("LB1 = " + lb);

        itemRect.setX(lb.getMinX());
        itemRect.setY(lb.getMinY());
        itemRect.setWidth(lb.getWidth());
        itemRect.setHeight(lb.getHeight());

        //System.err.println("itemrect X=" + itemRect.getX() + "; Y=" + itemRect.getY());
        itemRect.toFront();
        itemRect.setVisible(true);
        //StackPane.setAlignment(rootNode, Pos.CENTER);

    }

    protected void drawLines(TreeItem<ItemValue> from, TreeItem<ItemValue> to) {
        Bounds lb = EditorUtil.screenTreeItemBounds(to);
        //Bounds rootBounds = EditorUtil.screenNonValueLevelBounds(treeView, treeView.getRoot());
        Bounds rootBounds = screenNonValueLevelBounds(treeView.getRoot(), to);
        treeView.setPadding(Insets.EMPTY);
        Insets pins = treeView.getPadding();

        AnchorPane ap = (AnchorPane) from.getValue().getCellGraphic();

        Pane p = getTreeViewPane();
        Bounds bnd = null;
        //if (ap.getScene() != null) {
        //bnd = EditorUtil.screenNonValueLevelBounds(treeView, from);
        bnd = screenNonValueLevelBounds(from, to);
        //}

        int level = treeView.getTreeItemLevel(from);

        //double gap = EditorUtil.getRootStartGap(treeView);
        double gap = getItemParentOffset(to);
        //Bounds arrowBnd = EditorUtil.screenArrowBounds(from);
        Bounds arrowBnd = screenArrowBounds(from, to);
        /////
        TreeCell c = ((TreeCell) to.getValue().getCellGraphic().getParent());

        double startY = bnd.getMinY() + bnd.getHeight() + pins.getBottom();
        if (arrowBnd.getHeight() != 0) {
            startY = arrowBnd.getMinY() + arrowBnd.getHeight();
        }

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

        lb = treeViewPane.screenToLocal(lb);
        horLine.setStartX(lb.getMinX());
        horLine.setStartY(lb.getMinY() + lb.getHeight());
        horLine.setEndY(horLine.getStartY());
        horLine.setEndX(horLine.getStartX() + lb.getWidth());

        vertLine.setEndY(horLine.getStartY());

        horLine.toFront();
        horLine.setVisible(true);

    }

    protected double rootNonValueWidth(TreeItem<ItemValue> sample) {
        int level = treeView.getTreeItemLevel(sample);
        double cellOffset = getItemParentOffset(sample);
        Bounds sampleBounds = screenNonValueBounds(sample);
        return sampleBounds.getWidth() - cellOffset * level;
    }

    protected boolean isVisible(TreeItem<ItemValue> item) {

        boolean retval = false;
        Node g = item.getValue().getCellGraphic();
        if (g.getScene() != null && g.getScene().getWindow() != null) {
            retval = true;
        }
        return retval;
    }

     */
    protected TreeItem<ItemValue> getTargetTreeItem(DragEvent ev, TreeItem<ItemValue> item) {
        return getTargetTreeItem(ev.getScreenX(), ev.getScreenY(), item);
    }

    protected TreeItem<ItemValue> getTargetTreeItem(double x, double y, TreeItem<ItemValue> item) {

        TreeItem<ItemValue> retval = null;

        dragIndicator.hideDrawShapes();
        if (item != null) {
            Bounds[] bounds = dragIndicator.levelBoundsOf(item);
            int n = -1;
            for (int i = 0; i < bounds.length; i++) {
                if (bounds[i].contains(x, y)) {
                    n = i;
                    break;
                }
            }
            int level = treeView.getTreeItemLevel(item);
            if (n < 0) {
                retval = item;
                ((ItemValue) retval.getValue()).setDragDropQualifier(LAST);
            } else if (item.isLeaf()) {
                //System.err.println("n = " + n + "; level=" + level);
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
                    retval = item.getParent();
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        //System.err.println("sibling = null");
                        retval = dragIndicator.getParentTarget(item, n);
                        //retval = parentAtLevel(item, n+1);
                    } else {
                        retval = item.getParent();
                    }
                }
            } else if (!item.isExpanded()) {
                // not leaf and not expanded     
                if (n == level || n == level + 1 || n == level + 2) {
                    retval = item;
                } else if (n == level - 1) {
                    retval = item.getParent();
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        retval = dragIndicator.parentAtLevel(item, n);
                    } else {
                        retval = item.getParent();
                    }
                }
            } else {
                retval = item;
                ((ItemValue) item.getValue()).setDragDropQualifier(FIRST);
            }
        }
        return retval;
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

    public static class TreeItemCellDragEventHandler extends DragEventHandler {

        PauseTransition pt2;
        double sX = -1000;
        double sY = -1000;
        long sTime = System.currentTimeMillis();

        public TreeItemCellDragEventHandler(SceneGraphView editor, TreeCell targetCell) {
            super(editor, targetCell);
            PauseTransition pt2 = new PauseTransition(Duration.seconds(5));
        }

        @Override
        public void handle(DragEvent ev) {
            if (ev.getEventType() == DragEvent.DRAG_OVER) {
                /*            System.err.println("!!! 2) DRAGEVENT gestureSource = " + ev.getGestureSource());
            Node n =((DragGesture) ((Node) ev.getGestureSource()).getProperties().get(EditorUtil.GESTURE_SOURCE_KEY)).getGestureSource();
            System.err.println("!!! 2.1) DRAGEVENT DragGesture gestureSource = " + n);
            System.err.println("!!! 3) DRAGEVENT        source = " + ev.getSource());
                 */
                TreeView tv = getEditor().getTreeView();
                getEditor().getDragIndicator().hideDrawShapes();
//                System.err.println("HANDLE 1");
                if (!isAdmissiblePosition(ev)) {
//                    System.err.println("HANDLE 2 false");
                    ev.consume();
                } else {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    //Point2D pt = new Point2D(Math.round(ev.getX()), Math.round(ev.getY()));
                    //if (!pt.equals(getPoint())) {
                    // getSceneGraphView().treeItemDragOver(ev, (TreeItem) getTreeCell().getTreeItem());
                    drawIndicator(ev);
                    //}
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
        public TreeItem getTreeCellItem() {
            int sz = getEditor().getTreeView().getExpandedItemCount();
            return getEditor().getTreeView().getTreeItem(sz - 1);
        }

        @Override
        protected TreeItem<ItemValue> getTargetTreeItem(DragEvent ev) { //, TreeItem place) {
            return getEditor().getTreeView().getRoot();
        }

        @Override
        public void handle(DragEvent ev) {
            getEditor().getDragIndicator().hideDrawShapes();
            VirtualScrollBar sb = ((TreeViewEx) getEditor().getTreeView()).getScrollBar();
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
                getEditor().getDragIndicator().hideDrawShapes();
                TreeItem<ItemValue> targetItem = getEditor().getTreeView().getRoot();
                ItemValue targetValue = targetItem.getValue();
                //
                // Transfer the data to the place
                //
                if (isAdmissiblePosition(ev)) {
                    TreeItem place = getTreeCellItem();
                    targetValue.getBuilder().accept(getEditor().getTreeView(), targetItem, place, (Node) ev.getGestureSource());
                    ev.setDropCompleted(true);

                } else {
                    ev.setDropCompleted(false);
                }

            } else if (ev.getEventType() == DragEvent.DRAG_DONE) {
                getEditor().getDragIndicator().hideDrawShapes();
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
