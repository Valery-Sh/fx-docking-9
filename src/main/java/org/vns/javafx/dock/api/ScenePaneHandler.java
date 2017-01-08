package org.vns.javafx.dock.api;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery Shyshkin
 */
public class ScenePaneHandler extends PaneHandler{
    
    private final Dockable dockable;
    
    private ChangeListener<? super Parent> parentListener;
            
    public ScenePaneHandler(Dockable dockable) {
        super(dockable);
        this.dockable = dockable;
        init();
        
    }
    private void init() {
        parentListener  = this::parentChanged;
        if ( isDocked(dockable.node()) ) {
            changeDockedState(dockable, true);
            setDockPane((Pane) dockable.node().getParent());
        }
        dockable.node().parentProperty().addListener(parentListener);
    }
    protected void parentChanged(ObservableValue<? extends Parent> value, Parent oldValue, Parent newValue) {
        if ( newValue != null && ! (newValue instanceof Pane)) {
            return;
        }
        
        setDockPane((Pane) newValue);
        if ( newValue != null  ) {
            changeDockedState(dockable, true);

        }
        if ( oldValue != null ) {
            oldValue.parentProperty().removeListener(parentListener);
        }
    }
    
    @Override
    protected boolean isDocked(Node node) {
        boolean retval = false;
        if (DockRegistry.isDockable(node)) {
            if ( node.getParent() != null && (node.getParent() instanceof Pane)) {
                retval = true;
            }
        }
        return retval;
    }
    @Override
    public void remove(Node dockNode) {
        if ( dockNode.getParent() != null && (dockNode.getParent() instanceof Pane)) {
            ((Pane)dockNode.getParent()).getChildren().remove(dockNode);
        }
    }
    
}
