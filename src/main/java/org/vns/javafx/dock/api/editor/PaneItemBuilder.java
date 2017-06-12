package org.vns.javafx.dock.api.editor;

import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery Shyshkin
 */
public class PaneItemBuilder extends AbstractListBasedTreeItemBuilder<Node> {


    @Override
    public boolean isAcceptable(Object target, Object accepting) {
        return accepting instanceof Node;
    }
    @Override
    public ObservableList<Node> getList(Object obj) {
        return ((Pane)obj).getChildren();
    }

}//PaneItemBuilder

