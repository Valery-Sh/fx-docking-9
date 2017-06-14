package org.vns.javafx.dock.api.editor;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

/**
 *
 * @author Valery Shyshkin
 */
public class AccordionItemBuilder extends AbstractListBasedTreeItemBuilder<TitledPane> {

    @Override
    public boolean isAcceptable(Object target, Object accepting) {
        return accepting instanceof Node;
    }

    @Override
    public ObservableList<TitledPane> getList(Object obj) {
        return ((Accordion) obj).getPanes();
    }
    /*
    @Override
    public void registerChangeHandler(TreeItemEx item) {
        if ( ! (item.getValue().getTreeItemObject() != null && (item.getValue().getTreeItemObject() instanceof Node))) {
            return;
        }
        unregisterChangeHandler(item);
        Pane pane = (Pane) item.getValue().getTreeItemObject();
        BuilderListChangeListener listener = new BuilderListChangeListener(item);
        //pane.getProperties().put(EditorUtil.CHANGE_LISTENER, l);
        item.getValue().setChangeListener(listener);
        pane.getChildren().addListener(new BuilderListChangeListener(item));
    }
     */

 /*    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        int idx = getIndex(treeView, target, place);
        getList(target.getObject()).add(idx, (Node) sourceObject);
    }
    @Override
    public void updateOnMove(TreeItemEx child) {
        TreeItemEx parent = (TreeItemEx) child.getParent();
        ((Pane) parent.getObject()).getChildren().remove(child.getObject());
        //
        // remove listeners fron source and all it's children
        //
        TreeItemBuilder b = TreeItemBuilderRegistry.getInstance().getBuilder(child.getObject());
        b.unregisterChangeHandler(child);
                
    }
     */
 /*    @Override
    public void unregisterObjectChangeHandler(TreeItemEx item) {
        Pane pane = (Pane) item.getObject();
        if ( pane.getProperties().get(EditorUtil.CHANGE_LISTENER) == null ) {
            return;
        }
        pane.getChildren().removeListener((ListChangeListener<? super Node>) pane.getProperties().get(EditorUtil.CHANGE_LISTENER));
        pane.getProperties().remove(EditorUtil.CHANGE_LISTENER);
    }
     */
}//PaneItemBuilder

