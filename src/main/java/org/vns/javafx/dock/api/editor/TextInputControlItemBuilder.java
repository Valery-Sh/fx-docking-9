package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

/**
 *
 * @author Valery
 */
public class TextInputControlItemBuilder extends DefaultTreeItemBuilder {

    @Override
    public boolean isAcceptable(Object target,Object accepting) {
        return (accepting == null) || (accepting instanceof String);
    }

    @Override
    protected Node createDefaultContent(Object obj, Object... others) {
        String text = "";
        if (obj instanceof TextInputControl) {
            text = ((TextInputControl) obj).getText();
        }
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }

    /**
     * Checks whether the specified object is not null and is an instance of
     * Node and the specified target doesn't have children. The method returns {@literal false
     * } if one of the following conditions is not satisfied:
     * <ul>
     * <li>The method {@link #isAcceptable(java.lang.Object)} returns
     * {@literal false} }
     * </li>
     * <li>The specified {@literal target} has children. This means that the
     * {@literal Labeled} node has already it's {@literal  graphic} value set to
     * not null value
     * </li>
     * </ul>
     *
     * @param treeView ???
     * @param target the TreeItem object witch corresponds to the
     * {@literal  Labeled node}.
     * @param gestureSource an object to be checked
     * @param place ???
     * @return true if the parameter value is not null and is an instance of
     * Node and the specified target doesn't have children
     */
    @Override
    public TreeItemEx accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource) {
        TreeItemEx retval = null;

        DragGesture dg = (DragGesture) gestureSource.getProperties().get(EditorUtil.GESTURE_SOURCE_KEY);
        if (dg == null) {
            return retval;
        }

/*        if (dg.getGestureSource() != null && (dg.getGestureSource() instanceof TreeViewEx)) {
            TreeItem treeItem = ((DragTreeViewGesture) dg).getGestureSourceTreeItem();
            if (treeItem instanceof TreeItemEx) {
                treeView.updateSceneGraph((TreeItemEx) treeItem);
//                treeView.removeTreeItem(treeItem);                
            }
        } else 
*/
        if (dg.getGestureSourceObject() instanceof String) {
            String text = (String) dg.getGestureSourceObject();
            if (text == null) {
                text = "";
            }
            ((TextInputControl) target.getValue().getTreeItemObject()).setText(text);
            return (TreeItemEx) target;
        }

        return retval;
    }

}
