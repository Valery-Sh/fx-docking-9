package org.vns.javafx.dock.api;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery Shyshkin
 */
public class ScenePaneHandler extends PaneHandler {

    private final Dockable dockable;

    private ChangeListener<? super Parent> parentListener;

    public ScenePaneHandler(Dockable dockable) {
        super(dockable);
        this.dockable = dockable;
        init();

    }

    private void init() {
        parentListener = this::parentChanged;
//        if ( isDocked(dockable.node()) || dockable.node().getParent() != null ) {
        if (isDocked(dockable.node())) {
            changeDockedState(dockable, true);
            setDockPane((Pane) dockable.node().getParent());
        }
        dockable.node().parentProperty().addListener(parentListener);
    }

    protected void parentChanged(ObservableValue<? extends Parent> value, Parent oldValue, Parent newValue) {
        if (newValue != null && !(newValue instanceof Pane)) {
            return;
        }

        setDockPane((Pane) newValue);
        if (newValue != null) {
            System.err.println("ScenePaneHandler");
            //setDragPopup(new DragPopup(this));
        }
        if (newValue != null) {
            changeDockedState(dockable, true);

        }
        if (oldValue != null) {
            oldValue.parentProperty().removeListener(parentListener);
        }
    }

    @Override
    protected boolean isDocked(Node node) {
        System.err.println("DDD isDocked");
        boolean retval = false;
        if (DockRegistry.isDockable(node)) {
            if (node.getParent() != null && (node.getParent() instanceof Pane)) {
                retval = true;
            }
        }
        return retval;
    }

    @Override
    public void remove(Node dockNode) {
/*        System.err.println("ScenePaneHandler DDD remove dockNode.getParent()=" + dockNode.getParent());
        System.err.println("getParent().getParent=" + dockNode.getParent().getParent());
        System.err.println("getParent().getParent.getParent=" + dockNode.getParent().getParent().getParent());
*/        
        if (dockNode.getParent() != null && dockNode.getParent().getParent() != null
                && (dockNode.getParent().getParent() instanceof SplitPane)) {
//            System.err.println("DDD removed from splitPane");
            ((SplitPane) dockNode.getParent().getParent()).getItems().remove(dockNode);
        }

        if (dockNode.getParent() != null && (dockNode.getParent() instanceof Pane)) {
//            System.err.println("DDD removed !!!!!!!!!!!! sz=" + ((Pane) dockNode.getParent()).getChildren().size());
            ((Pane) dockNode.getParent()).getChildren().remove(dockNode);
            //System.err.println("getParent().getParent=" + dockNode.getParent().getParent());
            //System.err.println("getParent().getParent.getParent=" + dockNode.getParent().getParent().getParent());
        }
    }

}
