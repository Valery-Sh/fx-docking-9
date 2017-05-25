package org.vns.javafx.dock.api.editor;

import java.util.List;
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

}//PaneItemBuilder

