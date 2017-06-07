package org.vns.javafx.dock.api.editor;

import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;

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

    @Override
    public void registerChangeHandler(TreeItemEx item) {
        TabPane pane = (TabPane) item.getValue().getTreeItemObject();
        BuilderListChangeListener l = new BuilderListChangeListener(item);
        item.getValue().setChangeListener(l);
        pane.getTabs().addListener(new BuilderListChangeListener(item));
    }
    @Override
    public void unregisterChangeHandler(TreeItemEx item) {
        TabPane pane = (TabPane) item.getValue().getTreeItemObject();
        if ( item.getValue().getChangeListener() == null ) {
            return;
        }
        pane.getTabs().removeListener((ListChangeListener) item.getValue().getChangeListener());
        item.getValue().setChangeListener(null);        
    }

    
}//TabPaneItemBuilder

