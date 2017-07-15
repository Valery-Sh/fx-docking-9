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
/**
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractDockLoader {

    public static final String PERSIST_KEY = "docloader-stotere-node-key";

    public static final String FIELD_NAME_ATTR = "ld:fieldName";
    public static final String CLASS_NAME_ATTR = "ld:className";
    public static final String TAG_NAME_ATTR = "ld:tagName";
    public static final String IGNORE_ATTR = "ignore:treeItem";
    public static final String ISDOCKABLE_ATTR = "ld:isdockable";
    public static final String ISDOCKTARGET_ATTR = "ld:isdocktarget";

    private final static List<Node> stateChangedList = FXCollections.observableArrayList();

    private final Map<String, Node> explicitlyRegistered = FXCollections.observableHashMap();
    private final Map<String, Object> registered = FXCollections.observableHashMap();

    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> defaultDockTargets = FXCollections.observableHashMap();
    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> allDockTargets = FXCollections.observableHashMap();
    private final Map<String, TreeItem<Pair<ObjectProperty, Properties>>> defaultDockables = FXCollections.observableHashMap();

    private final Map<String, Node> freeDockTargets = FXCollections.observableHashMap();


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
    protected abstract boolean isDestroyed();
    protected abstract void resetPreferences();
    protected abstract void save();
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
     * Try to find in an {@link #registered } collection an object which 
     * is equal to the given object and return it's field name if found.
     *
     * @param obj an object whose field name has to be returned
     * @param dockTargetRoot the tree item which represents the root of a
     * DockTarget
     * @return the field name of the object if it is registered/ Otherwise
     * returns null.
     */
/*    protected String getFieldName(Object obj, TreeItem<Pair<ObjectProperty, Properties>> dockTargetRoot) {
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
        }

        return fieldName;
    }
*/
    public boolean isSaveOnClose() {
        return saveOnClose;
    }

    public void setSaveOnClose(boolean saveOnClose) {
        this.saveOnClose = saveOnClose;
    }


    public String getPreferencesRoot() {
        return preferencesRoot;
    }

    protected Map<String, Object> getRegistered() {
        return registered;
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
    }


    /**
     * Returns a
     *
     * @return
     */
    protected Map<String, Node> getExplicitlyRegistered() {
        return explicitlyRegistered;
    }

    public Map<String, TreeItem<Pair<ObjectProperty, Properties>>> getAllDockTargets() {
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

    protected Map<String, Node> getFreeDockTargets() {
        return freeDockTargets;
    }

    protected Map<String, TreeItem<Pair<ObjectProperty, Properties>>> getDefaultDockTargets() {
        if ( defaultDockTargets.isEmpty() ) {
            allDockTargets.forEach((k, v) -> {
                if (getFreeDockTargets().containsKey(k)) {
                    defaultDockTargets.put(k, v);
                }
            });
        }
        return defaultDockTargets;
    }

/*    protected Map<String, TreeItem<Pair<ObjectProperty, Properties>>> getAllDockTargets() {
        return allDockTargets;
    }
*/
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
            String existingName = getEntryName(node);
            getExplicitlyRegistered().remove(existingName);
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
        // Go through all registered DockTarget and set there this instance 
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

    public boolean isRegistered(Node node) {
        return registered.values().contains(node);
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
    protected PreferencesBuilder builder(Node dockTarget) {
        //DockRegistry.dockTarget(dockTarget).targetController().setDockLoader(this);

        PreferencesBuilder builder = DockRegistry.dockTarget(dockTarget).targetController()
                .getPreferencesBuilder();
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
     * @param item
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
                    getAllDockTargets().put(fieldName, tv.getTreeItem(i));
                }
                registered.put(fieldName, obj);
            }

        }

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


}
