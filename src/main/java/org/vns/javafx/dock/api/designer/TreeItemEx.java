package org.vns.javafx.dock.api.designer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.dock.api.bean.ReflectHelper;
import org.vns.javafx.dock.api.Selection.SelectionListener;

/**
 *
 * @author Valery
 *
 */
public class TreeItemEx extends TreeItem<Object> {

    private String propertyName;
    private Node cellGraphic;

    private int dragDropQualifier;

    private final Map<String, Object> changeListeners = FXCollections.observableHashMap();

    private ItemType itemType = ItemType.CONTENT;

    public static enum ItemType {
        CONTENT, LIST, DEFAULTLIST, ELEMENT,
    }

    public TreeItemEx() {
        init();
    }

/*    public TreeItemEx(Object value) {
        super(value);
    }

    public TreeItemEx(Object value, Node graphic) {
        super(value, graphic);
    }
*/
    private void init() {
        SceneGraphView gv = DesignerLookup.lookup(SceneGraphView.class);
        if ( gv != null ) {
            gv.addTreeItemEventHandlers(this);
        }
    }

    public Node getCellGraphic() {
        return cellGraphic;
    }

    public void setCellGraphic(Node cellGraphic) {
        this.cellGraphic = cellGraphic;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public int getDragDropQualifier() {
        return dragDropQualifier;
    }

    public void setDragDropQualifier(int dragDropQualifier) {
        this.dragDropQualifier = dragDropQualifier;
    }

    public ItemType getItemType() {
        return itemType;
    }

    protected void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public TreeItemEx getTreeItem(String propertyName) {
        //NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(this.getValue());
        TreeItemEx propTreeItem = null;

        for (int i = 0; i < this.getChildren().size(); i++) {
            TreeItemEx it = (TreeItemEx) this.getChildren().get(i);
            if (propertyName.equals(it.getPropertyName())) {
                propTreeItem = it;
                break;
            }
        }
        return propTreeItem;
    }

    public Property getProperty(String propertyName) {
        if (propertyName == null) {
            return null;
        }
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(this.getValue());
        Property prop = null;
        for (int i = 0; i < nd.getProperties().size(); i++) {
            if (propertyName.equals(nd.getProperties().get(i).getName())) {
                prop = nd.getProperties().get(i);
                break;
            }
        }
        return prop;
    }

    protected int getIndex(String propertyName) {
        int index = -1;
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(getValue());
        for (int i = 0; i < nd.getProperties().size(); i++) {
            if (propertyName.equals(nd.getProperties().get(i).getName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getInsertPos(String propName) {

        int index = getIndex(propName);
        int insertPos = -1;  //use it when propTreeItem is null

        for (int i = 0; i < getChildren().size(); i++) {
            TreeItemEx it = (TreeItemEx) getChildren().get(i);
            int idx = getIndex(it.getPropertyName());
            if (idx > index) {
                insertPos = i - 1;
                break;
            } else {
                insertPos = i + 1;
            }
        }

        return insertPos;
    }

    public TreeItemEx getParentSkipHeader() {
        TreeItemEx retval = (TreeItemEx) getParent();
        if (retval != null && retval.getItemType().equals(TreeItemEx.ItemType.LIST)) {
            retval = (TreeItemEx) retval.getParent();
        }
        return retval;
    }

    public void registerChangeHandlers() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if (getValue() == null) {
            return;
        }
        if ((getValue() instanceof Node)) {
            //if (Dockable.of(getValue()) != null) {
                SelectionListener l = DockRegistry.lookup(SelectionListener.class);
                ((Node) getValue()).addEventHandler(MouseEvent.MOUSE_PRESSED, l);
                ((Node) getValue()).addEventHandler(MouseEvent.MOUSE_RELEASED, l);
            //}
        }
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(getValue());
        Object changeListener;
        if (this.getItemType() == ItemType.LIST) {
            changeListener = new TreeItemListObjectChangeListener(this, getPropertyName());
            ObservableList ol = (ObservableList) getValue();
            ol.addListener((ListChangeListener) changeListener);
            changeListeners.put(getPropertyName(), changeListener);
            return;
        }

        for (int i = 0; i < nd.getProperties().size(); i++) {
            
            Property p = nd.getProperties().get(i);
            Object v = new BeanAdapter(getValue()).get(p.getName());
            
            if (v != null && (v instanceof List)) {
                if ((p instanceof NodeList) && ((NodeList) p).isAlwaysVisible()) {
                    continue;
                }
                TreeItemEx item = this;
                changeListener = new TreeItemListObjectChangeListener(item, p.getName());
                Object propValue = new BeanAdapter(getValue()).get(p.getName());
                Method addListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableList.class, "addListener", new Class[]{ListChangeListener.class});
                ReflectHelper.MethodUtil.invoke(addListenerMethod, propValue, new Object[]{changeListener});
                changeListeners.put(p.getName(), changeListener);
            } else {
                changeListener = new TreeItemObjectChangeListener(this, p.getName());
                Method propMethod = ReflectHelper.MethodUtil.getMethod(getValue().getClass(), p.getName() + "Property", new Class[0]);
                Object propValue = ReflectHelper.MethodUtil.invoke(propMethod, getValue(), new Object[0]);
                Method addListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, "addListener", new Class[]{ChangeListener.class});
                ReflectHelper.MethodUtil.invoke(addListenerMethod, propValue, new Object[]{changeListener});
                changeListeners.put(p.getName(), changeListener);
            }
        }

    }

    public void unregisterChangeHandlers() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (getValue() == null || changeListeners.isEmpty()) {
            return;
        }
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(getValue());

        for (int i = 0; i < nd.getProperties().size(); i++) {
            Object changeListener; // = changeListeners.get(propertyName);
            Property p = nd.getProperties().get(i);
            if (List.class.isAssignableFrom(getValue().getClass())) {

            } else {
                changeListener = changeListeners.get(p.getName());
                Method propMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, p.getName() + "Property", new Class[0]);
                Object propValue = ReflectHelper.MethodUtil.invoke(propMethod, getValue(), new Object[0]);
                Method removeListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, "removeListener", new Class[]{ChangeListener.class});
                ReflectHelper.MethodUtil.invoke(removeListenerMethod, propValue, new Object[]{changeListener});
                changeListeners.remove(p.getName());
            }
        }
    }
}
