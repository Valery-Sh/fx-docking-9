package org.vns.javafx.designer;

import com.sun.javafx.scene.control.skin.VirtualScrollBar;
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
import static org.vns.javafx.designer.TreeItemBuilder.CELL_UUID;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "rootNode")
public class SceneGraphView extends Control {

    public static final int LAST = 0;
    public static final int FIRST = 2;

    public static double ANCHOR_OFFSET = 4;

    private final TreeViewEx<ItemValue> treeView;
    private Node rootNode;

    private final Pane treeViewPane = new StackPane();

    //
    // ContentPane is a subclass of VBox
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

    public TreeViewEx<ItemValue> getTreeView() {
        return treeView;
    }

    public void childrenModification(TreeItem.TreeModificationEvent<ItemValue> ev) {
        if (ev.wasAdded()) {
            for (TreeItem<ItemValue> item : ev.getAddedChildren()) {
                //item.getValue().getBuilder().registerChangeHandler(item);
                //System.err.println("Event:  added item obj = " + item.getValue().getTreeItemObject());
            }
        }
        if (ev.wasRemoved()) {
            for (TreeItem<ItemValue> item : ev.getRemovedChildren()) {
                TreeViewEx.updateOnMove((TreeItemEx) item);
                //System.err.println("UPDATE ON MOVE");
            }
        }
    }

    protected TreeItemEx createSceneGraph(Node node) {
        TreeItemBuilder builder = new TreeItemBuilder();
        TreeItemEx item = builder.build(node);
        item.addEventHandler(TreeItem.<ItemValue>childrenModificationEvent(),
                this::childrenModification);
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
        TreeItem<ItemValue> it = createSceneGraph(rootNode);
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
        TreeView<ItemValue> t = treeView;
        t.setCellFactory((TreeView<ItemValue> tv) -> {
            TreeCell<ItemValue> cell = new TreeCell<ItemValue>() {
                @Override
                public void updateItem(ItemValue value, boolean empty) {
                    super.updateItem(value, empty);

                    if (empty || value == null) {
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
                    } else {
                        this.setGraphic(value.getCellGraphic());
                        if (value.getTreeItemObject() instanceof Node) {
                            setId(((Node) value.getTreeItemObject()).getId());
                        }
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
            Dragboard dragboard = treeView.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            DragGesture dg = new DragTreeViewGesture(treeView, (TreeItemEx) cell.getTreeItem());
            treeView.getProperties().put(EditorUtil.GESTURE_SOURCE_KEY, dg);
            ClipboardContent content = new ClipboardContent();
            content.putUrl(CELL_UUID);
            dragboard.setContent(content);
            treeView.getSelectionModel().clearSelection();
            ev.consume();
            /*            Platform.runLater(() -> {
                dragIndicator.getItemParentOffset(cell.getTreeItem());
            });
             */
        });
    }

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
                ItemValue targetValue = targetItem.getValue();
                TreeItem place = ((TreeCell) ev.getGestureTarget()).getTreeItem();
                //20.01targetValue.getBuilder().accept(treeView, (TreeItemEx) targetItem, (TreeItemEx) place, (Node) ev.getGestureSource());

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

    public static class TreeItemCellDragEventHandler extends DragEventHandler {

        public TreeItemCellDragEventHandler(SceneGraphView editor, TreeCell targetCell) {
            super(editor, targetCell);
        }

        @Override
        public void handle(DragEvent ev) {
//            System.err.println("=== HANDLE");
            ((TreeViewEx) getEditor().getTreeView()).notifyDragEvent(ev);

            if (ev.getEventType() == DragEvent.DRAG_OVER) {
                ((TreeViewEx) getEditor().getTreeView()).notifyDragAccepted(false);
                TreeView tv = getEditor().getTreeView();
                getEditor().getDragIndicator().hideDrawShapes();
                System.err.println("HANDLE 1");
                if (!isAdmissiblePosition(ev)) {
//                    System.err.println("=== HANDLE NOT isAdmissiblePosition");
//                    ev.acceptTransferModes(TransferMode.NONE);
                    //ev.setDropCompleted(true)
                    System.err.println("HANDLE 2");
                    ev.consume();
                } else {
                    ev.acceptTransferModes(TransferMode.COPY_OR_MOVE);
//                    System.err.println("handle acceptingMode = " + ev.getAcceptedTransferMode());                    
                    ((TreeViewEx) getEditor().getTreeView()).notifyDragEvent(ev);
                    ((TreeViewEx) getEditor().getTreeView()).notifyDragAccepted(true);
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
            int sz = getEditor().getTreeView().getExpandedItemCount();
            return (TreeItemEx) getEditor().getTreeView().getTreeItem(sz - 1);
        }

        @Override
        protected TreeItemEx getTargetTreeItem(DragEvent ev) { //, TreeItem place) {
            return (TreeItemEx) getEditor().getTreeView().getRoot();
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
//20.01                    targetValue.getBuilder().accept(getEditor().getTreeView(), (TreeItemEx) targetItem, (TreeItemEx) place, (Node) ev.getGestureSource());

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
