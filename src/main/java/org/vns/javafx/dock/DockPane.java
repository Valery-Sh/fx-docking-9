package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
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
@DefaultProperty(value = "root")
public class DockPane extends Control {

    //private HPane root;
    private final ReadOnlyObjectWrapper<HPane> rootWrapper = new ReadOnlyObjectWrapper<>(new HPane());    
    private final ReadOnlyObjectProperty<HPane> root = rootWrapper.getReadOnlyProperty();
    //private ObservableList<Node> items = root.get().getItems();
    
    private ObjectProperty<Node> titleBar = new SimpleObjectProperty<>();

    public DockPane() {
        super();
        init();
    }

    private void init() {
        LayoutContext tc = new DockPaneContext(this, getRoot());
        DockRegistry.makeDockLayout(this, tc);
        Dockable d = DockRegistry.makeDockable(this);
        d.getContext().setDragNode(null);
    }
    
    public ReadOnlyObjectProperty<HPane> rootProperty() {
        return root;
    }
    public HPane getRoot() {
        return root.get();
    }

    public ObservableList<Node> getItems() {
        return getRoot().getItems();
    }

    public Node getTitleBar() {
        return titleBar.get();
    }

    protected void setTitleBar(Node titleBar) {
        this.titleBar.set(titleBar);
    }

    protected ObjectProperty<Node> titleBarProperty() {
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
        Dockable dockable = Dockable.of(node);
        if (dockable == null) {
            return;
        }
        DockPaneContext layoutContex = (DockPaneContext) DockLayout.of(this).getLayoutContext();
        if (!layoutContex.isAcceptable(dockable)) {
            //throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getContext().getLayoutContext() != null) {
            //03.04dockable.getContext().getLayoutContext().undock(dockable.node());
            dockable.getContext().getLayoutContext().undock(dockable);
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
            //throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }
        if (dockable.getContext().getLayoutContext() != null) {
            //03.04dockable.getContext().getLayoutContext().undock(dockable.node());
            dockable.getContext().getLayoutContext().undock(dockable);
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
