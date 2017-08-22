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

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
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
import javafx.scene.control.TreeView;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TargetContext;

import static org.vns.javafx.dock.api.save.DockTreeItemBuilder.*;
import org.vns.javafx.dock.api.util.TreeItemStringConverter;
import org.vns.javafx.dock.api.util.prefs.DockPreferences;
import org.vns.javafx.dock.api.util.prefs.PrefProperties;

/**
 * The base implementation of the interface 
 * {@link org.vns.javafx.dock.api.save.StateLoader }. The class uses both {@code java.util.prefs.Preferences
 * } and {@code xml }
 * technology to save/restore state of objects of type 
 * {@link org.vns.javafx.dock.api.DockTarget } and 
 * {@link org.vns.javafx.dock.api.Dockable }.
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractDockStateLoader implements StateLoader {

    public static String DOCKTARGETS = "docktargets";
    public static String DOCKABLES = "dockables";
    public static String SAVE = "save";

    private final List<Node> stateChangedList = FXCollections.observableArrayList();

    private final Map<String, Node> explicitlyRegistered = FXCollections.observableHashMap();
    private final Map<String, Object> registered = FXCollections.observableHashMap();

    private final Map<String, TreeItem<Properties>> defaultDockTargets = FXCollections.observableHashMap();
    private final Map<String, TreeItem<Properties>> allDockTargets = FXCollections.observableHashMap();
    private final Map<String, TreeItem<Properties>> defaultDockables = FXCollections.observableHashMap();

    private final List<Node> saved = FXCollections.observableArrayList();
    private final String preferencesRoot;

    private boolean saveOnClose;

    private boolean loaded = false;

    /**
     * Creates a new instance of the class for the specified {@code prefEntry}.
     * The parameter must adhere to the rules for node's name path as specified
     * by the class {@code java.util.Preferences}. All back slashes will be
     * replaced with forward slashes.
     *
     * @param prefEntry the preferences node's relative path
     */
    protected AbstractDockStateLoader(String prefEntry) {
        this.preferencesRoot = prefEntry.replace("\\", "/");
    }

    /**
     * Creates a new instance of the class for the specified class used to
     * create the root of preferences root node. The root node path is
     * constructed by replacing all dots with the forward slash symbol in the
     * fully qualified class name.
     *
     * @param clazz used to create a preferences root node path.
     */
    protected AbstractDockStateLoader(Class clazz) {
        preferencesRoot = clazz.getName().replace(".", "/");
    }

    /**
     * @return {@code true} if the {@link #load() } method detected an
     * inconsistency between the early saved state and newly registered object's
     * state. {@code false } otherwise.
     */
    protected abstract boolean isDestroyed();

    /**
     * Clears preferences nodes so that there no properties exist in preferences
     * store.
     */
    protected abstract void resetPreferences();

    /**
     * Applying the method saves the current state of the registered objects.
     * Running a method for execution can be performed by the application when
     * certain user actions are performed, for example, clicking a button or
     * executing a menu item or closing the main window.
     */
    @Override
    public void save() {
        long start = System.currentTimeMillis();
        saved.clear();
        getDefaultDockTargets().forEach((k, v) -> {
            Node node = (Node) v.getValue().get(OBJECT_ATTR);
            //if (node != null && !saved.contains(node) && DockRegistry.instanceOfDockTarget(node)) {
            if (node != null && DockRegistry.instanceOfDockTarget(node)) {
                save(DockRegistry.dockTarget(node));
                //TreeItemStringConverter tc = new TreeItemStringConverter();
                DockPreferences cp = new DockPreferences(getPreferencesRoot());
                PrefProperties prefProps = cp.next(SAVE).getProperties(DOCKTARGETS);

                System.err.println("======  SAVE ===============");
                System.err.println(prefProps.getProperty(k));
                System.err.println("=================================================");

            }
        });
        System.err.println("Save all.  Time interval = " + (System.currentTimeMillis() - start));

    }

    /**
     * Saves the current state of the specified object.
     *
     * @param dockTarget the object of type 
     * {@link org.vns.javafx.dock.api.DockTarget } whose state is to be saved
     */
    protected void save(DockTarget dockTarget) {
        //save(dockTarget, true);
        long start = System.currentTimeMillis();

        String fieldName = getFieldName(dockTarget.target());

        if (fieldName == null) {
            return;
        }

        TreeItem<Properties> it = builder(dockTarget.target()).build(fieldName);
        System.err.println(fieldName + " TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        test(it);
        System.err.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        completeBuild(it, true);
        test(it);
        TreeItemStringConverter tc = new TreeItemStringConverter();
        String convertedTarget = tc.toString(it);
        DockPreferences cp = new DockPreferences(getPreferencesRoot()).next(SAVE);
        cp.getProperties(DOCKTARGETS).setProperty(fieldName, convertedTarget);
        System.err.println("Single Save time interval = " + (System.currentTimeMillis() - start));

    }

    protected void test(TreeItem<Properties> it) {
        TreeView<Properties> tv = new TreeView();
        tv.setRoot(it);
        for (int i = 0; i < tv.getExpandedItemCount(); i++) {
            System.err.println(tv.getTreeItem(i).getValue().getProperty(TAG_NAME_ATTR));
        }

    }

    /**
     * Saves the current state of the specified object.
     *
     * @param dockTarget the object of type 
     * {@link org.vns.javafx.dock.api.DockTarget } whose state is to be saved
     */
    //protected abstract void save(DockTarget dockTarget);
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
    protected abstract TreeItem<Properties> restore(DockTarget dockTarget);

    /**
     * The method restores the previously saved state of all registered
     * {@link org.vns.javafx.dock.api.DockTarget} objects. The method looks
     * through all objects of type {@code DockTarget} that are explicitly
     * registered by one of the {@code register} methods and for each calls the {@link #restore(org.vns.javafx.dock.api.DockTarget)
     * }
     * method.
     */
    protected void restore() {
        List<Node> store = FXCollections.observableArrayList();
        for (String key : getDefaultDockTargets().keySet()) {
            Node node = getExplicitlyRegistered().get(key);
            if (store.contains(node)) {
                continue;
            }
            if (DockRegistry.instanceOfDockTarget(node)) {
                TreeItem<Properties> item = restore(DockRegistry.dockTarget(node));
                markExists(item, store);
            }
        }
    }

    private void markExists(TreeItem<Properties> item, List<Node> store) {
        TreeView<Properties> tv = new TreeView();
        tv.setRoot(item);
        for (int i = 0; i < tv.getExpandedItemCount(); i++) {
            Object obj = tv.getTreeItem(i).getValue().get(OBJECT_ATTR);
            if (obj == null || !(obj instanceof Node)) {
                continue;
            }
            Node node = (Node) obj;
            if (DockRegistry.instanceOfDockTarget(node)) {
                store.add(node);
            }
        }
    }

    /**
     * Specifies whether the state of registered objects should be saved when
     * window close. The implementation may or may not use this opportunity
     *
     * @return {@code true} if the state should be saved. {@code false}
     * otherwise
     */
    public boolean isSaveOnClose() {
        return saveOnClose;
    }

    /**
     * Set the property value which specifies whether the state of registered
     * objects should be saved when window close. The implementation may or may
     * not use this opportunity
     *
     * @param saveOnClose {@code saveOnClose} if the state should be saved. {@code false
     * } otherwise
     */
    public void setSaveOnClose(boolean saveOnClose) {
        this.saveOnClose = saveOnClose;
    }

    /**
     * The preferences root path used to save/restore the state of registered
     * nodes.
     *
     * @return the preferences root path
     */
    protected String getPreferencesRoot() {
        return preferencesRoot;
    }

    /**
     * Returns a map of all registered explicitely or implicitly objects. The
     * collection may contain objects of any type which were found during
     * processing the {@code Scene Graph} of explicitely registered nodes. The
     * objects which were registered by applying one of the {@code register}
     * methods are considered as {@code explicitely registered}.
     *
     * @return a map of all registered explicitely or implicitly objects
     */
    protected Map<String, Object> getRegistered() {
        return registered;
    }

    /**
     * Return the boolean value which specifies has the method load() already
     * finished or not.
     *
     * @return {@code true} if the method {@code load() } has already finished. 
     *   {@code false } otherwise.
     */
    @Override
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Sets the boolean value which specifies has the method load() already
     * finished or not.
     *
     * @param loaded {@code true} if the method {@code load() } has already
     * finished. {@code false } otherwise.
     */
    protected void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * The method is a callback method that can be called by some class when the
     * layout of the specified node has changed in some way. For example, a new {@link org.vns.javafx.dock.api.DockSplitPane
     * }
     * object has been added to the {@code DockPane } instance or the 
     * {@code dividerPositions } property has changed. The method registers the
     * value specified by the parameter in the internal collection of the class.
     * In the future, if, for example, when closing a window, we can determine
     * whether there were any changes in some registered object.
     *
     * @param target the node whose layout has been changed
     */
    public void layoutChanged(Node target) {
        if (!loaded || stateChangedList.contains(target)) {
            return;
        }
        stateChangedList.add(target);
    }

    /**
     * Returns a map of all explicitly registered nodes. This collection
     * contains only nodes which were registered by applying one of the
     * {@code register} methods.
     *
     * @return a map of all explicitly registered nodes
     */
    protected Map<String, Node> getExplicitlyRegistered() {

        return explicitlyRegistered;
    }

    /**
     * The method returns a collection of all explicitly or implicitly
     * registered objects of type {@link org.vns.javafx.dock.api.DockTarget }.
     * Here the term {@code "implicitly registered" } objects means that some
     * object was not registered by the {@code register} method, but is a child
     * of some other explicitly registered object.
     *
     * @return a collection of all explicitly or implicitly registered objects
     */
    protected Map<String, TreeItem<Properties>> getAllDockTargets() {
        return allDockTargets;
    }

    /**
     * Defines and returns a field name by the specified object. Every
     * registered object has a string identifier or field name.
     *
     * @param obj The object for which the field name is defined
     * @return the field name for the specified object
     */
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

    /**
     * The method returns a collection of all explicitly registered objects of
     * type {@link org.vns.javafx.dock.api.DockTarget }. Here the term {@code "explicitly registered"
     * } objects means that the object was registered by one of {@code register}
     * method. Each {@code DockTarget} in the map cannot be a child of some
     * other explicitly registered object of type {@code DockTarget}.
     *
     * @return a collection of all explicitly registered objects of type
     * {@code DockTarget}.
     */
    protected Map<String, TreeItem<Properties>> getDefaultDockTargets() {
        return defaultDockTargets;
    }

    /**
     * The method returns a collection of all explicitly registered objects of
     * type {@link org.vns.javafx.dock.api.Dockable }. Here the term {@code "explicitly registered"
     * } objects means that the object was registered by one of {@code register}
     * method. Each {@code Dockable} in the map cannot be a child of some other
     * explicitly registered object of type {@code DockTarget}.
     *
     * @return a collection of all explicitly registered objects of type
     * {@code Dockable}.
     */
    protected Map<String, TreeItem<Properties>> getDefaultDockables() {
        return defaultDockables;
    }

    /**
     * Registers the specified object of type {@code javafx.scene.Node} with the
     * given {@code fieldName}.
     *
     * The method throws an exception if one of the following conditions is met:
     * <ul>
     * <li>
     * Method 'load' has already been invoked and {@code isLoaded()} method
     * returns {@code true}
     * </li>
     * <li>The parameter {@code fieldName} is {@code null}</li>
     * <li>An object with the specified fieldName has already been
     * registered</li>
     * <li>The specified object has already been registered</li>
     * <li>The specified node must be of type {@code Dockable} or
     * {@code DockTarget}</li>
     * </ul>
     *
     * @param fieldName the string value used as identifier for the node to be
     * registered.
     *
     * @param node the object to be registered
     */
    @Override
    public void register(String fieldName, Node node) {
        if (loaded) {
            throw new IllegalStateException("Attempts to register an entry '"
                    + fieldName + "' and class '" + node.getClass().getName() + "' but the method 'load' has already been invoked");
        }
        if (fieldName == null || getExplicitlyRegistered().containsKey(fieldName)) {
            throw new IllegalArgumentException("Dublicate entry name: " + fieldName);
        }
        //
        // May be the dockable node is allready in registered map with another fieldName name
        //
        if (DockRegistry.instanceOfDockable(node) && getExplicitlyRegistered().containsValue(node)) {
            //getExplicitlyRegistered().remove(getEntryName(node));
            throw new IllegalArgumentException("Dublicate node. entryName: " + fieldName);
        }

        if (!DockRegistry.instanceOfDockTarget(node) && !DockRegistry.instanceOfDockable(node)) {
            throw new IllegalArgumentException("Illegall className. entry name: " + fieldName + "; class=" + node.getClass().getName());
        }

        getExplicitlyRegistered().put(fieldName, node);
        if (DockRegistry.instanceOfDockTarget(node)) {
            addListeners(node);
        }
    }

    /**
     * Create the object of the specified class and registers it with the given
     * {@code fieldName}.
     *
     * The method throws an exception if one of the following conditions is met:
     * <ul>
     * <li>
     * Method 'load' has already been invoked and {@code isLoaded()} method
     * returns {@code true}
     * </li>
     * <li>The parameter {@code fieldName} is {@code null}</li>
     * <li>An object with the specified fieldName has already been
     * registered</li>
     * <li>The created node must be of type {@code Dockable} or
     * {@code DockTarget}</li>
     * </ul>
     *
     * @param fieldName the string value used as identifier for the node to be
     * registered.
     *
     * @param clazz the object of class Class to be registered
     */
    @Override
    public Node register(String fieldName, Class<? extends Node> clazz) {
        Node retval = null;
        if (loaded) {
            throw new IllegalStateException("Attempts to register an entry '"
                    + fieldName + "' and class '" + clazz.getName() + "' but the method 'load' has already been invoked");
        }
        if (fieldName == null || getExplicitlyRegistered().containsKey(fieldName)) {
            throw new IllegalArgumentException("Dublicate entry name: " + fieldName);
        }

        try {
            retval = clazz.newInstance();

            if (!DockRegistry.instanceOfDockTarget(retval) && !DockRegistry.instanceOfDockable(retval)) {
                throw new IllegalArgumentException("Illegall className. entry name: " + fieldName + "; class=" + clazz.getName());
            }

            getExplicitlyRegistered().put(fieldName, retval);
            if (DockRegistry.instanceOfDockTarget(retval)) {
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
                    if (isSaveOnClose()) {
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

    /**
     * Checks whether the specified node is registered (no matter explicitly or
     * implicitly).
     *
     * @param node the node to be checked
     * @return {@code true} if the {@code node} is registered. {@code false}
     * otherwise.
     */
    @Override
    public boolean isRegistered(Node node) {
        return registered.values().contains(node);
    }

    /**
     * The handy method to access an object of type 
     * {@link org.vns.javafx.dock.api.save.DockTreeItemBuilder } by the
     * specified parameter.
     *
     * @param dockTarget the object which owns an instance of type {@code DockTreeItemBuilder
     * }
     * @return the object of type {@code DockTreeItemBuilder }
     */
    protected DockTreeItemBuilder builder(Node dockTarget) {
        return getDockTreeTemBuilder(dockTarget);
    }

    public DockTreeItemBuilder getDockTreeTemBuilder(Node node) {
        DockTreeItemBuilder retval = null;
        DockTarget dockTarget = DockRegistry.dockTarget(node);
        TargetContext context = dockTarget.getTargetContext();
        DockTreeItemBuilderFactory f = context.getLookup().lookup(DockTreeItemBuilderFactory.class);
        if (f != null) {
            retval = f.getItemBuilder(dockTarget);
        }
        return retval;
        //return new DockTabPaneTreeItemBuilder((DockTabPane) getTargetNode());
    }

    /**
     * Builds a tree item representation by the specified Node and it's field
     * name. First the method calls the method {@code build(String}} of the 
     * {@link org.vns.javafx.dock.api.TargetContext#getDockTreeTemBuilder() }
     * instance and then invokes the method {@link #completeBuild(javafx.scene.control.TreeItem, boolean)
     * }
     *
     * @param fieldName the register identifier of the specified node
     * @param dockTarget the node for which the tree item is to be built
     * @return the tree item representation of the node
     */
    protected TreeItem<Properties> build(String fieldName, Node dockTarget) {
        TreeItem<Properties> item = builder(dockTarget).build(fieldName);
        completeBuild(item, false);
        return item;
    }

    /**
     * Defines fieldName attribute for each treeItem.
     *
     * @param root the object
     * @param loaded if {@code treu }
     */
    protected void completeBuild(TreeItem<Properties> root, boolean loaded) {

        TreeView<Properties> tv = new TreeView();
        tv.setRoot(root);

        String rootFieldName = root.getValue().getProperty(FIELD_NAME_ATTR);

        int level = 0;
        Stack<String> stack = new Stack<>();
        int idx = 0;

        stack.push(rootFieldName);

        for (int i = 0; i < tv.getExpandedItemCount(); i++) {
            Object obj = tv.getTreeItem(i).getValue().get(OBJECT_ATTR);
            String fieldName = getFieldName(obj);
            if (fieldName == null) {
                fieldName = rootFieldName + "_" + idx + "_" + obj.getClass().getSimpleName();
                if (loaded) {
                    fieldName += "_loaded";
                }
            }
            tv.getTreeItem(i).getValue().setProperty(FIELD_NAME_ATTR, fieldName);

            if (!loaded) {
                if ((obj instanceof Node) && DockRegistry.instanceOfDockTarget((Node) obj)) {
                    if (i == 0 && !getAllDockTargets().containsKey(fieldName)) {
                        getDefaultDockTargets().put(fieldName, tv.getTreeItem(i));
                    } else if (getAllDockTargets().containsKey(fieldName)) {
                        getDefaultDockTargets().remove(fieldName);
                    }
                    getAllDockTargets().put(fieldName, tv.getTreeItem(i));
                }

                registered.put(fieldName, obj);
            }
            idx++;
            if ((obj instanceof Node) && DockRegistry.instanceOfDockTarget((Node) obj)) {
                saved.add((Node) obj);
                int l = tv.getTreeItemLevel(tv.getTreeItem(i));
                if (l > level) {
                    stack.push(fieldName);
                    rootFieldName = fieldName;
                    idx = 0;
                } else if (l < level) {
                    rootFieldName = stack.pop();
                    idx = 0;
                } else {
                    stack.pop();
                    stack.push(fieldName);
                    idx = 0;
                }
                level = l;
            }

            //
            // For now we don't use parent dock target anywhere in code
            //
/*            if (i > 0 && (obj instanceof Node) && DockRegistry.instanceOfDockTarget((Node) obj)) {
                TreeItem<Properties> p = findParentDockTarget(tv, tv.getTreeItem(i));
                String parentFieldName = p.getValue().getProperty(FIELD_NAME_ATTR);
                tv.getTreeItem(i).getValue().setProperty(PARENT_DOCKTARGET_ATTR, parentFieldName);
            }
             */
        }//for

    }

    private TreeItem<Properties> findParentDockTarget(TreeView tv, TreeItem item) {
        TreeItem<Properties> retval = tv.getRoot();
        TreeItem<Properties> parent = item.getParent();
        while (parent != null) {
            Object obj = parent.getValue().get(OBJECT_ATTR);
            if ((obj instanceof Node) && DockRegistry.instanceOfDockTarget((Node) obj)) {
                retval = parent;
                break;
            }

            parent = parent.getParent();
        }
        return retval;
    }

    /*    protected String getFieldName(Object obj, String rootFieldName, int idx) {
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
     */
}//AbstractDockLoader
