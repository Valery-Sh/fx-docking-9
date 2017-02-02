package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty(value = "content")
public class DockNode extends Control implements Dockable {

    DockNodeHandler nodeHandler = new DockNodeHandler(this);


    private VBox delegate;// = new DockPane();

    public DockNode() {
        init(null);
    }

    public DockNode(String title) {
        init(title);

    }

    protected VBox getDelegate() {
        if (delegate == null) {
            delegate = new VBox();
        }
        return delegate;
    }

    private void init(String title) {
        Region titleBar = new DockTitleBar(this);
        getDelegate().getChildren().add(titleBar);
        getDelegate().getStyleClass().add("delegate");
        nodeHandler.setTitleBar(titleBar);
        nodeHandler.titleBarProperty().addListener(this::titlebarChanged);
        nodeHandler.removeTitleBarProperty().addListener(this::removeTitleBarPropertyChanged);
        setTitle(title);
    }

    protected void removeTitleBarPropertyChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue && getTitleBar() != null) {
            getDelegate().getChildren().remove(getTitleBar());
        }
    }

    public String getTitle() {
        return nodeHandler.getTitle();
    }

    public void setTitle(String title) {
        nodeHandler.setTitle(title);
    }

    public Region getTitleBar() {
        return nodeHandler().getTitleBar();
    }

    public void setTitleBar(Region node) {
        System.err.println("setTitleBar.DockNode=" + this + "; tb=" + node);
        nodeHandler().setTitleBar(node);
    }

    public boolean isRemoveTitleBar() {
        return getTitleBar() == null;
    }

    public void setRemoveTitleBar(boolean remove) {
        if (remove) {
            setTitleBar(null);
        }
    }

    /*    public String getDockPos() {
        return nodeHandler.getDockPos();
    }

    public void setDockPos(String dockpos) {
        this.nodeHandler.setDockPos(dockpos);
    }
     */
    public double getDividerPos() {
        return nodeHandler.getDividerPos();
    }

    public void setDividerPos(double divpos) {
        this.nodeHandler.setDividerPos(divpos);
    }

    public Node getDragNode() {
        return nodeHandler.getDragNode();
    }

    public void setDragNode(Node dragSource) {
        nodeHandler.setDragNode(dragSource);
    }

    @Override
    public Control node() {
        return this;
    }

    @Override
    public DockNodeHandler nodeHandler() {
        return nodeHandler;
    }

    protected void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        //Node oldTb = nodeHandler().getTitleBar();

        if (oldValue != null && newValue == null) {
            getDelegate().getChildren().remove(0);
        } else if (newValue != null) {
            getDelegate().getChildren().remove(0);
            getDelegate().getChildren().add(0, newValue);
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockNodeSkin(this);
    }

    public Region getContent() {
        if (getDelegate().getChildren().size() > 1) {
            return (Region) getDelegate().getChildren().get(1);
        } else {
            return null;
        }
    }

    public void setContent(Region content) {
        if (getDelegate().getChildren().size() > 1) {
            return;
        }
        getDelegate().getChildren().add(content);
    }

    public static class DockNodeSkin extends SkinBase<DockNode> {

        public DockNodeSkin(DockNode control) {
            super(control);
            getChildren().add(control.getDelegate());
        }
    }

}
