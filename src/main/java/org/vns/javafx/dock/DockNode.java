package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "content")
public class DockNode extends Control implements Dockable {

    private DockableContext dockableContext;
    ObjectProperty<Node> content = new SimpleObjectProperty<>();
    
    private VBox delegate;

    public DockNode() {
        init(null, null);
    }

    public DockNode(String title) {
        init(null, title);
    }

    public DockNode(String id, String title) {
        init(null, title);
    }

    private void init(String id, String title) {
        contentProperty().addListener(this::contentChanged);
        
        getStyleClass().add("dock-node");
        dockableContext = new DockableContext(this);
        dockableContext.titleBarProperty().addListener(this::titlebarChanged);
        dockableContext.createDefaultTitleBar(title);
        
        if (id != null) {
            setId(id);
        }
        
        setContent(new StackPane());
        
    }
    public ObjectProperty<Node> contentProperty() { 
        return content;
    }
    public Node getContent() {
        return content.get();
    }
    public void setContent(Node content) {
        this.content.set(content);
    }    
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    protected void contentChanged(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        if (oldValue != null) {
            getDelegate().getChildren().remove(oldValue);
        }
        if (newValue != null) {
            getDelegate().getChildren().add(newValue);
        }
    }

    protected VBox getDelegate() {
        if (delegate == null) {
            delegate = new VBox();
        }
        return delegate;
    }

    public String getTitle() {
        return dockableContext.getTitle();
    }

    public void setTitle(String title) {
        dockableContext.setTitle(title);
    }

    public Region getTitleBar() {
        return dockableContext.getTitleBar();
    }

    public void setTitleBar(Region node) {
        dockableContext.setTitleBar(node);
    }

    public boolean isRemoveTitleBar() {
        return getTitleBar() == null;
    }

    public void setRemoveTitleBar(boolean remove) {
        if (remove) {
            setTitleBar(null);
        }
    }

    public Node getDragNode() {
        return dockableContext.getDragNode();
    }

    public void setDragNode(Node dragSource) {
        dockableContext.setDragNode(dragSource);
    }

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableContext getContext() {
        return dockableContext;
    }

    protected void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if (oldValue != null) {
            getDelegate().getChildren().remove(oldValue);
        }
        if (newValue != null) {
            getDelegate().getChildren().add(0, newValue);
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockNodeSkin(this);
    }
/*
    @Override
    protected double computePrefHeight(double h) {
        return delegate.computePrefHeight(h);
    }

    @Override
    protected double computePrefWidth(double w) {
        return delegate.computePrefWidth(w);
    }

    @Override
    protected double computeMinHeight(double h) {
        return delegate.computeMinHeight(h);
    }

    @Override
    protected double computeMinWidth(double w) {
        return delegate.computeMinWidth(w);
    }

    @Override
    protected double computeMaxHeight(double h) {
        return delegate.computeMaxHeight(h);
    }

    @Override
    protected double computeMaxWidth(double w) {
        return delegate.computeMaxWidth(w);
    }
*/
    public static class DockNodeSkin extends SkinBase<DockNode> {

        public DockNodeSkin(DockNode control) {
            super(control);
            if (!getChildren().isEmpty()) {
                getChildren().clear();
            }
            getChildren().add(control.getDelegate());
/*            if (control.getTitleBar() != null && !control.getDelegate().getChildren().contains(control.getTitleBar())) {
                control.getDelegate().getChildren().add(control.getTitleBar());
            }
            control.getDelegate().getChildren().add(control.getContent());
*/
        }
    }

/*    public static class VBoxCustom extends VBox {

        public VBoxCustom() {
        }

        public VBoxCustom(double spacing) {
            super(spacing);
        }

        public VBoxCustom(Node... children) {
            super(children);
        }

        public VBoxCustom(double spacing, Node... children) {
            super(spacing, children);
        }

        @Override
        protected double computePrefHeight(double h) {
            return super.computePrefHeight(h);
        }

        @Override
        protected double computePrefWidth(double w) {
            return super.computePrefWidth(w);
        }

        @Override
        protected double computeMinHeight(double h) {
            return super.computeMinHeight(h);
        }

        @Override
        protected double computeMinWidth(double w) {
            return super.computeMinWidth(w);
        }

        @Override
        protected double computeMaxHeight(double h) {
            return super.computeMaxHeight(h);
        }

        @Override
        protected double computeMaxWidth(double w) {
            return super.computeMaxWidth(w);
        }
    }
*/
}
