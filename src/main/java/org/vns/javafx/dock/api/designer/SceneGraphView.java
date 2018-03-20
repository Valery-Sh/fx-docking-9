package org.vns.javafx.dock.api.designer;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
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
        getStyleClass().add("scene-graph-view");
        treeView.getStyleClass().add("tree-view");

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
                        getVisibleCells().remove(this);
                    } else {
                        this.setGraphic(((TreeItemEx) this.getTreeItem()).getCellGraphic());
                        if (value != null && (value instanceof Node)) {
                            setId(((Node) value).getId());
                        }
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

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SceneGraphViewSkin(this);
    }
    private EventHandler<TreeItem.TreeModificationEvent<Object>> treeItemEventHandler;
    private EventHandler<TreeItem.TreeModificationEvent<Object>> valueChangedHandler;
    private EventHandler<TreeItem.TreeModificationEvent<Object>> childrenModificationEvent;

    public void addTreeItemEventHandlers(TreeItemEx item) {
        valueChangedHandler = ev -> {
            System.err.println("SceneGraphView: TreeItem: (value...) EventType = " + ev.getEventType() + "; item = " + ev.getSource());
            System.err.println("   --- value = " +  ev.getTreeItem().getValue() + "; newValue = " + ev.getNewValue());
            
        };
        childrenModificationEvent = ev -> {
            System.err.println("SceneGraphView: TreeItem: (childremM...) EventType = " + ev.getEventType() + "; item = " + ev.getTreeItem());
            if (ev.wasAdded()) {
                for (TreeItem it : ev.getAddedChildren()) {
                    System.err.println("   --- added it = " +  it);
                    if (it.getValue() instanceof Node) {
                    }
                    
                }
                int row = getTreeView().getRow(ev.getAddedChildren().get(ev.getAddedSize()-1) );
                System.err.println("   --- SceneGraphView: TreeItem: row = " + row);
                System.err.println("   --- SceneGraphView: TreeItem: item = " + ev.getAddedChildren().get(ev.getAddedSize()-1));
                getTreeView().getSelectionModel().select(ev.getAddedChildren().get(ev.getAddedSize()-1));
            }
            if (ev.wasRemoved()) {
                
                for (TreeItem it : ev.getRemovedChildren()) {
                    System.err.println("   --- removed it = " +  it);                    
                }
            }
            if (ev.wasPermutated()) {
                for (TreeItem it : ev.getRemovedChildren()) {
                    System.err.println("   --- permutated it = " +  it);                    
                }
            }
            

        };

        treeItemEventHandler = ev -> {
            System.err.println("TreeItem: (ALL)  EventType = " + ev.getEventType() + "; item = " + ev.getSource());
        };

        item.addEventHandler(TreeItem.valueChangedEvent(), valueChangedHandler);
        item.addEventHandler(TreeItem.childrenModificationEvent(), childrenModificationEvent);

        //item.addEventHandler(TreeItem.treeNotificationEvent(),  treeItemEventHandler);                
    }

}// SceneGraphView
