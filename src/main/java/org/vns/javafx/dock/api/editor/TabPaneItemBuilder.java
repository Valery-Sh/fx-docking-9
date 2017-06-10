package org.vns.javafx.dock.api.editor;

import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;

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
        if (!(item.getValue().getTreeItemObject() != null && (item.getValue().getTreeItemObject() instanceof TabPane))) {
            return;
        }
        TabPane pane = (TabPane) item.getValue().getTreeItemObject();
        unregisterChangeHandler(item);        
        
        BuilderListChangeListener listener = new BuilderListChangeListener(item);
        pane.getTabs().addListener(listener);
        pane.getProperties().put(EditorUtil.CHANGE_LISTENER, listener);
    }

    @Override
    public void updateSourceSceneGraph(TreeItemEx parent, TreeItemEx child) {
        ((TabPane) parent.getObject()).getTabs().remove(child.getObject());
        //
        // remove listeners
        //
    }

    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        int idx = getIndex(treeView, target, place);
        getList(target.getObject()).add(idx, (Tab) sourceObject);
    }

    @Override
    public void unregisterObjectChangeHandler(Object obj) {
        TabPane pane = (TabPane) obj;
        if ( pane.getProperties().get(EditorUtil.CHANGE_LISTENER) == null ) {
            return;
        }
        pane.getTabs().removeListener((ListChangeListener<? super Tab>) pane.getProperties().get(EditorUtil.CHANGE_LISTENER));
        pane.getProperties().remove(EditorUtil.CHANGE_LISTENER);
        
    }
    
}//TabPaneItemBuilder

