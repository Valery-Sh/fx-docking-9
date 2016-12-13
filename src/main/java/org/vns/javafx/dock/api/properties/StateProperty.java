package org.vns.javafx.dock.api.properties;

import java.util.function.Consumer;
import java.util.function.Function;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.DockPaneDelegate;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.MultiTab;
import org.vns.javafx.dock.api.StateTransformer;
import org.vns.javafx.dock.DockTitleBar;

/**
 *
 * @author Valery
 * @param <T>
 */
public class StateProperty<T extends Dockable> {

    private final TitleBarProperty<Region> titleBarProperty;

    private final StringProperty titleProperty = new SimpleStringProperty("");

    private final ReadOnlyObjectWrapper<T> dockableWrapper = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyObjectProperty<T> dockableProperty = dockableWrapper.getReadOnlyProperty();

    private final DockFloatingProperty floatingProperty = new DockFloatingProperty(false);
    private final DockedProperty dockedProperty = new DockedProperty(false);
    private final DockResizableProperty resizableProperty = new DockResizableProperty(true);

    private DockPaneDelegate parent;

    private Dockable owner;

    public StateProperty(T dockable) {
        dockableWrapper.set(dockable);
        titleBarProperty = new TitleBarProperty(dockable);
        init();
    }

    private void init() {

        dockedProperty.addListener(this::dockedChanged);
        //titleBarProperty.addListener(this::titlebarChanged);

    }

    public Dockable getOwner() {
        return owner;
    }

    public void setOwner(Dockable owner) {
        this.owner = owner;
    }

    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        int idx = -1;
        Pane pane = null;
        if (oldValue != null && oldValue.getParent() != null && (oldValue.getParent() instanceof Pane)) {
            System.err.println("StateProperty: titleBarChanged oldValue != null parentClass=" + oldValue.getParent());
            idx = ((Pane) oldValue.getParent()).getChildren().indexOf(oldValue);
            if (idx >= 0) {
                pane = (Pane) oldValue.getParent();
                ((Pane) oldValue.getParent()).getChildren().remove(oldValue);
                if (newValue != null) {
                    //((Pane) oldValue.getParent()).getChildren().remove(oldValue);
                    pane.getChildren().add(idx, newValue);
                }
            }
        }

        String id = newValue.getId();
        getDockable().stateProperty().titleBarProperty().changeOwner(getDockable());
        if (newValue != null && getDockable().stateProperty().titleBarProperty().isActiveChoosedPseudoClass()) {
            //newValue.pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, true);            
        }
    }

    public TitleBarProperty<Region> titleBarProperty() {
        return titleBarProperty;
    }

    public DockPaneDelegate getParent() {
        return parent;
    }

    public void setParent(DockPaneDelegate dockPaneDelegate) {
        this.parent = dockPaneDelegate;
    }

    protected void dockedChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (oldValue && !newValue) {
            if (owner != null && (owner instanceof MultiTab)) {
                ((MultiTab) owner).remove(getDockable());
            } else {
                parent.remove(getNode());
            }
        }
    }

    public ReadOnlyObjectProperty<T> dockableProperty() {
        return dockableProperty;
    }

    public T getDockable() {
        return this.dockableProperty.get();
    }

    public Region getNode() {
        return (Region) this.dockableProperty.get();
    }

    public Region getTitleBar() {
        return titleBarProperty.get();
    }

    public void setTitleBar(Region node) {
        titleBarProperty.set(node);
    }

    public boolean addTitleBar(int idx, Region node, ObservableList children) {
        if (titleBarProperty.get() != null) {
            return false;
        }
        children.add(idx, node);
        titleBarProperty.set(node);
        return true;
    }

    public boolean replaceTitleBar(int idx, Region node, ObservableList children) {
        if (titleBarProperty.get() == null) {
            return false;
        }
        Node oldNode = titleBarProperty.get();
        int oldIdx = children.indexOf(oldNode);
        if (oldIdx < 0) {
            return false;
        }
        children.set(idx, node);
        titleBarProperty.set(node);
        return true;
    }

    public DockFloatingProperty floatingProperty() {
        return floatingProperty;
    }

    public boolean isFloating() {
        return this.floatingProperty.get();
    }

    public void undock() {
        if (!isDocked()) {
            return;
        }
        setDocked(false, owner);
        if (owner != null && (owner instanceof MultiTab)) {
            ((MultiTab) owner).remove(getDockable());
        } else {
            parent.remove(getNode());
        }
        //!!!!!!!! must we assign null to owner ?????
    }

    public void setFloating(boolean floating) {
        if (isFloating()) {
            return;
        }
        StateTransformer t = new StateTransformer(this);
        t.makeFloating();
        floatingProperty.set(floating);
    }

    public DockedProperty dockedProperty() {
        return dockedProperty;
    }

    public boolean isResizable() {
        return resizableProperty.get();
    }

    public void setResizable(boolean resizable) {
        resizableProperty.set(resizable);
    }

    public boolean isDocked() {
        
        if (!isFloating() && parent == null ) {
            return false;
        }
        if (isFloating() ) {
            return false;
        }
        if (!dockedProperty.get() ) {
            return false;
        }
        return true;
        //return !isFloating() && parent.parentSplitPane(getNode()) != null;
    }

    public void setDocked(boolean docked) {
        owner = null;
        this.dockedProperty.set(docked);
    }

    public void setDocked(boolean docked, Dockable owner ) {
        this.owner = owner;
        this.dockedProperty.set(docked);
    }

    public Region createDefaultTitleBar(String title) {
        DockTitleBar tb = new DockTitleBar(getDockable());
        tb.setId("FIRST");
        tb.getLabel().textProperty().bind(titleProperty);
        titleProperty.set(title);
        titleBarProperty().set(tb);
        return tb;
    }

    public Dockable getImmediateParent(Node node) {
        Dockable retval = getDockable();
        if (immediateParent != null) {
            retval = immediateParent.apply(node);
        }
        return retval;
    }

    private Function<Node, Dockable> immediateParent = null;

    public void setImmediateParentFunction(Function<Node, Dockable> f) {
        immediateParent = f;
    }
        public Function<Integer, String> converter = (i) -> Integer.toString(i);

        public void setConverter(Function<Integer, String> converter) {
            this.converter = converter;
        }
        public Function<Integer, String> getConverter() {
            return this.converter;
        }
}
