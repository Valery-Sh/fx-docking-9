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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import org.vns.javafx.dock.api.util.prefs.DockPreferences;
import org.vns.javafx.dock.api.util.prefs.PrefProperties;

/**
 *
 * @author Valery
 */
public class DockLoader extends AbstractDockLoader {
    
    protected DockLoader(String prefEntry) {
        super(prefEntry);
    }

    public DockLoader(Class clazz) {
        super(clazz);
    }


    @Override
    protected void saveStore() {
        DockPreferences cp = new DockPreferences(getPrefEntry());
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



    protected void save(DockTarget dockTarget, TreeItem<Pair<ObjectProperty,Properties>> root) {
        PreferencesBuilder builder = dockTarget.targetController().getPreferencesBuilder();
        DockPreferences cp = new DockPreferences(getPrefEntry());

        Pair<ObjectProperty,Properties> pair = root.getValue();
        String entryName = getEntryName(pair.getKey().get());
        cp.next(entryName).removelChildren();
        //PrefProperties ip1 = cp.next(entryName).next(PROPERTIES).getProperties(INFO);
        DockPreferences cpProps = cp.next(entryName).next(PROPERTIES);
        PrefProperties ip = cpProps.createProperties(INFO);
        ip.setProperty(FIELD_NAME, entryName);
        ip.setProperty(CLASS_NAME, pair.getKey().get().getClass().getName());
        final Map<String, String> nodeProps = builder.getProperties(pair.getKey().get());
        if (nodeProps != null && !nodeProps.isEmpty()) {
            PrefProperties nodeIp = cpProps.createProperties(NODE);
            nodeProps.keySet().forEach(k -> {
                nodeIp.setProperty(k, nodeProps.get(k));
            });
        }
        for (int i = 0; i < root.getChildren().size(); i++) {
            TreeItem<Pair<ObjectProperty,Properties>> it = root.getChildren().get(i);
            save(builder, it, entryName + "/" + String.valueOf(i));
        }

    }

    @Override
    public void save(DockTarget dockTarget) {
        PreferencesBuilder builder = dockTarget.targetController().getPreferencesBuilder();
        TreeItem<Pair<ObjectProperty,Properties>> root = builder.build();
        DockPreferences cp = new DockPreferences(getPrefEntry()).next(getEntryName(dockTarget));
        cp.removelChildren();
        String ename = getEntryName(dockTarget);
        long t1 = System.currentTimeMillis();
        save(dockTarget, root);
        long t2 = System.currentTimeMillis();
        System.err.println("Entry " + ename + "; time=" + (t2-t1));
    }

    protected void save(PreferencesBuilder builder, TreeItem<Pair<ObjectProperty,Properties>> item, String namespace) {
        DockPreferences cp = new DockPreferences(getPrefEntry()).next(namespace);
        DockPreferences cpProps = cp.next(PROPERTIES);
        Pair<ObjectProperty,Properties> pair = item.getValue();
        String entryName = getEntryName(pair.getKey().get());
        PrefProperties ip = cpProps.createProperties(INFO);
        if (entryName == null) {
            ip.setProperty(FIELD_NAME, NOT_REGISTERED);
        } else {
            ip.setProperty(FIELD_NAME, entryName);
        }
        ip.setProperty(CLASS_NAME, pair.getKey().get().getClass().getName());
        final Map<String, String> nodeProps = builder.getProperties(pair.getKey().get());
        if (nodeProps != null && !nodeProps.isEmpty()) {
            PrefProperties nodeIp = cpProps.createProperties(NODE);
            nodeProps.keySet().forEach(k -> {
                nodeIp.setProperty(k, nodeProps.get(k));
            });
        }
        for (int i = 0; i < item.getChildren().size(); i++) {
            TreeItem<Pair<ObjectProperty,Properties>> it = item.getChildren().get(i);
            save(builder, it, namespace + "/" + String.valueOf(i));
        }
    }


    @Override
    protected TreeItem<Pair<ObjectProperty,Properties>> restore(DockTarget dockTarget) {
        return restore("", getEntryName(dockTarget.target()));
    }

    protected TreeItem<Pair<ObjectProperty,Properties>> restore(String namespace, String entryName) {
        //PreferencesItem pair = new PreferencesItem();
        TreeItem<Pair<ObjectProperty,Properties>> treeItem = new TreeItem<>();
        Pair<ObjectProperty,Properties> pair;
        DockPreferences cp = new DockPreferences(getPrefEntry());
        PrefProperties infoPrefs = cp.next(namespace).next(entryName).next(PROPERTIES).getProperties(INFO);
        String storeEntry = infoPrefs.getProperty(FIELD_NAME);
        //
        // We store into PreferencesItem an actual object if registered or a 
        // class name of the unregistered object
        //
        if (storeEntry == null || NOT_REGISTERED.equals(storeEntry)) {
            pair = new Pair(new SimpleObjectProperty(null), new Properties());
            pair.getValue().put("-ignore:treeItem", treeItem);
            pair.getValue().put("-ld:className", infoPrefs.getProperty(CLASS_NAME));
        } else {
            pair = new Pair(new SimpleObjectProperty(getStore().get(storeEntry)), new Properties());
            pair.getValue().put("-ignore:treeItem", treeItem);
            pair.getValue().put("-ld:className", getStore().get(storeEntry).getClass().getName());
            pair.getKey().set(getStore().get(storeEntry));
        }
        treeItem.setValue(pair);
        //
        // Copy to PreferencesItem  node properties if exist 
        //
        PrefProperties ip = cp.next(namespace).next(entryName).next(PROPERTIES).getProperties(NODE);
        if (ip != null) {
            pair.getValue().putAll(ip.toMap());
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
    
    public void reset(DockPreferences cp) {
        //cp.getProperties(INFO).
        cp.removelChildren( name -> {return ! "properties".equals(name);});
    }
    public void reset() {
        //DockPreferences cp = new DockPreferences(prefEntry);
        //reset(cp);
        new DockPreferences(getPrefEntry()).clearRoot();
    }


    public void resetStore() {
        new DockPreferences(getPrefEntry()).next(REGISTRY_STORE_ENTRIES).clearRoot();
    }

/*    public String toString(DockTarget dockTarget) {
        StringBuilder sb = new StringBuilder(200);
        TreeItem ti = dockTarget.targetController().getPreferencesBuilder().build();
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


}
