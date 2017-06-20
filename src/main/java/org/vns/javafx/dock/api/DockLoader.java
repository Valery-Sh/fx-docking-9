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
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.vns.javafx.dock.api.util.prefs.DockPreferences;
import org.vns.javafx.dock.api.util.prefs.PrefProperties;

/**
 *
 * @author Valery
 */
public class DockLoader {

    public static final String STORE_ENTRY_NAME = "store-entry-name";
    public static final String NOT_REGISTERED = "not.registered";

    public static final String STORE_ENTRY_CLASS = "store-entry-class";
    public static final String INFO = "info";
    public static final String NODE = "node";
    public static final String PROPERTIES = "properties";

    public static final String REGISTRY_STORE_ENTRIES = "store-registered-classes";

    private final static Map<String, Node> store = FXCollections.observableHashMap();
    private final static List<Node> stateChangedList = FXCollections.observableArrayList();
    private final static List<TreeItem<PreferencesItem>> defaultState = FXCollections.observableArrayList();

    private String prefEntry;

    private boolean loaded = false;

    public DockLoader(String prefEntry) {
        this.prefEntry = prefEntry;
    }

    public DockLoader(Class clazz) {
        prefEntry = clazz.getName().replace(".", "/");
    }

    public synchronized static DockLoader create(String prefEntry) {
        return new DockLoader(prefEntry);
    }

    public synchronized static DockLoader create(Class clazz) {
        return create(clazz.getName().replace(".", "/"));
    }

    public String getRoot() {
        return prefEntry;
    }

    public void layoutChanged(Node target) {
        if (! loaded || stateChangedList.contains(target) ) {
            return;
        }
        stateChangedList.add(target);
    }

    private Map<String, Node> getStore() {
        return store;
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

            if (!DockRegistry.isDockPaneTarget(retval) && !DockRegistry.isDockable(retval)) {
                throw new IllegalArgumentException("Illegall className. entry name: " + entry + "; class=" + clazz.getName());
            }

            getStore().put(entry, retval);
            if (DockRegistry.isDockPaneTarget(retval)) {
                addListeners(retval);
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DockLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    /*    public Dockable registerDockable(String entry, Class<? extends Dockable> clazz) {
        Dockable retval = null;
        if (loaded) {
            throw new IllegalStateException("Attempts to register an entry '"
                    + entry + "' and class '" + clazz.getName() + "' but the method 'load' has already been invoked");
        }
        if (entry == null || getStore().containsKey(entry)) {
            throw new IllegalArgumentException("Dublicate entry name: " + entry);
        }

        if (entry == null || getStore().containsKey(entry)) {
            throw new IllegalArgumentException("Dublicate entry name: " + entry);
        }

        try {
            retval = clazz.newInstance();
            getStore().put(entry, DockRegistry.dockable(retval));
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DockLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    public DockTarget registerDockTarget(String entry, Class<? extends DockTarget> clazz) {
        DockTarget retval = null;

        if (loaded) {
            throw new IllegalStateException("Attempts to register an entry '"
                    + entry + "' and class '" + clazz.getName() + "' but the method 'load' has already been invoked");
        }
        if (entry == null || getStore().containsKey(entry)) {
            throw new IllegalArgumentException("Dublicate entry name: " + entry);
        }

        if (entry == null || getStore().containsKey(entry)) {
            throw new IllegalArgumentException("Dublicate entry name: " + entry);
        }

        try {
            retval = clazz.newInstance();
            getStore().put(entry, retval);
            addListeners(DockRegistry.dockPaneTarget(node));
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DockLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }
     */
    protected void addListeners(Node target) {

        EventHandler<WindowEvent> wh = new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                Platform.runLater(() -> {
                    if (!stateChangedList.isEmpty()) {
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
        //scenelisteners.put(node, sl);
        //windowlisteners.put(node, wl);

    }

    public void load() {
        if (loaded) {
            return;
        }

        //
        // Check weather the registry store changed and if so reset and reload
        //
        if (!isDestroyed()) {
            reset();
            save();
        }
        //
        // Go through all registered DockTarget and set there this instance 
        //
        getStore().values().forEach(node -> {
            if (DockRegistry.isDockPaneTarget(node)) {
                DockRegistry.dockPaneTarget(node).targetController().setDockLoader(this);
            }
        });
        
        
        Long start = System.currentTimeMillis();

        //
        // Save default state
        //
        getStore().values().forEach(node -> {
            if (DockRegistry.isDockPaneTarget(node)) {
                DockTarget t = DockRegistry.dockPaneTarget(node);
                defaultState.add(t.targetController().getPreferencesBuilder().build(t));
            }
        });
        System.err.println("DEFAULT STATE " + (System.currentTimeMillis() - start));
        
        List<TreeItem<PreferencesItem>> list = FXCollections.observableArrayList();
        getStore().values().forEach(node -> {
            if (DockRegistry.isDockPaneTarget(node)) {
                list.add(restore(DockRegistry.dockPaneTarget(node)));
            }
        });
        
        TreeItem<PreferencesItem> item = list.get(0);
//        String s = toString(item, 0);
//        System.err.println("================ RESTORED in LOAD ==========================");
//        System.err.println(s);
//        System.err.println("================= END RESTORED in LOAD=========================");
//        s = preferencesStringValue( DockRegistry.dockPaneTarget((Node) item.getValue().getItemObject()));
//        System.err.println(s);
        list.forEach(it -> {
            DockTarget dt = DockRegistry.dockPaneTarget((Node) item.getValue().getItemObject());
            dt.targetController().getPreferencesBuilder().restoreFrom(it);
        });
        Long end = System.currentTimeMillis();
        System.err.println("!!!!!!!!! TIME !!!!!! " + (end - start));
        loaded = true;
        
    }

    private final Map<Node, ChangeListener<Scene>> scenelisteners = FXCollections.observableHashMap();
    private final Map<Node, ChangeListener<Window>> windowlisteners = FXCollections.observableHashMap();

    protected boolean isRegistered(Object node) {
        return getEntryName(node) != null;
    }

    public void saveStore() {

        DockPreferences cp = new DockPreferences(prefEntry);
        DockPreferences registered = cp.next(REGISTRY_STORE_ENTRIES);

        PrefProperties registeredProps = registered.getProperties(PROPERTIES);

        if (registeredProps == null || registeredProps.keys().length == 0) {
            PrefProperties ip = registered.createProperties(PROPERTIES);
            getStore().forEach((k, v) -> {
                ip.setProperty(k, v.getClass().getName());
            });
            registered.next("NULL");
        }
    }

    public boolean isDestroyed() {
        boolean retval = true;
        DockPreferences cp = new DockPreferences(prefEntry);
        DockPreferences registered = cp.next(REGISTRY_STORE_ENTRIES);

        PrefProperties registeredProps = registered.getProperties(PROPERTIES);
        if (!getStore().isEmpty() && (registeredProps == null || registeredProps.keys().length == 0)) {
            return false;
        }
        if (getStore().isEmpty() && registeredProps != null && registeredProps.keys().length > 0) {
            return false;
        }
        if (getStore().size() != registeredProps.size()) {
            return false;
        }
        Properties props = registeredProps.toProperties();
        for (String key : getStore().keySet()) {
            if (!props.containsKey(key)) {
                return false;
            }
            String class1 = props.getProperty(key);
            String class2 = getStore().get(key).getClass().getName();
            if (!class2.equals(class1)) {
                return false;
            }
        }//for
        return retval;
    }

    public String getEntryName(Object obj) {
        String retval = null;
        for (Entry<String, Node> e : getStore().entrySet()) {
            if (e.getValue() == obj) {
                retval = e.getKey();
                break;
            }
        }

        return retval;
    }

    public void save() {
        Long start = System.currentTimeMillis();

        DockPreferences cp = new DockPreferences(prefEntry);
        cp.clearRoot();
        saveStore();
        getStore().values().forEach((node) -> {
            if (DockRegistry.isDockPaneTarget(node)) {
                save(DockRegistry.dockPaneTarget(node));
            }
        });
        Long end = System.currentTimeMillis();
        System.err.println("!!!!!!!!! ON CLOSE TIME !!!!!! " + (end - start));

    }

    protected void save(DockTarget dockTarget, TreeItem<PreferencesItem> root) {
        PreferencesBuilder builder = dockTarget.targetController().getPreferencesBuilder();
        DockPreferences cp = new DockPreferences(prefEntry);

        PreferencesItem pit = root.getValue();
        String entryName = getEntryName(pit.getItemObject());
        cp.next(entryName).removelChildren();
        //PrefProperties ip1 = cp.next(entryName).next(PROPERTIES).getProperties(INFO);
        DockPreferences cpProps = cp.next(entryName).next(PROPERTIES);
        PrefProperties ip = cpProps.createProperties(INFO);
        ip.setProperty(STORE_ENTRY_NAME, entryName);
        ip.setProperty(STORE_ENTRY_CLASS, pit.getItemObject().getClass().getName());
        final Map<String, String> nodeProps = builder.getProperties(pit.getItemObject());
        if (nodeProps != null && !nodeProps.isEmpty()) {
            PrefProperties nodeIp = cpProps.createProperties(NODE);
            nodeProps.keySet().forEach(k -> {
                nodeIp.setProperty(k, nodeProps.get(k));
            });
        }
        for (int i = 0; i < root.getChildren().size(); i++) {
            TreeItem<PreferencesItem> it = root.getChildren().get(i);
            save(builder, it, entryName + "/" + String.valueOf(i));
        }
        
    }    
    public void save(DockTarget dockTarget) {
        PreferencesBuilder builder = dockTarget.targetController().getPreferencesBuilder();
        TreeItem<PreferencesItem> root = builder.build(dockTarget);
        save(dockTarget, root);
    }

    protected void save(PreferencesBuilder builder, TreeItem<PreferencesItem> item, String namespace) {
        DockPreferences cp = new DockPreferences(prefEntry).next(namespace);
        DockPreferences cpProps = cp.next(PROPERTIES);
        PreferencesItem pit = item.getValue();
        String entryName = getEntryName(pit.getItemObject());
        PrefProperties ip = cpProps.createProperties(INFO);
        if (entryName == null) {
            ip.setProperty(STORE_ENTRY_NAME, NOT_REGISTERED);
        } else {
            ip.setProperty(STORE_ENTRY_NAME, entryName);
        }
        ip.setProperty(STORE_ENTRY_CLASS, pit.getItemObject().getClass().getName());
        final Map<String, String> nodeProps = builder.getProperties(pit.getItemObject());
        if (nodeProps != null && !nodeProps.isEmpty()) {
            PrefProperties nodeIp = cpProps.createProperties(NODE);
            nodeProps.keySet().forEach(k -> {
                nodeIp.setProperty(k, nodeProps.get(k));
            });
        }
        for (int i = 0; i < item.getChildren().size(); i++) {
            TreeItem<PreferencesItem> it = item.getChildren().get(i);
            save(builder, it, namespace + "/" + String.valueOf(i));
        }
    }

    public String preferencesStringValue(DockTarget dockTarget) {
        return preferencesStringValue("", getEntryName(dockTarget), 0);
    }

    protected String preferencesStringValue(String namespace, String entryName, int offset) {
        String sep = System.lineSeparator();
        StringBuilder sb = new StringBuilder();

        DockPreferences cp = new DockPreferences(prefEntry);
        if (cp.childrenNames().length == 0) {
            return "root '" + prefEntry + "' is empty";
        }
        DockPreferences cpEntry = cp.next(namespace).next(entryName);
        PrefProperties ip = cpEntry.next(PROPERTIES).getProperties(INFO);

        sb.append(sep)
                .append(spaces(offset))
                .append(entryName);

        sb.append(sep)
                .append(spaces((offset = offset + 2)))
                .append(PROPERTIES);
        int propOffset = offset;
        sb.append(sep)
                .append(spaces((offset = offset + 2)))
                .append(INFO);

        Properties props = ip.toProperties();
        String spaces = spaces(offset + 2);
        props.forEach((k, v) -> {
            sb.append(sep)
                    .append(spaces)
                    .append("key=" + k + ", val=" + v);
        });

        ip = cpEntry.next(PROPERTIES).getProperties(NODE);

        sb.append(sep)
                .append(spaces(offset))
                .append(NODE);
        if (ip != null) {
            props = ip.toProperties();
            String spaces1 = spaces(offset + 2);
            props.forEach((k, v) -> {
                sb.append(sep)
                        .append(spaces1)
                        .append("key=" + k + ", val=" + v);
            });
        }
        ////////////
        String[] childs = cpEntry.childrenNames();

        for (int i = 0; i < childs.length; i++) {
            if (PROPERTIES.equals(childs[i])) {
                continue;
            }
            sb.append(sep)
                    .append(spaces(propOffset))
                    .append(preferencesStringValue(cpEntry.currentNamespace(), childs[i], propOffset));
        }
        return sb.toString();
    }

    public TreeItem<PreferencesItem> restore(DockTarget dockTarget) {
        return restore("", getEntryName(dockTarget.target()));
    }

    protected TreeItem<PreferencesItem> restore(String namespace, String entryName) {
        //PreferencesItem pit = new PreferencesItem();
        TreeItem<PreferencesItem> treeItem = new TreeItem<>();
        PreferencesItem pit;
        DockPreferences cp = new DockPreferences(prefEntry);
        PrefProperties infoPrefs = cp.next(namespace).next(entryName).next(PROPERTIES).getProperties(INFO);
        String storeEntry = infoPrefs.getProperty(STORE_ENTRY_NAME);
        //
        // We store intto PreferencesItem an actual object if registered or a 
        // class name of the unregistered object
        //
        if (storeEntry == null || NOT_REGISTERED.equals(storeEntry)) {
            pit = new PreferencesItem(infoPrefs.getProperty(STORE_ENTRY_CLASS));
        } else {
            pit = new PreferencesItem(getStore().get(storeEntry));
        }
        treeItem.setValue(pit);
        //
        // Copy to PreferencesItem  node properties if exist 
        //
        PrefProperties ip = cp.next(namespace).next(entryName).next(PROPERTIES).getProperties(NODE);
        if (ip != null) {
            pit.getProperties().putAll(ip.toMap());
        }

        DockPreferences entryRoot = cp.next(namespace).next(entryName);

        String[] childs = entryRoot.childrenNames();

        for (int i = 0; i < childs.length; i++) {
            if (PROPERTIES.equals(childs[i])) {
                continue;
            }
            treeItem.getChildren().add(restore(entryRoot.currentNamespace(), childs[i]));
        }
        return treeItem;
    }

    public void reset() {
        new DockPreferences(prefEntry).clearRoot();
    }
    public void reload() {
        reset();
        saveStore();
        
        Long start = System.currentTimeMillis();
        
        defaultState.forEach(treeItem -> {
            save(DockRegistry.dockPaneTarget( (Node)treeItem.getValue().getItemObject()), treeItem );
        });
        
        List<TreeItem<PreferencesItem>> list = FXCollections.observableArrayList();
        getStore().values().forEach(node -> {
            if (DockRegistry.isDockPaneTarget(node)) {
                list.add(restore(DockRegistry.dockPaneTarget(node)));
            }
        });
        
        TreeItem<PreferencesItem> item = list.get(0);
//        String s = toString(item, 0);
//        System.err.println("================ RESTORED in LOAD ==========================");
//        System.err.println(s);
//        System.err.println("================= END RESTORED in LOAD=========================");
//        s = preferencesStringValue( DockRegistry.dockPaneTarget((Node) item.getValue().getItemObject()));
//        System.err.println(s);
        list.forEach(it -> {
            DockTarget dt = DockRegistry.dockPaneTarget((Node) item.getValue().getItemObject());
            dt.targetController().getPreferencesBuilder().restoreFrom(it);
        });
        Long end = System.currentTimeMillis();
        System.err.println("!!!!!!!!! RELOAD TIME !!!!!! " + (end - start));
        loaded = true;
        
    }

    public void resetStore() {
        new DockPreferences(prefEntry).next(REGISTRY_STORE_ENTRIES).clearRoot();
    }

    public String toString(DockTarget dockTarget) {
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

    private String spaces(int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private static class SingletonInstance {

    }

}
