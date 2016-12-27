/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

/**
 *
 * @author Valery
 */
public interface DockConverter {
    public static final int BEFORE_DOCK = 0;
    public static final int AFTER_DOCK = 0;
    
    Dockable convert(Dockable source, int when);
}
