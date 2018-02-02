package org.vns.javafx.dock.api.save;

import java.util.Properties;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TargetContext;

/**
 * Defines the methods needed to convert a {@code Scene Graph } node to a tree
 * object of type {@code javafx.scene.control.TreeItem} and vice versa.
 *
 * Each class that inherits the
 * {@link org.vns.javafx.dock.api.TargetContext}} provides a class object
 * that implements this interface. The object is accessed using the following
 * method of the class {@code TargetContext}:
 * <pre>
 *  public DockTreeItemBuilder getPreferencesBuilder()
 * </pre> In the {@code TargetContext} class, the method returns
 * {@code null}.
 *
 * @author Valery Shyshkin
 */
public interface DockTreeItemBuilder {

    public static final Object OBJECT_ATTR = "ignore:object";
    public static final String FIELD_NAME_ATTR = "ld:fieldName";
    public static final String CLASS_NAME_ATTR = "ld:className";
    public static final String TAG_NAME_ATTR = "ld:tagName";
    public static final String TREEITEM_ATTR = "ignore:treeItem";
    public static final String PARENT_DOCKTARGET_ATTR = "ignore:parentDockTarget";

    public static final String ISDOCKABLE_ATTR = "ld:isdockable";
    public static final String ISDOCKTARGET_ATTR = "ld:isdocktarget";

    /**
     * Returns a tree of objects of type {@code javafx.scene.control.TreeItem},
     * each node of which corresponds to some object from the {@code Scene Graph
     * }
     * of the {@link org.vns.javafx.dock.api.DockTarget } node. The root node
     * corresponds to an object of the {@code DockTarget} type. For which
     * objects the {@code TreeItem} nodes are created is determined by the
     * specific implementation of the corresponding {@code DockTarget}.
     *
     * The method uses the property named {@link #OBJECT_ATTR } to store the
     * reference to the registered object.
     *
     * <pre>
     *  treeItem.getValue().GetKey().set (obj)
     * </pre> where the {@code obj} is the Scene Graph object for which the
     * {@code TreeItem} is built.
     * <p>
     * Sets property values whose names are defined as static constants in the
     * interface.
     * </p>
     * <pre>
     * CLASS_NAME_ATTR = "ld:className";
     * TAG_NAME_ATTR = "ld:tagName";
     * TREEITEM_ATTR = "ignore:treeItem";
     * ISDOCKABLE_ATTR = "ld:isdockable";
     * ISDOCKTARGET_ATTR = "ld:isdocktarget";
     * </pre> The {@code CLASS_NAME_ATTR} property is the string value of the
     * fully specified object class name from the Scene Graph for which the
     * TreeItem is built.
     * <p>
     * The {@code TAG_NAME_ATTR} property is the string value which may be used
     * as an {@code xml} name of the tag corresponding to the Scene Graph
     * object. Usually this value is the same as the simple class name of the
     * object, but the {@code DockTreeItemBuilder} implementation can use any
     * valid value for {@code xml tag} names of {@code xml }documents.
     * </p>
     * <p>
     * The TREEITEM_ATTR property is a reference to the {@code TreeItem} object.
     * The name space value {@code "ignore" } indicates that this value is used
     * for internal API purposes and is not intended to be persisted.
     * </p>
     * <p>
     * If an object from the {@code Scene Graph} is a
     * {@link org.vns.javafx.dock.api.Dockable} object, then the value of the
     * {@code ISDOCKABLE_ATTR} property is set. This can be the value
     * {@code "yes ", * "true" or "no ", "false "}. If the property value is not
     * set, then the default value is {@code "no"}.
     * </p>
     * <p>
     * If an object from the {@code Scene Graph} is a
     * {@link org.vns.javafx.dock.api.DockTarget} object, then the value of the
     * {@code ISDOCKTARGET_ATTR} property is set. This can be the value
     * {@code "yes ", * "true" or "no ", "false "}. If the property value is not
     * set, then the default value is {@code "no"}.
     * </p>
     * <p>
     * Such an important property as {@code FIELD_NAME_ATTR} is set only if the
     * value of the {@code fieldName} parameter is not null. Typically, this
     * value is different from {@code null} only for the root {@code TreeItem}.
     * For its children, this value is set by the{@link org.vns.javafx.dock.api.save.DockStateLoader
     * }
     * </p>
     * <p>
     * In addition to the specialized properties mentioned above, the interface
     * implementation can set in {@code TreeItem} any additional properties of
     * objects from the {@code Scene Graph}. Typically, these properties affect
     * layout in one way or another, for example, {@code dividerPositions} from
     * {@code SplitPane} or {@code ORIENTATION}.
     * </p>
     *
     * @param fieldName the String name used to register an object of type
     * {@code org.vns.javafx.dock.api.DockTarget } may be {@code null}.
     *
     * @return an object of type {@code javafx.scene.control.TreeItem}
     * @see #build()
     */
    TreeItem<Properties> build(String fieldName);

    /**
     * This is a handy method used when you need to call the 
     * {@link #build(java.lang.String) } method with a parameter value of
     * {@code null}. The method is implemented as {@code default}.
     *
     * @return an object of type {@code javafx.scene.control.TreeItem}
     * @see #build(java.lang.String)
     */
    default TreeItem<Properties> build() {
        return build(null);
    }

    /**
     * Modifies the layout and composition of the nodes of the source object
     * which is an object of type {@link org.vns.javafx.dock.api.DockTarget }
     * by information received from the {@code javafx.scene.control.TreeItem}
     * specified by the parameter and then returns the result.
     *
     * @param root the item to start restore from
     * @return the object of type {@code Node }
     */
    Node restore(TreeItem<Properties> root);

    void setOnBuildItem(Consumer<TreeItem<Properties>> consumer);

    Consumer<TreeItem<Properties>> getOnBuildItem();

    /**
     * A convenient method when the value of the field name is null. It simply
     * calls the {@link  #build(java.lang.String, java.lang.Object) }
     * method with the first parameter equal to {@code null }.
     *
     * @param obj the object for which a node of type
     * {@code javafx.scene.control.TreeItem} is built
     * @return an object of type {@code javafx.scene.control.TreeItem}
     * @see #build(java.lang.String,java.lang.Object)
     */
    static TreeItem<Properties> build(Object obj) {
        return build(null, obj);
    }

    /**
     * Partially builds a single object of type
     * {@code javafx.scene.control.TreeItem}, by the given field name and
     * object. The field name can be {@code null}. The work of the method is to
     * set
     * <pre>
     * item.getValue().getKey().set(obj)
     * </pre>
     *
     * and populate the standard properties whose names are defined by
     * constants:
     * <pre>
     * CLASS_NAME_ATTR
     * TAG_NAME_ATTR
     * TREEITEM_ATTR
     * ISDOCKABLE_ATTR
     * SDOCKTARGET_ATTR
     * FIELD_NAME_ATTR
     * </pre>
     *
     * @param fieldName the String name used to register an object of type
     *   {@code org.vns.javafx.dock.api.DockTarget } may be {@code null}.
     * @param obj the object for which a node of type
     * {@code javafx.scene.control.TreeItem} is built
     * @return an object of type {@code javafx.scene.control.TreeItem}
     * @see #build(java.lang.Object)
     */
    static TreeItem<Properties> build(String fieldName, Object obj) {
        if (obj == null) {
            return null;
        }
        TreeItem<Properties> retval = new TreeItem<>();
        retval.setExpanded(true);
        Node node = null;
        if (obj instanceof Node) {
            node = (Node) obj;
        }
        Properties props = new Properties();
        props.put(OBJECT_ATTR, obj);
        props.put(TREEITEM_ATTR, retval);
        if (fieldName != null) {
            props.put(FIELD_NAME_ATTR, fieldName);
        }
        props.put(CLASS_NAME_ATTR, obj.getClass().getName());
        props.put(TAG_NAME_ATTR, obj.getClass().getSimpleName());
        retval.setValue(props);

        if (node != null && node.getId() != null) {
            props.put("id", node.getId());
        }

        if (node != null && (DockRegistry.instanceOfDockable(node) || DockRegistry.instanceOfDockTarget(node))) {
            if (DockRegistry.instanceOfDockable(node)) {
                props.put(ISDOCKABLE_ATTR, "yes");
            }
            if (DockRegistry.instanceOfDockTarget(node)) {
                props.put(ISDOCKTARGET_ATTR, "yes");
            }
        }
        return retval;
    }
    default DockTreeItemBuilder getDockTreeItemBuilder(Node node) {
        DockTreeItemBuilder retval = null;
        DockTarget dockTarget = DockRegistry.dockTarget(node);
        TargetContext context = dockTarget.getTargetContext();
        DockTreeItemBuilderFactory f = context.getLookup().lookup(DockTreeItemBuilderFactory.class);
        if (f != null) {
            retval = f.getItemBuilder(dockTarget);
        }
        return retval;
    }

}
