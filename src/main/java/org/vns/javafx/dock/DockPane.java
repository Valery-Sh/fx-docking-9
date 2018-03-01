package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.vns.javafx.dock.api.DockPaneContext;
import org.vns.javafx.dock.api.DockPaneSkin;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.DockLayout;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "items")
public class DockPane extends Control {

    private HPane root;
    private ObjectProperty<Node> titleBar = new SimpleObjectProperty<>();
            
    public DockPane() {
        super();
        init();
    }

    private void init() {
        root = new HPane();
        LayoutContext tc = new DockPaneContext(this, root);
        DockRegistry.makeDockLayout(this, tc);
        Dockable d = DockRegistry.makeDockable(this);
        d.getContext().setDragNode(null);
    }

    public HPane getRoot() {
        return root;
    }

    
    public ObservableList<Node> getItems() {
        return root.getItems();
    }

    public Node getTitleBar() {
        return titleBar.get();
    }

    public void setTitleBar(Node titleBar) {
        this.titleBar.set(titleBar);
    }
    
    public ObjectProperty<Node> titleBarProperty() {
        return titleBar;
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        DockPaneSkin skin = new DockPaneSkin(this);
        return skin;
    }

    public void dock(Node node, Side side) {
        Dockable dockable =  Dockable.of(node);
        if ( dockable == null ) {
            return;
        }
        DockPaneContext layoutContex = (DockPaneContext) DockLayout.of(this).getLayoutContext();
        if (!layoutContex.isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getContext().getLayoutContext() != null) {
            dockable.getContext().getLayoutContext().undock(dockable.node());
        }
        layoutContex.dock(dockable, side);
    }
 /*   public void dockNode(Node dockableNode, Side side) {
        dock( dockableNode, side);
    }

    public void dockNode(Node dockableNode, Side side, Dockable layoutNode) {
        dock( dockableNode, side, layoutNode);
    }
*/    
    public void dock(Node dockableNode, Side side, Dockable dockableTarget) {
        Dockable dockable = Dockable.of(dockableNode);
        Dockable target = Dockable.of(dockableTarget);
        
                
        DockPaneContext targetContext = (DockPaneContext) DockLayout.of(this).getLayoutContext();
        if (!targetContext.isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getContext().getLayoutContext() != null) {
            dockable.getContext().getLayoutContext().undock(dockable.node());
        }
        targetContext.dock(dockable, side, target);
    }

    public boolean isUsedAsDockLayout() {
        DockPaneContext layoutContext = (DockPaneContext) DockLayout.of(this).getLayoutContext();
        return layoutContext.isUsedAsDockLayout();
    }

    public void setUsedAsDockLayout(boolean usedAsDockLayout) {
        DockPaneContext targetContext = (DockPaneContext) DockLayout.of(this).getLayoutContext();        
        targetContext.setUsedAsDockLayout(usedAsDockLayout);
    }
}//class
