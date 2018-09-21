package org.vns.javafx.dock.api.designer.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.bean.BeanAdapter;

/**
 *
 * @author Valery
 */
public class PropertyPaneDescriptorRegistry {

    private PropertyPaneCollection propertyPaneCollection;
    private final ObservableMap<Class<?>, Introspection> introspection = FXCollections.observableHashMap();

    public static PropertyPaneDescriptorRegistry getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public static PropertyPaneCollection getPropertyPaneCollection() {
        PropertyPaneCollection gd = getInstance().propertyPaneCollection;
        if (gd == null) {
            gd = getInstance().loadDefaultDescriptors();
            getInstance().propertyPaneCollection = gd;
        }
        return gd;
    }

    private final ObservableList<Class<?>> defaultClasses = FXCollections.observableArrayList();

    protected void createInternalDescriptors() {
        propertyPaneCollection = new PropertyPaneCollection();
        
        addObjectPropertyPaneDescriptor(propertyPaneCollection);
        addNodePropertyPaneDescriptor(propertyPaneCollection);
        addRegionPropertyPaneDescriptor(propertyPaneCollection);
        
    }

    protected PropertyPaneCollection createInternalDescriptors(boolean set) {
        PropertyPaneCollection ppc = new PropertyPaneCollection();
        addObjectPropertyPaneDescriptor(ppc);
        addNodePropertyPaneDescriptor(ppc);
        addRegionPropertyPaneDescriptor(ppc);
        return ppc;
    }

    public ObservableList<Class<?>> getDefaultClasses() {
        return defaultClasses;
    }

    protected void addObjectPropertyPaneDescriptor(PropertyPaneCollection ppc) {
        PropertyPaneDescriptor ppd = new PropertyPaneDescriptor();
        ppd.setBeanType(Object.class);
        ppd.setBeanClassName(Object.class.getName());
        Category cat = new Category();
        cat.setName("properties");
        cat.setDisplayName("Properties");
        ppd.getCategories().add(cat);
        ppc.getPropertyPaneDescriptors().add(ppd);

    }

    protected void addNodePropertyPaneDescriptor(PropertyPaneCollection ppc) {
        PropertyPaneDescriptor ppd = new PropertyPaneDescriptor();
        ppc.getPropertyPaneDescriptors().add(ppd);
        
        ppd.setBeanType(Node.class);
        ppd.setBeanClassName(Node.class.getName());

        Category propCat = ppd.addCategory("properties", "Properties");
        Category layoutCat = ppd.addCategory("layout", "Layout");
        Category codeCat = ppd.addCategory("code", "Code");
        //ppd.getCategories().addAll(propCat,layoutCat,code);
        //
        // "Properties" category
        //

        // --- "Node" Section
        Section nodeSec = propCat.addSection("node", "Node");
        nodeSec.add("disable", "disabled", "opacity", "nodeOrientation", "visible", "focusTraversable", "focused", "cursor", "effect", "pressed", "managed");

        nodeSec = propCat.addSection("javafxcss", "JavaFX CSS");
        nodeSec.add("style", "styleClass", "id");
        // --- "Extras" Section 
        Section propExtrasSec = propCat.addSection("extras", "Extras");
        propExtrasSec.add("blendMode", "cache", "cacheHint", "depthTest", "mouseTransparent", "pickOnBounds");

        // --- "Accessibility" Section
        Section accessSec = propCat.addSection("accessibility", "Accessibility");
        accessSec.add("accessibleText", "accessibleHelp", "accessibleRole", "accessibleRoleDescription");

        //
        // "Layout" category
        //
        // --- "Position" Section
        Section positionSec = layoutCat.addSection("position", "Position");
        positionSec.add("layoutX", "layoutY");
        // --- "Transforms" Section

        Section transformsSec = layoutCat.addSection("transforms", "Transforms");
        transformsSec.add("rotate", "rotationAxis", "scaleX", "scaleY", "scaleZ", "translateX", "translateY", "translateZ");
        // --- "Bounds" Section        
        Section boundsSec = layoutCat.addSection("bounds", "Bounds");
        boundsSec.add("layoutBounds", "boundsInLocal", "boundsInParent");
        // --- "Extras" Section        
        Section extrasSec = layoutCat.addSection("extras", "extras");
        extrasSec.add("effectiveNodeOrientation");

        //
        // "Code" category
        //
        // --- "Main" Section
        //List<String> list = 
        String[] str = new String[]{"onContextMenuRequested", "onDragDetected", "onDragDone",
            "onDragDropped", "onDragEntered", "onDragExited", "onDragOver",
            "onInputMethodTextChanged",
            "onKeyPressed", "onKeyReleased", "onKeyTyped", "onMouseClicked",
            "onMouseDragEntered", "onMouseDragExited", "onMouseDragOver",
            "onMouseDragReleased", "onMouseDragged", "onMouseEntered",
            "onMouseExited", "onMouseMoved", "onMousePressed", "onMouseReleased",
            "onRotate", "onRotationFinished", "onRotationStarted", "onScroll",
            "onScrollFinished", "onScrollStarted", "onSwipeDown", "onSwipeLeft",
            "onSwipeRight", "onSwipeUp", "onTouchMoved", "onTouchPressed",
            "onTouchReleased", "onTouchStationary", "onZoom", "onZoomFinished",
            "onZoomStarted"};
        List<String> list = Arrays.asList(str);
        List<String> excl = new ArrayList<>();
        if (list.contains("onAction")) {
            Section sec = codeCat.addSection("main", "Main");
            sec.add("onAction");
            excl.add("onAction");
        }

        List<String> list1 = new ArrayList<>();
        list.forEach(s -> {
            if (s.startsWith("onDrag")) {
                list1.add(s);
                excl.add(s);
            }
        });
        if (!list1.isEmpty()) {
            Section sec = codeCat.addSection("dragdrop", "DragDrop");
            list1.forEach(s -> {
                sec.add(s);
            });
        }

        //
        // MouseDragDrop
        //
        list1.clear();
        list.forEach(s -> {
            if (s.startsWith("onMouseDrag")) {
                list1.add(s);
                excl.add(s);
            }
        });
        if (!list1.isEmpty()) {
            Section sec = codeCat.addSection("mousedragdrop", "Mouse DragDrop");
            list1.forEach(s -> {
                sec.add(s);
            });
        }
        //
        // Key Events
        //
        list1.clear();
        list.forEach(s -> {
            if (s.startsWith("onKey")) {
                list1.add(s);
                excl.add(s);
            }
        });
        
        if (!list1.isEmpty() || list.indexOf("onInputMethodTextChanged") >= 0 ) {
            
            Section sec = codeCat.addSection("keyboard", "Keyboard");
            if ( list.indexOf("onInputMethodTextChanged") >= 0 ) {
                sec.add("onInputMethodTextChanged");
            }
            list1.forEach(s -> {
                sec.add(s);
            });
        }
        //
        // Mouse Events
        //
        list1.clear();
        list.forEach(s -> {
            if (s.startsWith("onMouse") && !s.startsWith("onMouseDrag")) {
                list1.add(s);
                excl.add(s);
            }
        });
        if (!list1.isEmpty() || list.contains("onContextMenuRequested") ) {
            Section sec = codeCat.addSection("mouse", "Mouse");
            sec.add("onContextMenuRequested");
            list1.forEach(s -> {
                sec.add(s);
            });
        }

        //
        // Scroll Events
        //
        list1.clear();
        list.forEach(s -> {
            if (s.startsWith("onScroll")) {
                list1.add(s);
                excl.add(s);
            }
        });
        if (!list1.isEmpty()) {
            Section sec = codeCat.addSection("scroll", "Scroll");
            list1.forEach(s -> {
                sec.add(s);
            });
        }
        //
        // Rotation Events
        //
        list1.clear();
        if (list.indexOf("onRotate") >= 0 || list.indexOf("onRotationStarted") >= 0 || list.indexOf("onRotationFinished") >= 0) {
            Section sec = codeCat.addSection("rotation", "Rotation");

            String s = "onRotate";
            int idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(s);
                excl.add(s);
            }
            s = "onRotationStarted";

            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
            s = "onRotationFinished";

            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
        }

        //
        // Swipe Events
        //
        list1.clear();
        if (list.indexOf("onSwipeLeft") >= 0 || list.indexOf("onSwipeRight") >= 0 || list.indexOf("onSwipeUp") >= 0 || list.indexOf("onSwipeDown") >= 0) {
            Section sec = codeCat.addSection("swipe", "Swipe");

            String s = "onSwipeLeft";
            int idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }

            s = "onSwipeRight";
            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
            s = "onSwipeUp";
            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
            s = "onSwipeDown";
            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }

        }
        //
        // Touch Events
        //
        list1.clear();
        if (list.indexOf("onTouchMoved") >= 0 || list.indexOf("onTouchPressed") >= 0 || list.indexOf("onTouchReleased") >= 0 || list.indexOf("onTouchStationary") >= 0) {
            Section sec = codeCat.addSection("touch", "Touch");

            String s = "onTouchMoved";
            int idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
            s = "onTouchPressed";
            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
            s = "onTouchReleased";
            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
            s = "onTouchStationary";
            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
        }
        //
        // Zoom Events
        //
        list1.clear();
        if (list.indexOf("onZoom") >= 0 || list.indexOf("onZoomStarted") >= 0 || list.indexOf("onZoomFinished") >= 0) {
            Section sec = codeCat.addSection("zoom", "Zoom");

            String s = "onZoom";
            int idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
            s = "onZoomStarted";
            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
            s = "onZoomFinished";
            idx = list.indexOf(s);
            if (idx >= 0) {
                sec.add(list.get(idx));
                excl.add(s);
            }
        }
    }
    protected void addRegionPropertyPaneDescriptor(PropertyPaneCollection ppc) {

        PropertyPaneDescriptor ppd = ppc.getPropertyPaneDescriptor(Node.class).getCopyFor(Region.class);
        System.err.println("addRegionPropertyPaneDescriptor categories.size() = " + ppd.getCategories().size());
        ppc.getPropertyPaneDescriptors().add(ppd);
        ppd.setBeanType(Region.class);
        ppd.setBeanClassName(Region.class.getName());

        Category propCat = ppd.addCategory("properties", "Properties");
        Section sec = propCat.addSection("node","Node");
        sec.addAfter("focusTraversable", "cacheShape","centerShape","scaleShape",
                "opaqueInsets");
      
        sec = propCat.addSection("javafxcss","JavaFX CSS");        
        sec.addAfter("styleClass","stylesheets");
        sec = propCat.addSection("extras","Extras");
        sec.addAfter("depthTest", "insets","padding","border","background");
        
        Category layoutCat = ppd.addCategory("layout", "Layout");
//        sec = layoutCat.addSectionBefore("position","internal","Internal");
//        sec.add("padding","border","background");
        sec = layoutCat.addSectionBefore("position","size","Size");
        sec.add("minWidth","minHeight","prefWidth","prefHeight","maxWidth","maxHeight","width","height");        
        sec.add("snapToPixel");
        
        Category codeCat = ppd.addCategory("code", "Code");
        
        System.err.println("2) addRegionPropertyPaneDescriptor categories.size() = " + ppd.getCategories().size());
        
    }
    public static ObservableList<String> getPropertyNames(PropertyPaneDescriptor ppd) {
        ObservableList<String> list = FXCollections.observableArrayList();
        ppd.getCategories().forEach(cat -> {
            cat.getSections().forEach(sec -> {
                sec.getFXProperties().forEach(pd -> {
                    list.add(pd.getName());
                });
            });
        });
        return list.sorted();
    }

    protected static PropertyPaneDescriptor getPropertyPaneDescriptor(String beanClassName) {
        PropertyPaneDescriptor ppd = null;
        for (PropertyPaneDescriptor d : getPropertyPaneCollection().getPropertyPaneDescriptors()) {
            if (beanClassName.equals(d.getBeanClassName())) {
                ppd = d;
                break;
            }
        }
        return ppd;
    }

    public static void printPropertyPaneDescriptor(String beanClassName) {
        PropertyPaneDescriptor ppd = getPropertyPaneDescriptor(beanClassName);
        ObservableList<String> list = FXCollections.observableArrayList();
        System.err.println("===================================================");

        ppd.getCategories().forEach(cat -> {
            cat.getSections().forEach(sec -> {
                sec.getFXProperties().forEach(pd -> {
                    list.add(cat.getDisplayName() + ":" + sec.getDisplayName() + ":" + pd.getName());
                });
            });
        });
        System.err.println("Bean Class Name: " + beanClassName + "; size = " + list.size());
        List<String> toPrint = list.sorted();
        toPrint.forEach(s -> {
            System.err.println(s);
        });

    }

    public static void printPropertyPaneDescriptor(String beanClassName, boolean byCategories) {
        PropertyPaneDescriptor ppd = getPropertyPaneDescriptor(beanClassName);
        ObservableList<String> list = FXCollections.observableArrayList();
        System.err.println("===================================================");
        System.err.println("Bean Class Name: " + beanClassName + "; size = " + ppd.getCategories().size());
        System.err.println("---------------------------------------------------");

        for (Category cat : ppd.getCategories()) {
            System.err.println("Category id = " + cat.getName() + "; name = " + cat.getDisplayName());
            for (Section sec : cat.getSections()) {
                System.err.println("   Section = " + sec.getName() + "; name = " + sec.getDisplayName());
                sec.getFXProperties().forEach(pd -> {
                    list.add(pd.getName());
                    System.err.println("      " + pd.getName());
                });
            }
        }
        System.err.println("Bean Class Name: " + beanClassName + "; size = " + list.size());

    }

    public static PropertyPaneDescriptor getPropertyPaneDescriptor(Class<?> beanClass) {
        PropertyPaneDescriptor ppd = null;
        for (PropertyPaneDescriptor d : getPropertyPaneCollection().getPropertyPaneDescriptors()) {
            if (beanClass.equals(d.getBeanType())) {
                ppd = d;
                break;
            }
        }
        return ppd;
    }

    protected PropertyPaneCollection loadDefaultDescriptors() {
        FXMLLoader loader = new FXMLLoader();
        PropertyPaneCollection root = null;
        try {
            InputStream is = getClass().getResourceAsStream("/org/vns/javafx/dock/api/designer/resources/DefaultPropertyPaneCollection.fxml");
            root = loader.load(is);
            /*            root.getBeanDescriptors().forEach(d -> {
                String className = d.getType();
                Class clazz;//
                try {
                    clazz = Class.forName(className);
                    register(clazz, d);
                } catch (ClassNotFoundException ex) {
                    System.err.println("ClassNotFoundException EXCEPTION: " + ex.getMessage());
                    Logger.getLogger(PropertyPaneDescriptorRegistry.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
             */
        } catch (IOException ex) {
            Logger.getLogger(PropertyPaneDescriptorRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }

        return root;
    }
    /**
     * The method assumes that an instance of {@link PropertyPaneCollection} 
     * has already been created
     * @param update the instance of class PropertyPaneCollection to be resolved 
     * with the main instance
     */
    protected boolean updateBy(PropertyPaneCollection update) {
        //
        // First convert the source update 
        //
        List<PropertyPaneDescriptor> errors = new ArrayList<>();
        ObservableList<PropertyPaneDescriptor> ppdList = FXCollections.observableArrayList();
        ppdList.addAll(update.getPropertyPaneDescriptors());
        for ( int i=0; i < ppdList.size(); i++ ) {
            PropertyPaneDescriptor ppd = ppdList.get(i);
            if ( ! resolveBeanClass(ppd)) {
                errors.add(ppd);
            }
        }
        if ( ! errors.isEmpty() ) {
            return false;
        }
        //
        //Find the nearest class that already exists, in the sense of inheritance
        //
        ObservableList<PropertyPaneDescriptor> copy = FXCollections.observableArrayList();
        copy.addAll(ppdList);
        ObservableList<PropertyPaneDescriptor> part = FXCollections.observableArrayList();
        while ( ! copy.isEmpty() ) {
            
            fillPart(copy.get(0), part,copy);
            copy.removeAll(part);
            
            for ( int i=0; i < part.size(); i++) {
                System.err.println("part " + part.get(i).getBeanClassName());
                updateBy(part.get(i));
            }
        }
        
        return true;
        
    }
    protected PropertyPaneDescriptor updateBy(PropertyPaneDescriptor update) {
        //
        // Try to find a PropertyPaneDescriptor in the propertyPaneDescriptors 
        // collection of this class such which is assignable from the update.
        //
        //
        PropertyPaneDescriptor retval = null;
        for ( PropertyPaneDescriptor ppd : propertyPaneCollection.getPropertyPaneDescriptors()) {
            if ( ! ppd.getBeanType().isAssignableFrom(update.getBeanType())) {
                continue;
            }
            if ( retval == null || retval.getBeanType().isAssignableFrom(ppd.getBeanType())) {
                retval = ppd;
            }
        }
        if ( retval == null ) {
            addObjectPropertyPaneDescriptor(getPropertyPaneCollection());
        }
        retval = retval.getCopyFor(update.getBeanType());
        retval.setName(update.getName());
        retval.setBeanType(update.getBeanType());
        retval.setBeanClassName(update.getBeanType().getName());
        retval.setBean(update.getBean());
        retval.merge(update.getCategories());
        System.err.println("Class     = " + retval.getBeanType());
        System.err.println("ClassName = " + retval.getBeanClassName());
        for ( Category c : retval.getCategories()) {
            System.err.println("Category name        = " + c.getName());
            System.err.println("Category displayName = " + c.getDisplayName());
        }
        System.err.println("================================");
        System.err.println("ClassName = " + retval.getBeanClassName());
        propertyPaneCollection.getPropertyPaneDescriptors().add(retval);        
        return retval;
    }
    private void fillPart(PropertyPaneDescriptor ppd, ObservableList<PropertyPaneDescriptor> part, ObservableList<PropertyPaneDescriptor> copy) {
        part.clear();
        part.add(ppd);
        for ( PropertyPaneDescriptor p : copy) {
            if ( p == ppd ) {
                continue;
            }
            if ( p.getBeanType().isAssignableFrom(ppd.getBeanType()) || ppd.getBeanType().isAssignableFrom(p.getBeanType()) ) {
                part.add(p);
            }
        }
        if ( part.size() > 1 ) {
            
            part.sort((o1, o2) -> {
                if ( o1 == o2 ) {
                    return 0;
                }
                if ( o1 == null ) {
                    return -1;
                }
                if ( o2 == null ) {
                    return 1;
                }
                Class c1 = o1.getBeanType();
                Class c2 = o2.getBeanType();
                
                int retval = 1;
                if ( c1.equals(c2)) {
                    retval = 0;
                } else if ( c1.isAssignableFrom(c2)  ) {
                    retval = -1;
                }
                return retval; 
            });
        }
    }
    protected boolean resolveBeanClass(PropertyPaneDescriptor ppd) {
        boolean retval = false;
        if ( ppd.getBean() != null ) {
            ppd.setBeanType(ppd.getBean().getClass());
            ppd.setBeanClassName(ppd.getBean().getClass().getName());
        }
        if ( ppd.getBeanType() != null ) {
            retval = true;
        } else if (ppd.getBeanClassName() != null) {
            try {
                Class<?> clazz = Class.forName(ppd.getBeanClassName());
                ppd.setBeanType(clazz);
                retval = true;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PropertyPaneDescriptorRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retval;
    }
    
    private static class SingletonInstance {

        private static final PropertyPaneDescriptorRegistry INSTANCE = new PropertyPaneDescriptorRegistry();
    }

    public Introspection introspect(Class<?> clazz) {
        Introspection retval = new Introspection(clazz);
        long start = System.currentTimeMillis();
        List<String> excludeProps = FXCollections.observableArrayList("properties", "pseudoClassStates", "scene", "parent", "skin",
                "graphic", "contentMenu", "clip", "transforms");

        //Map<String, MethodDescriptor> methodDescr = new HashMap<>();
        //Map<String, PropertyDescriptor> propDescrs = new HashMap<>();
        try {
            BeanInfo info = Introspector.getBeanInfo(clazz);
            MethodDescriptor[] mds = info.getMethodDescriptors();
            for (MethodDescriptor md : mds) {
//                if ( ! md.getMethod().getName().startsWith("get") && ! md.getMethod().getName().startsWith("set")) {
//                    System.err.println("not get not set" + md.getMethod().getName());
//                }

                if (md.getName().endsWith("Property")) {
                    retval.getMethodDescriptors().put(md.getName(), md);
                    System.err.println("fxProperty: " + md.getName());
                }
            }
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if (retval.getMethodDescriptors().containsKey(pd.getName() + "Property")) {
                    Class<?> returnType = pd.getReadMethod().getReturnType();

                    if (hasPropertyEditor(pd.getReadMethod())) {
                        retval.getPropertyDescriptors().put(pd.getName(), pd);

                        if (EventHandler.class.isAssignableFrom(returnType)) {
                            retval.getEventPropertyDescriptors().put(pd.getName(), pd);
                        }
                    }
                } else if (pd.getReadMethod() != null) {

                    Class<?> returnType = pd.getReadMethod().getReturnType();
                    if (hasPropertyEditor(pd.getReadMethod())) {

                        if (ObservableList.class.isAssignableFrom(returnType) || ObservableMap.class.isAssignableFrom(returnType) || ObservableSet.class.isAssignableFrom(returnType)) {
                            System.err.println("MMM = " + pd.getReadMethod().getName());
                            retval.getPropertyDescriptors().put(pd.getName(), pd);
                        }
                    }
                }
            }

            retval.getPropertyDescriptors().forEach((k, v) -> {
                //System.err.println("prop name = " + k);
            });
            /*            for ( MethodDescriptor md : mds) {
                System.err.println("MethodName = " + md.getMethod().getName());
                System.err.println("returnType = " + md.getMethod().getReturnType().getSimpleName());
                System.err.println("toGeneric = " + md.getMethod().toGenericString());
            }
             */
            //PropertyDescriptor pd1 = new PropertyDescriptor("styleClass", btn1.getClass(),"getStyleClass", null);
            //System.err.println("readMethod Name = " + pd1.getReadMethod().getName() + "; name = " + pd1.getName());
            //System.err.println("writeMethod Name = " + pd.getWriteMethod().getName());
        } catch (IntrospectionException ex) {
            System.err.println("INROSPECTION Exception ex = " + ex.getMessage());
        }
        long end = System.currentTimeMillis();
        System.err.println("Interval = " + (end - start));
        System.err.println("propertyDescriptors.size = " + retval.getPropertyDescriptors().size());
        System.err.println("eventPropertyDescriptors.size = " + retval.getEventPropertyDescriptors().size());
        return retval;
    }

    public boolean hasPropertyEditor(Method readMethod) {
        if (ObservableList.class.isAssignableFrom(readMethod.getReturnType())) {
            Class<?> clazz = BeanAdapter.getListItemType(readMethod.getGenericReturnType());
            System.err.println("$$$$$$$$$$$$$$$$ generic observablelist item = " + clazz);
        }
        return true;
    }

    public static class Introspection {

        private final ObservableMap<String, MethodDescriptor> methodDescriptors = FXCollections.observableHashMap();
        private final ObservableMap<String, PropertyDescriptor> propertyDescriptors = FXCollections.observableHashMap();
        private final ObservableMap<String, PropertyDescriptor> eventPropertyDescriptors = FXCollections.observableHashMap();

        private final Class<?> beanClass;

        public Introspection(Class<?> beanClass) {
            this.beanClass = beanClass;
        }

        public ObservableMap<String, MethodDescriptor> getMethodDescriptors() {
            return methodDescriptors;
        }

        public ObservableMap<String, PropertyDescriptor> getPropertyDescriptors() {
            return propertyDescriptors;
        }

        public ObservableMap<String, PropertyDescriptor> getEventPropertyDescriptors() {
            return eventPropertyDescriptors;
        }

        public Class<?> getBeanClass() {
            return beanClass;
        }

    }
}
