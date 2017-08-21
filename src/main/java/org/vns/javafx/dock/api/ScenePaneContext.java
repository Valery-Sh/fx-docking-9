package org.vns.javafx.dock.api;

import org.vns.javafx.dock.api.save.DockTreeItemBuilder;
import org.vns.javafx.dock.api.indicator.PositionIndicator;
import org.vns.javafx.dock.api.indicator.SideIndicator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery Shyshkin
 */
public class ScenePaneContext extends TargetContext {

    private final Dockable dockable;

    private ChangeListener<? super Parent> parentListener;

    public ScenePaneContext(Dockable dockable) {
        super(dockable);
        this.dockable = dockable;
        init();

    }

    private void init() {
        parentListener = this::parentChanged;
        if (isDocked(dockable.node())) {
            setTargetNode((Pane) dockable.node().getParent());
        }
        dockable.node().parentProperty().addListener(parentListener);
    }

    @Override
    protected PositionIndicator createPositionIndicator() {
        return new SideIndicator.PaneSideIndicator(this);
    }

    protected void parentChanged(ObservableValue<? extends Parent> value, Parent oldValue, Parent newValue) {
        if (newValue != null && !(newValue instanceof Pane)) {
            return;
        }

        setTargetNode((Pane) newValue);

        if (oldValue != null) {
            oldValue.parentProperty().removeListener(parentListener);
        }
    }

    @Override
    protected boolean isDocked(Node node) {
        boolean retval = false;
        if (DockRegistry.instanceOfDockable(node)) {
            if (node.getParent() != null && (node.getParent() instanceof Pane)) {
                retval = true;
            }
        }
        return retval;
    }
    /**
     * For test purpose
     *
     * @return the list of dockables
     */    
    public ObservableList<Dockable> getDockables() {
        ObservableList<Dockable> list = FXCollections.observableArrayList();
        
/*        if (dockNode.getParent() != null && (dockNode.getParent() instanceof Pane)) {
            ((Pane) dockNode.getParent()).getChildren().remove(dockNode);
        }
*/
        return null;
    }

    @Override
    public void remove(Node dockNode) {
/*22.06.2017        if (dockNode.getParent() != null && dockNode.getParent().getParent() != null
                && (dockNode.getParent().getParent() instanceof SplitPane)) {
            ((SplitPane) dockNode.getParent().getParent()).getItems().remove(dockNode);
        } else 
*/
        if (dockNode.getParent() != null && (dockNode.getParent() instanceof Pane)) {
            ((Pane) dockNode.getParent()).getChildren().remove(dockNode);
        }
    }

    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DockTreeItemBuilder getDockTreeTemBuilder() {
        return null;
    }

    @Override
    public Object getRestorePosition(Dockable dockable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restore(Dockable dockable, Object restoreposition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
