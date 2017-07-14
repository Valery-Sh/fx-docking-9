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

import java.util.ArrayList;
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
import org.vns.javafx.dock.api.util.prefs.DockPreferences;
import org.vns.javafx.dock.api.util.prefs.PrefProperties;
//import org.vns.javafx.dock.api.util.prefs.DockPreferences;
//import org.vns.javafx.dock.api.util.prefs.PrefProperties;

/**
 *
 * @author Valery
 */
public abstract class AbstractDockLoader {

    public static final String PERSIST_KEY = "docloader-stotere-node-key";

    public static final String FIELD_NAME_ATTR = "ld:fieldName";
    public static final String CLASS_NAME_ATTR = "ld:className";
    public static final String TAG_NAME_ATTR = "ld:tagName";
    public static final String IGNORE_ATTR = "ignore:treeItem";
    public static final String REGSTERED_ATTR = "ld:registered";
    public static final String ISDOCKABLE_ATTR = "ld:isdockable";
    public static final String ISDOCKTARGET_ATTR = "ld:isdocktarget";

    public static final String NOT_REGISTERED = "not.registered";

    public static final String INFO = "info";
    public static final String NODE = "node";
    public static final String PROPERTIES = "properties";

    public static final String REGISTRY_STORE_ENTRIES = "store-registered-classes";
    public static final String IMPLICIT = "_implicit_registered_";

    private final static List<Node> stateChangedList = FXCollections.observableArrayList();
    //private final static List<TreeItem<Pair<ObjectProperty, Properties>>> defaultState = FXCollections.observableArrayList();
    ///////////////////////////
    ///////////////////////////
    private final Map<String, Node> explicitlyRegistered = FXCollections.observableHashMap();
    private final Map<String, Object> allRegistered = FXCollections.observableHashMap();

    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> defaultDockTargets = FXCollections.observableHashMap();
    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> allDockTargets = FXCollections.observableHashMap();
    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> defaultDockables = FXCollections.observableHashMap();

    private final Map<String, Node> freeDockTargets = FXCollections.observableHashMap();

    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> saveDockTargets = FXCollections.observableHashMap();

    private String preferencesRoot;

    private boolean saveOnClose;

    private boolean loaded = false;

    protected AbstractDockLoader(String prefEntry) {
        this.preferencesRoot = prefEntry;
    }

    protected AbstractDockLoader(Class clazz) {
        preferencesRoot = clazz.getName().replace(".", "/");
    }
    
    public abstract void reset();
    protected abstract void resetPreferences();

    protected abstract void save(DockTarget dockTarget);

    protected abstract TreeItem<Pair<ObjectProperty, Properties>> restore(DockTarget dockTarget);

    protected void restore() {
        for (String key : getDefaultDockTargets().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.isDockTarget(node)) {
                restore(DockRegistry.dockTarget(node));
            }
        }
    }

    /**
     * Try to find a allRegistered object which is equal to the given object and
     * return it's field name if found.
     *
     * @param obj an object whose field name has to be returned
     * @param dockTargetRoot the tree item which represents the root of a
     * DockTarget
     * @return the field name of the object if it is allRegistered/ Otherwise
     * returns null.
     */
    protected String getFieldName(Object obj, TreeItem<Pair<ObjectProperty, Properties>> dockTargetRoot) {
        String fieldName = null;
        if ((obj instanceof Node) && getExplicitlyRegistered().containsValue((Node) obj)) {
            for (String key : getExplicitlyRegistered().keySet()) {
                if (obj == getExplicitlyRegistered().get(key)) {
                    fieldName = key;
                    break;
                }
            }
        }

        if (fieldName != null && !isLoaded()) {
            getFreeDockTargets().remove(fieldName);
            getDefaultDockables().remove(fieldName);
        } else if (fieldName == null && !isLoaded()) {
            TreeView treeView = new TreeView();
            treeView.setRoot(dockTargetRoot);
            String rootFieldName = dockTargetRoot.getValue().getValue().getProperty(FIELD_NAME_ATTR);
            int idx = treeView.getExpandedItemCount();
            fieldName = rootFieldName + "_" + idx + "_" + obj.getClass().getSimpleName();
        } else if (fieldName == null) {
            //
            // default state has already been created ( isLoaded() == true)
            //
            return getFieldName(obj);
/*            TreeView treeView = new TreeView();

            String rootFieldName = dockTargetRoot.getValue().getValue().getProperty(FIELD_NAME_ATTR);
            treeView.setRoot(getAllDockTargets().get(rootFieldName));
            //treeView.setRoot(dockTargetRoot);
            for (int i = 0; i < treeView.getExpandedItemCount(); i++) {
                TreeItem<Pair<ObjectProperty, Properties>> it = treeView.getTreeItem(i);
                if (obj == it.getValue().getKey().get()) {
                    if (isRegistered(it)) {
                        fieldName = it.getValue().getValue().getProperty(FIELD_NAME_ATTR);
                    } else {
                        fieldName = rootFieldName + "_" + i + "_" + obj.getClass().getSimpleName() + "-loaded";
                    }
                }
            }
*/            
        }

        return fieldName;
    }

    public boolean isSaveOnClose() {
        return saveOnClose;
    }

    public void setSaveOnClose(boolean saveOnClose) {
        this.saveOnClose = saveOnClose;
    }

    protected boolean isRegistered(TreeItem<Pair<ObjectProperty, Properties>> item) {
        String val = item.getValue().getValue().getProperty(REGSTERED_ATTR);
        return val != null && ("yes".equals(val) || "true".equals(val));
    }

    protected Map<String, TreeItem<Pair<ObjectProperty, Properties>>> getSaveDockTargets() {
        return saveDockTargets;
    }

    public void notifyTreeItemBuilt(TreeItem<Pair<ObjectProperty, Properties>> treeItem) {
        String fieldName = treeItem.getValue().getValue().getProperty(FIELD_NAME_ATTR);
        if (!isLoaded()) {
            Node node = (Node) treeItem.getValue().getKey().get();
            if (DockRegistry.isDockTarget(node)) {
//                getFreeDockTargets().remove(fieldName);
                getAllDockTargets().put(fieldName, treeItem);
                DockRegistry.dockTarget(node).targetController().setDockLoader(this);
            }
            //getAllRegistered().put(treeItem.getValue().getKey().get(), fieldName);
            getAllRegistered().put(fieldName, treeItem.getValue().getKey().get());
        } else {
            getSaveDockTargets().put(fieldName, treeItem);
        }
    }

    public String getPreferencesRoot() {
        return preferencesRoot;
    }

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
        System.err.println("Layout Changed count=" + stateChangedList.size());
    }

    protected Map<Node, String> getDockTargets() {
        Map<Node, String> retval = FXCollections.observableHashMap();
        getAllDockTargets().forEach((k, v) -> {
            retval.put((Node) v.getValue().getKey().get(), k);
        });
        return retval;
    }

    /**
     * Returns a
     *
     * @return
     */
    protected Map<String, Node> getExplicitlyRegistered() {
        return explicitlyRegistered;
    }

    protected Map<String, Object> getAllRegistered() {
        return allRegistered;
    }
    protected String getFieldName(Object obj) {
        String retval = null;
        for ( String key : allRegistered.keySet()) {
            if ( obj == allRegistered.get(key)) {
                retval = key;
                break;
            }
        }
        return retval;
    }

    protected Map<String, Node> getFreeDockTargets() {
        return freeDockTargets;
    }

    protected Map<String, TreeItem<Pair<ObjectProperty, Properties>>> getDefaultDockTargets() {
        return defaultDockTargets;
    }

    protected Map<String, TreeItem<Pair<ObjectProperty, Properties>>> getAllDockTargets() {
        return allDockTargets;
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
        // May be the dockable node is allready allRegistered with another entry name
        //
        if (DockRegistry.isDockable(node) && getExplicitlyRegistered().containsValue(node)) {
            String existingName = getEntryName(node);
            if (!existingName.contains(IMPLICIT)) {
                throw new IllegalArgumentException("Dublicate entry name: " + entry);
            } else {
                getExplicitlyRegistered().remove(existingName);
            }
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
            Logger.getLogger(AbstractDockLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    protected void addListeners(Node target) {
        EventHandler<WindowEvent> wh = new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                Platform.runLater(() -> {
                    System.err.println("1) *** SAVE ***");
                    if (!stateChangedList.isEmpty()) {
                        List<Node> list = new ArrayList<>();
                        list.addAll(stateChangedList);
                        list.forEach(node -> {
                            System.err.println("SAVE FROM LISTENER node = " + node);
                            //save(DockRegistry.dockTarget(node));
                            //stateChangedList.remove(node);
                        });
                        System.err.println("2) *** SAVE ***");
                        //save();
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
            //System.err.println("sceneProperty().addEventHandler target=" + target);
            target.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, wh);
        }

        //scenelisteners.put(node, sl);
        //windowlisteners.put(node, wl);
    }

    public abstract void load();

    /*    {
        if (loaded) {
            return;
        }

        Long start = System.currentTimeMillis();
        DockPreferences cp = new DockPreferences(preferencesRoot);
        if (cp.getProperties(PROPERTIES) == null) {
            cp.createProperties(PROPERTIES).setProperty("description", "application");

        }

        List<Node> nodeList = new ArrayList<>();
        nodeList.addAll(getExplicitlyRegistered().values());
        nodeList.forEach(node -> {
            if (DockRegistry.isDockTarget(node)) {
                registerImplicit(getEntryName(node), node);
            }
        });
        //
        // Go through all allRegistered DockTarget and set there this instance 
        //
        getExplicitlyRegistered().values().forEach(node -> {
            if (DockRegistry.isDockTarget(node)) {
                DockRegistry.dockTarget(node).targetController().setDockLoader(this);
            }
        });

        //
        // Save default state
        //
        //defaultState.clear();

        try {
            getExplicitlyRegistered().values().forEach(node -> {
                if (DockRegistry.isDockTarget(node)) {
                    DockTarget t = DockRegistry.dockTarget(node);
                    //??? defaultState.add(t.targetController().getPreferencesBuilder().build());
                }
            });
        } catch (Exception ex) {
            defaultState.clear();
        }

        //
        // Check weather the registry explicitlyRegistered changed and if so resetPreferences and reload
        //
        if (isDestroyed()) {
            resetPreferences();
            save();
        }

        List<TreeItem<Pair<ObjectProperty, Properties>>> list = FXCollections.observableArrayList();
        getExplicitlyRegistered().values().forEach(node -> {
            if (DockRegistry.isDockTarget(node)) {
                list.add(restore(DockRegistry.dockTarget(node)));
            }
        });

        //TreeItem<PreferencesItem> item = list.get(0);
//        String s = toString(item, 0);
//        System.err.println("================ RESTORED in LOAD ==========================");
//        System.err.println(s);
//        System.err.println("================= END RESTORED in LOAD=========================");
//        s = preferencesStringValue( DockRegistry.dockTarget((Node) item.getValue().getItemObject()));
//        System.err.println(s);
        list.forEach(it -> {
            DockTarget dt = DockRegistry.dockTarget((Node) it.getValue().getKey().get());
            dt.targetController().getPreferencesBuilder().restore(it);
        });
        Long end = System.currentTimeMillis();
        System.err.println("!!!!!!!!! TIME !!!!!! " + (end - start));
        loaded = true;

    }
     */
    public void reload() {
        resetPreferences();
//        saveStore();

        Long start = System.currentTimeMillis();

//        defaultState.forEach(treeItem -> {
//            save(DockRegistry.dockTarget((Node) treeItem.getValue().getKey().get()), treeItem);
//        });
        List<TreeItem<Pair<ObjectProperty, Properties>>> list = FXCollections.observableArrayList();
        getExplicitlyRegistered().values().forEach(node -> {
            if (DockRegistry.isDockTarget(node)) {
                list.add(restore(DockRegistry.dockTarget(node)));
            }
        });

        TreeItem<Pair<ObjectProperty, Properties>> item = list.get(0);
//        String s = toString(item, 0);
//        System.err.println("================ RESTORED in LOAD ==========================");
//        System.err.println(s);
//        System.err.println("================= END RESTORED in LOAD=========================");
//        s = preferencesStringValue( DockRegistry.dockTarget((Node) item.getValue().getItemObject()));
//        System.err.println(s);
        list.forEach(it -> {
            DockTarget dt = DockRegistry.dockTarget((Node) item.getValue().getKey().get());
            dt.targetController().getPreferencesBuilder().restore(it);
        });
        Long end = System.currentTimeMillis();
        System.err.println("!!!!!!!!! RELOAD TIME !!!!!! " + (end - start));
        setLoaded(true);

    }

    public abstract void save();

    public boolean isRegistered(Node node) {
        return getAllRegistered().values().contains(node);
    }

    public String getEntryName(Object obj) {
        String retval = null;
        for (Map.Entry<String, Node> e : getExplicitlyRegistered().entrySet()) {
            if (e.getValue() == obj) {
                retval = e.getKey();
                break;
            }
        }
        return retval;
    }

    /*    public String toString(DockTarget dockTarget) {
        StringBuilder sb = new StringBuilder(200);
        TreeItem<PreferencesItem> ti = dockTarget.targetController().getPreferencesBuilder().build(dockTarget);
        sb.append("---------------------------------------------------").append(System.lineSeparator())
                .append("ROOT " + ti.toString());

        for (int i = 0; i < ti.getChildren().size(); i++) {
            sb.append(System.lineSeparator())
                    .append(toString(ti.getChildren().get(i), 2));
        }
        sb.append(System.lineSeparator())
                .append("---------------------------------------------------")
                .append(System.lineSeparator());
        return sb.toString();
    }

    protected String toString(TreeItem<PreferencesItem> treeItem, int offset) {
        StringBuilder sb = new StringBuilder(200);
        int newOffset = offset + 2;

        sb.append(spaces(offset))
                .append(treeItem.toString());

        for (int i = 0; i < treeItem.getChildren().size(); i++) {
            sb.append(System.lineSeparator())
                    .append(toString(treeItem.getChildren().get(i), newOffset));

        }
        return sb.toString();
    }
     */
    private String spaces(int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    protected boolean isDestroyed() {
        boolean retval = false;
        DockPreferences cp = new DockPreferences(preferencesRoot);
        DockPreferences registered = cp.next(REGISTRY_STORE_ENTRIES);

        PrefProperties registeredProps = registered.getProperties(PROPERTIES);
        if (!getExplicitlyRegistered().isEmpty() && (registeredProps == null || registeredProps.keys().length == 0)) {
            return true;
        }

        Map<String, String> props = registeredProps.toMap();
        for (String key : props.keySet()) {
            if (!getExplicitlyRegistered().containsKey(key)) {
                return true;
            }
            String class1 = props.get(key);
            String class2 = getExplicitlyRegistered().get(key).getClass().getName();
            if (!class2.equals(class1)) {
                return true;
            }
        }//for

        return retval;
    }

}
