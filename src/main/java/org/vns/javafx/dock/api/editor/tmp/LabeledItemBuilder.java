package org.vns.javafx.dock.api.editor.tmp;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;

/**
 *
 * @author Valery
 */
public class LabeledItemBuilder extends TreeItemBuilder {

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof Labeled) {
            Labeled node = (Labeled) obj;
            retval = createItem((Node) obj);
            if (node.getGraphic() != null) {
                TreeItemBuilder gb = TreeItemRegistry.getInstance().getBuilder(node.getGraphic());
                retval.getChildren().add(gb.build(node.getGraphic()));
            }
        }
        return retval;
    }

    @Override
    protected Node createDefaultContent(Node node) {
        String text = ((Labeled) node).getText();
        Label label = new Label(node.getClass().getSimpleName() + " " + text);
        return label;
    }
    
    @Override
    public boolean isAcceptable(Object obj) {
        return obj instanceof Node;
    }
    @Override
    public TreeItemEx accept(TreeItemEx parent,Object obj) {
        TreeItemEx retval = null;
        if (isAcceptable(obj) && (obj instanceof Node) ) {
            retval = TreeItemRegistry.getInstance().getBuilder(obj).build(obj);            
            parent.getChildren().add(retval);
            ((Labeled)parent.getNode()).setGraphic((Node) obj);
        }
        return retval;
    }
    @Override
    public TreeItemEx release(TreeItemEx parent,Object obj) {
        return null;
    }   
    
    public boolean isDragTarget() {
        return true;
    }
    
}
