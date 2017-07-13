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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Pair;
import org.vns.common.xml.javafx.TreeItemStringConverter;
import static org.vns.javafx.dock.api.AbstractDockLoader.PROPERTIES;
import org.vns.javafx.dock.api.util.prefs.DockPreferences;
import org.vns.javafx.dock.api.util.prefs.PrefProperties;

/**
 *
 * @author Valery
 */
public class DockLoader extends AbstractDockLoader {

    public static String DUMMY_KEY = "DUMMY_123_DUMMY_321";
    public static String DEFAULT = "default";
    public static String DOCKTARGETS = "docktargets";
    public static String DOCKABLES = "dockables";
    public static String SAVE = "save";
    public static String DESCRIPTOR = "descriptor";

    protected DockLoader(String preferencesRoot) {
        super(preferencesRoot);
    }

    public DockLoader(Class clazz) {
        super(clazz);
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }

        Long start = System.currentTimeMillis();
        DockPreferences cp = new DockPreferences(getPreferencesRoot());

        PrefProperties descrProps = cp.next(DEFAULT).getProperties(DESCRIPTOR);
        if (descrProps == null) {
            descrProps = cp.next(DEFAULT).createProperties(DESCRIPTOR);
            descrProps.setProperty("title", "descriptor");
        }

        descrProps = cp.next(SAVE).getProperties(DESCRIPTOR);
        if (descrProps == null) {
            descrProps = cp.next(SAVE).createProperties(SAVE);
            descrProps.setProperty("title", "descriptor");
        }
        descrProps = cp.next(SAVE).getProperties(DOCKTARGETS);
        if (descrProps == null) {
            descrProps = cp.next(SAVE).createProperties(DOCKTARGETS);
        }
        descrProps.setProperty(DUMMY_KEY, DUMMY_KEY);

        //reset();
/*        PrefProperties dProps = cp.next("default").getProperties("docktargets");
            dProps.forEach((k, v) -> {
            System.err.println("dockTarget = " + k);
            System.err.println("-----------------------------");
            System.err.println(v);
            System.err.println("-----------------------------");

        });
         */
        for (String key : getExplicitlyRegistered().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.isDockable(node) && !DockRegistry.isDockTarget(node)) {
                TreeItem item = PreferencesBuilder.build(key, node, true);
                getDefaultDockables().put(key, item);
            }
        }
        //
        // 1. Set Dockloader instance to all explicitly registered DockTargets
        // 2. As a result of build all object will be implicitly registered
        //
        for (String key : getExplicitlyRegistered().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.isDockTarget(node)) {
                DockRegistry.dockTarget(node).targetController().setDockLoader(this);

                DockRegistry.dockTarget(node).targetController()
                        .getPreferencesBuilder()
                        .build(key);
                /*                TreeItemStringConverter tc = new TreeItemStringConverter();
                System.err.println("--- 1111111111111111111111111");
                System.err.println(tc.toString(getDefaultDockTargets().get(key)));
                System.err.println("--- END 1111111111111111111111111");                
                 */
            }
        }

        getDefaultDockTargets().forEach((k, v) -> {
            getDockTargets().put((Node) v.getValue().getKey().get(), k);
        });

        /*        for (String key : getDefaultDockables().keySet()) {
            TreeItemStringConverter tc = new TreeItemStringConverter();
            System.err.println("1111 ==================");
            System.err.println(tc.toString(getDefaultDockables().get(key)));
            System.err.println("1111 ==================");
        }
         */
        PrefProperties dockTargetProps = cp.next(DEFAULT).getProperties(DOCKTARGETS);
        PrefProperties dockableProps = cp.next(DEFAULT).getProperties(DOCKABLES);
        Map<String, String> docktargetMap = FXCollections.observableHashMap();
        Map<String, String> dockableMap = FXCollections.observableHashMap();

        TreeItemStringConverter converter = new TreeItemStringConverter();

        for (String key : getDefaultDockTargets().keySet()) {
            docktargetMap.put(key, converter.toString(getDefaultDockTargets().get(key)));
        }
        for (String key : getDefaultDockables().keySet()) {
            dockableMap.put(key, converter.toString(getDefaultDockables().get(key)));
        }
        System.err.println("BEFORE isDestroyed TIME !!!!!! " + (System.currentTimeMillis() - start));
        if (isDestroyed(docktargetMap, dockableMap, dockTargetProps, dockableProps)) {
            reset();
            System.err.println("AFTER RESET TIME !!!!!! " + (System.currentTimeMillis() - start));
            if (dockTargetProps == null) {
                dockTargetProps = cp.next(DEFAULT).createProperties(DOCKTARGETS);
            }
            for (String key : getDefaultDockTargets().keySet()) {
                dockTargetProps.setProperty(key, docktargetMap.get(key));
            }
            for (String key : getExplicitlyRegistered().keySet()) {
                Node node = getExplicitlyRegistered().get(key);
                if (DockRegistry.isDockTarget(node)) {
                    dockTargetProps.setProperty(key, docktargetMap.get(key));
                }
            }

            dockTargetProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            System.err.println("AFTER DOCKTARGET TIME !!!!!! " + (System.currentTimeMillis() - start));

            if (!dockableMap.isEmpty()) {
                if (dockableProps == null) {
                    dockableProps = cp.next(DEFAULT).createProperties(DOCKABLES);
                }
                for (String key : getDefaultDockables().keySet()) {
                    dockableProps.setProperty(key, dockableMap.get(key));
                }
            }
            dockableProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            System.err.println("AFTER DOCKABLES TIME !!!!!! " + (System.currentTimeMillis() - start));

        }
        getDefaultDockables().forEach((k, v) -> {
            getRegistered().put(v.getValue().getKey().get(), k);

        });
        /*        getRegistered().forEach((o, s) -> {
            System.err.println("Obj=" + o + "; field=" + s);
        });
         */
 /*        dockTargetProps.forEach((k, v) -> {
            System.err.println("dockTarget = " + k);
            System.err.println("-----------------------------");
            System.err.println(v);
            System.err.println("-----------------------------");

        });
        System.err.println("=========================================");
        dockableProps.forEach((k, v) -> {
            System.err.println("dockable = " + k);
            System.err.println("-----------------------------");
            System.err.println(v);
            System.err.println("-----------------------------");

        });
         */
        Long end = System.currentTimeMillis();
        System.err.println("!!!!!!!!! TIME !!!!!! " + (end - start));
        setLoaded(true);
        //
        // Debug: Try save
        //
/*        for (String key : getExplicitlyRegistered().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.isDockTarget(node)) {
                DockRegistry.dockTarget(node).targetController().setDockLoader(this);
                DockRegistry.dockTarget(node).targetController()
                        .getPreferencesBuilder()
                        .build(key);
                TreeItemStringConverter tc = new TreeItemStringConverter();
                System.err.println("*********************************");
                System.err.println(tc.toString(getSaveDockTargets().get(key)));

            }
        }
         */
        for (String key : getExplicitlyRegistered().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.isDockTarget(node)) {
                restore(DockRegistry.dockTarget(node));
            }
        }
        end = System.currentTimeMillis();
        System.err.println("LAST !!!!!!!!! TIME !!!!!! " + (end - start));
    }

    protected boolean isDestroyed(Map<String, String> docktargetMap, Map<String, String> dockableMap, PrefProperties docktargetProps, PrefProperties dockableProps) {
        boolean retval = false;
        if (docktargetProps == null || (dockableProps == null && !dockableMap.isEmpty())) {
            retval = true;
        }
        if (!retval) {
            if (docktargetProps.size() != docktargetMap.size() + 1 || dockableProps.size() != dockableMap.size() + 1) {
                retval = true;
            }
        }

        if (!retval) {
            for (String key : docktargetMap.keySet()) {
                if (!docktargetMap.get(key).equals(docktargetProps.getProperty(key))) {
                    retval = true;
                    break;
                }
            }
        }
        if (!retval) {
            for (String key : dockableMap.keySet()) {
                if (!dockableMap.get(key).equals(dockableProps.getProperty(key))) {
                    retval = true;
                    break;
                }
            }
        }

        return retval;
    }

/*    @Override
    protected void saveStore() {
        DockPreferences cp = new DockPreferences(getPreferencesRoot());
        DockPreferences registered = cp.next(REGISTRY_STORE_ENTRIES);

        PrefProperties registeredProps = registered.getProperties(PROPERTIES);

        if (registeredProps == null || registeredProps.keys().length == 0) {
            PrefProperties ip = registered.createProperties(PROPERTIES);
            getExplicitlyRegistered().forEach((k, v) -> {
                ip.setProperty(k, v.getClass().getName());
            });
            registered.next("NULL");
        }
    }
*/
/*    protected void save(DockTarget dockTarget, TreeItem<Pair<ObjectProperty, Properties>> root) {
        PreferencesBuilder builder = dockTarget.targetController().getPreferencesBuilder();
        DockPreferences cp = new DockPreferences(getPreferencesRoot());

        Pair<ObjectProperty, Properties> pair = root.getValue();
        String entryName = getEntryName(pair.getKey().get());
        cp.next(entryName).removelChildren();
        //PrefProperties ip1 = cp.next(entryName).next(PROPERTIES).getProperties(INFO);
        DockPreferences cpProps = cp.next(entryName).next(PROPERTIES);
        PrefProperties ip = cpProps.createProperties(INFO);
        ip.setProperty(FIELD_NAME_ATTR, entryName);
        ip.setProperty(CLASS_NAME_ATTR, pair.getKey().get().getClass().getName());
        final Map<String, String> nodeProps = builder.getProperties(pair.getKey().get());
        if (nodeProps != null && !nodeProps.isEmpty()) {
            PrefProperties nodeIp = cpProps.createProperties(NODE);
            nodeProps.keySet().forEach(k -> {
                nodeIp.setProperty(k, nodeProps.get(k));
            });
        }
        for (int i = 0; i < root.getChildren().size(); i++) {
            TreeItem<Pair<ObjectProperty, Properties>> it = root.getChildren().get(i);
            save(builder, it, entryName + "/" + String.valueOf(i));
        }

    }
*/
    @Override
    public void save() {
        getExplicitlyRegistered().forEach((k, v) -> {
            if (DockRegistry.isDockTarget(v)) {
                save(DockRegistry.dockTarget(v));
            }
        });
    }

    @Override
    public void save(DockTarget dockTarget) {
        long start = System.currentTimeMillis();
        String fieldName = getDockTargets().get(dockTarget.target());
        TreeItem<Pair<ObjectProperty, Properties>> it = dockTarget.targetController()
                .getPreferencesBuilder()
                .build(fieldName);
        TreeItemStringConverter tc = new TreeItemStringConverter();
        String convertedTarget = tc.toString(getSaveDockTargets().get(fieldName));
        DockPreferences cp = new DockPreferences(getPreferencesRoot()).next(SAVE);
        cp.getProperties(DOCKTARGETS).setProperty(fieldName, convertedTarget);

        /*        System.err.println(" --- SAVE TIME = " + (System.currentTimeMillis() - start) );
        TreeItem ti = tc.fromString(convertedTarget);
        System.err.println("******* 3333333333 *************");
        System.err.println(tc.toString(ti));
        System.err.println("******* 444444444 *************");
         */
 /*        convertedTarget = cp.getProperties(DOCKTARGETS).getProperty(fieldName);
        System.err.println("2 *********************************");
        System.err.println(convertedTarget);
         */
//            }
//        }
        /*        PreferencesBuilder builder = dockTarget.targetController().getPreferencesBuilder();
        TreeItem<Pair<ObjectProperty,Properties>> root = builder.build();
        DockPreferences cp = new DockPreferences(getPreferencesRoot()).next(getEntryName(dockTarget));
        cp.removelChildren();
        String ename = getEntryName(dockTarget);
        long t1 = System.currentTimeMillis();
        save(dockTarget, root);
        long t2 = System.currentTimeMillis();
        System.err.println("Entry " + ename + "; time=" + (t2-t1));
         */
    }


/*    protected void save(PreferencesBuilder builder, TreeItem<Pair<ObjectProperty, Properties>> item, String namespace) {
        DockPreferences cp = new DockPreferences(getPreferencesRoot()).next(namespace);
        DockPreferences cpProps = cp.next(PROPERTIES);
        Pair<ObjectProperty, Properties> pair = item.getValue();
        String entryName = getEntryName(pair.getKey().get());
        PrefProperties ip = cpProps.createProperties(INFO);
        if (entryName == null) {
            ip.setProperty(FIELD_NAME_ATTR, NOT_REGISTERED);
        } else {
            ip.setProperty(FIELD_NAME_ATTR, entryName);
        }
        ip.setProperty(CLASS_NAME_ATTR, pair.getKey().get().getClass().getName());
        final Map<String, String> nodeProps = builder.getProperties(pair.getKey().get());
        if (nodeProps != null && !nodeProps.isEmpty()) {
            PrefProperties nodeIp = cpProps.createProperties(NODE);
            nodeProps.keySet().forEach(k -> {
                nodeIp.setProperty(k, nodeProps.get(k));
            });
        }
        for (int i = 0; i < item.getChildren().size(); i++) {
            TreeItem<Pair<ObjectProperty, Properties>> it = item.getChildren().get(i);
            save(builder, it, namespace + "/" + String.valueOf(i));
        }
    }
*/
    @Override
    public TreeItem<Pair<ObjectProperty, Properties>> restore(DockTarget dockTarget) {
        //return restore("", getEntryName(dockTarget.target()));
        String fieldName = getDockTargets().get(dockTarget.target());
        DockPreferences cp = new DockPreferences(getPreferencesRoot());
        PrefProperties prefProps = cp.next(SAVE).getProperties(DOCKTARGETS);
        String strItem = prefProps.getProperty(fieldName);
        if (strItem == null) {
            return null;
        }
        TreeItemStringConverter tc = new TreeItemStringConverter();
        TreeItem<Pair<ObjectProperty, Properties>> item = tc.fromString(strItem);
        //
        // Assign a registered (explicitly or implicitly) value for the items
        //
        TreeView tv = new TreeView();
        tv.setRoot(item);
        Map<String, Object> names = new HashMap<>();
        getRegistered().forEach((k, v) -> {
            names.put(v, k);
        });
        System.err.println("&&&&&&&& tv.getExpandedItemCount() = " + tv.getExpandedItemCount());
        for (int i = 0; i < tv.getExpandedItemCount(); i++) {
            TreeItem<Pair<ObjectProperty, Properties>> it = tv.getTreeItem(i);
            if (isRegistered(it)) {
                fieldName = it.getValue().getValue().getProperty(FIELD_NAME_ATTR);
                it.getValue().getKey().set(names.get(fieldName));
                if (names.get(fieldName) == null) {
                    System.err.println("NULLLLLLL = " + fieldName);
                }
            }
        }

        dockTarget.targetController().getPreferencesBuilder().restore(item);

        return item;
    }

/*    protected TreeItem<Pair<ObjectProperty, Properties>> restore(String namespace, String entryName) {
        //PreferencesItem pair = new PreferencesItem();
        TreeItem<Pair<ObjectProperty, Properties>> treeItem = new TreeItem<>();
        Pair<ObjectProperty, Properties> pair;
        DockPreferences cp = new DockPreferences(getPreferencesRoot());
        PrefProperties infoPrefs = cp.next(namespace).next(entryName).next(PROPERTIES).getProperties(INFO);
        String storeEntry = infoPrefs.getProperty(FIELD_NAME_ATTR);
        //
        // We store into PreferencesItem an actual object if registered or a 
        // class name of the unregistered object
        //
        if (storeEntry == null || NOT_REGISTERED.equals(storeEntry)) {
            pair = new Pair(new SimpleObjectProperty(null), new Properties());
            pair.getValue().put(IGNORE_ATTR, treeItem);
            pair.getValue().put(CLASS_NAME_ATTR, infoPrefs.getProperty(CLASS_NAME_ATTR));
        } else {
            pair = new Pair(new SimpleObjectProperty(getExplicitlyRegistered().get(storeEntry)), new Properties());
            pair.getValue().put(IGNORE_ATTR, treeItem);
            pair.getValue().put(CLASS_NAME_ATTR, getExplicitlyRegistered().get(storeEntry).getClass().getName());
            pair.getKey().set(getExplicitlyRegistered().get(storeEntry));
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
*/
/*    public void reset(DockPreferences cp) {
        //cp.getProperties(INFO).
        cp.removelChildren(name -> {
            return !"properties".equals(name);
        });
    }
*/
    @Override
    public void reset() {
        DockPreferences cp = new DockPreferences(getPreferencesRoot());
        PrefProperties prefProps = cp.next(DEFAULT).getProperties(DOCKTARGETS);
        if (prefProps != null) {
            int sz = prefProps.size();
            String p = prefProps.getProperty(DUMMY_KEY);
            prefProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            for (String key : prefProps.keys()) {
                if (!DUMMY_KEY.equals(key)) {
                    prefProps.removeKey(key);
                }
            }
            //prefProps.clear();
        }
        prefProps = cp.next(DEFAULT).getProperties(DOCKABLES);
        if (prefProps != null) {
            //prefProps.clear();
            prefProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            for (String key : prefProps.keys()) {
                if (!DUMMY_KEY.equals(key)) {
                    prefProps.removeKey(key);
                }
            }

        }

        prefProps = cp.next(SAVE).getProperties(DOCKTARGETS);
        if (prefProps != null) {
//            prefProps.clear();
            prefProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            for (String key : prefProps.keys()) {
                if (!DUMMY_KEY.equals(key)) {
                    prefProps.removeKey(key);
                }
            }
        }

        System.err.println("!!!!!!!!!!!!!!!  " + cp.next("default").getProperties("descriptor").getProperty("title"));

        //reset(cp);
        //new DockPreferences(getPreferencesRoot()).clearRoot();
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
