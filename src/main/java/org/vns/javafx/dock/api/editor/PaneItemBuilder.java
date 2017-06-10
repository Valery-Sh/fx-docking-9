package org.vns.javafx.dock.api.editor;

import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery Shyshkin
 */
public class PaneItemBuilder extends AbstractListBasedTreeItemBuilder<Node> {


    @Override
    public boolean isAcceptable(Object obj) {
        return obj instanceof Node;
    }
    @Override
    public List<Node> getList(Object obj) {
        return ((Pane)obj).getChildren();
    }

    @Override
    public void registerChangeHandler(TreeItemEx item) {
        if ( ! (item.getValue().getTreeItemObject() != null && (item.getValue().getTreeItemObject() instanceof Node))) {
            return;
        }
        unregisterChangeHandler(item);
        Pane pane = (Pane) item.getValue().getTreeItemObject();
        BuilderListChangeListener l = new BuilderListChangeListener(item);
        pane.getProperties().put(EditorUtil.CHANGE_LISTENER, l);
        pane.getChildren().addListener(new BuilderListChangeListener(item));
    }

    @Override
    public void updateSourceSceneGraph(TreeItemEx parent, TreeItemEx child) {
        ((Pane) parent.getObject()).getChildren().remove(child.getObject());
        //
        // remove listeners fron source and all it's children
        //
        TreeItemBuilder b = TreeItemBuilderRegistry.getInstance().getBuilder(child.getObject());
        b.unregisterChangeHandler(child);
                
    }

    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        int idx = getIndex(treeView, target, place);
        getList(target.getObject()).add(idx, (Node) sourceObject);
    }

    @Override
    public void unregisterObjectChangeHandler(Object obj) {
        Pane pane = (Pane) obj;
        if ( pane.getProperties().get(EditorUtil.CHANGE_LISTENER) == null ) {
            return;
        }
        pane.getChildren().removeListener((ListChangeListener<? super Node>) pane.getProperties().get(EditorUtil.CHANGE_LISTENER));
        pane.getProperties().remove(EditorUtil.CHANGE_LISTENER);
    }


}//PaneItemBuilder

