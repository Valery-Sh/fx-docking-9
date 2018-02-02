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
package org.vns.javafx.dock.api.save;

import java.util.Properties;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.util.TreeItemStringConverter;
import org.vns.javafx.dock.api.util.prefs.DockPreferences;
import org.vns.javafx.dock.api.util.prefs.PrefProperties;
import static org.vns.javafx.dock.api.save.DockTreeItemBuilder.*;

/**
 *
 * @author Valery Shyshkin
 */
public class DockStateLoader extends AbstractDockStateLoader {

    public static String DUMMY_KEY = "DUMMY_123_DUMMY_321";
    public static String DEFAULT = "default";
    public static String DESCRIPTOR = "descriptor";

    protected DockStateLoader(String preferencesRoot) {
        super(preferencesRoot);
    }

    public DockStateLoader(Class clazz) {
        super(clazz);
    }

    /**
     * The method checks the initial (default) state of objects to detect
     * inconsistencies between the registered objects and their state, which was
     * saved during the previous execution of the application. If
     * inconsistencies are found, the stored state is considered invalid and the
     * new default state is saved the registration of new objects is not
     * allowed.
     */
    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }

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
        // Create properies for a dock targets and set a DUMMY property.
        // This allows to escape an excess creation of preferences entries 
        //
        prefProps = cp.next(SAVE).getProperties(DOCKTARGETS);
        if (prefProps == null) {
            prefProps = cp.next(SAVE).createProperties(DOCKTARGETS);
        }
        prefProps.setProperty(DUMMY_KEY, DUMMY_KEY);

        //reset();
        //
        // Copies all Dockable objects which are not DockTarget.
        //
        for (String key : getExplicitlyRegistered().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.instanceOfDockable(node) && !DockRegistry.instanceOfDockTarget(node)) {
                TreeItem item = DockTreeItemBuilder.build(key, node);
                getDefaultDockables().put(key, item);
            }
            getRegistered().put(key, node);
        }
        //
        // 1. As a result all objects will be implicitly registered
        // 2. The method getDefaultDockables now contains only those dockable
        //    objects which are not childs of some DockTarget
        //
        TargetContext context = DockRegistry.dockTarget(getExplicitlyRegistered().get("dockTabPane1")).getTargetContext();
        getDockTreeTemBuilder(context.getTargetNode());
        TreeItem<Properties> it1 = getDockTreeTemBuilder(context.getTargetNode()).build("dockTabPane1");

        test(it1);

        for (String key : getExplicitlyRegistered().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (DockRegistry.instanceOfDockTarget(node)) {
                TreeItem<Properties> it = build(key, node);
                if ("dockTabPane1".equals(key)) {
                    test(it);
                }
            }
        }

        boolean needSave = false;

        if (isDestroyed()) {

            resetPreferences();

            PrefProperties docktargetProps = cp.next(DEFAULT).getProperties(DOCKTARGETS);
            PrefProperties dockableProps = cp.next(DEFAULT).getProperties(DOCKABLES);
            PrefProperties docktargetSaveProps = cp.next(SAVE).getProperties(DOCKTARGETS);

            TreeItemStringConverter converter = new TreeItemStringConverter();

            if (docktargetProps == null) {
                docktargetProps = cp.next(DEFAULT).createProperties(DOCKTARGETS);
            }
            for (String key : getDefaultDockTargets().keySet()) {
                String converted = converter.toString(getDefaultDockTargets().get(key));
                docktargetProps.setProperty(key, converted);
                docktargetSaveProps.setProperty(key, converted);
            }

            docktargetProps.setProperty(DUMMY_KEY, DUMMY_KEY);

            if (!getDefaultDockables().isEmpty()) {
                if (dockableProps == null) {
                    dockableProps = cp.next(DEFAULT).createProperties(DOCKABLES);
                }
                for (String key : getDefaultDockables().keySet()) {
                    dockableProps.setProperty(key, converter.toString(getDefaultDockables().get(key)));
                }
            }
            dockableProps.setProperty(DUMMY_KEY, DUMMY_KEY);

            needSave = true;
        }

        Long end = System.currentTimeMillis();

        setLoaded(true);

        restore();
    }

    /**
     * @return {@code true} if the {@link #load() } method detected an
     * inconsistency between the early saved state and newly registered object's
     * state. {@code false } otherwise.
     */
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

    /**
     * Restores the previously saved state of the specified node.
     *
     * @param dockTarget the object of type {@link org.vns.javafx.dock.api.DockTarget
     * } whose state is to be restored.
     * @return the object of type {@code javafx.scene.control.TreeItem } which
     * is the root of TreeItem's tree which corresponds to the {@code Scene Graph
     * }
     * of the node specified by the parameter {@code dockTarget}.
     */
    @Override
    public TreeItem<Properties> restore(DockTarget dockTarget) {
        String fieldName = getFieldName(dockTarget.target());

        if (fieldName == null) {
            return null;
        }
        DockPreferences cp = new DockPreferences(getPreferencesRoot());
        PrefProperties prefProps = cp.next(SAVE).getProperties(DOCKTARGETS);
        String strItem = prefProps.getProperty(fieldName);
        if (strItem == null) {
            return null;
        }
/*        System.err.println("retore strItem:");
        System.err.println("---------------");
        System.err.println(strItem);
        System.err.println("---------------");
*/        
        TreeItemStringConverter tc = new TreeItemStringConverter();
        TreeItem<Properties> item = tc.fromString(strItem);

/*        System.err.println("retore AFTER RESTORE :");
        System.err.println("---------------");
        System.err.println(tc.toString(item));
        System.err.println("---------------");
*/
        //
        // Assign a registered (explicitly or implicitly) value for the items
        //
        TreeView tv = new TreeView();
        tv.setRoot(item);

        for (int i = 0; i < tv.getExpandedItemCount(); i++) {
            TreeItem<Properties> it = tv.getTreeItem(i);
            it.setExpanded(true);
            fieldName = it.getValue().getProperty(FIELD_NAME_ATTR);
            if (getRegistered().get(fieldName) != null) {
                it.getValue().put(OBJECT_ATTR, getRegistered().get(fieldName));
            }
        }
        getDockTreeTemBuilder(dockTarget.getTargetContext().getTargetNode()).restore(item);
        return item;
    }

    /**
     * The method is used to set the state of registered objects to the default
     * state. The method can be applied at any time during the execution of the
     * application. If used when the {@code isLoaded()} method returns
     * {@code false}, then the method should not do anything.
     */
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

        if (!getDefaultDockables().isEmpty()) {
            if (dockableProps == null) {
                dockableProps = cp.next(DEFAULT).createProperties(DOCKABLES);
            }
            for (String key : getDefaultDockables().keySet()) {
                dockableProps.setProperty(key, converter.toString(getDefaultDockables().get(key)));
            }
        }
        dockableProps.setProperty(DUMMY_KEY, DUMMY_KEY);

        getDefaultDockTargets().forEach((k, v) -> {
            Node node = (Node) v.getValue().get(OBJECT_ATTR);
            TreeItemStringConverter tc = new TreeItemStringConverter();
            String convertedTarget = tc.toString(v);
            cp.next(SAVE).getProperties(DOCKTARGETS).setProperty(k, convertedTarget);
        });
        restore();

        Platform.runLater(() -> {
            restore();
        });

    }

    /**
     * Clears preferences nodes so that there no properties exist in preferences
     * store.
     */
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

        if (prefProps != null) {
            prefProps.setProperty(DUMMY_KEY, DUMMY_KEY);
            for (String key : prefProps.keys()) {
                if (!DUMMY_KEY.equals(key)) {
                    prefProps.removeKey(key);
                }
            }
        }
    }

}
