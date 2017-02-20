package org.vns.javafx.dock.api.editor.tmp;

import javafx.scene.shape.Shape;

/**
 *
 * @author Valery
 */
public class ShapeItemBuilder extends TreeItemBuilder {

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof Shape) {
            retval = createItem((Shape) obj);
        }
        return retval;
    }

    
    @Override
    public TreeItemEx release(TreeItemEx parent,Object obj) {
        return null;
    }    
}
