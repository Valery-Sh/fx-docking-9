package org.vns.javafx.dock;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.DockTabPaneContext;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTabPane extends TabPane implements Dockable, DockTarget {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    //private final StringProperty title = new SimpleStringProperty();
    private final DockableContext dockableContext = new DockableContext(this);

//    private Label dragLabel;
//    private Button dragButton;
    private Node dragShape;

    //private final Map<Dockable, Object> listeners = new HashMap<>();
    private DockTabPaneContext paneContext;

    public DockTabPane() {
        init();
    }

    private void init() {
        paneContext = new DockTabPaneContext(this);
//        paneContext.getLookup().add(new IndicatorPopup(paneContext));
        dragShape = createDefaultDragNode();
        getChildren().add(dragShape);
        dockableContext.setDragNode(dragShape);
        dragShape.setLayoutX(4);
        dragShape.setLayoutY(4);

        Platform.runLater(() -> {
            dragShape.toFront();
        });

        getStyleClass().add("dock-tab-pane");
        getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

        getContext().setTitleBar(new DockTitleBar(this));

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

    protected Node createDefaultDragNode() {
        DropShadow ds = new DropShadow();
        ds.setOffsetY(1.0);
        ds.setOffsetX(1.0);
        ds.setColor(Color.GRAY);
        Circle c = new Circle();
        c.setEffect(ds);
        c.setCenterX(0);
        c.setCenterY(0);

        //c.setRadius(30.0f);
        c.setRadius(3.0f);
        c.setFill(Color.WHITE);
        return c;
    }

    public void setDragNode(Node dragNode) {
        dockableContext.setDragNode(dragNode);
    }

    /*    public void openDragTag() {
        openDragTag(0);
    }
     */
    public StringProperty titleProperty() {
        return getContext().titleProperty();
    }

    public DockableContext getNodeContext() {
        return dockableContext;
    }

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableContext getContext() {
        return this.dockableContext;
    }

    @Override
    public Region target() {
        return this;
    }

    @Override
    public TargetContext getTargetContext() {
        return paneContext;
    }

    public void dock(Dockable dockable) {
        paneContext.doDock(0, dockable.node());
    }

    public void dock(int idx, Dockable dockable) {
        if (!getTargetContext().isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }

        if (dockable.getContext().getTargetContext() != null) {
            dockable.getContext().getTargetContext().undock(dockable.node());
        }
        paneContext.doDock(idx, dockable.node());
    }

    public void dockNode(Node node) {
        paneContext.doDock(0, node);
    }

    public void dock(int idx, Node node) {
        Dockable dockable = Dockable.of(node);
        if (!getTargetContext().isAcceptable(dockable)) {
            throw new UnsupportedOperationException("The node '" + dockable + "' to be docked is not registered by the DockLoader");
        }

        if (dockable.getContext().getTargetContext() != null) {
            dockable.getContext().getTargetContext().undock(dockable.node());
        }
        paneContext.doDock(idx, dockable.node());
    }

}//DockTabPane
