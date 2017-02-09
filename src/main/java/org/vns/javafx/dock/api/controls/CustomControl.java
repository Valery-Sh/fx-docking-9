/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.controls;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
//@DefaultProperty(value = "content1")
//@DefaultProperty(value = "children")
public class CustomControl extends TitledPane {

    private VBox delegate;// = new DockPane();
    private ObjectProperty<Region> contentProperty = new SimpleObjectProperty<>();
    
    public CustomControl() {
        getStyleClass().add("custom-control");
    }

    protected VBox getDelegate() {
        if (delegate == null) {
            delegate = new VBox();
        }
        return delegate;
    }
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }
    private void init(String id, String title, double dividerPos) {
    }
    
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    /*    public String getDockPos() {
        return nodeHandler.getDockPos();
    }
    public void setDockPos(String dockpos) {
        this.nodeHandler.setDockPos(dockpos);
    }
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomControlSkin(this);
    }
    public Region getContent1() {
        return contentProperty.get();
    }
    public ObjectProperty<Region> content1Property() {
        return contentProperty;
    }
    public void setContent1(Region content) {
        contentProperty.set(content);
        getDelegate().getChildren().add(content);

    }

}
