package org.vns.javafx.dock;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DockTabPaneContext;
import org.vns.javafx.dock.api.DockTabPaneMouseDragHandler;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TabPaneHelper;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPane extends TabPane {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");


    public DockTabPane() {
        init();
    }

    private void init() {
        TargetContext paneContext = new DockTabPaneContext(this);
        DockRegistry.makeDockTarget(this, paneContext);
        DockableContext context = DockRegistry.makeDockable(this).getContext();
        context.setTargetContext(DockTarget.of(this).getTargetContext());

        getStyleClass().add("dock-tab-pane");
        getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

        DockTabPaneMouseDragHandler dragHandler = new DockTabPaneMouseDragHandler(context);
        context.getLookup().putUnique(MouseDragHandler.class, dragHandler);
        
        //context.setTitleBar(new DockTitleBar(Dockable.of(this)));

        setRotateGraphic(true);
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public void dock(Dockable dockable) {
        ((DockTabPaneContext) DockTarget.of(this).getTargetContext()).doDock(0, dockable.node());
    }

    public void dock(int idx, Dockable dockable) {
        if (!DockTarget.of(this).getTargetContext().isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }

        if (dockable.getContext().getTargetContext() != null) {
            dockable.getContext().getTargetContext().undock(dockable.node());
        }
        ((DockTabPaneContext) DockTarget.of(this).getTargetContext()).doDock(idx, dockable.node());
    }

    public void dockNode(Node node) {
        ((DockTabPaneContext) DockTarget.of(this).getTargetContext()).doDock(0, node);
    }

    public void dock(int idx, Node node) {
        Dockable dockable = Dockable.of(node);
        if (!DockTarget.of(this).getTargetContext().isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }

        if (dockable.getContext().getTargetContext() != null) {
            dockable.getContext().getTargetContext().undock(dockable.node());
        }
        ((DockTabPaneContext) DockTarget.of(this).getTargetContext()).doDock(idx, dockable.node());
    }
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if ( Dockable.of(this).getContext().getDragNode() == null ) {
            TabPaneHelper helper = new TabPaneHelper(DockTarget.of(this).getTargetContext());
            Dockable.of(this).getContext().setDragNode(helper.getHeaderArea());
        }
    }

}//DockTabPane
