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
import java.util.List;
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
import org.vns.javafx.dock.api.designer.PropertyEditorBeanPane;

/**
 *
 * @author Valery Shyshkin
 */
public class PropertyPaneModelRegistry {

    private PropertyPaneModel propertyPaneModel;
    private final ObservableMap<Class<?>, Introspection> introspection = FXCollections.observableHashMap();

    public static PropertyPaneModelRegistry getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public static PropertyPaneModel getPropertyPaneModel() {
        PropertyPaneModel retval = getInstance().propertyPaneModel;
        if (retval == null) {
            getInstance().createInternalDescriptors();
            PropertyPaneModel loaded = getInstance().loadDefaultDescriptors();
            getInstance().updateBy(loaded);
            retval = getInstance().propertyPaneModel;
        }
        return retval;
    }

    private final ObservableMap<Class<?>,BeanModel> beanModels = FXCollections.observableHashMap();
    private final ObservableMap<Object,PropertyEditorBeanPane> beanPanes = FXCollections.observableHashMap();
    
    protected void createInternalDescriptors() {
        propertyPaneModel = new PropertyPaneModel();

        addObjectBeanModel(propertyPaneModel);
        addNodeBeanModel(propertyPaneModel);
        addRegionBeanModel(propertyPaneModel);

    }

    public ObservableMap<Class<?>,BeanModel> getBeanModels() {
        return beanModels;
    }
    public ObservableMap<Object,PropertyEditorBeanPane> getBeanPanes() {
        return beanPanes;
    }

    protected void addObjectBeanModel(PropertyPaneModel paneModel) {
        BeanModel beanModel = new BeanModel();
        beanModel.setBeanType(Object.class);
        beanModel.setBeanClassName(Object.class.getName());
        Category cat = new Category();
        cat.setName("properties");
        cat.setDisplayName("Properties");
        beanModel.getItems().add(cat);
        paneModel.getBeanModels().add(beanModel);

    }

    protected void addNodeBeanModel(PropertyPaneModel paneModel) {
        BeanModel beanModel = new BeanModel();
      

        beanModel.setBeanType(Node.class);
        beanModel.setBeanClassName(Node.class.getName());
        paneModel.getBeanModels().add(beanModel);
        
        Category propCat = beanModel.addCategory("properties", "Properties");
        Category layoutCat = beanModel.addCategory("layout", "Layout");
        Category codeCat = beanModel.addCategory("code", "Code");
        //ppd.getCategories().addAll(propCat,layoutCat,code);
        //
        // "Properties" category
        //

        // --- "Node" Section
        Section nodeSec = ModelUtil.addSection(propCat, "node", "Node");
        ModelUtil.add(nodeSec, "disable", "disabled", "opacity", "nodeOrientation", "visible", "focusTraversable", "focused", "cursor", "effect", "pressed", "managed");

        nodeSec = ModelUtil.addSection(propCat, "javafxcss", "JavaFX CSS");
        ModelUtil.add(nodeSec, "style", "styleClass", "id");
        // --- "Extras" Section 
        Section propExtrasSec = ModelUtil.addSection(propCat, "extras", "Extras");
        ModelUtil.add(propExtrasSec, "blendMode", "cache", "cacheHint", "depthTest", "mouseTransparent", "pickOnBounds");

        // --- "Accessibility" Section
        Section accessSec = ModelUtil.addSection(propCat, "accessibility", "Accessibility");
        ModelUtil.add(accessSec, "accessibleText", "accessibleHelp", "accessibleRole", "accessibleRoleDescription");

        //
        // "Layout" category
        //
        // --- "Position" Section
        Section positionSec = ModelUtil.addSection(layoutCat, "position", "Position");
        ModelUtil.add(positionSec, "layoutX", "layoutY");
        // --- "Transforms" Section

        Section transformsSec = ModelUtil.addSection(layoutCat, "transforms", "Transforms");
        ModelUtil.add(transformsSec, "rotate", "rotationAxis", "scaleX", "scaleY", "scaleZ", "translateX", "translateY", "translateZ");
        // --- "Bounds" Section        
        Section boundsSec = ModelUtil.addSection(layoutCat, "bounds", "Bounds");
        ModelUtil.add(boundsSec, "layoutBounds", "boundsInLocal", "boundsInParent");
        // --- "Extras" Section        
        Section extrasSec = ModelUtil.addSection(layoutCat, "extras", "extras");
        ModelUtil.add(extrasSec, "effectiveNodeOrientation");

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
            Section sec = ModelUtil.addSection(codeCat, "main", "Main");
            ModelUtil.add(sec, "onAction");
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
            Section sec = ModelUtil.addSection(codeCat, "dragdrop", "DragDrop");
            list1.forEach(s -> {
                ModelUtil.add(sec, s);
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
            Section sec = ModelUtil.addSection(codeCat, "mousedragdrop", "Mouse DragDrop");
            list1.forEach(s -> {
                ModelUtil.add(sec, s);
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

        if (!list1.isEmpty() || list.indexOf("onInputMethodTextChanged") >= 0) {

            Section sec = ModelUtil.addSection(codeCat, "keyboard", "Keyboard");
            if (list.indexOf("onInputMethodTextChanged") >= 0) {
                ModelUtil.add(sec, "onInputMethodTextChanged");
            }
            list1.forEach(s -> {
                ModelUtil.add(sec, s);
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
        if (!list1.isEmpty() || list.contains("onContextMenuRequested")) {
            Section sec = ModelUtil.addSection(codeCat, "mouse", "Mouse");
            ModelUtil.add(sec, "onContextMenuRequested");
            list1.forEach(s -> {
                ModelUtil.add(sec, s);
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
            Section sec = ModelUtil.addSection(codeCat, "scroll", "Scroll");
            list1.forEach(s -> {
                ModelUtil.add(sec, s);
            });
        }
        //
        // Rotation Events
        //
        list1.clear();
        if (list.indexOf("onRotate") >= 0 || list.indexOf("onRotationStarted") >= 0 || list.indexOf("onRotationFinished") >= 0) {
            Section sec = ModelUtil.addSection(codeCat, "rotation", "Rotation");

            String s = "onRotate";
            int idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, s);
                excl.add(s);
            }
            s = "onRotationStarted";

            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
            s = "onRotationFinished";

            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
        }

        //
        // Swipe Events
        //
        list1.clear();
        if (list.indexOf("onSwipeLeft") >= 0 || list.indexOf("onSwipeRight") >= 0 || list.indexOf("onSwipeUp") >= 0 || list.indexOf("onSwipeDown") >= 0) {
            Section sec = ModelUtil.addSection(codeCat, "swipe", "Swipe");

            String s = "onSwipeLeft";
            int idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }

            s = "onSwipeRight";
            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
            s = "onSwipeUp";
            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
            s = "onSwipeDown";
            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }

        }
        //
        // Touch Events
        //
        list1.clear();
        if (list.indexOf("onTouchMoved") >= 0 || list.indexOf("onTouchPressed") >= 0 || list.indexOf("onTouchReleased") >= 0 || list.indexOf("onTouchStationary") >= 0) {
            Section sec = ModelUtil.addSection(codeCat, "touch", "Touch");

            String s = "onTouchMoved";
            int idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
            s = "onTouchPressed";
            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
            s = "onTouchReleased";
            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
            s = "onTouchStationary";
            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
        }
        //
        // Zoom Events
        //
        list1.clear();
        if (list.indexOf("onZoom") >= 0 || list.indexOf("onZoomStarted") >= 0 || list.indexOf("onZoomFinished") >= 0) {
            Section sec = ModelUtil.addSection(codeCat, "zoom", "Zoom");

            String s = "onZoom";
            int idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
            s = "onZoomStarted";
            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
            s = "onZoomFinished";
            idx = list.indexOf(s);
            if (idx >= 0) {
                ModelUtil.add(sec, list.get(idx));
                excl.add(s);
            }
        }
    }

    protected void addRegionBeanModel(PropertyPaneModel paneModel) {

        BeanModel beanModel = paneModel.getBeanModel(Node.class.getName()).getCopyFor(Region.class);
        System.err.println("addRegionPropertyPaneDescriptor categories.size() = " + beanModel.getItems().size());
        //paneModel.getBeanModels().add(beanModel);
        beanModel.setBeanType(Region.class);
        beanModel.setBeanClassName(Region.class.getName());
        paneModel.getBeanModels().add(beanModel);
        
        Category propCat = beanModel.addCategory("properties", "Properties");
        Section sec = ModelUtil.addSection(propCat, "node", "Node");
        ModelUtil.addAfter(sec, "focusTraversable", "cacheShape", "centerShape", "scaleShape",
                "opaqueInsets");

        sec = ModelUtil.addSection(propCat, "javafxcss", "JavaFX CSS");
        ModelUtil.addAfter(sec, "styleClass", "stylesheets");
        sec = ModelUtil.addSection(propCat, "extras", "Extras");
        ModelUtil.addAfter(sec, "depthTest", "insets", "border", "background");

        Category layoutCat = beanModel.addCategory("layout", "Layout");
//        sec = layoutCat.addSectionBefore("position","internal","Internal");
//        sec.add("padding","border","background");
        
        sec = ModelUtil.addSectionBefore(layoutCat, "position", "size", "Size");
        ModelUtil.add(sec, "minWidth", "minHeight", "prefWidth", "prefHeight", "maxWidth", "maxHeight", "width", "height");
        ModelUtil.add(sec, "snapToPixel");

        sec = ModelUtil.addSectionBefore(layoutCat, "size", "internal", "Internal");
        ModelUtil.add(sec, "padding");
        
        
        Category codeCat = beanModel.addCategory("code", "Code");

        System.err.println("2) addRegionPropertyPaneDescriptor categories.size() = " + beanModel.getItems().size());

    }

    public static ObservableList<String> getPropertyNames(BeanModel beanModel) {
        ObservableList<String> list = FXCollections.observableArrayList();
        beanModel.getItems().forEach(cat -> {
            cat.getItems().forEach(sec -> {
                sec.getItems().forEach(pd -> {
                    list.add(pd.getName());
                });
            });
        });
        return list.sorted();
    }

    protected static BeanModel getBeanModel(String beanClassName) {
        BeanModel ppd = null;
        for (BeanModel d : getPropertyPaneModel().getBeanModels()) {
            if (beanClassName.equals(d.getBeanClassName())) {
                ppd = d;
                break;
            }
        }
        return ppd;
    }

    public static void printBeanModel(String beanClassName) {
        BeanModel ppd = PropertyPaneModelRegistry.getBeanModel(beanClassName);
        ObservableList<String> list = FXCollections.observableArrayList();
        System.err.println("===================================================");

        ppd.getItems().forEach(cat -> {
            cat.getItems().forEach(sec -> {
                sec.getItems().forEach(pd -> {
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

    public static void printBeanModel(String beanClassName, boolean byCategories) {
        BeanModel ppd = PropertyPaneModelRegistry.getBeanModel(beanClassName);
        ObservableList<String> list = FXCollections.observableArrayList();
        System.err.println("===================================================");
        System.err.println("Bean Class Name: " + beanClassName + "; size = " + ppd.getItems().size());
        System.err.println("---------------------------------------------------");

        for (Category cat : ppd.getItems()) {
            System.err.println("Category id = " + cat.getName() + "; name = " + cat.getDisplayName());
            for (Section sec : cat.getItems()) {
                System.err.println("   Section = " + sec.getName() + "; name = " + sec.getDisplayName());
                sec.getItems().forEach(pd -> {
                    list.add(pd.getName());
                    System.err.println("      " + pd.getName());
                });
            }
        }
        System.err.println("Bean Class Name: " + beanClassName + "; size = " + list.size());

    }

    public static BeanModel getBeanModel(Class<?> beanClass) {
        BeanModel beanModel = null;
        for (BeanModel bm : getPropertyPaneModel().getBeanModels()) {
            if (beanClass.equals(bm.getBeanType())) {
                beanModel = bm;
                break;
            }
        }
        return beanModel;
        
    }

    protected PropertyPaneModel loadDefaultDescriptors() {
        FXMLLoader loader = new FXMLLoader();
        PropertyPaneModel root = null;
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
            Logger.getLogger(PropertyPaneModelRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }

        return root;
    }

    /**
     * The method assumes that an instance of {@link PropertyPaneModel} has
     * already been created
     *
     * @param update the instance of class PropertyPaneModel to be resolved with
     * the internal instance
     */
    protected boolean updateBy(PropertyPaneModel update) {
        //
        // First convert the source update 
        //
        List<BeanModel> errors = new ArrayList<>();
        ObservableList<BeanModel> updateCopy = FXCollections.observableArrayList();
        //updateCopy.addAll(update.getBeanModels().values());
        updateCopy.addAll(update.getBeanModels());
        for (int i = 0; i < updateCopy.size(); i++) {
            BeanModel ppd = updateCopy.get(i);
            if (!resolveBeanClass(ppd)) {
                errors.add(ppd);
            }
        }
        if (!errors.isEmpty()) {
            return false;
        }
        //
        //Find the nearest class that already exists, in the sense of inheritance
        //
        //ObservableList<BeanModel> copy = FXCollections.observableArrayList();
        //copy.addAll(updateCopy);
        ObservableList<BeanModel> part = FXCollections.observableArrayList();
        while (!updateCopy.isEmpty()) {

            fillPart(updateCopy.get(0), part, updateCopy);
            updateCopy.removeAll(part);

            for (int i = 0; i < part.size(); i++) {
                if ( "javafx.scene.layout.VBox".equals(part.get(i).getBeanClassName()) ) {
                    System.err.println("part " + part.get(i).getBeanClassName());
                }
                
                updateBy(part.get(i));
            }
        }

        return true;

    }

    private void fillPart(BeanModel beanModel, ObservableList<BeanModel> part, ObservableList<BeanModel> copy) {
        part.clear();
        part.add(beanModel);
        for (BeanModel p : copy) {
            if (p == beanModel) {
                continue;
            }
            if (p.getBeanType().isAssignableFrom(beanModel.getBeanType()) || beanModel.getBeanType().isAssignableFrom(p.getBeanType())) {
                part.add(p);
            }
        }
        if (part.size() > 1) {

            part.sort((o1, o2) -> {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                Class c1 = o1.getBeanType();
                Class c2 = o2.getBeanType();

                int retval = 1;
                if (c1.equals(c2)) {
                    retval = 0;
                } else if (c1.isAssignableFrom(c2)) {
                    retval = -1;
                }
                return retval;
            });
        }
    }

    protected BeanModel updateBy(BeanModel update) {
        //
        // Try to find a BeanModel in the propertyPaneModel
        // collection of this class such which is assignable from the update.
        //
        //
        BeanModel retval = null;
        for (BeanModel mod : propertyPaneModel.getBeanModels()) {
            if (!mod.getBeanType().isAssignableFrom(update.getBeanType())) {
                continue;
            }
            if (retval == null || retval.getBeanType().isAssignableFrom(mod.getBeanType())) {
                retval = mod;
            }
        }

        retval = retval.getCopyFor(update.getBeanType());
        retval.setName(update.getName());
        retval.setBeanType(update.getBeanType());
        retval.setBeanClassName(update.getBeanType().getName());
        retval.setBean(update.getBean());
        retval.merge(update.getItems());
        
        propertyPaneModel.getBeanModels().add(retval);
        
        return retval;
    }

    protected boolean resolveBeanClass(BeanModel beanModel) {
        boolean retval = false;
        if (beanModel.getBean() != null) {
            beanModel.setBeanType(beanModel.getBean().getClass());
            beanModel.setBeanClassName(beanModel.getBean().getClass().getName());
        }
        if (beanModel.getBeanType() != null) {
            retval = true;
        } else if (beanModel.getBeanClassName() != null) {
            try {
                Class<?> clazz = Class.forName(beanModel.getBeanClassName());
                beanModel.setBeanType(clazz);
                retval = true;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PropertyPaneModelRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retval;
    }

    private static class SingletonInstance {

        private static final PropertyPaneModelRegistry INSTANCE = new PropertyPaneModelRegistry();
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
