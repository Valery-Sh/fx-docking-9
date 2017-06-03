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

    //private TreeViewDragEventHandler dragDropHandler;

    public static double ANCHOR_OFFSET = 4;

    private final TreeView<ItemValue> treeView;
    private Node rootNode;

    private final Pane treeViewPane = new StackPane();

    //
    // is a subclass of VBox
    // 
    private final ContentPane contentPane = new ContentPane(treeViewPane);

    private DragIndicator dragIndicator;
    private Region statusBar;

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

        sceneProperty().addListener(this::sceneChanged);
        customizeCell();
        dragIndicator.initIndicatorPane();
        //treeView.setStyle("-fx-border-width:  0 0 10 0; ;-fx-border-color: blue; -fx-background-color: aqua");
        
        //treeView.setPadding(new Insets(2,2,2,2));
        scrollAnimation = new ScrollAnimation((TreeViewEx) treeView);
        treeView.addEventHandler(DragEvent.ANY, new TreeViewDragEventHandler(this));

        //
        // TO DELETE
        treeViewPane.setStyle("-fx-background-color: green;-fx-insets: 0;-fx-border-width: 0;  ");
        /*-fx-padding: 0 3 0 3; */
        //END TO DELETE    

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

    public TreeView<ItemValue> getTreeView() {
        return treeView;
    }

    protected TreeItem createSceneGraph(Node node) {
        TreeItem item = null;
        if (TreeItemBuilderRegistry.getInstance().exists(node)) {
            item = TreeItemBuilderRegistry.getInstance().getBuilder(node).build(node);
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
            //dragIndicator.getItemParentOffset(it);
            //dragIndicator.getDisclosureBounds();
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
                        //System.err.println("UPDATE cell = " + this);
                        //if ( getTreeItem() != null && getTreeItem().getValue() != null )
                        //System.err.println("CUSTOMIZE: EMPTY " + getTreeItem().getValue().getTreeItemObject());
                        setText(null);
                        setGraphic(null);
                        if (this.getUserData() != null) {
                            Object[] o = (Object[]) this.getUserData();
                            if (o[0] != null) {
                                this.removeEventHandler(DragEvent.DRAG_OVER, (TreeItemCellDragEventHandler) o[0]);
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
                        String id = "UUU";
                        if ( value.getTreeItemObject() instanceof Node)
                            setId(((Node)value.getTreeItemObject()).getId());
                        //getDragIndicator().setDisclosureBounds(this);
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

/*    protected void registerDragDetected(TreeCell cell) {
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
*/    
    protected void registerDragDetected(TreeCell cell) {
        cell.setOnDragDetected(ev -> {
            Dragboard dragboard = treeView.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            DragGesture dg = new DragTreeViewGesture(treeView, cell.getTreeItem());
            treeView.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
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
        cell.setOnDragDropped((DragEvent ev) -> {
            System.err.println("DROPPED TransferMode="  + ev.getAcceptedTransferMode());
            
            //System.err.println("ev DRAG_DROPPED cell id=" + ev.getGestureSource());
            TreeItem<ItemValue> targetItem = getTargetTreeItem(ev, ((TreeCell) ev.getGestureTarget()).getTreeItem());
            //
            // Try transfer data to the place
            //

            if (targetItem != null ) {
                ItemValue targetValue = targetItem.getValue();
                TreeItem place = ((TreeCell) ev.getGestureTarget()).getTreeItem();
                targetValue.getBuilder().accept(treeView, targetItem, place, (Node) ev.getGestureSource());

                ev.setDropCompleted(true);
            } else {
                ev.setDropCompleted(false);
            }
            ev.consume();

        });
    }

    protected TreeItem<ItemValue> getTargetTreeItem(DragEvent ev, TreeItem<ItemValue> item) {
        return dragIndicator.getTargetTreeItem(ev.getScreenX(), ev.getScreenY(), item);
    }

/*    protected TreeItem<ItemValue> getTargetTreeItem(double x, double y, TreeItem<ItemValue> item) {

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
                if (n == level - 1 || n == level || n == level + 1 || n == level + 2) {
                    retval = item.getParent();
                } else if (n < level - 1) {
                    if (item.nextSibling() == null) {
                        retval = dragIndicator.getParentTarget(item, n);
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
*/
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
            ((TreeViewEx)getEditor().getTreeView()).notifyDragEvent(ev);
            
            if (ev.getEventType() == DragEvent.DRAG_OVER) {
                System.err.println("ev DRAG_OVER cell id=" + ev.getGestureSource());
                TreeView tv = getEditor().getTreeView();
                getEditor().getDragIndicator().hideDrawShapes();
                if (!isAdmissiblePosition(ev)) {
                    //System.err.println(" 1  --- ev DRAG_OVER cell id=" + ev.getGestureSource());
                    ev.consume();
                } else {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    ((TreeViewEx)getEditor().getTreeView()).notifyDragEvent(ev);
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
            VirtualScrollBar sb = ((TreeViewEx) getEditor().getTreeView()).getVScrollBar();
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
