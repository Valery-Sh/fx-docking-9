/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import javafx.scene.layout.Pane;

/**
 *
 * @author Valery
 */
public interface DockPaneTarget extends DockTarget{
    Pane pane();
    DockPaneHandler paneHandler();
}
