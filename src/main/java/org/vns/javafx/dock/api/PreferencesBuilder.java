package org.vns.javafx.dock.api;

import java.util.Map;
import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

/**
 *
 * @author Valery
 */
public interface PreferencesBuilder {
    TreeItem<Pair<ObjectProperty, Properties>> build();
    void restore(TreeItem<Pair<ObjectProperty, Properties>> targetRoot);
    Map<String,String> getProperties(Object node);
}
