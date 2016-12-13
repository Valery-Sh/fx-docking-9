/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.DockPaneDelegate;
import org.vns.javafx.dock.api.properties.DockPaneDelegateProperty;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery
 */
public class DockPane extends StackPane implements DockTarget{
    
    private final DockPaneDelegateProperty<DockPaneDelegate> delegeteProperty = new DockPaneDelegateProperty<>();
            
    public DockPane() {
        init();
    }

    public DockPane(Node... children) {
        super(children);
    }
    private void init() {
        DockPaneDelegate dlg = new DockPaneDelegate(this);
        delegeteProperty.set(dlg);
    }
    protected DockPaneDelegate getDelegate() {
        return this.delegeteProperty.get();
    }
    @Override
    public void dock(Node dockable, Side dockPos) {
        delegeteProperty.get().dock(dockable, dockPos);
    }
}
