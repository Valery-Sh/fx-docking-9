/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.editor.tmp;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valery
 */
public class PaneItemBuilder extends TreeItemBuilder {

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof Pane) {
            Pane pane = (Pane) obj;
            retval = createItem((Pane) obj);
            for (Node node : pane.getChildren()) {
                TreeItemBuilder gb = TreeItemRegistry.getInstance().getBuilder(node);
                retval.getChildren().add(gb.build(node));
            }
        }
        return retval;
    }

    @Override
    public boolean isAcceptable(Object obj) {
        return obj instanceof Node;
    }

    @Override
    public TreeItemEx accept(TreeItemEx parent, Object obj) {
        TreeItemEx retval = null;
        if (isAcceptable(obj) && (obj instanceof Node)) {
            if (!((Pane) parent.getNode()).getChildren().contains((Node) obj)) {
                retval = TreeItemRegistry.getInstance().getBuilder(obj).build(obj);
                parent.getChildren().add(retval);

                ((Pane) parent.getNode()).getChildren().add((Node) obj);
            }
        }
        return retval;
    }
    @Override
    public boolean isDragTarget() {
        return true;
    }
    
}//PaneItemBuilder

