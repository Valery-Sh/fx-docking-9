/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.BorderPaneContext;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TargetContext;

/**
 *
 * @author Valery
 */
public class DockBorderPane extends BorderPane implements DockTarget {

    //private BorderPane targetPane;
    private TargetContext targetContext;

    public DockBorderPane() {
    }

    @Override
    public Region target() {
        return this;
    }

    @Override
    public TargetContext getTargetContext() {
        if (targetContext == null) {
            targetContext = new BorderPaneContext(this);
        }
        return targetContext;
    }


}
