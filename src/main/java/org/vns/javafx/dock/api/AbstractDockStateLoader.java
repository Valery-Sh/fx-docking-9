/*
 * Copyright 2017 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import static org.vns.javafx.dock.api.DockTreeItemBuilder.*;
/**
 * The base implementation of the interface 
 * {@link org.vns.javafx.dock.api.NodeStateLoader }.
 * The class uses both {@code java.util.prefs.Preferences }  and {@code xml }
 * technology to save/restore state of objects of type 
 * {@link org.vns.javafx.dock.api.DockTarget } and 
 * {@link org.vns.javafx.dock.api.Dockable }.
 * 
 * @author Valery Shyshkin
 */
public abstract class AbstractDockStateLoader implements NodeStateLoader{


    private final static List<Node> stateChangedList = FXCollections.observableArrayList();

    private final Map<String, Node> explicitlyRegistered = FXCollections.observableHashMap();
    private final Map<String, Object> registered = FXCollections.observableHashMap();

    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> defaultDockTargets = FXCollections.observableHashMap();
    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> allDockTargets = FXCollections.observableHashMap();
    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> defaultDockables = FXCollections.observableHashMap();

    private final String preferencesRoot;

    private boolean saveOnClose;

    private boolean loaded = false;
    /**
     * Creates a new instance of the class for the specified {@code prefEntry}. 
     * The parameter must adhere to the rules for node's names as specified 
     * by the class {@code java.util.Preferences}.
     * 
     * @param prefEntry the preferences node's relative path
     */
    protected AbstractDockStateLoader(String prefEntry) {
        this.preferencesRoot = prefEntry;
    }
    /**
     * Creates a new instance of the class for the specified class used to create
     * the root of preferences root node. 
     * The root node path is constructed by replacing all dots  with the 
     * forward slash symbol in the fully qualified class name.
     * 
     * @param clazz used to create a preferences root node path.
     */
    protected AbstractDockStateLoader(Class clazz) {
        preferencesRoot = clazz.getName().replace(".", "/");
    }
    /**
     * @return {@code true} if the {@link #load() } method detected an 
     *   inconsistency in the early saved state and newly registered object's state. {@code false } otherwise.
     */
    protected abstract boolean isDestroyed();
    /**
     * Clears preferences nodes so that there no properties exist 
     * in preferences store.
     */
    protected abstract void resetPreferences();
    /**
     * Saves the current state of all registered nodes.
     * @param dockTarget the object of type 
     * {@link org.vns.javafx.dock.api.DockTarget } whose state is to be saved
     */
    protected abstract void save(DockTarget dockTarget);
    /**
     * Restores the previously saved  state of the specified  node.
     * @param dockTarget the object of type {@link org.vns.javafx.dock.api.DockTarget }
     * whose state is to be restored.
     * @return the object of type {@code javafx.scene.control.TreeItem } which 
     *   is the root of TreeItem's tree which corresponds to the {@code Scene Graph } 
     *   of the node specified by the parameter {@code dockTarget}.
     */
    protected abstract TreeItem<Pair<ObjectProperty, Properties>> restore(DockTarget dockTarget);
    /**
     * Restores the previously saved  state of all specified  node.
     * Just scans all registered objects of type of type {@link org.vns.javafx.dock.api.DockTarget }
     * and invokes the method {@link #restore(org.vns.javafx.dock.api.DockTarget) }. 
     */
    protected void restore() {
        for (String key : getDefaultDockTargets().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.isDockTarget(node)) {
                restore(DockRegistry.dockTarget(node));
            }
        }
    }
    /**
     * Specifies whether the state of registered objects should be saved
     * when window close.
     * The implementation may or may not use this opportunity
     *
     * @return {@code true} if the state should be saved. {@code false} otherwise
     */
    public boolean isSaveOnClose() {
        return saveOnClose;
    }
    /**
     * Set the property value which specifies whether the state of registered objects should be saved
     * when window close.
     * The implementation may or may not use this opportunity
     *
     * @param saveOnClose {@code saveOnClose} if the state should be saved. {@code false } otherwise
     */
    public void setSaveOnClose(boolean saveOnClose) {
        this.saveOnClose = saveOnClose;
    }
    /**
     * The preferences root path used to save/restore the state of registered nodes.+
     * @return the preferences root path
     */
    protected String getPreferencesRoot() {
        return preferencesRoot;
    }
    /**
     * Returns a map of all registered explicitely or implicitly objects.
     * The collection may contain objects of any type which were found
     * during processing the {@code Scene Graph} of explicitely registered nodes.
     * The objects which were registered by applying one of the 
     * {@code register} methods are considered as {@code explicitely registered}.
     * 
     * @return a map of all registered explicitely or implicitly objects
     */
    protected Map<String, Object> getRegistered() {
        return registered;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    protected void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void layoutChanged(Node target) {
        if (!loaded || stateChangedList.contains(target)) {
            return;
        }
        stateChangedList.add(target);
    }


    /**
     * @return a map of all explicitly registered nodes
     */
    protected Map<String, Node> getExplicitlyRegistered() {
        return explicitlyRegistered;
    }

    protected Map<String, TreeItem<Pair<ObjectProperty, Properties>>> getAllDockTargets() {
        return allDockTargets;
    }


    protected String getFieldName(Object obj) {
        String retval = null;
        for (String key : registered.keySet()) {
            if (obj == registered.get(key)) {
                retval = key;
                break;
            }
        }
        return retval;
    }


    protected Map<String, TreeItem<Pair<ObjectProperty, Properties>>> getDefaultDockTargets() {
        return defaultDockTargets;
    }


    protected Map<String, TreeItem<Pair<ObjectProperty, Properties>>> getDefaultDockables() {
        return defaultDockables;
    }

    public void register(String entry, Node node) {
        if (loaded) {
            throw new IllegalStateException("Attempts to register an entry '"
                    + entry + "' and class '" + node.getClass().getName() + "' but the method 'load' has already been invoked");
        }
        if (entry == null || getExplicitlyRegistered().containsKey(entry)) {
            throw new IllegalArgumentException("Dublicate entry name: " + entry);
        }
        //
        // May be the dockable node is allready in registered map with another entry name
        //
        if (DockRegistry.isDockable(node) && getExplicitlyRegistered().containsValue(node)) {
            //getExplicitlyRegistered().remove(getEntryName(node));
            throw new IllegalArgumentException("Dublicate node. entryName: " + entry);
        }

        if (!DockRegistry.isDockTarget(node) && !DockRegistry.isDockable(node)) {
            throw new IllegalArgumentException("Illegall className. entry name: " + entry + "; class=" + node.getClass().getName());
        }

        getExplicitlyRegistered().put(entry, node);
        if (DockRegistry.isDockTarget(node)) {
            addListeners(node);
        }
    }

    public Node register(String entry, Class clazz) {
        Node retval = null;
        if (loaded) {
            throw new IllegalStateException("Attempts to register an entry '"
                    + entry + "' and class '" + clazz.getName() + "' but the method 'load' has already been invoked");
        }
        if (entry == null || getExplicitlyRegistered().containsKey(entry)) {
            throw new IllegalArgumentException("Dublicate entry name: " + entry);
        }

        try {
            Object o = clazz.newInstance();
            if (!(o instanceof Node)) {
                throw new IllegalArgumentException("Illegall className. entry name: " + entry + "; class=" + clazz.getName());
            }
            retval = (Node) o;

            if (!DockRegistry.isDockTarget(retval) && !DockRegistry.isDockable(retval)) {
                throw new IllegalArgumentException("Illegall className. entry name: " + entry + "; class=" + clazz.getName());
            }

            getExplicitlyRegistered().put(entry, retval);
            if (DockRegistry.isDockTarget(retval)) {
                addListeners(retval);
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(AbstractDockStateLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    protected void addListeners(Node target) {
        EventHandler<WindowEvent> wh = new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                Platform.runLater(() -> {
                    
                    /*if (!stateChangedList.isEmpty()) {
                        List<Node> list = new ArrayList<>();
                        list.addAll(stateChangedList);
                        list.forEach(node -> {
                            //System.err.println("SAVE FROM LISTENER node = " + node);
                            //save(DockRegistry.dockTarget(node));
                            //stateChangedList.remove(node);
                        });
                        System.err.println("2) *** SAVE ***");
                        //save();
                    }
                    */
                    if ( isSaveOnClose()) {
                        save();
                    }
                });

            }
        };
        ChangeListener<Window> wl = (ObservableValue<? extends Window> observable, Window oldValue, Window newValue) -> {
            if (newValue != null) {
                newValue.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, wh);
            } else {
                oldValue.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, wh);
            }

        };

        ChangeListener<Scene> sl = (ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) -> {
            if (newValue != null) {
                newValue.windowProperty().addListener(wl);
            }
            if (oldValue != null) {
                oldValue.windowProperty().removeListener(wl);
                oldValue.getWindow().removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, wh);
            }

        };

        target.sceneProperty().addListener(sl);
        if (target.getScene() != null && target.getScene().getWindow() != null) {
            target.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, wh);
        }
    }

    public boolean isRegistered(Node node) {
        return registered.values().contains(node);
    }

    protected DockTreeItemBuilder builder(Node dockTarget) {
        DockTreeItemBuilder builder = DockRegistry.dockTarget(dockTarget).targetController()
                .getDockTreeTemBuilder();
        return builder;
    }

    protected TreeItem<Pair<ObjectProperty, Properties>> build(String fieldName, Node dockTarget) {
        TreeItem<Pair<ObjectProperty, Properties>> item = builder(dockTarget).build(fieldName);
        completeBuild(item, false);
        return item;
    }
    /**
     * Defines fieldName attribute for each treeItem.
     *
     * @param root the object
     * @param loaded if {@code treu }
     */
    protected void completeBuild(TreeItem<Pair<ObjectProperty, Properties>> root, boolean loaded) {
        TreeView<Pair<ObjectProperty, Properties>> tv = new TreeView();
        tv.setRoot(root);
        String rootFieldName = root.getValue().getValue().getProperty(FIELD_NAME_ATTR);
        for (int i = 0; i < tv.getExpandedItemCount(); i++) {
            Object obj = tv.getTreeItem(i).getValue().getKey().get();
            String fieldName = getFieldName(obj);
            if (fieldName == null) {
                fieldName = rootFieldName + "_" + i + "_" + obj.getClass().getSimpleName();
                if (loaded) {
                    fieldName += "_loaded";
                }
            }
            tv.getTreeItem(i).getValue().getValue().setProperty(FIELD_NAME_ATTR, fieldName);
            if ( !loaded ) {
                if ((obj instanceof Node) && DockRegistry.isDockTarget((Node)obj)) {
                    if ( i == 0 && ! getAllDockTargets().containsKey(fieldName) ) {
                        getDefaultDockTargets().put(fieldName, tv.getTreeItem(i));
                    } else if (getAllDockTargets().containsKey(fieldName)) {
                        getDefaultDockTargets().remove(fieldName);
                    }
                    getAllDockTargets().put(fieldName, tv.getTreeItem(i));
                }
                registered.put(fieldName, obj);
            }
            //
            // For now we don't use parent dock target anywhere in code
            //
            if ( i > 0 && (obj instanceof Node) && DockRegistry.isDockTarget((Node) obj) ) {
                TreeItem<Pair<ObjectProperty, Properties>> p = findParentDockTarget(tv,tv.getTreeItem(i));
                String parentFieldName = p.getValue().getValue().getProperty(FIELD_NAME_ATTR);
                tv.getTreeItem(i).getValue().getValue().setProperty(PARENT_DOCKTARGET_ATTR, parentFieldName);
            }
        }//for
        
        
    }
    
    private TreeItem<Pair<ObjectProperty, Properties>> findParentDockTarget(TreeView tv,TreeItem item) {
        TreeItem<Pair<ObjectProperty, Properties>> retval = tv.getRoot();
        TreeItem<Pair<ObjectProperty, Properties>> parent = item.getParent();
        while ( parent != null ) {
            Object obj = parent.getValue().getKey().get();            
            if ( (obj instanceof Node) && DockRegistry.isDockTarget((Node) obj) ) {
                retval = parent;
                break;
            }
            
            parent = parent.getParent();
        }
        return retval;
    }
    protected String getFieldName(Object obj, String rootFieldName, int idx) {
        String fieldName = getFieldName(obj);
        if (fieldName != null) {
            return fieldName;
        }
        if ((obj instanceof Node) && getExplicitlyRegistered().containsValue((Node) obj)) {
            for (String key : getExplicitlyRegistered().keySet()) {
                if (obj == getExplicitlyRegistered().get(key)) {
                    fieldName = key;
                    break;
                }
            }
        }
        if (fieldName == null) {
            for (String key : registered.keySet()) {
                if (obj == registered.get(key)) {
                    fieldName = key;
                    break;
                }
            }
        }
        if (fieldName == null && !isLoaded()) {
            fieldName = rootFieldName + "_" + idx + "_" + obj.getClass().getSimpleName();
        } else if (fieldName == null) {
            //
            // default state has already been created ( isLoaded() == true)
            //
            return getFieldName(obj);
        }
        if (fieldName == null) {
            //
            // default state has already been created ( isLoaded() == true)
            //
            return getFieldName(obj);
        }

        return fieldName;
    }
}//AbstractDockLoader
