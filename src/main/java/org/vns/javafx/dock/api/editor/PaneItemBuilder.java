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
        Pane pane = (Pane) item.getValue().getTreeItemObject();
        BuilderListChangeListener l = new BuilderListChangeListener(item);
        item.getValue().setChangeListener(l);
        pane.getChildren().addListener(new BuilderListChangeListener(item));
    }
    @Override
    public void unregisterChangeHandler(TreeItemEx item) {
        Pane pane = (Pane) item.getValue().getTreeItemObject();
        if ( item.getValue().getChangeListener() == null ) {
            return;
        }
        pane.getChildren().removeListener((ListChangeListener) item.getValue().getChangeListener());
        item.getValue().setChangeListener(null);        
    }

}//PaneItemBuilder

