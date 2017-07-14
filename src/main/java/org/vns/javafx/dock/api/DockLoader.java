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

import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Pair;
import org.vns.common.xml.javafx.TreeItemStringConverter;
import org.vns.javafx.dock.DockPane;
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

        PrefProperties prefProps = cp.next(DEFAULT).getProperties(DESCRIPTOR);
        if (prefProps == null) {
            prefProps = cp.next(DEFAULT).createProperties(DESCRIPTOR);
            prefProps.setProperty("title", "descriptor");
        }

        prefProps = cp.next(SAVE).getProperties(DESCRIPTOR);
        if (prefProps == null) {
            prefProps = cp.next(SAVE).createProperties(DESCRIPTOR);
            prefProps.setProperty("title", "descriptor");
        }
        //
        // Create properies for a dock target and set a DUMMY property.
        // This allows to escape an excess creation of preferences entries 
        //
        prefProps = cp.next(SAVE).getProperties(DOCKTARGETS);
        if (prefProps == null) {
            prefProps = cp.next(SAVE).createProperties(DOCKTARGETS);
        }
        prefProps.setProperty(DUMMY_KEY, DUMMY_KEY);

        reset();
        
        //
        // Copies all Dockable objects which are not DockTarget.
        //
        for (String key : getExplicitlyRegistered().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.isDockable(node) && !DockRegistry.isDockTarget(node)) {
                TreeItem item = PreferencesBuilder.build(key, node, true);
                getDefaultDockables().put(key, item);
            }
        }
        //
        // Copies all DockTargets objects 
        //
        for (String key : getExplicitlyRegistered().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.isDockTarget(node)) {
                getFreeDockTargets().put(key, node);
            }
        }

        //
        // 1. Set Dockloader instance to all explicitly registered DockTargets
        // 2. As a result all objects will be implicitly registered
        // 3. The method getDefaultDockables now contains only those dockable
        //    objects which are not childs of some DockTarget
        //
        for (String key : getExplicitlyRegistered().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.isDockTarget(node)) {
                DockRegistry.dockTarget(node).targetController().setDockLoader(this);

                DockRegistry.dockTarget(node).targetController()
                        .getPreferencesBuilder()
                        .build(key);
            }
        }
        //
        // Modify defaltDockTarget map. The result map contains only
        // DockTargets which are not childs of any other DockTarget
        //
        getAllDockTargets().forEach((k, v) -> {
            if (getFreeDockTargets().containsKey(k)) {
                getDefaultDockTargets().put(k, v);
            }
        });
        TreeItemStringConverter tc = new TreeItemStringConverter();
        System.err.println(tc.toString(getDefaultDockTargets().get("dockPane1")));
        System.err.println("BEFORE isDestroyed TIME !!!!!! " + (System.currentTimeMillis() - start));

        if ( isDestroyed() ) {
            
            resetPreferences();

            PrefProperties docktargetProps = cp.next(DEFAULT).getProperties(DOCKTARGETS);
            PrefProperties dockableProps = cp.next(DEFAULT).getProperties(DOCKABLES);

            TreeItemStringConverter converter = new TreeItemStringConverter();

            System.err.println("isDestroyed AFTER RESET TIME !!!!!! " + (System.currentTimeMillis() - start));
            if (docktargetProps == null) {
                System.err.println("isDestroyed CREATE dockTargets");
                docktargetProps = cp.next(DEFAULT).createProperties(DOCKTARGETS);
            }
            for (String key : getDefaultDockTargets().keySet()) {
                System.err.println("isDestroyed CREATE dockables");
                docktargetProps.setProperty(key, converter.toString(getDefaultDockTargets().get(key)));
            }
            docktargetProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            
            System.err.println("isDestroyed AFTER DOCKTARGET TIME !!!!!! " + (System.currentTimeMillis() - start));

            if (!getDefaultDockables().isEmpty()) {
                if (dockableProps == null) {
                    dockableProps = cp.next(DEFAULT).createProperties(DOCKABLES);
                }
                for (String key : getDefaultDockables().keySet()) {
                    dockableProps.setProperty(key, converter.toString(getDefaultDockables().get(key)));
                }
            }
            dockableProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            
            save(true);
            
            System.err.println("isDestroyed AFTER DOCKABLES TIME !!!!!! " + (System.currentTimeMillis() - start));

        }

        Long end = System.currentTimeMillis();
        System.err.println("!!!!!!!!! TIME !!!!!! " + (end - start));

        setLoaded(true);

        restore();
        //Map m = getSaveDockTargets();
        //System.err.println("========= dockPane1 ====================");
        //System.err.println(toString("dockPane1"));
        
        System.err.println("LAST !!!!!!!!! TIME !!!!!! " + (System.currentTimeMillis() - start));
    }

    @Override
    protected boolean isDestroyed() {
        boolean retval = false;
        
        DockPreferences cp = new DockPreferences(getPreferencesRoot());
        PrefProperties docktargetProps = cp.next(DEFAULT).getProperties(DOCKTARGETS);
        PrefProperties dockableProps = cp.next(DEFAULT).getProperties(DOCKABLES);

        TreeItemStringConverter converter = new TreeItemStringConverter();
        
        if (docktargetProps == null || (dockableProps == null && !getDefaultDockables().isEmpty())) {
            retval = true;
        }
        if (!retval) {
            docktargetProps.forEach((k,v) ->{
                String s = k;
                String s1 = v;
                s1 = "";
            } );
            if (docktargetProps.size() != getDefaultDockTargets().size() + 1 || dockableProps.size() != getDefaultDockables().size() + 1) {
                retval = true;
            }
        }

        if (!retval) {
            for (String key : getDefaultDockTargets().keySet()) {
                String s = converter.toString(getDefaultDockTargets().get(key));                
                if (!s.equals(docktargetProps.getProperty(key))) {
                    retval = true;
                    break;
                }
            }
        }
        if (!retval) {
            for (String key : getDefaultDockables().keySet()) {
                String s = converter.toString(getDefaultDockables().get(key));                
                if (!s.equals(dockableProps.getProperty(key))) {
                    retval = true;
                    break;
                }
            }
        }
        return retval;
    }

    @Override
    public void save() {
        //resetPreferences();
        save(isLoaded());
    }

    @Override
    public void save(DockTarget dockTarget) {
        save(dockTarget, isLoaded());
    }

    protected void save(boolean loaded) {
        if ( loaded ) {
            getSaveDockTargets().clear();
        } else {
            
        }
        getDefaultDockTargets().forEach((k, v) -> {
            Node node = (Node) v.getValue().getKey().get();
            if (DockRegistry.isDockTarget(node)) {
                save(DockRegistry.dockTarget(node),loaded);
            }
        });
    }

    protected void save(DockTarget dockTarget, boolean loaded) {
        
        String fieldName = getDockTargets().get(dockTarget.target());
        if (fieldName == null) {
            return;
        }
        System.err.println("SAVE field=" + fieldName);
        boolean saveLoaded = isLoaded();
        setLoaded(loaded);
        TreeItem<Pair<ObjectProperty, Properties>> it = dockTarget.targetController()
                .getPreferencesBuilder()
                .build(fieldName);
        setLoaded(saveLoaded);
        TreeItemStringConverter tc = new TreeItemStringConverter();
        String convertedTarget;
        if ( loaded ) {
            convertedTarget = tc.toString(getSaveDockTargets().get(fieldName));
        } else {
            convertedTarget = tc.toString(getDefaultDockTargets().get(fieldName));
        }
        DockPreferences cp = new DockPreferences(getPreferencesRoot()).next(SAVE);
        cp.getProperties(DOCKTARGETS).setProperty(fieldName, convertedTarget);

    }
    
    
    @Override
    public TreeItem<Pair<ObjectProperty, Properties>> restore(DockTarget dockTarget) {
        String fieldName = getDockTargets().get(dockTarget.target());
        DockPane dp = (DockPane) dockTarget.target();
        System.err.println("111 dp " + dp.getItems().get(0));
        if (fieldName == null) {
            return null;
        }
        DockPreferences cp = new DockPreferences(getPreferencesRoot());
        PrefProperties prefProps = cp.next(SAVE).getProperties(DOCKTARGETS);
        String strItem = prefProps.getProperty(fieldName);
        if (strItem == null) {
            return null;
        }
        System.err.println("0 RESTORE:");
        System.err.println(strItem);
        
        TreeItemStringConverter tc = new TreeItemStringConverter();
        TreeItem<Pair<ObjectProperty, Properties>> item = tc.fromString(strItem);
        //
        // Assign a registered (explicitly or implicitly) value for the items
        //
        TreeView tv = new TreeView();
        tv.setRoot(item);

        for (int i = 0; i < tv.getExpandedItemCount(); i++) {
            TreeItem<Pair<ObjectProperty, Properties>> it = tv.getTreeItem(i);
            if (isRegistered(it)) {
                String s = it.getValue().getValue().getProperty("id");
                s = it.getValue().getValue().getProperty(CLASS_NAME_ATTR);
                fieldName = it.getValue().getValue().getProperty(FIELD_NAME_ATTR);
                System.err.println("DocLoader restore = " + fieldName);
                it.getValue().getKey().set(getAllRegistered().get(fieldName));
            }
        }
        System.err.println("1 RESTORE:");
        System.err.println("222 dp " + dp.getItems().get(0));
        System.err.println(tc.toString(item));
        
        dockTarget.targetController().getPreferencesBuilder().restore(item);
        System.err.println("2 RESTORE:");
        System.err.println("333 dp " + dp.getItems().get(0));
        System.err.println(tc.toString(item));
        
        return item;
    }
    
    @Override
    public void reset() {
        
            long start = System.currentTimeMillis();
            
            resetPreferences();
            
            DockPreferences cp = new DockPreferences(getPreferencesRoot());

            PrefProperties docktargetProps = cp.next(DEFAULT).getProperties(DOCKTARGETS);
            PrefProperties dockableProps = cp.next(DEFAULT).getProperties(DOCKABLES);

            TreeItemStringConverter converter = new TreeItemStringConverter();

            if (docktargetProps == null) {
                docktargetProps = cp.next(DEFAULT).createProperties(DOCKTARGETS);
            }
            for (String key : getDefaultDockTargets().keySet()) {
                docktargetProps.setProperty(key, converter.toString(getDefaultDockTargets().get(key)));
            }

            docktargetProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            System.err.println("reset AFTER DOCKTARGET TIME !!!!!! " + (System.currentTimeMillis() - start));
            
            if (!getDefaultDockables().isEmpty()) {
                if (dockableProps == null) {
                    dockableProps = cp.next(DEFAULT).createProperties(DOCKABLES);
                }
                for (String key : getDefaultDockables().keySet()) {
                    dockableProps.setProperty(key, converter.toString(getDefaultDockables().get(key)));
                }
            }
            dockableProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            
            getSaveDockTargets().clear();
            save(false);
            restore();
            System.err.println("AFTER reset TIME !!!!!! " + (System.currentTimeMillis() - start));

        
    }
    
    @Override
    protected void resetPreferences() {
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
        }
        prefProps = cp.next(DEFAULT).getProperties(DOCKABLES);
        if (prefProps != null) {
            prefProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            for (String key : prefProps.keys()) {
                if (!DUMMY_KEY.equals(key)) {
                    prefProps.removeKey(key);
                }
            }
        }

        prefProps = cp.next(SAVE).getProperties(DOCKTARGETS);
        System.err.println("*************** prefProps = " + prefProps);
        if (prefProps != null) {
            prefProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            for (String key : prefProps.keys()) {
                if (!DUMMY_KEY.equals(key)) {
                    prefProps.removeKey(key);
                }
            }
        }

    }
    public String toString(DockTarget dt) {
        String fieldName = getDockTargets().get(dt.target());
        return new TreeItemStringConverter().toString(getSaveDockTargets().get(fieldName));
    }
    public String toString(String fieldName) {
        return new TreeItemStringConverter().toString(getSaveDockTargets().get(fieldName));
    }
    
}
