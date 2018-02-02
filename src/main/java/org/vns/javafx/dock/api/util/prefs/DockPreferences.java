package org.vns.javafx.dock.api.util.prefs;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The class is a specialized wrapper around the class
 * {@literal AbstractPreferences} from the package {@literal java.util.prefs}.
 * The main objective of the class is to provide access to various settings
 * (properties,preferences etc.) specific to an application or module. The
 * method {@link #getProperties(java.lang.String) } return an instance of type
 * {@link PropertyCollection}. This instance allows to store data and extract
 * them in a manner like we do when use {@literal java.util.Properties} but
 * without worrying about persistence.
 * <p>
 * For example, we can execute the following code:
 * <pre>
 Path forDirPath = Paths.get("c:/my-tests/first-test");
 DirectoryPreferences reg = DirectoryPreferences.newInstance("my-examples/1", dir);
 PropertyCollection props = reg.getProperties{"example-properties");
 </pre> As a result, somewhere in the name space defined by the class
 * {@literal AbstractPreferences} the following structure will be created:
 * <pre>
 *   UUID_ROOT/my-examples/1/c_/my-tests/first-test/<i>example-properties</i>
 * </pre> The full path, shown above, has the following structure:
 * <ul>
 * <li> {@literal UUID_ROOT } is a string value of the static constant defined
 * in the class.
 * </li>
 * <li> {@literal  my-examples/1 } is a string value passed as a first parameter
 * value in the {@literal newInstance } method call.
 * </li>
 * <li> {@literal c_/my-tests/first-test } is a string value representation of
 * the second parameter of type {@literal Path } passed in the {@literal newInstance
 * } method call. Pay attention that the original value contains a colon
 * character which is replaced with an underline symbol. The backslash is also
 * replaced by a forward slash.
 * </li>
 * </ul>
 * We call the first part plus second part as a {@literal "registry root"}. And
 * "registry root" + third part - "directory node". The last part defines a root
 * for properties whose value is used as a parameter for the method 
 * {@literal getProperties() } call.
 *
 * <p>
 * Here UUID_ROOT  is a string value of the static constant defined in
 * the class.
 * <p>
 * We can create just another properties store:
 * <pre>
 *     props2 = reg.getProperties{"example-properties-2");
 * </pre> and receive as a result:
 * <pre>
 *   UUID_ROOT/my-examples/1/c:/my-tests/first-test/<i>example-properties-1</i>
 * </pre> Now that we have an object of type {@link PropertyCollection} , we
 * can read or write various properties, for example:
 * <pre>
 *  props.setProperty("myProp1","My first property");
 *  String value = props.getProperty("myProp1");
 * </pre> There are many useful methods in the class
 * {@link PropertyCollection} that we can use to work with the properties.
 * 
 * 
 *
 * 
 * 
 * 
 * 
 *
 * @author V. Shyshkin
 */
public class DockPreferences {

    private static final Logger LOG = Logger.getLogger(DockPreferences.class.getName());

    public static String TEST_COMMON_ROOT = "TEST_UUID-ROOT";

    public static String COMMON_ROOT = "common-root-18101f98-ab5c-49f3-9942-8baa188a5c17";

    protected String[] rootExtentions;

    private String rootNamespace;

    protected Preferences rootNode;
    
    public DockPreferences(String root) {
        this(root, new String[0]);
    }
    

    protected DockPreferences(String root, String... rootExtentions) {
        this(AbstractPreferences.userRoot().node(root.replace("\\", "/")), rootExtentions);
    }

    protected DockPreferences(Preferences rootNode, String... rootExtentions) {
        this.rootExtentions = rootExtentions;
        this.rootNode = rootNode;
        init();

    }

    private void init() {
        rootExtentions = normalize(rootExtentions);
        if ( rootNamespace != null ) {
            return;
        }
        String root = normalize(rootNode.absolutePath());
        String user = normalize(userRoot().absolutePath());
        if (root.isEmpty()) {
            rootNamespace =  "";
        } else {
            root = root.substring(user.length());
            if (root.startsWith("/") && root.length() > 1) {
                root = root.substring(1);
            }        
        }
        rootNamespace = root;
        
    }

    public DockPreferences next(String newNamespace) {
        if ( newNamespace.trim().isEmpty() ) {
            return this;
        }
        String[] extentions = Arrays.copyOf(rootExtentions, rootExtentions.length + 1);
        extentions[rootExtentions.length] = normalize(newNamespace);
        return new DockPreferences(rootNamespace, extentions);
    }

    /**
     * Return a string value that is used to create a root node of the registry.
     *
     * @return Return a string value which is used to create a root node of the
     * registry
     */
    public String rootNamespace() {
        return rootNamespace;
    }

    /**
     * Return a string value that is used to create a root node of the registry.
     *
     * @return Return a string value which is used to create a root node of the
     * registry
     */
    public String currentNamespace() {
        //Path commonUserRoot = Paths.get(commonUserRoot().absolutePath());
        String result = "";
        for (String item : rootExtentions) {
            result += "/" + item;
        }
        if (result.length() != 0) {
            result = result.substring(1);
        }
        return result;
    }

    /**
     * Returns the root preference node for the calling user with extended name
     * space as specified by the constant {@link #COMMON_ROOT}. Just calls:
     * <pre>
     *  AbstractPreferences.userRoot().node()COMMON_ROOT;
     * </pre>
     *
     * @return the root preference node for the calling user.
     * @throws java.lang.SecurityException - If a security manager is present
     * and it denies RuntimePermission("preferences").
     * @see java.lang.RuntimePermission
     */
    public Preferences userRoot() {
        return AbstractPreferences.userRoot();
    }

    public Preferences rootNode() {
        return rootNode;
    }

    public Preferences getPreferences() {
        Preferences p = rootNode();

        if (rootExtentions != null && rootExtentions.length > 0) {
            for (int i = 0; i < rootExtentions.length; i++) {
                p = p.node(rootExtentions[i]);
            }
        }
        return p;
    }

    public String normalize(String path) {
        String result = path.replace("\\", "/");

        if (result.startsWith("/")) {
            result = result.substring(1);
        }

        if (result.trim().isEmpty()) {
            return "";
        }
        if (result.trim().equals("/")) {
            return "";
        }

        if (result.trim().startsWith("/")) {
            result = result.trim().substring(1);
        }
        while (true) {
            if (!result.contains("//")) {
                break;
            }
            result = result.replace("//", "/");
        }
        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    protected String[] normalize(String[] paths) {
        if (paths == null) {
            return new String[0];
        }
        String[] result = new String[paths.length];

        for (int i = 0; i < paths.length; i++) {
            result[i] = normalize(paths[i]);
        }

        return result;
    }

    public String join(String[] paths) {
        String[] norm = normalize(paths);
        return String.join("/", norm);
    }


    public String[] childrenNames(Preferences forNode) {
        String[] result = new String[0];
        synchronized (this) {
            try {
                result = forNode.childrenNames();
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
        }
        return result;
    }

    public String[] childrenNames(String relativePath) throws BackingStoreException {
        String rp = normalize(relativePath);
        synchronized (this) {
            return getPreferences().node(rp).childrenNames();
        }
    }

    public String[] childrenNames() {
        String[] result = new String[0];
        synchronized (this) {
            try {
                result = getPreferences().childrenNames();
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
            return result;
        }
    }

    public boolean clearRoot() {

        boolean result = false;

        synchronized (this) {
            try {
                String[] childs = rootNode().childrenNames();
                for (String c : childs) {
                    rootNode().node(c).removeNode();
                }
                result = true;
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);

            }
            return result;
        }
    }
    public boolean removelChildren() {
        return clear();
    }
    public boolean removelChildren(Predicate<String> p) {
        boolean result = false;

        synchronized (this) {
            try {
                String[] childs = getPreferences().childrenNames();
                for (String c : childs) {
                    if ( p.test(c) ) {
                        getPreferences().node(c).removeNode();
                    }
                }
                result = true;
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);

            }
            return result;
        }

    }

    public boolean clear() {

        boolean result = false;

        synchronized (this) {
            try {
                String[] childs = getPreferences().childrenNames();
                
                for (String c : childs) {
                    getPreferences().node(c).removeNode();
                }
                result = true;
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);

            }
            return result;
        }
    }

    public boolean clearPropertiesRoot() {

        boolean result = false;

        synchronized (this) {
            try {
                String[] childs = getPreferences().childrenNames();
                for (String c : childs) {
                    getPreferences().node(c).removeNode();
                }
                result = true;
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);

            }
            return result;
        }
    }

    public boolean hasChilderens(Preferences root) {
        synchronized (this) {
            return childrenNames().length > 0;
        }
    }

    protected Preferences clearCommonUserRoot() throws BackingStoreException {
        synchronized (this) {
            String[] childs = userRoot().childrenNames();
            for (String c : childs) {
                userRoot().node(c).removeNode();
            }
            return userRoot();
        }
    }

    public Preferences addNode(Preferences source, String path) {
        String newpath = normalize(path);
        synchronized (this) {
            return source.node(newpath);
        }
    }

    /**
     * Removes directory nodes starting from the given node. Be careful
     * regardless whether the child node exists the method always tries to
     * remove the given node. Once the node is deleted, the method recursively
     * removes all parent nodes as long as one of the conditions is satisfied
     * <ul>
     * <li>The parent node is a root registry node as specified by the method
     * {@link #getPreferences()}
     * </li>
     * <li>The parent node has children nodes
     * </li>
     * </ul>
     *
     * The above-mentioned node is not removed.<p>
     * To remove the node specified by the first parameter set the second
     * parameter to {@code null } or to an empty string.
     *
     * @param rootToDelete initial node to delete
     * @param path a relative path to the node specified by the first parameter
     * @return return true when remove success. If the node to delete doesn't
     * exist then the method does nothing and returns false
     */
    public boolean remove(Preferences rootToDelete, String path) {
        String newpath = "";
        if (path != null) {
            newpath = normalize(path);
        }

        boolean result = true;
        synchronized (this) {
            try {
                if (!rootToDelete.nodeExists("") || !rootToDelete.nodeExists(newpath)) {
                    return false;
                }
                DockPreferences.this.remove(rootToDelete.node(newpath));
                userRoot().flush();
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
                result = false;
            }
        }
        return result;
    }

 
    /**
     * Checks whether a node specified by the parameter exists. The parameter
     * {@code namespace} must be relative path to the {@link #getPreferences() }.
     *
     * @param relativePath a string that specifies a path relative to the node
     * as defined by the method {@link #getPreferences() }.
     * @return {@code  true} if the node exists, {@code false} - otherwise
     */
    public boolean nodeExists(String relativePath) {
        boolean b = false;
        try {
            b = getPreferences().nodeExists(normalize(relativePath));
        } catch (BackingStoreException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return b;
    }

    /**
     * Checks whether a node specified by the parameter exists. The parameter
     * {@code relativePath} must be relative path specified by the first
     * parameter.
     *
     * @param root the node that is used to check whether the node specified by
     * the second parameter exists
     * @param relativePath a string that specifies a path relative to the node
     * specified by the first parameter.
     * @return {@code  true} if the node exists, {@code false} - otherwise
     */
    public boolean nodeExists(Preferences root, String... relativePath) {
        boolean b = false;
        try {
            b = root.nodeExists(join(relativePath));
        } catch (BackingStoreException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return b;
    }

    /**
     * Removes directory nodes starting from the given node. Be careful
     * regardless whether the child node exists the method always tries to
     * remove the given node. Once the node is deleted, the method recursively
     * removes all parent nodes as long as one of the conditions is satisfied
     * <ul>
     * <li>The parent node is a root registry node as specified by the method
     * {@link #getPreferences() }
     * </li>
     * <li>The parent node has children nodes
     * </li>
     * </ul>
     *
     * The above-mentioned node is not removed.
     *
     * @param prefs initial node to delete
     */
    protected void remove(Preferences prefs) {

        Preferences parent = prefs.parent();
        //String nm = prefs.name();
        try {
            if ( userRoot().equals(prefs)) {
                return;
            }
            prefs.removeNode();
            prefs.flush();
            if (parent.childrenNames().length > 0) {
                return;
            }
            DockPreferences.this.remove(parent);
        } catch (BackingStoreException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

    }

    public String firstName(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return null;
        }
        return normalize(namespace).split("/")[0];
    }

    public Preferences remove(Preferences toRemove, Preferences upperNode) {
        Preferences parent = toRemove.parent();
        try {
            toRemove.removeNode();
            if (parent.absolutePath().equals(upperNode.absolutePath())) {
                return parent;
            }
            if (parent.childrenNames().length > 0) {
                return parent;
            }
            remove(parent, upperNode);
            userRoot().flush();
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
        }
        return parent;

    }

    /**
     * The method returns an instance of {@link  PropertyCollection} class.
     * The {@literal id} is used to create a node relatively to a directory node.
     *
     * @param id the value that specifies a name for a node where properties are
     * written and read.
     *
     * @return an object of type {@link PropertyCollection}
     */
    public PrefProperties getProperties(String id) {

        String cid = normalize(id);

        Preferences prefs = getPreferences();
        try {
            synchronized (this) {
                if (!prefs.nodeExists(cid)) {
                    return null;
                }
                prefs = prefs.node(cid);
                PropertyCollection properties = new PrefProperties(cid, prefs);//factory.create(id, prefs);                
                return (PrefProperties) properties;
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    public PrefProperties createProperties(String id) {
        String cid = normalize(id);
        Preferences prefs = getPreferences();
        try {
            synchronized (this) {
                prefs = prefs.node(cid);
                initProperties(prefs);
                prefs.flush();

                PropertyCollection created = new PrefProperties(cid, prefs);//factory.create(id, prefs);                
                String s = created.getPreferences().absolutePath();
                return (PrefProperties) created;
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    public PrefProperties createProperties(String id, boolean needInitProperties) {
        String cid = normalize(id);

        Preferences prefs = getPreferences();
        try {
            synchronized (this) {
                prefs = prefs.node(cid);
                if (needInitProperties) {
                    initProperties(prefs);
                }
                prefs.flush();
                PropertyCollection created = new PrefProperties(cid, prefs);//factory.create(id, prefs);                
                return (PrefProperties) created;
            }
        } catch (BackingStoreException ex) {
            LOG.log(Level.INFO, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    protected void initProperties(Preferences prefs) {
    }

    /**
     * Removes directory nodes starting from the {@code propertiesRoot }
     * node. Be careful regardless whether the child node exists the method
     * always tries to remove the given node. Once the node is deleted, the
     * method recursively removes all parent nodes as long as one of the
     * conditions is satisfied
     * <ul>
     * <li>The parent node is a root registry node as specified by the method
     * {@link #remove(java.util.prefs.Preferences)}
     * </li>
     * <li>The parent node has children nodes
     * </li>
     * </ul>
     *
     * The above-mentioned node is not removed.
     *
     */
    public void remove() {
        remove(getPreferences());
    }
    
    
    protected static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    private String removeDoubleSlashes(String path) {
        String result = path;
        while ( true ) {
            if ( ! result.contains("//") ) {
                break;
            }
            result = result.replace("//", "/");
        }
        return result;
    }
    public String absolutePath(String... propNamespaces) {
        String propsPath = join(propNamespaces);
        String result = userRoot().absolutePath();

        if (!rootNamespace().isEmpty()) {
            try {
                if (!userRoot().nodeExists(rootNamespace())) {
                    return result;
                }
                result += "/" + rootNamespace();
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
        }

        if (!currentNamespace().isEmpty()) {
            try {
                if (!rootNode().nodeExists(currentNamespace())) {
                    return removeDoubleSlashes(result);
                }
                result += "/" + currentNamespace();
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
        }
        if (!propsPath.isEmpty()) {
            try {
                if (! getPreferences().nodeExists(propsPath)) {
                    return removeDoubleSlashes(result);
                }
                result += "/" + propsPath;
            } catch (BackingStoreException ex) {
                LOG.log(Level.INFO, null, ex);
            }
        }
        return removeDoubleSlashes(result);

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.deepHashCode(this.rootExtentions);
        hash = 97 * hash + Objects.hashCode(this.rootNode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DockPreferences other = (DockPreferences) obj;
        if (!Arrays.deepEquals(this.rootExtentions, other.rootExtentions)) {
            return false;
        }
        if (!Objects.equals(this.rootNode, other.rootNode)) {
            return false;
        }
        return true;
    }

}
