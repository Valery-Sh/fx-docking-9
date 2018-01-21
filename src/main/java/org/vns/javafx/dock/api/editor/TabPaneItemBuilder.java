package org.vns.javafx.dock.api.editor;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 *
 * @author Valery
 */
public class TabPaneItemBuilder extends AbstractListBasedTreeItemBuilder<Tab> {

    @Override
    public boolean isAcceptable(Object target,Object accepting) {
        return accepting instanceof Tab;
    }

    @Override
    public ObservableList<Tab> getList(Object obj) {
        return ((TabPane)obj).getTabs();
    }
}//TabPaneItemBuilder

