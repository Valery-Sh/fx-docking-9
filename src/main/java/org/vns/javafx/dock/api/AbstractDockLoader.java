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

    public static final String FIELD_NAME = "-ld:fieldName";
    public static final String NOT_REGISTERED = "not.registered";

    public static final String CLASS_NAME = "-ld:className";
    public static final String INFO = "info";
    public static final String NODE = "node";
    public static final String PROPERTIES = "properties";

    public static final String REGISTRY_STORE_ENTRIES = "store-registered-classes";
    public static final String IMPLICIT = "_implicit_registered_";

    private final static Map<String, Node> store = FXCollections.observableHashMap();
    private final static List<Node> stateChangedList = FXCollections.observableArrayList();
    private final static List<TreeItem<Pair<ObjectProperty, Properties>>> defaultState = FXCollections.observableArrayList();

    private String prefEntry;

    private boolean loaded = false;

    protected AbstractDockLoader(String prefEntry) {
        this.prefEntry = prefEntry;
    }

    protected AbstractDockLoader(Class clazz) {
        prefEntry = clazz.getName().replace(".", "/");
    }
    
    protected abstract void save(DockTarget dockTarget, TreeItem<Pair<ObjectProperty,Properties>> root);
    public abstract void resetStore(); 
    public abstract void reset(); 
    protected abstract void saveStore();
    //protected abstract boolean isDestroyed(); 
    public abstract void save(DockTarget dockTarget); 
    protected abstract TreeItem<Pair<ObjectProperty,Properties>> restore(DockTarget dockTarget); 

    public String getPrefEntry() {
        return prefEntry;
    }

    public String getRoot() {
        return prefEntry;
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

    protected Map<String, Node> getStore() {
        return store;
    }

    private void registerImplicit(String entry, Node dockTarget) {
        List<Dockable> list = DockRegistry.dockTarget(dockTarget)
                .targetController()
                .getDockables();
        for (int i = 0; i < list.size(); i++) {
            if (!isRegistered(list.get(i).node())) {
                getStore().put(entry + IMPLICIT + i, list.get(i).node());
            }
            //
            // Mark the node as a subject to persist
            //
            list.get(i).node().getProperties().put(PERSIST_KEY, Boolean.TRUE);
        }
    }

    public void register(String entry, Node node) {
        if (loaded) {
            throw new IllegalStateException("Attempts to register an entry '"
                    + entry + "' and class '" + node.getClass().getName() + "' but the method 'load' has already been invoked");
        }
        if (entry == null || getStore().containsKey(entry)) {
            throw new IllegalArgumentException("Dublicate entry name: " + entry);
        }
        //
        // May be the dockable node is allready registered with another entry name
        //
        if (DockRegistry.isDockable(node) && getStore().containsValue(node)) {
            String existingName = getEntryName(node);
            if (!existingName.contains(IMPLICIT)) {
                throw new IllegalArgumentException("Dublicate entry name: " + entry);
            } else {
                getStore().remove(existingName);
            }
        }

        if (!DockRegistry.isDockTarget(node) && !DockRegistry.isDockable(node)) {
            throw new IllegalArgumentException("Illegall className. entry name: " + entry + "; class=" + node.getClass().getName());
        }

        getStore().put(entry, node);
        if (DockRegistry.isDockTarget(node)) {
            //registerImplicit(entry, node);
            addListeners(node);
        }
    }

    public Node register(String entry, Class clazz) {
        Node retval = null;
        if (loaded) {
            throw new IllegalStateException("Attempts to register an entry '"
                    + entry + "' and class '" + clazz.getName() + "' but the method 'load' has already been invoked");
        }
        if (entry == null || getStore().containsKey(entry)) {
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

            getStore().put(entry, retval);
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
                    if (!stateChangedList.isEmpty()) {
                        List<Node> list = new ArrayList<>();
                        list.addAll(stateChangedList);
                        list.forEach(node -> {
                            System.err.println("SAVE FROM LISTENER node = " + node);
                            save(DockRegistry.dockTarget(node));
                            stateChangedList.remove(node);
                        });
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
        System.err.println("sceneProperty().addListener target=" + target);
        if (target.getScene() != null && target.getScene().getWindow() != null) {
            System.err.println("sceneProperty().addEventHandler target=" + target);
            target.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, wh);
        }

        //scenelisteners.put(node, sl);
        //windowlisteners.put(node, wl);
    }

    public void load() {
        if (loaded) {
            return;
        }

        Long start = System.currentTimeMillis();
        DockPreferences cp = new DockPreferences(prefEntry);
        if (cp.getProperties(PROPERTIES) == null) {
            cp.createProperties(PROPERTIES).setProperty("description", "application");

        }

        List<Node> nodeList = new ArrayList<>();
        nodeList.addAll(getStore().values());
        nodeList.forEach(node -> {
            if (DockRegistry.isDockTarget(node)) {
                registerImplicit(getEntryName(node), node);
            }
        });
        //
        // Go through all registered DockTarget and set there this instance 
        //
        getStore().values().forEach(node -> {
            if (DockRegistry.isDockTarget(node)) {
                DockRegistry.dockTarget(node).targetController().setDockLoader(this);
            }
        });

        //
        // Save default state
        //
        defaultState.clear();

        try {
            getStore().values().forEach(node -> {
                if (DockRegistry.isDockTarget(node)) {
                    DockTarget t = DockRegistry.dockTarget(node);
                    defaultState.add(t.targetController().getPreferencesBuilder().build());
                }
            });
        } catch (Exception ex) {
            defaultState.clear();
        }

        //
        // Check weather the registry store changed and if so reset and reload
        //
        if (isDestroyed()) {
            reset();
            save();
        }

        List<TreeItem<Pair<ObjectProperty, Properties>>> list = FXCollections.observableArrayList();
        getStore().values().forEach(node -> {
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

    public void reload() {
        reset();
        saveStore();

        Long start = System.currentTimeMillis();

        defaultState.forEach(treeItem -> {
            save(DockRegistry.dockTarget((Node) treeItem.getValue().getKey().get()), treeItem);
        });

        List<TreeItem<Pair<ObjectProperty, Properties>>> list = FXCollections.observableArrayList();
        getStore().values().forEach(node -> {
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

    public void save() {
        Long start = System.currentTimeMillis();

        DockPreferences cp = new DockPreferences(prefEntry);
        //cp.clearRoot();
        reset();
        saveStore();
        getStore().values().forEach((node) -> {
            if (DockRegistry.isDockTarget(node)) {
                save(DockRegistry.dockTarget(node));
            }
        });
        Long end = System.currentTimeMillis();
        System.err.println("!!!!!!!!! ON SAVE TIME !!!!!! " + (end - start));

    }

    public boolean isRegistered(Node node) {
        return getEntryName(node) != null;
    }

    public String getEntryName(Object obj) {
        String retval = null;
        for (Map.Entry<String, Node> e : getStore().entrySet()) {
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
        DockPreferences cp = new DockPreferences(prefEntry);
        DockPreferences registered = cp.next(REGISTRY_STORE_ENTRIES);

        PrefProperties registeredProps = registered.getProperties(PROPERTIES);
        if (!getStore().isEmpty() && (registeredProps == null || registeredProps.keys().length == 0)) {
            return true;
        }

        Map<String, String> props = registeredProps.toMap();
        for (String key : props.keySet()) {
            if (!getStore().containsKey(key)) {
                return true;
            }
            String class1 = props.get(key);
            String class2 = getStore().get(key).getClass().getName();
            if (!class2.equals(class1)) {
                return true;
            }
        }//for

        return retval;
    }

}
