package org.vns.javafx.dock.api.demo;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class CustomControl extends Control {

    private DockPane delegate = new DockPane();

    public DockPane getDelegate() {
        return delegate;
    }
    
    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomControlSkin(this);
    }
    
    public void addItem(Dockable dockNode, Side side) {
        delegate.dock(dockNode, side);
    }
    public void addItem(Dockable dockNode, Side side, Dockable target) {
        delegate.paneHandler().dock(dockNode, side, target);
    }
    
    public static class CustomControlSkin extends SkinBase<CustomControl> {

        public CustomControlSkin(CustomControl control) {
            super(control);
            getChildren().add(control.getDelegate());
        }
    }
    

}
