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
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.dragging.DefaultMouseDragHandler;
import org.vns.javafx.dock.api.dragging.DragManager;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;

/**
 *
 * @author Valery Shyshkin
 */
public class PalettePane extends Control {

    public static final String PALETTE_PANE = "palette-pane";
    private PaletteModel model;

    private final ObjectProperty<ScrollPane.ScrollBarPolicy> scrollVBarPolicy = new SimpleObjectProperty<>(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    private final ObjectProperty<Node> dragNode = new SimpleObjectProperty<>();

    private BooleanProperty animated = new SimpleBooleanProperty();

    public PalettePane() {
        this(false);
    }

    public PalettePane(boolean createDefault) {
        initModel(createDefault);
        DockRegistry.makeDockable(this).getContext().setDragNode(null);
        getStyleClass().add(PALETTE_PANE);
    }

    private void initModel(boolean createDefault) {
        if (createDefault) {
            model = createDefaultPaleteModel();
        } else {
            model = new PaletteModel();
        }
    }

    public void setDragValueCustomizer(DragValueCustomizer customizer) {
        getModel().setDragValueCustomizer(customizer);
    }

    public BooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public boolean isAnimated() {
        return this.animated.get();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PalettePaneSkin(this);
    }

    public PaletteModel getModel() {
        return model;
    }

    public void setDragNode(Node node) {
        dragNode.set(node);
        if (Dockable.of(this) != null) {
            Dockable.of(this).getContext().setDragNode(node);
        }
    }

    public void getDragNode() {
        dragNode.get();
    }

    public ObjectProperty<Node> dragNodeProperty() {
        return dragNode;
    }

    public void setScrollPaneVbarPolicy(ScrollPane.ScrollBarPolicy value) {
        this.scrollVBarPolicy.set(value);
    }

    public ScrollPane.ScrollBarPolicy getScrollPaneVbarPolicy() {
        return scrollVBarPolicy.get();
    }

    public ObjectProperty<ScrollPane.ScrollBarPolicy> scrollPaneVbarPolicy() {
        return scrollVBarPolicy;
    }

    protected PaletteModel createDefaultPaleteModel() {
        PaletteModel model = new PaletteModel();

        Label lb = new Label("Containers");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        PaletteCategory pc = model.addCategory("containers", lb);
        lb.getStyleClass().add("tree-item-font-bold");

        lb = new Label("Tab");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        lb.getStyleClass().add("tree-item-node-tab");
        lb.applyCss();
        pc.addItem(lb, Tab.class);

        lb = new Label("VBox");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        lb.getStyleClass().add("tree-item-node-vbox");
        lb.applyCss();
        pc.addItem(lb, VBox.class);

        lb = new Label("HBox");

        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        pc.addItem(lb, HBox.class);
        lb.getStyleClass().add("tree-item-node-hbox");
        lb.applyCss();

        lb = new Label("Shapes");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        lb.getStyleClass().add("tree-item-font-bold");

        pc = model.addCategory("shapes", lb);
        lb.applyCss();

        lb = new Label("Rectangle");
        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        pc.addItem(lb, Rectangle.class);
        lb.getStyleClass().add("tree-item-node-rectangle");
        lb.applyCss();

        lb = new Label("Arc");

        lb.setStyle("-fx-border-color: green; -fx-background-color: yellow ");
        pc.addItem(lb, Arc.class);
        lb.getStyleClass().add("tree-item-node-rectangle");
        return model;
    }

    public static class PaletteItem {

        private final ObjectProperty<Label> label = new SimpleObjectProperty<>();
        private final Class<?> valueClass;
        private PaletteModel model;

        public Class<?> getValueClass() {
            return valueClass;
        }

        public PaletteItem(PaletteModel model, Label lb, Class<?> clazz) {
            label.set(lb);
            valueClass = clazz;
            this.model = model;
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

        public PaletteModel getModel() {
            return model;
        }

        public Label getLabel() {
            return label.get();
        }

        public void setLabel(Label graphic) {
            this.label.set(graphic);
        }

        public ObservableValue<Label> labelProperty() {
            return label;
        }

    }

    public static class PaletteCategory extends PaletteItem {

        private final StringProperty id = new SimpleStringProperty();
        private final ObservableList<PaletteItem> items = FXCollections.observableArrayList();
        private final ObjectProperty<TilePane> graphic = new SimpleObjectProperty<>();

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
            setGraphic(tp);
        }

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

        public String getId() {
            return id.get();
        }

        public void setId1(String id) {
            this.id.set(id);
        }

        public TilePane getGraphic() {
            return graphic.get();
        }

        public void setGraphic(TilePane pane) {
            this.graphic.set(pane);
        }

        public ObjectProperty<TilePane> graphicProperty() {
            return graphic;
        }

        public PaletteItem addItem(Label label, Class<?> clazz) {
            return addItem(items.size(), label, clazz);
        }

        public PaletteItem addItem(int idx, Label label, Class<?> clazz) {
            if ( getModel().containsItem(clazz)) {   
               throw new IllegalArgumentException("A PaletteCategory alredy contains a PaletteItem with the specified valueClassClass(valueClass=" + clazz.getName() + ")");
            }
            PaletteItem item = new PaletteItem(getModel(), label, clazz);
            items.add(item);
            getGraphic().getChildren().add(idx, item.getLabel());

            return item;
        }
    }

    public static class PaletteModel {

        private final ObservableList<PaletteCategory> categories = FXCollections.observableArrayList();
        private DragValueCustomizer dragValueCustomizer;

        public PaletteModel() {
            dragValueCustomizer = new DefaultDragValueCustomizer();
        }

        public ObservableList<PaletteCategory> getCategories() {
            return categories;
        }

        public DragValueCustomizer getDragValueCustomizer() {
            return dragValueCustomizer;
        }

        public void setDragValueCustomizer(DragValueCustomizer customizer) {
            this.dragValueCustomizer = customizer;
        }

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
        public boolean containsItem(Class<?> valueClass) {
            boolean retval = false;
            for (PaletteCategory pc : categories) {
                for ( PaletteItem it : pc.getItems()) {
                    if (it.getValueClass().equals(valueClass)) {
                        retval = true;
                        break;
                    }
                }
            }
            return retval;
        }

        
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

            try {
                Object value = item.getValueClass().newInstance();
                item.getModel().getDragValueCustomizer().customize(value);
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
                    }
                }

            } catch (InstantiationException | IllegalAccessException ex) {
                System.err.println("PaletteItemMouseDragHandler EXCEPTION ");
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
    public static interface DragValueCustomizer {

        void customize(Object value);
    }

    public static class DefaultDragValueCustomizer implements DragValueCustomizer {

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
}//PalettePane
