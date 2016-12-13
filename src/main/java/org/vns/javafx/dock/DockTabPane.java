package org.vns.javafx.dock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.MultiTab;
import org.vns.javafx.dock.api.properties.StateProperty;
import org.vns.javafx.dock.api.properties.TitleBarProperty;
import org.vns.javafx.dock.api.HasOwner;
import org.vns.javafx.dock.api.properties.DockedProperty;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPane extends VBox implements Dockable, MultiTab {

    private final TitleBarProperty<Region> titleBarProperty;

    private final StringProperty titleProperty = new SimpleStringProperty();

    private final StateProperty<Dockable> stateProperty = new StateProperty(this);

    private Button menuButton;

    private Map<Dockable, Object> listeners = new HashMap<>();
    
/*    private final ObjectProperty<Pane> dockPaneProperty = new SimpleObjectProperty() {
        
        @Override
        protected void invalidated() {
            super.invalidated();
            for (Node node : getContents()) {
                if (node instanceof Dockable) {
                    //((Dockable) node).setDockPane((DockPane) get());
                    //((Dockable) node).stateProperty().setDockPane((DockPane) get());
                }
            }
        }

    };

    public DockTabPane(Pane dockPane) {
        init(dockPane);
        titleBarProperty = new TitleBarProperty<>(null);
    }
*/
    public DockTabPane() {
        init();
        titleBarProperty = new TitleBarProperty<>(null);
    }
    
    private final HBox titleBarBox = new HBox();

    private void init() {
        stateProperty.setImmediateParentFunction(this::getImmediateParent);        

        StackPane stackPane = new StackPane();
        menuButton = new Button();
        menuButton.focusTraversableProperty().set(false);
        menuButton.borderProperty().set(Border.EMPTY);
        menuButton.getStyleClass().add(DockTitleBar.StyleClasses.MENU_BUTTON.cssClass());
        menuButton.setTooltip(new Tooltip("List items"));
        menuButton.setContextMenu(new ContextMenu());
        menuButton.setOnAction(ev -> {
            menuButton.getContextMenu().show(menuButton, Side.BOTTOM, 0, 0);
        });
        Pane fillPane = new Pane();
        HBox.setHgrow(fillPane, Priority.ALWAYS);
        HBox full = new HBox(titleBarBox, fillPane, menuButton);
        getChildren().addAll(full, stackPane);
        //DockTabPane.this.dockPaneProperty.set(dockPane);
        
        getTitleBars().addListener(DockTabPane.this::onChangeTitleBars);
    }

    public Dockable getImmediateParent(Node child) {
        //Node retval = DockUtil.getDockableParent(this, child);
        Node retval = DockUtil.getImmediateParent(this, child, (p) -> { return (p instanceof Dockable); });
        
        if ( retval == null  ) {
            retval = getDockableParentByTitleBar(getTitleBars(), child);
        }

        if ( retval == null ) {
            retval = this;
        }
        return (Dockable) retval;
    }
    
    public static Node getDockableParentByTitleBar(List<Node> titleBars, Node child) {
        if ( child == null || child.getScene() == null || child.getScene().getRoot() == null ) {
            return null;
        }
        Node retval = null;
        
        for ( Node bar : titleBars ) {
            if ( bar.isFocused() ) {
                if ( bar instanceof HasOwner) {
                    return (Node) ((HasOwner) bar).getOwner();
                } else {
                    return null;
                }
            }
            if ( bar instanceof Parent ) {
                Node node = DockUtil.findNode((Parent) bar, child);
                if ( node != null && (bar instanceof HasOwner) ) {
                    retval = (Node) ((HasOwner)bar).getOwner();
                    break;
                }
            }
        }
        return  retval;
    }

    public void onChangeTitleBars(Change<? extends Node> c) {
        while (c.next()) {
            if (c.wasUpdated()) {

            } else if (c.wasReplaced()) {
                List<? extends Node> rList = c.getList().subList(c.getFrom(), c.getTo());

            } else {

                if (c.wasRemoved()) {

                } else if (c.wasAdded()) {

                }
            }
        }
    }
    
    protected void addDockedListener(Dockable source) {
        DockedProperty dp = source.stateProperty().dockedProperty();
        DockedChangeListener dcl = new DockedChangeListener(source);
        dp.addListener(dcl);
        listeners.put(source, dcl);
    }
    protected void removeDockedListener(Dockable source) {
        DockedProperty dp = source.stateProperty().dockedProperty();
        DockedChangeListener dcl = (DockedChangeListener) listeners.get(source);
        if ( dcl != null ) {
            dp.removeListener(dcl);
            listeners.remove(source);            
        }
    }
    
    protected void dockedChanged(Dockable source,ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (oldValue && !newValue) {
        }
    }
    
    private void add(Dockable dockable) {
        getTitleBars().add(dockable.stateProperty().getTitleBar());
        MenuItem mi = new MenuItem();
        menuButton.getContextMenu().getItems().add(mi);
        getContents().add((Node) dockable);
        addDockedListener(dockable);
    }


/*    public ObjectProperty<Pane> dockPaneProperty() {
        return dockPaneProperty;
    }
*/
    protected boolean addDockNode(Dockable dockable) {
        dockable.stateProperty().setParent(stateProperty().getParent());
        if (getChildren().isEmpty()) {
            add(dockable);
            return true;
        }
        return addDockNode(getTitleBars().size(), dockable);
    }

    protected boolean addDockNode(int tabPos, Dockable dockable) {
        if (tabPos < 0 || tabPos > getTitleBars().size()) {
            return false;
        }
        if (getChildren().isEmpty()) {
            add(dockable);
            //dockable.getDockableState().setDocked(this, true);
            return true;
        }
        getTitleBars().add(tabPos, dockable.stateProperty().getTitleBar());
        getContents().add(tabPos, (Node) dockable);
        //dockable.getDockableState().setDocked(this, true);
        MenuItem mi = new MenuItem();
        mi.setText(dockable.titleProperty().get());
        menuButton.getContextMenu().getItems().add(mi);
        addDockedListener(dockable);
        return true;
    }

    @Override
    public ObservableList<Node> getTitleBars() {
        //return ((HBox) getChildren().get(0)).getChildren();
        Node n = null;
        if ( ! titleBarBox.getChildren().isEmpty())
            n = titleBarBox.getChildren().get(0);
        return titleBarBox.getChildren();
    }

    @Override
    public ObservableList<Node> getContents() {
        return ((StackPane) getChildren().get(1)).getChildren();
    }

    @Override
    public void undock(Dockable child) {
        DockTitleBar found = null;
        for (Node tb : getTitleBars()) {
            if (!(tb instanceof DockTitleBar)) {
                continue;
            }
            if (((DockTitleBar) tb).getOwner() == child) {
                found = (DockTitleBar) tb;
                break;
            }
        }
        getContents().remove((Node) child);
        if (found != null) {
            getTitleBars().remove(found);
            ((Dockable)child).stateProperty().titleBarProperty().set(null);
            ((Dockable)child).stateProperty().titleBarProperty().set(found);
        }
    }

    public TitleBarProperty titleBarProperty() {
        return null;
    }

    @Override
    public StringProperty titleProperty() {
        return titleProperty;
    }

    @Override
    public StateProperty stateProperty() {
        return stateProperty;
    }

    @Override
    public void dock(int pos, Dockable node) {
        /*        if ( stateProperty.isDocked() || node == null ) {
            return;
        }
         */
        if (this.addDockNode(pos, node)) {
            ((Dockable) node).stateProperty().setDocked(true);
            ((Dockable) node).stateProperty().setOwner(this);
            ((Dockable) node).stateProperty().setParent(this.stateProperty.getParent());
        }

    }

    @Override
    public void dock(Node node) {
        /*        if ( stateProperty.isDocked() || ( node instanceof Dockable) ) {
            return;
        }
         */
        if (addDockNode((Dockable) node)) {
            ((Dockable) node).stateProperty().setDocked(true);
            ((Dockable) node).stateProperty().setOwner(this);
            ((Dockable) node).stateProperty().setParent(stateProperty.getParent());
        }
    }

    
    public class DockedChangeListener implements ChangeListener<Boolean> {
        
        private final Dockable source;

        public DockedChangeListener(Dockable source) {
            this.source = source;
        }
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if ( ! newValue ) {
                undock(source);
                removeDockedListener(source);
            }
        }
    }
    
}//DockTabPane
