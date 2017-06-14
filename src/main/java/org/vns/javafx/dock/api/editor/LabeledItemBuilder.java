package org.vns.javafx.dock.api.editor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import org.vns.javafx.dock.api.editor.GraphicPlaceholderBuilder.GraphicChangeListener;
import org.vns.javafx.dock.api.editor.TreeItemBuilder.PlaceholderBuilderFactory;

/**
 *
 * @author Valery
 */
public class LabeledItemBuilder extends AbstractTreeItemBuilder implements PlaceholderBuilderFactory {

    //private final TreeItemBuilder[] placeholderBuilders;
    public LabeledItemBuilder() {
        //this.placeholderBuilders = new LabelPlaceholderBuilder();
        //placeholderBuilders = new TreeItemBuilder[] {new LabelPlaceholderBuilder()};
    }

    private void init() {

    }

    @Override
    public TreeItemEx build(Object obj) {
        TreeItemEx retval = null;
        if (obj instanceof Labeled) {
            Labeled node = (Labeled) obj;

            retval = createItem((Node) obj);
            if (node.getGraphic() != null) {
                TreeItemEx ph = PlaceholderBuilder.createPlaceholder(0, obj, node.getGraphic());
                retval.getChildren().add(ph);
                //PlaceholderBuilder builder = getPlaceholderBuilder(0);
                //retval.getChildren().add(builder.createPlaceholder(node.getGraphic()));
            }
        }
        return retval;
    }

    @Override
    protected HBox createDefaultContent(Object obj) {
        HBox box = new HBox(new HBox()); // placeholder 
        String text = ((Labeled) obj).getText();
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        box.getChildren().add(label);
        return box;
    }

    /**
     * Checks whether the specified object can be used as a value of the graphic
     * property. May accepts only objects of type {@literal Node} witch becomes
     * a value of the graphic property. of the
     *
     * @param target ??
     * @param accepting an object to be checked
     * @return true if the parameter value is not null and is an instance of
     * Node/
     */
    @Override
    public boolean isAcceptable(Object target, Object accepting) {
        return accepting != null && ((accepting instanceof Node) || (accepting instanceof String));
    }

    @Override
    public boolean isAdmissiblePosition(TreeView treeView, TreeItemEx target,
            TreeItemEx place,
            Object dragObject) {
        boolean retval = super.isAdmissiblePosition(treeView, target, place, dragObject);
        if (!retval) {
            return false;
        }
        if (place.getParent() == target) {
            return false;
        }
        Labeled lb = (Labeled) place.getValue().getTreeItemObject();
        if (place == target && lb.getGraphic() != null) {
            return false;
        }
        return true;
    }

    @Override
    public void updateOnMove(TreeItemEx child) {
        TreeItemEx parent = (TreeItemEx) child.getParent();
        if (parent.getObject() instanceof Labeled) {
            ((Labeled) parent.getObject()).setGraphic(null);
        }
    }

    @Override
    protected void update(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Object sourceObject) {
        ItemValue v = target.getValue();
        ((Labeled) v.getTreeItemObject()).setGraphic((Node) sourceObject);
    }

    @Override
    protected Object createAndAddListener(TreeItemEx item) {
        GraphicChangeListener listener = new GraphicChangeListener(0, item);
        ((Labeled) item.getObject()).graphicProperty().addListener(listener);
        return listener;
    }

    @Override
    protected void removelistener(TreeItemEx item, Object listener) {
        
        ((Labeled) item.getObject()).graphicProperty().removeListener((ChangeListener) listener);
    }

    @Override
    public PlaceholderBuilder getPlaceholderBuilder(int placeholderId) {
        return new LabelPlaceholderBuilder(placeholderId);
    }

    public static class LabelPlaceholderBuilder extends GraphicPlaceholderBuilder {

        public LabelPlaceholderBuilder(int placeHolderId) {
            super(placeHolderId);
        }


    }//LabeledPlaceholderBuilder

}
