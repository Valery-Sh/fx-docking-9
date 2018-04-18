package org.vns.javafx.dock;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DockTabPaneContext;
import org.vns.javafx.dock.api.DockTabPaneMouseDragHandler;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.TabPaneHelper;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.DockLayout;

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
        LayoutContext paneContext = new DockTabPaneContext(this);
        DockRegistry.makeDockLayout(this, paneContext);
        DockableContext context = DockRegistry.makeDockable(this).getContext();
        context.setLayoutContext(DockLayout.of(this).getLayoutContext());

        getStyleClass().add("dock-tab-pane");
        getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

        DockTabPaneMouseDragHandler dragHandler = new DockTabPaneMouseDragHandler(context);
        context.getLookup().putUnique(MouseDragHandler.class, dragHandler);
        
        //context.setTitleBar(new DockTitleBar(Dockable.of(this)));

        setRotateGraphic(true);
    }


    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public void dock(Dockable dockable) {
        ((DockTabPaneContext) DockLayout.of(this).getLayoutContext()).doDock(0, dockable.node());
    }

    public void dock(int idx, Dockable dockable) {
        if (!DockLayout.of(this).getLayoutContext().isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }

        if (dockable.getContext().getLayoutContext() != null) {
            //03.04dockable.getContext().getLayoutContext().undock(dockable.node());
            dockable.getContext().getLayoutContext().undock(dockable);
        }
        ((DockTabPaneContext) DockLayout.of(this).getLayoutContext()).doDock(idx, dockable.node());
    }

    public void dockNode(Node node) {
        ((DockTabPaneContext) DockLayout.of(this).getLayoutContext()).doDock(0, node);
    }

    public void dock(int idx, Node node) {
        Dockable dockable = Dockable.of(node);
        if (!DockLayout.of(this).getLayoutContext().isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }

        if (dockable.getContext().getLayoutContext() != null) {
            //03.04dockable.getContext().getLayoutContext().undock(dockable.node());
            dockable.getContext().getLayoutContext().undock(dockable);
        }
        ((DockTabPaneContext) DockLayout.of(this).getLayoutContext()).doDock(idx, dockable.node());
    }
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if ( Dockable.of(this).getContext().getDragNode() == null ) {
            TabPaneHelper helper = new TabPaneHelper(DockLayout.of(this).getLayoutContext());
            Dockable.of(this).getContext().setDragNode(helper.getHeaderArea());
        }
    }

}//DockTabPane
