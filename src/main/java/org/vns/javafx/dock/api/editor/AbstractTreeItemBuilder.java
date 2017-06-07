package org.vns.javafx.dock.api.editor;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static org.vns.javafx.dock.api.editor.SceneGraphView.ANCHOR_OFFSET;

/**
 *
 * @author Valery
 */
public abstract class AbstractTreeItemBuilder implements TreeItemBuilder {

    public AbstractTreeItemBuilder() {
        init();
    }

    private void init() {
    }

    @Override
    public abstract boolean isAcceptable(Object obj);
    
    @Override
    public Object getChangeHandler(Node node) {
        return null;
    }

    @Override
    public TreeItem build(Object obj) {
        TreeItem retval = null;
        retval = createItem(obj);
        return retval;
    }
/*    @Override
    public TreeItem buildRoot(Object obj) {
        TreeItem<ItemValue> retval = null;
        retval = createItem(obj);
        retval.addEventHandler(TreeItem.<ItemValue>childrenModificationEvent(),
                this::childrenModification);        
        return retval;
    }
*/
    @Override
    public final TreeItem createItem(Object obj, Object... others) {
        HBox box = new HBox();
        AnchorPane anchorPane = new AnchorPane(box);
        AnchorPane.setBottomAnchor(box, ANCHOR_OFFSET);
        AnchorPane.setTopAnchor(box, ANCHOR_OFFSET);
        anchorPane.setStyle("-fx-background-color: yellow");
        TreeItemEx item = new TreeItemEx();
        ItemValue itv = new ItemValue(item);
        item.setValue(itv);
        itv.setTreeItemObject(obj);
        itv.setCellGraphic(anchorPane);
        

        
        box.getChildren().add(createItemContent(obj, others));
        //
        // 
        //
        //registerChangeHandler(item);
        return item;
    }

    protected HBox getItemContentPane(TreeItem<ItemValue> item) {
        return (HBox) ((AnchorPane) item.getValue().getCellGraphic()).getChildren().get(0);
    }

    @Override
    public Node createItemContent(Object obj, Object... others) {
        return createDefaultContent(obj, others);
    }

    protected Node createDefaultContent(Object obj, Object... others) {
        String text = "";
        if (obj != null && (obj instanceof Labeled)) {
            text = ((Labeled) obj).getText();
        }
        Label label = new Label(obj.getClass().getSimpleName() + " " + text);
        String styleClass = "tree-item-node-" + obj.getClass().getSimpleName().toLowerCase();
        label.getStyleClass().add(styleClass);
        return label;
    }

    @Override
    public abstract TreeItem accept(TreeViewEx treeView, TreeItemEx target, TreeItemEx place, Node gestureSource);

    @Override
    public abstract void removeChildObject(Object parent, Object toRemove);

    @Override
    public void removeChildTreeItem(TreeItemEx parent, TreeItemEx toRemove) {
        parent.getChildren().remove(toRemove);
    }


}// DefaultTreeItemBuilder
