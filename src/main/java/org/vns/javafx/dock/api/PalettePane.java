/*
 * Copyright 2018 Your Organisation.
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

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import static org.vns.javafx.dock.api.PalettePane.NodePolicy.BOTH;
import static org.vns.javafx.dock.api.PalettePane.NodePolicy.DOCKABLE;
import static org.vns.javafx.dock.api.PalettePane.NodePolicy.DOCKLAYOUT;
import org.vns.javafx.dock.api.dragging.DefaultMouseDragHandler;
import org.vns.javafx.dock.api.dragging.DragManager;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.dragging.view.FloatView;

/**
 * Provides a set of {@code javafx.scene.control.Label } nodes, each of which
 * corresponds to the object of type {@code java.lang.Class } of the object
 * provided through the JavaFX API. This class can be one of the heirs of the
 * Node class or a different type, for example, a Tab class. When the
 * drag-and-drop operation is initiated, the palette element where the mouse is
 * located is determined, the object of the class is created to which this
 * element corresponds and the created object is dragged and becomes available
 * when executing the drop (or mouse released).
 * <p>
 * The class allows you to create a palette from scratch or use the previously
 * created palette of categories and their elements. If you want to create a
 * palette from scratch, use the constructor without parameters. If you want to
 * use the existing default palette, use a constructor with a {@code boolean}
 * parameter, indicating the value of the parameter to be {@code true}.
 * </p>
 * <p>
 * When an object of the class is created it registers as {@code Dockable }
 * object.
 *
 * @author Valery Shyshkin Shyskin
 */
public class PalettePane extends Control {

    public static final String PALETTE_PANE = "palette-pane";

    public static enum NodePolicy {
        DOCKABLE,
        DOCKLAYOUT,
        BOTH,
        NONE
    }

    private PaletteModel model;

    private final ObjectProperty<ScrollPane.ScrollBarPolicy> scrollVBarPolicy = new SimpleObjectProperty<>(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    private final ObjectProperty<Node> dragNode = new SimpleObjectProperty<>();

    private BooleanProperty animated = new SimpleBooleanProperty(true);

    private ObjectProperty<NodePolicy> producedNodePolicy = new SimpleObjectProperty(BOTH);

    /**
     * Creates an instance of the class. The created palette contains an empty
     * list of categories.
     */
    public PalettePane() {
        this(false);
    }

    /**
     * Creates an instance of the class. If the given parameter is {@code false
     * } then the created palette contains an empty list of categories.
     * Otherwise a default categories and there items are created.
     *
     * @param createDefault specifies whether the new object has a default
     * items.
     */
    public PalettePane(boolean createDefault) {
        initModel(createDefault);
        getStyleClass().add(PALETTE_PANE);
    }

    private void initModel(boolean createDefault) {
        if (createDefault) {
            model = createDefaultPaleteModel();
        } else {
            model = new PaletteModel(this);
        }
        DockRegistry.makeDockable(this).getContext().setDragNode(null);
    }
    /**
     * Sets the custom customizer of the palette paletteModel.
     *
     * @param customizer the custom customizer
     */
    public void setDragValueCustomizer(DragValueCustomizer customizer) {
        getModel().setCustomizer(customizer);
    }
    public ObjectProperty<NodePolicy> producedNodePolicy() {
        return producedNodePolicy;
    }

    public void setProducedNodePolicy(NodePolicy policy) {
        this.producedNodePolicy.set(policy);
    }

    public NodePolicy getProducedNodePolicy() {
        return this.producedNodePolicy.get();
    }

    /**
     * The animated state of the {@code TitledPane}. The {@code TitledPane} is
     * used to visually represent a category item.
     *
     * @return true the titled bar is animated.
     */
    public BooleanProperty animatedProperty() {
        return animated;
    }

    /**
     * Sets the value of The animated state of the {@code TitledPane}. The
     * {@code TitledPane} is used to visually represent a category item.
     *
     * @param animated the boolean value to be set
     */
    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    /**
     * Returns the value of The animated state of the {@code TitledPane}. The
     * {@code TitledPane} is used to visually represent a category item.
     *
     * @return true the titled bar is animated.
     */
    public boolean isAnimated() {
        return this.animated.get();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PalettePaneSkin(this);
    }

    /**
     * Returns the paletteModel of the palette.
     *
     * @return the object of type {@link PaletteModel}
     */
    public PaletteModel getModel() {
        return model;
    }

    /**
     * When you create a palette, the created palette is registered as
     * {@code Dockable}. This assigns null as the dragNode property value.The
     * developer can create and assign an object of type {@code Node} as the
     * value of the {@code dragNode} property. This node is located at the top
     * of the palette.
     *
     * @param node the object to become a drag node
     */
    public void setDragNode(Node node) {
        dragNode.set(node);
        if (Dockable.of(this) != null) {
            Dockable.of(this).getContext().setDragNode(node);
        }
    }

    /**
     * Returns a node which serves as a drag node
     *
     * @return the node used as a drag node
     */
    public Node getDragNode() {
        return dragNode.get();
    }

    /**
     * The drag node of the palette
     *
     * @return an object of type {@code ObjectProperty<Node> }
     */
    public ObjectProperty<Node> dragNodeProperty() {
        return dragNode;
    }

    /**
     * Sets the policy for showing the vertical scroll bar.
     *
     * @param value the value of the scroll bar policy
     */
    public void setScrollPaneVbarPolicy(ScrollPane.ScrollBarPolicy value) {
        this.scrollVBarPolicy.set(value);
    }

    /**
     * Gets the value of the property vbarPolicy of the vertical scroll bar.
     *
     * @return the value of the property vbarPolicy of the vertical scroll bar.
     */
    public ScrollPane.ScrollBarPolicy getScrollPaneVbarPolicy() {
        return scrollVBarPolicy.get();
    }

    /**
     * Specifies the policy for showing the vertical scroll bar.
     *
     * @return value the value of the scroll bar policy
     */
    public ObjectProperty<ScrollPane.ScrollBarPolicy> scrollPaneVbarPolicy() {
        return scrollVBarPolicy;
    }

    protected PaletteModel createDefaultPaleteModel() {
        PaletteModel paletteModel = new PaletteModel(this);

        Label lb = new Label("Containers");
        PaletteCategory pc = paletteModel.addCategory("containers", lb);
        lb.getStyleClass().add("tree-item-font-bold");

        lb = new Label("Accordion");
        pc.addItem(lb, Accordion.class);
        lb.getStyleClass().add("tree-item-node-accordion");
        lb.applyCss();

        lb = new Label("AnchorPane");
        pc.addItem(lb, AnchorPane.class);
        lb.getStyleClass().add("tree-item-node-anchorpane");
        lb.applyCss();

        lb = new Label("BorderPane");
        pc.addItem(lb, BorderPane.class, node -> { ((Pane)node).setPrefSize(100, 100); ((Pane)node).setMinHeight(100);System.err.println("BORDERPANE +++++++++++++");});
        lb.getStyleClass().add("tree-item-node-borderpane");
        lb.applyCss();

        lb = new Label("FlowPane");
        pc.addItem(lb, FlowPane.class);
        lb.getStyleClass().add("tree-item-node-flowpane");
        lb.applyCss();

        lb = new Label("GridPane");
        pc.addItem(lb, GridPane.class);
        lb.getStyleClass().add("tree-item-node-gridpane");
        lb.applyCss();

        lb = new Label("HBox");
        pc.addItem(lb, HBox.class);
        lb.getStyleClass().add("tree-item-node-hbox");
        lb.applyCss();

        lb = new Label("VBox");
        lb.getStyleClass().add("tree-item-node-vbox");
        lb.applyCss();
        pc.addItem(lb, VBox.class);

        lb = new Label("Pane");
        pc.addItem(lb, Pane.class);
        lb.getStyleClass().add("tree-item-node-pane");
        lb.applyCss();

        lb = new Label("ScrollPane");
        pc.addItem(lb, ScrollPane.class);
        lb.getStyleClass().add("tree-item-node-scrollpane");
        lb.applyCss();

        lb = new Label("SplitPane");
        pc.addItem(lb, SplitPane.class);
        lb.getStyleClass().add("tree-item-node-splitpane");
        lb.applyCss();

        lb = new Label("StackPane");
        pc.addItem(lb, StackPane.class);
        lb.getStyleClass().add("tree-item-node-stackpane");
        lb.applyCss();

        lb = new Label("Tab");
        lb.getStyleClass().add("tree-item-node-tab");
        lb.applyCss();
        pc.addItem(lb, Tab.class);

        lb = new Label("TabPane");
        pc.addItem(lb, TabPane.class);
        lb.getStyleClass().add("tree-item-node-tabpane");
        lb.applyCss();

        lb = new Label("TilePane");
        pc.addItem(lb, TilePane.class);
        lb.getStyleClass().add("tree-item-node-tilepane");
        lb.applyCss();

        lb = new Label("TitledPane");
        pc.addItem(lb, TitledPane.class);
        lb.getStyleClass().add("tree-item-node-titledpane");
        lb.applyCss();

        //
        // Controls
        //
        lb = new Label("Controls");
        lb.getStyleClass().add("tree-item-font-bold");

        pc = paletteModel.addCategory("controls", lb);
        lb.applyCss();

        lb = new Label("Button");
        pc.addItem(lb, Button.class);
        lb.getStyleClass().add("tree-item-node-button");

        lb = new Label("CheckBox");
        PaletteItem item = pc.addItem(lb, CheckBox.class, v -> {
            ((CheckBox) v).setText("CheckBox");
        });
        //
        // Prevents MOUSE_RELEASED
        //
        item.setEventDispatcher(new MouseEventDispatcher());

        lb.getStyleClass().add("tree-item-node-checkbox");

        lb = new Label("ChoiceBox");
        pc.addItem(lb, ChoiceBox.class);
        lb.getStyleClass().add("tree-item-node-choicebox");

        lb = new Label("ComboBox");
        item = pc.addItem(lb, ComboBox.class);
        lb.getStyleClass().add("tree-item-node-combobox");
        item.setEventDispatcher(new MouseEventDispatcher( n -> { return ((ComboBox) n).getItems().isEmpty();} ));
        

        lb = new Label("ListView");
        pc.addItem(lb, ListView.class);
        lb.getStyleClass().add("tree-item-node-listview");

        lb = new Label("Label");
        pc.addItem(lb, Label.class);
        lb.getStyleClass().add("tree-item-node-label");

        lb = new Label("TextArea");
        pc.addItem(lb, TextArea.class);
        lb.getStyleClass().add("tree-item-node-textarea");
        lb.applyCss();

        lb = new Label("TextField");
        pc.addItem(lb, TextField.class);
        lb.getStyleClass().add("tree-item-node-textfield");
        lb.applyCss();

        //
        // Spapes
        //
        lb = new Label("Shapes");
        lb.getStyleClass().add("tree-item-font-bold");

        pc = paletteModel.addCategory("shapes", lb);
        lb.applyCss();

        lb = new Label("Arc");
        pc.addItem(lb, Arc.class);
        lb.getStyleClass().add("tree-item-node-rectangle");

        lb = new Label("Rectangle");
        pc.addItem(lb, Rectangle.class, v -> {
            Rectangle r = (Rectangle) v;
            r.setWidth(75);
            r.setHeight(20);
            r.setFill(Color.WHITE);
        });
        lb.getStyleClass().add("tree-item-node-rectangle");
        lb.applyCss();

        lb = new Label("Text");
        pc.addItem(lb, Text.class, v -> {
            ((Text) v).setText("Text");
        });
        lb.getStyleClass().add("tree-item-node-text");

        return paletteModel;

    }

    /**
     * Defines the base type for the elements in the component palette.
     */
    public static class PaletteItem {

        private final ObjectProperty<Label> label = new SimpleObjectProperty<>();
        private final Class<?> valueClass;
        private PaletteModel model;
        private DragValueCustomizer customizer;
        private final ObjectProperty<NodePolicy> producedNodePolicy = new SimpleObjectProperty(BOTH);
        private final ObjectProperty<Class<?>> layoutContextClass = new SimpleObjectProperty<>();

        private final ObjectProperty<PaletteEventDispatcher> eventDispatcher = new SimpleObjectProperty<>();

        /**
         * Create an instance of the class for the specified parameters.
         *
         * @param model the object of type PaletteModel
         * @param lb this object of type {@code Label} which is used to visually
         * represent an item of the palette.
         *
         * @param clazz the object of type {@code java.lang.Class} of the
         * objects this element represents
         */
        public PaletteItem(PaletteModel model, Label lb, Class<?> clazz) {
            this(model, lb, clazz, null);
        }

        /**
         * Create an instance of the class for the specified parameters.
         *
         * @param model the object of type PaletteModel
         * @param lb this object of type {@code Label} which is used to visually
         * represent an item of the palette.
         *
         * @param clazz the object of type {@code java.lang.Class } of the
         * objects this element represents
         * @param customizer modifies an object created when the palette item is
         * dragged. For example, for objects of type {@code Labeled }, its
         * {@code text} property is assigned a simple class name with the first
         * character converted to lower case ("label" for Label, "button" for
         * Button, etc.).
         *
         */
        public PaletteItem(PaletteModel model, Label lb, Class<?> clazz, DragValueCustomizer customizer) {
            label.set(lb);
            valueClass = clazz;
            this.model = model;
            this.customizer = customizer;
            init();
        }

        private void init() {
            if (valueClass != null) {
                DockRegistry.makeDockable(getLabel());
                DockableContext context = Dockable.of(getLabel()).getContext();
                PaletteItemMouseDragHandler handler = new PaletteItemMouseDragHandler(context, this);
                Dockable.of(getLabel())
                        .getContext()
                        .getLookup()
                        .putUnique(MouseDragHandler.class, handler);
            }
        }

        public ObjectProperty<PaletteEventDispatcher> eventDispatcherProperty() {
            return eventDispatcher;
        }

        public PaletteEventDispatcher getEventDispatcher() {
            return eventDispatcher.get();
        }

        public void setEventDispatcher(PaletteEventDispatcher eventDispatcher) {
            this.eventDispatcher.set(eventDispatcher);
        }

        public ObjectProperty<NodePolicy> producedNodePolicy() {
            return producedNodePolicy;
        }

        public void setProducedNodePolicy(NodePolicy policy) {
            this.producedNodePolicy.set(policy);
        }

        public NodePolicy getProducedNodePolicy() {
            return this.producedNodePolicy.get();
        }

        public ObjectProperty<Class<?>> layoutContextClassProperty() {
            return layoutContextClass;
        }

        public Class<?> getLayoutContextClass() {
            return layoutContextClass.get();
        }

        public void setLayoutContextClass(Class<?> layoutContextClass) {
            this.layoutContextClass.set(layoutContextClass);
        }

        /**
         * Returns the paletteModel of the palette.
         *
         * @return the object of type PaletteModel which contains a set of
         * categories of items/
         */
        public PaletteModel getModel() {
            return model;
        }

        /**
         * Return the object of type {@code java.lang.Class } of the objects
         * this element represents
         *
         * @return the object of type {@code java.lang.Class }
         */
        public Class<?> getValueClass() {
            return valueClass;
        }

        /**
         * Returns the label used to visually represent this element.
         *
         * @return the object of type {@code Label } used to visually represent
         * this element.
         */
        public Label getLabel() {
            return label.get();
        }

        /**
         * Returns the object which serves to modify an object created when the
         * palette item is dragged. For example, for objects of type {@code Labeled
         * }, its {@code text} property is assigned a simple class name with the
         * first character converted to lower case ("label" for Label, "button"
         * for Button, etc.).
         *
         * @return the object to customize the value
         */
        public DragValueCustomizer getCustomizer() {
            return customizer;
        }

        /**
         *
         * Sets the object which serves to modify an object created when the
         * palette item is dragged. For example, for objects of type {@code Labeled
         * }, its {@code text} property is assigned a simple class name with the
         * first character converted to lower case ("label" for Label, "button"
         * for Button, etc.).
         *
         * @param customizer the object to be set
         */
        public void setCustomizer(DragValueCustomizer customizer) {
            this.customizer = customizer;
        }

        /**
         * Sets the label used to visually represent this element. this element.
         *
         * @param label the object used to visually represent this element. this
         * element.
         */
        public void setLabel(Label label) {
            this.label.set(label);
        }

        public ObservableValue<Label> labelProperty() {
            return label;
        }

    }

    /**
     * The object of the class represents an category of the
     * {@code PaletteModel}
     *
     */
    public static class PaletteCategory extends PaletteItem {

        private final StringProperty id = new SimpleStringProperty();
        private final ObservableList<PaletteItem> items = FXCollections.observableArrayList();
        private final ObjectProperty<TilePane> pane = new SimpleObjectProperty<>();

        /**
         * Creates an instance of the class for the given parameters.
         *
         * @param model the paletteModel of the palette
         * @param id the unique identifier for all categories in the
         * paletteModel of the palette.
         * @param lb the object of type {@code Label } to visually represent the
         * category
         */
        public PaletteCategory(PaletteModel model, String id, Label lb) {
            super(model, lb, null);
            this.id.set(id);
            init();
        }

        private void init() {
            TilePane tp = new TilePane();
            tp.getStyleClass().add("tile-pane");

            tp.setHgap(10);
            tp.setVgap(5);
            tp.setPrefColumns(1);
            tp.setTileAlignment(Pos.TOP_LEFT);
            setPane(tp);
        }

        /**
         * Returns a list of items of this category.
         *
         * @return a list of items of the category
         */
        public ObservableList<PaletteItem> getItems() {
            return items;
        }

        protected void itemsChanged(ListChangeListener.Change<? extends PaletteItem> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends PaletteItem> list = change.getRemoved();
                }
                if (change.wasAdded()) {
                    List<? extends PaletteItem> list = change.getAddedSubList();
                }
            }//while
        }

        /**
         * Returns the identifier of the category
         *
         * @return the identifier of the category
         */
        public String getId() {
            return id.get();
        }

        /**
         * Sets the identifier of the category. The value must be unique for all
         * categories
         *
         * @param id the identifier to be set
         */
        public void setId(String id) {
            this.id.set(id);
        }

        protected TilePane getPane() {
            return pane.get();
        }

        protected void setPane(TilePane pane) {
            this.pane.set(pane);
        }

        protected ObjectProperty<TilePane> paneProperty() {
            return pane;
        }

        /**
         * Creates and a new item of type {@code PaletteItem} and add it to the
         * end of the list of items.
         *
         * @param label used to visually represent the item in the palette
         * @param clazz the object of type {@code java.lang.Class } of the
         * objects this element represents
         * @return a newly created instance
         */
        public PaletteItem addItem(Label label, Class<?> clazz) {
            return addItem(items.size(), label, clazz);
        }

        /**
         *
         * Creates and a new item of type {@code PaletteItem} and add it to the
         * specified index of the list of items.
         *
         * @param idx the index the new item must be placed
         * @param label used to visually represent the item in the palette
         * @param clazz the object of type {@code java.lang.Class } of the
         * objects this element represents
         * @return a newly created instance
         */
        public PaletteItem addItem(int idx, Label label, Class<?> clazz) {
            if (getModel().containsItem(clazz)) {
                throw new IllegalArgumentException("A PaletteCategory alredy contains a PaletteItem with the specified valueClassClass(valueClass=" + clazz.getName() + ")");
            }
            PaletteItem item = new PaletteItem(getModel(), label, clazz);
            items.add(item);
            getPane().getChildren().add(idx, item.getLabel());

            return item;
        }

        /**
         * Creates and a new item of type {@code PaletteItem} and add it to the
         * end of the list of items.
         *
         * @param label used to visually represent the item in the palette
         * @param clazz the object of type {@code java.lang.Class } of the
         * objects this element represents
         * @param customizer the value customizer
         * @return a newly created instance
         */
        public PaletteItem addItem(Label label, Class<?> clazz, DragValueCustomizer customizer) {
            return addItem(items.size(), label, clazz, customizer);
        }

        /**
         * Creates and a new item of type {@code PaletteItem} and add it to the
         * specified index of the list of items.
         *
         * @param idx the index the new item must be placed
         * @param label used to visually represent the item in the palette
         * @param clazz the object of type {@code java.lang.Class } of the
         * objects this element represents
         * @param customizer the value customizer
         * @return a newly created instance
         */
        public PaletteItem addItem(int idx, Label label, Class<?> clazz, DragValueCustomizer customizer) {
            if (getModel().containsItem(clazz)) {
                throw new IllegalArgumentException("A PaletteCategory alredy contains a PaletteItem with the specified valueClassClass(valueClass=" + clazz.getName() + ")");
            }
            PaletteItem item = new PaletteItem(getModel(), label, clazz, customizer);
            items.add(item);
            getPane().getChildren().add(idx, item.getLabel());

            return item;
        }
    }

    /**
     * The object of the class contains a set of items broken down by category.
     * Serves as a paletteModel for {@link PalettePane}.
     *
     */
    public static class PaletteModel {

        private final ObservableList<PaletteCategory> categories = FXCollections.observableArrayList();
        private DragValueCustomizer customizer;
        private final PalettePane palette;

        /**
         * Creates a new instance of the class. The created object has the
         * property {@code customizer} set to the object of type
         * {@link DefaultDragValueCustomizer}.
         */
        public PaletteModel(PalettePane palette) {
            this.palette = palette;
            customizer = new DefaultDragValueCustomizer();
        }

        /**
         * Returns a list of categories.
         *
         * @return a list of categories
         */
        public ObservableList<PaletteCategory> getCategories() {
            return categories;
        }

        /**
         * Return a customizer or null if the customizer is not specified.
         *
         * @return a customizer
         */
        public DragValueCustomizer getCustomizer() {
            return customizer;
        }

        /**
         * Sets a new value of type {@code DragValueCustomizer}.
         *
         * @param customizer the value of type {@link DragValueCustomizer}
         */
        public void setCustomizer(DragValueCustomizer customizer) {
            this.customizer = customizer;
        }

        /**
         * Checks whether the paletteModel contains a category with the given
         * identifier.
         *
         * @param id the identifier to be checked
         * @return true if the category with the given identifier exists. false
         * otherwise
         */
        public boolean containsCategory(String id) {
            boolean retval = false;
            for (PaletteCategory pc : categories) {
                if (pc.getId().equals(id)) {
                    retval = true;
                    break;
                }
            }
            return retval;
        }

        /**
         * Checks whether the paletteModel contains a category for the given
         * {@code java.lang.Class}..
         *
         * @param valueClass the class to be checked
         * @return true if the category for the given {@code Class} exists.
         * false otherwise
         */
        public boolean containsItem(Class<?> valueClass) {
            boolean retval = false;
            for (PaletteCategory pc : categories) {
                for (PaletteItem it : pc.getItems()) {
                    if (it.getValueClass().equals(valueClass)) {
                        retval = true;
                        break;
                    }
                }
            }
            return retval;
        }

        /**
         * Creates a new instance of type {@link PaletteCategory} and ads it to
         * the end of the categories list.
         *
         * @param id the identifier for the new category/ Must be unique
         * otherwise the exception of type {@code IllegalArgumentException} will
         * be thrown
         * @param label the label used to represent the new category in the
         * palette.
         * @return the newly created category
         */
        public PaletteCategory addCategory(String id, Label label) {
            return addCategory(this, id, label);
        }

        protected PaletteCategory addCategory(PaletteModel model, String id, Label label) {
            if (containsCategory(id)) {
                throw new IllegalArgumentException("A PaletteCategory with the same id already exists (id=" + id + ")");
            }

            PaletteCategory c = new PaletteCategory(model, id, label);
            categories.add(c);
            return c;
        }

        /**
         * Returns the category with the given identifier.
         *
         * @param id the identifier to search for
         * @return the category with the given identifier.
         */
        public PaletteCategory getCategory(String id) {
            PaletteCategory retval = null;
            for (PaletteCategory c : categories) {
                if (c.getId().equals(id)) {
                    retval = c;
                    break;
                }
            }
            return retval;
        }

        public PalettePane getPalette() {
            return palette;
        }

    }

    public static class PaletteItemMouseDragHandler extends DefaultMouseDragHandler {

        private final PaletteItem item;

        public PaletteItemMouseDragHandler(DockableContext context, PaletteItem item) {
            super(context);
            this.item = item;
        }

        @Override
        public void mousePressed(MouseEvent ev) {
            setStartMousePos(null);
            Point2D pos = new Point2D(ev.getX(), ev.getY());

            if (!ev.isPrimaryButtonDown()) {
                return;
            }
            PalettePane palette = item.getModel().getPalette();
            try {
                Object value = item.getValueClass().newInstance();
                if (Dockable.of(value) == null && (value instanceof Node)) {
                    NodePolicy itemPolicy = item.getProducedNodePolicy();
                    if (itemPolicy == DOCKABLE || itemPolicy == BOTH) {
                        if (palette.getProducedNodePolicy() == DOCKABLE || palette.getProducedNodePolicy() == BOTH) {
                            DockRegistry.makeDockable((Node) value);
                        }
                    }
                }
                if (DockLayout.of(value) == null && (value instanceof Node)) {
                    NodePolicy itemPolicy = item.getProducedNodePolicy();
                    if (itemPolicy == DOCKLAYOUT || itemPolicy == BOTH) {
                        if (palette.getProducedNodePolicy() == DOCKLAYOUT || palette.getProducedNodePolicy() == BOTH) {
                            LayoutContext lc = null;
                            if (item.getLayoutContextClass() == null) {
                                LayoutContextFactory f = new LayoutContextFactory();
                                lc = f.getContext((Node) value);
                            } else {

                            }
                            if (lc != null) {
                                DockRegistry.makeDockLayout((Node) value, lc);
                            }
                        }
                    }
                }
                if (item.getModel().getCustomizer() != null) {
                    item.getModel().getCustomizer().customize(value);
                }
                if (item.getCustomizer() != null) {
                    item.getCustomizer().customize(value);
                }

                if ((value instanceof Node) && item.getEventDispatcher() != null) {
                    item.getEventDispatcher().start((Node) value);
                }

                String tx = "";
                if (value instanceof Labeled) {
                    tx = ((Labeled) value).getText();
                }

                Label label = item.getLabel();

                WritableImage image;

                if (label != null) {
                    image = label.snapshot(null, null);
                    if (image != null) {
                        Node imageNode = new ImageView(image);
                        imageNode.setOpacity(0.75);
                        getContext().setDragContainer(new DragContainer(imageNode, value));
                        if ((value instanceof Node) || DockRegistry.isDockable(value)) {
                            getContext().getDragContainer().setDragAsObject(true);
                        }
                    }
                }

            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(PalettePane.class.getName()).log(Level.SEVERE, null, ex);
            }

            setStartMousePos(pos);
        }

        @Override
        public DragManager getDragManager(MouseEvent ev) {
            DragManager dm = super.getDragManager(ev);
            dm.setHideOption(DragManager.HideOption.CARRIERED);
            return dm;
        }
    }//PalettePaneMouseDragHandler

    @FunctionalInterface
    public static interface DragValueCustomizer<T> {
        void customize(T value);
    }

    /**
     * The objects of the class are used to customize a value which is used
     * during the drag operation.
     */
    public static class DefaultDragValueCustomizer implements DragValueCustomizer {

        /**
         * Customize the value depending of the value type.
         * <p>
         * if the value is an instance of class {@code javafx.scene.control.Tab
         * }
         * then the method sets the text property of the {@code Tab} object to
         * "tab".
         * </p>
         * <p>
         * if the value is an instance of class {@code javafx.scene.control.Labeled
         * }
         * then the method sets the text property of the {@code Labeled} object
         * to the simple class name of the value with the first letter converted
         * to low case.
         * </p>
         *
         * @param value the value to be customized
         */
        @Override
        public void customize(Object value) {
            if (value instanceof Tab) {
                ((Tab) value).setText("tab");
            } else if (value instanceof Labeled) {
                String tx = value.getClass().getSimpleName();
                tx = tx.substring(0, 1).toLowerCase() + tx.substring(1);
                ((Labeled) value).setText(tx);
            }
        }
    }

    public static interface PaletteEventDispatcher extends EventDispatcher {

        void start(Node node);

    }

    public static class MouseEventDispatcher implements PaletteEventDispatcher {

        private EventDispatcher initial;
        private Node node;
        private Predicate<Node> preventCondition;

        public MouseEventDispatcher() {
            this(null);
        }

        public MouseEventDispatcher(Predicate<Node> cond) {
            preventCondition = cond;
            init();
        }

        private void init() {
        }

        public void start(Node node) {
            this.node = node;
            initial = node.getEventDispatcher();
            node.setEventDispatcher(this);
        }

        @Override
        public Event dispatchEvent(Event event, EventDispatchChain tail) {
            if (event instanceof MouseEvent) {

                MouseEvent mouseEvent = (MouseEvent) event;

                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    return pressed(event, tail);
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    return released(event, tail);
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    return clicked(event, tail);
                } else if (mouseEvent.getEventType() == MouseEvent.DRAG_DETECTED) {
                    return dragDetected(event, tail);
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    return dragged(event, tail);
                }
            }

            return initial.dispatchEvent(event, tail);
        }

        protected Event pressed(Event event, EventDispatchChain tail) {
            return initial.dispatchEvent(event, tail);
        }

        protected Event released(Event event, EventDispatchChain tail) {
            if (preventCondition == null || preventCondition.test(node)) {
                return null;
            }
            return initial.dispatchEvent(event, tail);
        }

        protected Event clicked(Event event, EventDispatchChain tail) {
            return initial.dispatchEvent(event, tail);
        }

        protected Event dragDetected(Event event, EventDispatchChain tail) {
            return initial.dispatchEvent(event, tail);
        }

        protected Event dragged(Event event, EventDispatchChain tail) {
            return initial.dispatchEvent(event, tail);
        }

    }

}//PalettePane
