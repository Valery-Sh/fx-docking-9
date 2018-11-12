package org.vns.javafx.designer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.dragging.DragType;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.Scope;
import org.vns.javafx.dock.api.bean.BeanAdapter;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "root")
public class SceneGraphView extends Control implements DockLayout {

    private SceneGraphViewLayoutContext targetContext;

    private DragType dragType = DragType.SIMPLE;

    public static final int LAST = 0;
    public static final int FIRST = 2;

    public static double ANCHOR_OFFSET = 4;

    private final TreeViewEx treeView;

    private final ObjectProperty<Node> root = new SimpleObjectProperty<>();

    private final ObjectProperty<Node> statusBar = new SimpleObjectProperty<>();

    private final ObservableList<TreeCell> visibleCells = FXCollections.observableArrayList();

    private Map<Class<?>, Map<String, Object>> saved = new HashMap<>();

    private boolean designer;
    
    public SceneGraphView() {
        this(null,false);
    }

    public SceneGraphView(Node rootNode) {
        this(rootNode,false);
    }

    public SceneGraphView(boolean designer) {
        this(null,designer);
    }

    public SceneGraphView(Node rootNode, boolean designer) {
        this.treeView = new TreeViewEx<>(this);
        root.set(rootNode);
        this.designer = designer;
        init();
    }
    
    private void init() {
        //getStyleClass().add("scene-graph-view");
        //treeView.getStyleClass().add("tree-view");
        if ( getRoot() != null && isDesigner() ) {
            PalettePane palette = DesignerLookup.lookup(PalettePane.class);
            if ( palette != null ) {
                //TreeItemEx item = new TreeItemBuilder(isDesigner()).build(getRoot());
                //getTreeView().setRoot(item);
            }
        }
        customizeCell();
    }

    public boolean isDesigner() {
        return designer;
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    public void save() {
        TreeViewEx tv = getTreeView();
        TreeItemEx r = (TreeItemEx) tv.getRoot();
        saveItem(r);
        r.getChildren().forEach((it) -> {
            save((TreeItemEx) it);
        });
    }

    private void save(TreeItemEx item) {
        saveItem(item);
        item.getChildren().forEach((it) -> {
            save((TreeItemEx) it);
        });
    }

    private void saveItem(TreeItemEx item) {
        Object o = item.getValue();
        if (o == null) {
            return;
        }
        BeanAdapter ba = new BeanAdapter(o);
        Set<String> set = BeanAdapter.getPropertyNames(o.getClass());
        Map<String, Object> map = new HashMap<>();
        set.forEach(name -> {
            Object obj = ba.get(name);
            ChoiceBox bb = null;

            Method fxPropMethod = ba.fxPropertyMethod(name);
            if (!name.equals("class") && !ba.isReadOnly(name) && fxPropMethod != null) {
                map.put(name, ba.get(name));
            }
        });
        saved.put(item.getValue().getClass(), map);

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
            targetContext = new SceneGraphViewLayoutContext(this);
            if ( isDesigner() ) {
                targetContext.getScopes().add(new Scope("designer"));
            }
        }
        return targetContext;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SceneGraphViewSkin(this);
    }
    
/*    private EventHandler<TreeItem.TreeModificationEvent<Object>> treeItemEventHandler
            = (TreeItem.TreeModificationEvent<Object> ev) -> {
                System.err.println("WAS ADDED");
                if (ev.wasAdded() && getTreeView().getRoot() == null) {
                    getTreeView().setRoot(ev.getTreeItem());
                }
            };
  */  
    /*    private EventHandler<TreeItem.TreeModificationEvent<Object>> treeItemEventHandler;
    private EventHandler<TreeItem.TreeModificationEvent<Object>> valueChangedHandler;
    private EventHandler<TreeItem.TreeModificationEvent<Object>> childrenModificationEvent;

    public void addTreeItemEventHandlers(TreeItemEx item) {
        valueChangedHandler = ev -> {
        };
        childrenModificationEvent = ev -> {
            if (ev.wasAdded()) {
                for (TreeItem it : ev.getAddedChildren()) {
                    if (it.getValue() instanceof Node) {
                    }
                }
            }

            if (ev.wasRemoved()) {

                for (TreeItem it : ev.getRemovedChildren()) {
//                    System.err.println("   --- removed it = " +  it);                    
                }
            }
            if (ev.wasPermutated()) {
                for (TreeItem it : ev.getRemovedChildren()) {
//                    System.err.println("   --- permutated it = " +  it);                    
                }
            }

        };

        treeItemEventHandler = ev -> {
        };

        item.addEventHandler(TreeItem.valueChangedEvent(), valueChangedHandler);
        item.addEventHandler(TreeItem.childrenModificationEvent(), childrenModificationEvent);

        //item.addEventHandler(TreeItem.treeNotificationEvent(),  treeItemEventHandler);                
    }
     */
}// SceneGraphView
