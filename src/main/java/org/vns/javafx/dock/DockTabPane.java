package org.vns.javafx.dock;

import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
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

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPane extends VBox implements Dockable, MultiTab {
    
    private TitleBarProperty<Region> titleBarProperty;
    
    private StringProperty titleProperty = new SimpleStringProperty();
    
    private StateProperty stateProperty = new StateProperty(this);
    
    private Button menuButton;

    private final ObjectProperty<DockPane> dockPaneProperty = new SimpleObjectProperty() {

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

    public DockTabPane(DockPane dockPane) {
        init(dockPane);
        titleBarProperty = new TitleBarProperty<>(null);
    }
    private final HBox titleBarBox = new HBox();

    private void init(DockPane dockPane) {
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
        this.dockPaneProperty.set(dockPane);
        //this.dockableState = new DockableState(this);
        getTitleBars().addListener(this::onChangeTitleBars);
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

    private void add(Dockable dockable) {
        getTitleBars().add(dockable.stateProperty().getTitleBar());
        MenuItem mi = new MenuItem();
        menuButton.getContextMenu().getItems().add(mi);
        getContents().add((Node)dockable);
    }


    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public ObjectProperty<DockPane> dockPaneProperty() {
        return dockPaneProperty;
    }


    protected boolean addDockNode(Dockable dockable) {
        dockable.stateProperty().setParent(stateProperty().getParent());
        if (getChildren().isEmpty()) {
            add(dockable);
            return true;
        }
        return addDockNode( getTitleBars().size(), dockable);
    }

    protected boolean addDockNode(int tabPos, Dockable dockable ) {
        if (tabPos < 0 || tabPos > getTitleBars().size()) {
            return false;
        }
        if (getChildren().isEmpty()) {
            add(dockable);
            //dockable.getDockableState().setDocked(this, true);
            return true;
        }
        getTitleBars().add(tabPos, dockable.stateProperty().getTitleBar());
        getContents().add(tabPos, (Node)dockable);
        //dockable.getDockableState().setDocked(this, true);
        MenuItem mi = new MenuItem();
        mi.setText(dockable.titleProperty().get());
        menuButton.getContextMenu().getItems().add(mi);
        return true;
    }

    @Override
    public ObservableList<Node> getTitleBars() {
        //return ((HBox) getChildren().get(0)).getChildren();
        return titleBarBox.getChildren();
    }

    @Override
    public ObservableList<Node> getContents() {
        return ((StackPane) getChildren().get(1)).getChildren();
    }

    @Override
    public void remove(Dockable child) {

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
    //!!!        DockNode dn = (DockNode) child;
    //!!!        dn.titleBarProperty().set(found);
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
        if ( this.addDockNode(pos, node) ) {
            ((Dockable)node).stateProperty().setDocked(this, true);
        }
        
    }
    @Override
    public void dock(Node node) {
/*        if ( stateProperty.isDocked() || ( node instanceof Dockable) ) {
            return;
        }
*/
        if ( addDockNode((Dockable)node) ) {
            ((Dockable)node).stateProperty().setDocked(this, true);
        }
    }
}//DockTabPane