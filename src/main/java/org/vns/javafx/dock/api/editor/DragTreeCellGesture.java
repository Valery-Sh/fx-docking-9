package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Valery Shyshkin
 */
public class DragTreeCellGesture extends DragNodeGesture{

    public DragTreeCellGesture(Node gestureSource) {
        super(gestureSource);
        //init(sourceGestureObject);
    }
    
/*    private void init(Object sourceGestureObject) {
        setSourceGestureObject(sourceGestureObject);        
    }
*/
    @Override
    public Object getGestureSourceObject() {
        TreeItem it = ((TreeCell)getGestureSource()).getTreeItem();
        return ((ItemValue)it.getValue()).getTreeItemObject();
    }
}
