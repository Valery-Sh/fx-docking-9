/*
 * Copyright 2017 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.incubator;


import javafx.beans.DefaultProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
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
public class DockNodeOld extends TitledPane implements Dockable {

    private DockableContext dockableContext;

    private VBoxCustom delegate;

    public DockNodeOld() {
        init(null, null);
    }

    public DockNodeOld(String title) {
        init(null, title);
    }

    public DockNodeOld(String id, String title) {
        init(null, title);
    }

    private void init(String id, String title) {
        setContent(new StackPane());
        contentProperty().addListener(this::contentChanged);
        getStyleClass().add("dock-node");
        dockableContext = new DockableContext(this);
        dockableContext.createDefaultTitleBar(title);
        dockableContext.titleBarProperty().addListener(this::titlebarChanged);
        if (id != null) {
            setId(id);
        }
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

    protected VBoxCustom getDelegate() {
        if (delegate == null) {
            delegate = new VBoxCustom();
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
    public DockableContext getDockableContext() {
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

    public static class DockNodeSkin extends SkinBase<DockNodeOld> {

        public DockNodeSkin(DockNodeOld control) {
            super(control);
            if (!getChildren().isEmpty()) {
                getChildren().clear();
            }
            getChildren().add(control.getDelegate());
            if (control.getTitleBar() != null && !control.getDelegate().getChildren().contains(control.getTitleBar())) {
                control.getDelegate().getChildren().add(control.getTitleBar());
            }
            control.getDelegate().getChildren().add(control.getContent());
        }
    }

    public static class VBoxCustom extends VBox {

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
}
