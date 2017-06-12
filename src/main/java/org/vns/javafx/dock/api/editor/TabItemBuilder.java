package org.vns.javafx.dock.api.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;

/**
 *
 * @author Valery
 */
public class TabItemBuilder extends AbstractContentBasedTreeItemBuilder<Node> {

    public TabItemBuilder() {
    }



    @Override
    protected Node createDefaultContent(Object obj, Object... others) {
        String text = ((Tab) obj).getText();
        text = text == null ? "" : text;
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }

    /**
     * Checks whether the specified object can be used as a value of the graphic
     * property. May accepts only objects of type {@literal Node} witch becomes
     * a value of the graphic property. of the
     *
     * @param obj an object to be checked
     * @return true if the parameter value is not null and is an instance of
     * Node/
     */
    @Override
    public boolean isAcceptable(Object target, Object accepting) {
        return accepting != null && (accepting instanceof Node);
    }


/*    @Override
    protected Object createAndAddListener(TreeItemEx item) {
        ObjectProperty<Node> contentProperty = getContentProperty(item.getObject());
        TabChangeListener listener = new TabChangeListener(item);
        contentProperty.addListener(listener);
        return listener;
        
    }
    @Override
    protected void removeListener(TreeItemEx item, Object listener) {
         getContentProperty(item.getObject()).removeListener((TabChangeListener) listener);
    }
*/    
/*    @Override
    public void registerChangeHandler(TreeItemEx item) {
        if (!(item.getValue().getTreeItemObject() != null && (item.getValue().getTreeItemObject() instanceof Tab))) {
            return;
        }
        Tab tab = (Tab) item.getValue().getTreeItemObject();
        unregisterChangeHandler(item);        
        TabChangeListener listener = new TabChangeListener(item);
        tab.contentProperty().addListener(listener);
        //tab.getProperties().put(EditorUtil.CHANGE_LISTENER, listener);
        item.getValue().setChangeListener(listener);
    }

    @Override
    public void unregisterObjectChangeHandler(TreeItemEx item) {
        Tab tab = (Tab) item.getObject();
        //TabChangeListener listener = (TabChangeListener) tab.getProperties().get(EditorUtil.CHANGE_LISTENER);
        TabChangeListener listener = (TabChangeListener) item.getValue().getChangeListener();
        if ( listener == null ) {
            return;
        }
        tab.contentProperty().removeListener(listener);
        //tab.getProperties().remove(EditorUtil.CHANGE_LISTENER);
        item.getValue().setChangeListener(null);

        
    }
*/
/*    public class TabChangeListener implements ChangeListener<Node> {

        private final TreeItemEx treeItem;

        public TabChangeListener(TreeItemEx treeItem) {
            this.treeItem = treeItem;
        }

        @Override
        public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
            if (oldValue != null && newValue == null) {
                treeItem.getChildren().clear();
            } else if (oldValue == null && newValue != null) {
                TreeItemEx item = TreeItemBuilderRegistry.getInstance().getBuilder(newValue).build(newValue);
                treeItem.getChildren().add(item);
            }
        }

    }
*/
}
