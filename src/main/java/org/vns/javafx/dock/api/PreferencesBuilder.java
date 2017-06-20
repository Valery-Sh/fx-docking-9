package org.vns.javafx.dock.api;

import java.util.Map;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery
 */
public interface PreferencesBuilder {
    TreeItem<PreferencesItem> build(DockTarget dockTarget);
    //void fillDockTarget(DockLoader loader, DockTarget dockTarget);
    void restoreFrom(TreeItem<PreferencesItem> targetRoot);
    Map<String,String> getProperties(Object node);
    void setProperties(Object node, Map<String,String> prefProps );
    
}
