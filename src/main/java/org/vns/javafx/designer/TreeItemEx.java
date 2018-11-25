package org.vns.javafx.designer;

import org.vns.javafx.designer.descr.NodeProperty;
import org.vns.javafx.designer.descr.NodeDescriptorRegistry;
import org.vns.javafx.designer.descr.NodeList;
import org.vns.javafx.designer.descr.NodeDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.dock.api.bean.ReflectHelper;

/**
 *
 * @author Valery
 *
 */
public class TreeItemEx extends TreeItem<Object> {

    private String propertyName;
    private Node cellGraphic;

    private int dragDropQualifier;

    private final Map<String, Object> changeListeners2 = FXCollections.observableHashMap();
    private final Map<ObservableList, ListChangeListener> listChangeListeners = FXCollections.observableHashMap();
    private final Map<ObservableValue, ChangeListener> propChangeListeners = FXCollections.observableHashMap();

    private ItemType itemType = ItemType.CONTENT;

    public static enum ItemType {
        CONTENT, LIST, DEFAULTLIST, ELEMENT,
    }

    public TreeItemEx() {
        init();
    }

    private void init() {
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

    public NodeProperty getProperty(String propertyName) {
        if (propertyName == null) {
            return null;
        }
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(this.getValue().getClass());
        NodeProperty prop = null;
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
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(getValue().getClass());
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

        if (SceneView.isFrame(getValue())) {
            return;
        }
        if (getValue() == null) {
            return;
        }
        if ((getValue() instanceof Node)) {
            Selection.addListeners((Node) getValue());
        }
        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(getValue().getClass());
        Object changeListener;
        if (this.getItemType() == ItemType.LIST) {
            changeListener = new TreeItemListObjectChangeListener(this, getPropertyName());
            ObservableList ol = (ObservableList) getValue();
            ol.addListener((ListChangeListener) changeListener);
            listChangeListeners.put(ol, (ListChangeListener) changeListener);
            changeListeners2.put(getPropertyName(), changeListener);
            return;
        }

        for (int i = 0; i < nd.getProperties().size(); i++) {

            NodeProperty p = nd.getProperties().get(i);
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
                changeListeners2.put(p.getName(), changeListener);
                listChangeListeners.put((ObservableList) propValue, (ListChangeListener) changeListener);
            } else {
                changeListener = new TreeItemObjectChangeListener(this, p.getName());
                Method propMethod = ReflectHelper.MethodUtil.getMethod(getValue().getClass(), p.getName() + "Property", new Class[0]);
                Object propValue = ReflectHelper.MethodUtil.invoke(propMethod, getValue(), new Object[0]);
                Method addListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, "addListener", new Class[]{ChangeListener.class});
                ReflectHelper.MethodUtil.invoke(addListenerMethod, propValue, new Object[]{changeListener});
                propChangeListeners.put((ObservableValue) propValue, (ChangeListener) changeListener);

                changeListeners2.put(p.getName(), changeListener);
            }
        }

    }

    private void removeListeners() {
        listChangeListeners.forEach((k, v) -> {
            try {
                k.removeListener(v);
            } catch (Exception ex) {
                System.err.println("EXCEPTION : " + ex.getMessage());
            }
        });
        listChangeListeners.clear();

        propChangeListeners.forEach((k, v) -> {
            try {
                k.removeListener(v);
            } catch (Exception ex) {
                System.err.println("1 EXCEPTION : " + ex.getMessage());
            }
        });
        propChangeListeners.clear();

    }

    public void unregisterChangeHandlers_OLD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (SceneView.isFrame(getValue())) {
            return;
        }
       
        if (getValue() == null) {
            //removeListeners();
            return;
        }
        if (getValue() instanceof Node) {
            Selection.removeListeners((Node) getValue());
        }

        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(getValue().getClass());

        for (int i = 0; i < nd.getProperties().size(); i++) {

            Object changeListener; // = changeListeners.get(propertyName);
            NodeProperty p = nd.getProperties().get(i);
            if (List.class.isAssignableFrom(getValue().getClass())) {
            } else {
                changeListener = changeListeners2.get(p.getName());
                //Method propMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, p.getName() + "Property", new Class[0]);
                String propertyName = p.getName() + "Property";
                if (changeListener instanceof ListChangeListener) {
                    propertyName = "get" + p.getName().substring(0, 1).toUpperCase() + p.getName().substring(1);
                }
                Method propMethod = ReflectHelper.MethodUtil.getMethod(getValue().getClass(), propertyName, new Class[0]);
                Object propValue = ReflectHelper.MethodUtil.invoke(propMethod, getValue(), new Object[0]);
                //Method removeListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, "removeListener", new Class[]{ChangeListener.class});
                if (changeListener instanceof ListChangeListener) {
                    Method removeListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableList.class, "removeListener", new Class[]{ListChangeListener.class});
                    ReflectHelper.MethodUtil.invoke(removeListenerMethod, propValue, new Object[]{changeListener});
                } else {
                    Method removeListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, "removeListener", new Class[]{ChangeListener.class});
                    ReflectHelper.MethodUtil.invoke(removeListenerMethod, propValue, new Object[]{changeListener});
                }

                changeListeners2.remove(p.getName());
            }
        }
    }

    public void unregisterChangeHandlers(){
        if (SceneView.isFrame(getValue())) {
            return;
        }
        if (getValue() == null) {
            //removeListeners();
            return;
        }
        if (getValue() instanceof Node) {
            Selection.removeListeners((Node) getValue());
        }

        NodeDescriptor nd = NodeDescriptorRegistry.getInstance().getDescriptor(getValue().getClass());
        removeListeners();
        if ( true ) {
            return;
        }
        for (int i = 0; i < nd.getProperties().size(); i++) {
            Object changeListener; // = changeListeners.get(propertyName);
            NodeProperty p = nd.getProperties().get(i);
            if (List.class.isAssignableFrom(getValue().getClass())) {
            } else {
                try {
                    changeListener = changeListeners2.get(p.getName());
                    //Method propMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, p.getName() + "Property", new Class[0]);
                    String propertyName = p.getName() + "Property";
                    if (changeListener instanceof ListChangeListener) {
                        propertyName = "get" + p.getName().substring(0, 1).toUpperCase() + p.getName().substring(1);
                    }
                    Method propMethod = ReflectHelper.MethodUtil.getMethod(getValue().getClass(), propertyName, new Class[0]);
                    Object propValue = ReflectHelper.MethodUtil.invoke(propMethod, getValue(), new Object[0]);
                    //Method removeListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, "removeListener", new Class[]{ChangeListener.class});
                    if (changeListener instanceof ListChangeListener) {
                        Method removeListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableList.class, "removeListener", new Class[]{ListChangeListener.class});
                        ReflectHelper.MethodUtil.invoke(removeListenerMethod, propValue, new Object[]{changeListener});
                    } else {
                        Method removeListenerMethod = ReflectHelper.MethodUtil.getMethod(ObservableValue.class, "removeListener", new Class[]{ChangeListener.class});
                        ReflectHelper.MethodUtil.invoke(removeListenerMethod, propValue, new Object[]{changeListener});
                    }
                    
                    changeListeners2.remove(p.getName());
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                    Logger.getLogger(TreeItemEx.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
