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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.util.prefs.CommonPreferences;
import org.vns.javafx.dock.api.util.prefs.InstancePreferences;

/**
 *
 * @author Valery
 */
public class DockLoader {
    
    public static final String STORE_ENTRY_NAME = "store-entry-name";
    public static final String STORE_ENTRY_CLASS = "store-entry-class";    
    public static final String INFO = "info";    
    public static final String NODE = "node";    
    public static final String PROPERTIES = "properties";    
    
    public static final String REGISTRY_STORE_ENTRIES = "store-registered-classes";

    private final Map<String, Object> store = FXCollections.observableHashMap();

    private String prefEntry;
    private boolean loaded = false;
    
    public synchronized static DockLoader create(String prefEntry) {
        if (SingletonInstance.INSTANCE.prefEntry == null && prefEntry != null) {
            SingletonInstance.INSTANCE.prefEntry = prefEntry;
        }
        return SingletonInstance.INSTANCE;
    }

    public synchronized static DockLoader create(Class clazz) {
        if (SingletonInstance.INSTANCE.prefEntry == null && clazz != null) {
            String prefEntry = clazz.getName().replace(".", "/");
            SingletonInstance.INSTANCE.prefEntry = prefEntry;
        }
        return SingletonInstance.INSTANCE;
    }

    private synchronized static DockLoader getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private Map<String, Object> getStore() {
        return store;
    }
    
    public Object register(String entry, Class clazz) {
        Object retval = null;
        if ( loaded ) {
            throw new IllegalStateException("Attempts to register an entry '" 
                    + entry + "' and class '" + clazz.getName() + "' but the method 'load' has already been invoked");
        }
        if (entry == null || getStore().containsKey(entry)) {
            throw new IllegalArgumentException("Dublicate entry name: " + entry);
        }

        try {
            retval = clazz.newInstance();
            if (!Dockable.class.isInstance(retval) && !DockTarget.class.isInstance(retval)) {
                throw new IllegalArgumentException("Illegall className. entry name: " + entry + "; class=" + clazz.getName());
            }
            getStore().put(entry, retval);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DockLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    public Dockable registerDockable(String entry, Class<? extends Dockable> clazz) {
        Dockable retval = null;
        if ( loaded ) {
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
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DockLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    public DockTarget registerDockTarget(String entry, Class<? extends DockTarget> clazz) {
        DockTarget retval = null;

        if ( loaded ) {
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
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DockLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    public void load() {
       loaded = true;
       //
       // Go through all registered DockTarget and set there this instance 
       //
       getStore().values().forEach( obj -> {
           if ( obj instanceof DockTarget )  {
               ((DockTarget)obj).targetController().setDockLoader(this);
           }
       });
       
    }
    
    protected boolean isRegistered(Object node) {
        return getEntryName(node) != null;
    }
    
    public void saveStore() {

        CommonPreferences cp = new CommonPreferences(prefEntry);
        CommonPreferences registered = cp.next(REGISTRY_STORE_ENTRIES);
        
        InstancePreferences registeredProps = registered.getProperties(PROPERTIES);

        if (registeredProps == null || registeredProps.keys().length == 0) {
            InstancePreferences ip = registered.createProperties(PROPERTIES);
            getStore().forEach((k, v) -> {
                ip.setProperty(k, v.getClass().getName());
            });
        }
    }

    public String getEntryName(Object obj) {
        String retval = null;
        if ((obj instanceof Node) && DockRegistry.isDockable((Node) obj) && getStore().containsValue(obj)) {
            for (Entry<String, Object> e : getStore().entrySet()) {
                if (e.getValue() == obj) {
                    retval = e.getKey();
                    break;
                }
            }
        } else if ((obj instanceof Node) && DockRegistry.isDockPaneTarget((Node) obj) && getStore().containsValue(obj)) {
            for (Entry<String, Object> e : getStore().entrySet()) {
                if (e.getValue() == obj) {
                    retval = e.getKey();
                    break;
                }
            }
        }

        return retval;
    }
    
    public void save(DockTarget dockTarget) {
        PreferencesBuilder builder = dockTarget.targetController().getPreferencesBuilder();
        TreeItem<PreferencesItem> root = builder.build(dockTarget);
        CommonPreferences cp = new CommonPreferences(prefEntry);
        PreferencesItem pit = root.getValue();
        String entryName = getEntryName(pit.getItemObject());
        CommonPreferences cpProps = cp.next(entryName).next(PROPERTIES);
        InstancePreferences ip = cpProps.createProperties(INFO);
        ip.setProperty(STORE_ENTRY_NAME, entryName);
        ip.setProperty(STORE_ENTRY_CLASS, pit.getItemObject().getClass().getName());
        final Map<String, String> nodeProps = builder.getProperties(pit.getItemObject());
        if (nodeProps != null && !nodeProps.isEmpty()) {
            InstancePreferences nodeIp = cpProps.createProperties(NODE);
            nodeProps.keySet().forEach(k -> {
                nodeIp.setProperty(k, nodeProps.get(k));
            });
        }
        for (int i = 0; i < root.getChildren().size(); i++) {
            System.err.println(i + "). " + root.getChildren().get(i));
            TreeItem<PreferencesItem> it = root.getChildren().get(i);
            save(builder, it, entryName + "/" + String.valueOf(i));
        }
    }

    protected void save(PreferencesBuilder builder, TreeItem<PreferencesItem> item, String namespace) {
        CommonPreferences cp = new CommonPreferences(prefEntry).next(namespace);
        CommonPreferences cpProps = cp.next(PROPERTIES);
        PreferencesItem pit = item.getValue();
        String entryName = getEntryName(pit.getItemObject());
        InstancePreferences ip = cpProps.createProperties(INFO);
        if ( entryName == null ) {
            ip.setProperty(STORE_ENTRY_NAME, "not.registered");
        } else {
            ip.setProperty(STORE_ENTRY_NAME, entryName);
        }
        ip.setProperty(STORE_ENTRY_CLASS, pit.getItemObject().getClass().getName());
        final Map<String, String> nodeProps = builder.getProperties(pit.getItemObject());
        if (nodeProps != null && !nodeProps.isEmpty()) {
            InstancePreferences nodeIp = cpProps.createProperties(NODE);
            nodeProps.keySet().forEach(k -> {
                nodeIp.setProperty(k, nodeProps.get(k));
            });
        }

        for (int i = 0; i < item.getChildren().size(); i++) {
            TreeItem<PreferencesItem> it = item.getChildren().get(i);
            save(builder, it, namespace + "/" + String.valueOf(i));
        }

    }

    public String namespaceStringValue(DockTarget dockTarget) {
        return namespaceStringValue("", getEntryName(dockTarget), 0);
    }

    public String namespaceStringValue(String namespace, String entryName, int offset) {
        String sep = System.lineSeparator();
        StringBuilder sb = new StringBuilder();

        CommonPreferences cp = new CommonPreferences(prefEntry);
        if (cp.childrenNames().length == 0) {
            return "root '" + prefEntry + "' is empty";
        }
        CommonPreferences cpEntry = cp.next(namespace).next(entryName);
        //System.err.println("!!!!!!!! cpEntry.directoryNamespace() = " + cpEntry.directoryNamespace());
        InstancePreferences ip = cpEntry.next(PROPERTIES).getProperties(INFO);

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
                    .append(namespaceStringValue(cpEntry.rootExtendedNamespace(), childs[i], propOffset));
        }
        return sb.toString();
    }

    public void reset() {
        new CommonPreferences(prefEntry).clearRoot();
    }
    public void resetStore() {
        new CommonPreferences(prefEntry).next(REGISTRY_STORE_ENTRIES).clearRoot();
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
        private static final DockLoader INSTANCE = new DockLoader();
    }

}
