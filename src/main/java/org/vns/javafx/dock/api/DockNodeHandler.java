package org.vns.javafx.dock.api;

import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.util.Properties;
import java.util.function.Function;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.properties.DockFloatingProperty;
import org.vns.javafx.dock.api.properties.DockPaneHandlerProperty;
import org.vns.javafx.dock.api.properties.DockResizableProperty;
import org.vns.javafx.dock.api.properties.DockedProperty;
import org.vns.javafx.dock.api.properties.TitleBarProperty;

/**
 *
 * @author Valery
 */
public class DockNodeHandler {

    private DoubleProperty dividerPosProperty = new SimpleDoubleProperty(-1);

    private final TitleBarProperty<Region> titleBarProperty;

    private final StringProperty titleProperty = new SimpleStringProperty("");
    private final Dockable dockable;
    private final DockFloatingProperty floatingProperty = new DockFloatingProperty(false);
    private final DockedProperty dockedProperty = new DockedProperty(false);
    private final DockResizableProperty resizableProperty = new DockResizableProperty(true);

    private boolean usedAsDockTarget = true;

    private DragManager dragManager;

    PaneHandler scenePaneHandler;
    //private PaneDelegate paneDelegate;
    /**
     * Last dock target pane
     */
    //private DockPaneHandler originalPaneHandler;

    private final DockPaneHandlerProperty<PaneHandler> paneHandlerProperty = new DockPaneHandlerProperty<>();

//    private Pane lastDockPane;
    private StringProperty dockPosProperty = new SimpleStringProperty(null);

    private Properties properties;

    public DockNodeHandler(Dockable dockable) {
        this.dockable = dockable;
        titleBarProperty = new TitleBarProperty(dockable.node());
        init();
    }

    private void init() {
        dockedProperty.addListener(this::dockedChanged);
        dragManager = getDragManager();
        titleBarProperty.addListener(this::titlebarChanged);
        scenePaneHandler = new ScenePaneHandler(dockable);
        paneHandlerProperty.set(scenePaneHandler);
        paneHandlerProperty.addListener(this::paneHandlerChanged);
        dividerPosProperty.addListener(this::dividerPosChanged);
    }

    protected void dividerPosChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if ( getPaneHandler() != null ) {
            getPaneHandler().dividerPosChanged(dockable().node(),(Double)oldValue, (Double)newValue);
        }
    }

    public Node getDragNode() {
        return getDragManager().getDragNode();
    }

    public void setDragNode(Node dragSource) {
        getDragManager().setDragNode(dragSource);
    }

    protected DragManager getDragManager() {
        if (dragManager == null) {
            dragManager = new DragManager(dockable);
        }
        return dragManager;
    }

    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }

    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

    protected void paneHandlerChanged(ObservableValue<? extends PaneHandler> observable, PaneHandler oldValue, PaneHandler newValue) {
        if (newValue == null) {
            paneHandlerProperty.set(scenePaneHandler);
        }
    }

    public StringProperty dockPosProperty() {
        return dockPosProperty;
    }

    public String getDockPos() {
        return dockPosProperty.get();
    }

    public DoubleProperty dividerPosProperty() {
        return dividerPosProperty;
    }

    public double getDividerPos() {
        return dividerPosProperty.get();
    }

    public void setDividerPos(double dividerPos) {
        this.dividerPosProperty.set(dividerPos);
    }

    public Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
        }
        return properties;
    }

    public void setDockPos(String dockPos) {
        this.dockPosProperty.set(dockPos);
    }

    public TitleBarProperty<Region> titleBarProperty() {
        return titleBarProperty;
    }

    public StringProperty titleProperty() {
        return titleProperty;
    }

    public String getTitle() {
        return titleProperty.get();
    }

    public void setTitle(String title) {
        this.titleProperty.set(title);
    }

    public PaneHandler getPaneHandler() {
        return paneHandlerProperty.get();
    }

    public void setPaneHandler(PaneHandler paneHandler) {
        //System.err.println("setPaneHandler = " + paneHandler);
        this.paneHandlerProperty.set(paneHandler);
    }

    protected void dockedChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            //System.err.println("DockNodeHandler : dockedChanged");
            getPaneHandler().remove(node());
            setPaneHandler(null);
            //setDividerPos(-1);
            setDockPos("TOP");
        }
    }

    public Dockable dockable() {
        return this.dockable;
    }

    public Region node() {
        return dockable.node();
    }

    public Region getTitleBar() {
        return titleBarProperty.get();
    }

    public void setTitleBar(Region node) {
        titleBarProperty.set(node);
    }

    public boolean addTitleBar(int idx, Region newTitleBar, ObservableList children) {
        if (titleBarProperty.get() != null) {
            return false;
        }
        children.add(idx, newTitleBar);
        titleBarProperty.set(newTitleBar);
        return true;
    }

    public boolean replaceTitleBar(int idx, Region newTitleBar, ObservableList children) {
        if (titleBarProperty.get() == null) {
            return false;
        }
        Node oldNode = titleBarProperty.get();
        int oldIdx = children.indexOf(oldNode);
        if (oldIdx < 0) {
            return false;
        }
        children.set(idx, newTitleBar);
        titleBarProperty.set(newTitleBar);
        return true;
    }

    public DockFloatingProperty floatingProperty() {
        return floatingProperty;
    }

    public boolean isFloating() {
        return this.floatingProperty.get();
    }

    public void setFloating(boolean floating) {
        if (!isFloating() && floating) {
            FloatStageBuilder t = getStageBuilder();
            t.makeFloating();
            floatingProperty.set(floating);
        } else if (!floating) {
            floatingProperty.set(floating);
        }
    }

    public FloatStageBuilder getStageBuilder() {
        return getPaneHandler().getStageBuilder(dockable);
    }

    protected DockedProperty dockedProperty() {
        return dockedProperty;
    }

    public boolean isResizable() {
        return resizableProperty.get();
    }

    public void setResizable(boolean resizable) {
        resizableProperty.set(resizable);
    }

    public boolean isDocked() {
        return dockedProperty.get();
    }

    protected void setDocked(boolean docked) {
        this.dockedProperty.set(docked);
    }

    public Region createDefaultTitleBar(String title) {
        DockTitleBar tb = new DockTitleBar(dockable());
        tb.setId("FIRST");
        tb.getLabel().textProperty().bind(titleProperty);
        titleProperty.set(title);
        titleBarProperty().set(tb);
        return tb;
    }

    public Dockable getImmediateParent(Node node) {
        Dockable retval = dockable();
        if (immediateParent != null) {
            retval = immediateParent.apply(node);
        }
        return retval;
    }

    private Function<Node, Dockable> immediateParent = null;

    public void setImmediateParentFunction(Function<Node, Dockable> f) {
        immediateParent = f;
    }

    protected void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        getProperties().remove("nodeHandler-titlebar-minheight");
        getProperties().remove("nodeHandler-titlebar-minwidth");
        dragManager.titlebarChanged(ov, oldValue, newValue);
    }

    public boolean isTitleBarHidden() {
        return getTitleBar() == null || (getTitleBar().getMinHeight() == 0 && getTitleBar().getMinWidth() == 0);
    }

    protected void saveTitleBar() {
        if (getTitleBar() == null) {
            getProperties().remove("nodeHandler-titlebar-minheight");
            getProperties().remove("nodeHandler-titlebar-minwidth");
            return;
        }
        getProperties().put("nodeHandler-titlebar-minheight", getTitleBar().getMinHeight());
        getProperties().put("nodeHandler-titlebar-minwidth", getTitleBar().getMinWidth());
    }

    public void hideTitleBar() {
        saveTitleBar();
        if (getTitleBar() == null) {
            return;
        }
        getTitleBar().setMinHeight(0);
        getTitleBar().setPrefHeight(0);
    }

    public void showTitleBar() {
        if (getTitleBar() == null) {
            return;
        }
        if (isTitleBarHidden()) {
            getTitleBar().setMinHeight((double) getProperties().get("nodeHandler-titlebar-minheight"));
            getTitleBar().setMinWidth((double) getProperties().get("nodeHandler-titlebar-minwidth"));
        }
    }

}
