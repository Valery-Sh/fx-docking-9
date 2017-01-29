/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.controls;

import javafx.beans.DefaultProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.DockNodeBase;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "content")
public class DockNode extends Control implements Dockable {

    DockNodeHandler nodeHandler = new DockNodeHandler(this);

    private DockNodeBase delegate1;// = new DockPane();
    //private Node root;
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
        nodeHandler.setTitleBar(titleBar);
        nodeHandler.titleBarProperty().addListener(this::titlebarChanged);
        setTitle(title);
    }

    public String getTitle() {
        return nodeHandler.getTitle();
    }

    public void setTitle(String title) {
        nodeHandler.setTitle(title);
    }

    public DockNodeHandler getDockNodeHandler() {
        return nodeHandler;
    }

    public String getDockPos() {
        return nodeHandler.getDockPos();
    }

    public void setDockPos(String dockpos) {
        this.nodeHandler.setDockPos(dockpos);
    }

    public void setDragSource(Node dragSource) {
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
        if (oldValue != null && newValue == null) {
            getChildren().remove(0);
        } else if (newValue != null) {
            getChildren().remove(0);
            getChildren().add(newValue);
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockNodeSkin(this);
    }

    public Region getContent() {
        if (getDelegate().getChildren().size() > 1) {
            System.err.println("1. getContent()");
            return (Region) getDelegate().getChildren().get(1);
        } else {
            System.err.println("2. getContent()");
            return null;
        }
    }

    public void setContent(Region content) {
        if (getDelegate().getChildren().size() > 1) {
            System.err.println("1. setContent()");
            return;
        }
        System.err.println("1. setContent() = " + content);
        getDelegate().getChildren().add(content);
    }

    public static class DockNodeSkin extends SkinBase<DockNode> {

        public DockNodeSkin(DockNode control) {
            super(control);
            getChildren().add(control.getDelegate());
        }
    }

}
