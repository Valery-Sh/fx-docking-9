package org.vns.javafx.dock.api;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 *
 * @author Valery Shyshkin
 */
public class ScenePaneContext extends LayoutContext {

    private final Dockable dockable;
    private LayoutContext restoreContext;
    
    //private ChangeListener<? super Parent> parentListener;

    public ScenePaneContext(Dockable dockable) {
        super();
        this.dockable = dockable;
        init();

    }

    private void init() {
        //parentListener = this::parentChanged;
        //if (isDocked(dockable.node())) {
        //if (dockable.node().getParent() != null ) {
            //setTargetNode(dockable.node().getParent());
        //}
        dockable.node().parentProperty().addListener(this::parentChanged);
    }

    @Override
    protected void initLookup(ContextLookup lookup) {
    }

    protected void parentChanged(ObservableValue<? extends Parent> value, Parent oldValue, Parent newValue) {
        //if (newValue != null && !(newValue instanceof Pane)) {
        if (oldValue != null) {
            oldValue.parentProperty().removeListener(this::parentChanged);
        }
        
/*        if (newValue != null) {
            return;
        }
*/
        setLayoutNode(newValue);

    }

    @Override
    protected boolean isDocked(Node node) {
        return Dockable.of(node) != null && Dockable.of(node).getContext().getLayoutContext() == this && node.getParent() != null;
//        return node.getParent() != null;
/*        boolean retval = false;
        if (DockRegistry.isDockable(node)) {
            retval = DockUtil.getOwnerWindow(node) != null;
        }
        return retval;
*/        
    }

    /**
     * For test purpose
     *
     * @return the list of dockables
     */
/*    public ObservableList<Dockable> getDockables() {
        ObservableList<Dockable> list = FXCollections.observableArrayList();
        return null;
    }
*/
    @Override
    public void remove(Node dockNode) {
        if ( ! isDocked(dockNode) ) {
            return;
        }
        if ( DockRegistry.getInstance().getBeanRemover() != null ) {
            DockRegistry.getInstance().getBeanRemover().remove(dockNode);
        } 
        //else if (dockNode.getParent() != null && (dockNode.getParent() instanceof Pane)) {
            //((Pane) dockNode.getParent()).getChildren().remove(dockNode);
        //}
    }

    public LayoutContext getRestoreContext() {
        return restoreContext;
    }

    public void setRestoreContext(LayoutContext restoreContext) {
        this.restoreContext = restoreContext;
    }

    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean restore(Dockable dockable) {
        return false;

    }

}
