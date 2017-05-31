package org.vns.javafx.dock.api.editor;

import java.util.List;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 *
 * @author Valery
 */
public class TabPaneItemBuilder extends AbstractListBasedTreeItemBuilder<Tab> {

    @Override
    public boolean isAcceptable(Object obj) {
        return obj instanceof Tab;
    }

    @Override
    public List<Tab> getList(Object obj) {
        return ((TabPane)obj).getTabs();
    }
}//TabPaneItemBuilder

