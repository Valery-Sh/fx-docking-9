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
package org.vns.javafx.dock.api.designer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.DefaultProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.SaveRestore;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.dragging.MouseDragHandler;
import org.vns.javafx.dock.api.dragging.view.FloatStageView;
import static org.vns.javafx.dock.api.dragging.view.FloatView.DEFAULT_CURSORS;
import static org.vns.javafx.dock.api.dragging.view.FloatView.FLOATVIEW;
import static org.vns.javafx.dock.api.dragging.view.FloatView.FLOAT_WINDOW;

/**
 *
 * @author Valery Shishkin
 */
@DefaultProperty("items")
public class TrashTray extends Control {

    private final ObservableList<TrayItem> items = FXCollections.observableArrayList();
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private final ObjectProperty<Bounds> windowBounds = new SimpleObjectProperty<>();
    private final ReadOnlyObjectWrapper<TableView<TrayItem>> tableViewWrapper = new ReadOnlyObjectWrapper<>();
    private final ObservableList<TableRow> visibleRows = FXCollections.observableArrayList();

    private ListChangeListener<TrayItem> itemsChangeListener = (change) -> {
        while (change.next()) {

            if (change.wasRemoved()) {
                List list = change.getRemoved();
                if (!list.isEmpty()) {
                    SaveRestore sr = DockRegistry.lookup(SaveRestore.class);
                    if (sr != null) {
//                        System.err.println("CHANGE FROM =" + change.getFrom() + " TO = " + change.getTo());
                        TrayItem ti = (TrayItem) list.get(list.size() - 1);

                        //
                        sr.save(ti.getElement(), change.getTo());
                    }
                }
            }
            if (change.wasAdded()) {
                List<TrayItem> list = (List<TrayItem>) change.getAddedSubList();
                list.forEach(item -> {
                    DockLayout.of(this).getLayoutContext().commitDock(item.getElement());
                });
            }
        }//while        
    };

    public TrashTray() {
        init();
    }

    private void init() {
        setImage(new Image(getClass().getResourceAsStream("/org/vns/javafx/dock/api/designer/resources/images/trash-empty.png")));
        getStyleClass().add("trash-tray");
        DockRegistry.makeDockLayout(this, new TrashTrayLayoutContext(this));

        setTableView(createTableView());
        getItems().addListener(itemsChangeListener);
    }
    
    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    public ObjectProperty<Bounds> windowBoundsProperty() {
        return windowBounds;
    }

    public Bounds getWindowBounds() {
        return windowBounds.get();
    }

    public void getWindowBounds(Bounds bounds) {
        this.windowBounds.set(bounds);
    }

    protected void add(Object toSave, SaveRestore saveRestore) {
        TrayItem te = new TrayItem(this, toSave, saveRestore);
        items.add(0, te);
    }

    protected void add(Object toSave) {
        TrayItem te = new TrayItem(this, toSave);
        items.add(0, te);
    }

    protected void add(int idx, Object toSave) {
        if (idx < 0 || idx > items.size()) {
            return;
        }
        TrayItem te = new TrayItem(this, toSave);
        items.add(idx, te);
    }

    public void remove(Object obj) {
        items.remove(findByObject(obj));
    }

    public boolean contains(Object obj) {
        return findByObject(obj) != null;
    }

    protected TrayItem findByObject(Object obj) {
        TrayItem retval = null;
        for (TrayItem te : items) {
            if (te.getElement() == obj) {
                retval = te;
                break;
            }
        }
        //System.err.println("findByObj obj = " + obj + "; retval =" + retval);
        return retval;
    }

    public void clear() {
        items.clear();
    }

    public ObservableList<TrayItem> getItems() {
        return items;
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public Image getImage() {
        return image.get();
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public ReadOnlyObjectProperty<TableView<TrayItem>> tableViewProperty() {
        return tableViewWrapper.getReadOnlyProperty();
    }

    public TableView<TrayItem> getTableView() {
        return tableViewWrapper.getValue();
    }

    private void setTableView(TableView<TrayItem> item) {
        tableViewWrapper.setValue(item);
    }

    protected ObservableList<TableRow> getVisibleRows() {
        return visibleRows;
    }

    protected TableView createTableView() {
        TableView<TrayItem> tv = new TableView(getItems());
        tv.setRowFactory((TableView<TrayItem> v) -> {
            TableRow<TrayItem> tableRow = new TableRow<TrayItem>() {

                @Override
                public void updateItem(TrayItem item, boolean empty) {
                    if (empty) {
                        this.setItem(null);
                        getVisibleRows().remove(this);
                    } else {
                        if (!getVisibleRows().contains(this)) {
                            getVisibleRows().add(this);
                        }
                        this.setItem(item);
                    }
                }
            };
            return tableRow;
        });

        TableColumn<TrayItem, ImageView> graphicColumn = new TableColumn<>();
        graphicColumn.setCellValueFactory(new PropertyValueFactory("graphic"));
        tv.getColumns().add(graphicColumn);

        TableColumn<TrayItem, String> classNameColumn = new TableColumn<>("Class Name");
        classNameColumn.setCellValueFactory(new PropertyValueFactory("className"));
        tv.getColumns().add(classNameColumn);

        TableColumn<TrayItem, String> varNameColumn = new TableColumn<>("Variable Name");
        varNameColumn.setCellValueFactory(new PropertyValueFactory("varName"));
        tv.getColumns().add(varNameColumn);

        TableColumn<TrayItem, Long> saveTimeColumn = new TableColumn<>("Save Time");
        saveTimeColumn.setCellValueFactory(new PropertyValueFactory("saveTime"));
        tv.getColumns().add(saveTimeColumn);

        Dockable d = DockRegistry.makeDockable(tv);
        TrashTrayTableViewMouseDragHandler dragHandler = new TrashTrayTableViewMouseDragHandler(this, d.getContext());

        d.getContext().getLookup().putUnique(MouseDragHandler.class, dragHandler);
        d.getContext().setLayoutContext(DockLayout.of(this).getLayoutContext());

        return tv;
    }

    public TrayItem getTrayItem(double x, double y) {
        TrayItem retval = null;

//        System.err.println("getVisibleRows.size = " + getVisibleRows().size());
        for (TableRow<TrayItem> row : getVisibleRows()) {
//            System.err.println("   --- row = " + row.getBoundsInLocal());
            Point2D p = row.screenToLocal(x, y);
//            System.err.println("   --- p = " + p);
//            System.err.println("   --- contains = " + row.getBoundsInLocal().contains(p));

            if (row.getBoundsInLocal().contains(p)) {
                //if (DockUtil.contains(r, x, y)) {
//                System.err.println("   --- row = " + row);
                retval = row.getItem();
//                System.err.println("   --- retval = " + retval);
                break;
            }
        }
        return retval;
    }

    public static void showPopup(Window owner) {
        TrashTray tray = new TrashTray();
        DockRegistry.makeDockable(tray);
        TrashFloatView fp = new TrashFloatView(Dockable.of(tray));
        fp.makePopup(Dockable.of(tray), owner, tray, true);

        DockRegistry.makeDockLayout(tray, new TrashTrayLayoutContext(tray));

    }

    public static Stage showStage(Window owner) {
        TrashTray tray = new TrashTray();
        Dockable d = DockRegistry.makeDockable(tray);
        d.getContext().setAcceptable(false);
        TrashFloatView fp = new TrashFloatView(Dockable.of(tray));
        DockRegistry.makeDockLayout(tray, new TrashTrayLayoutContext(tray));
        return (Stage) fp.makeStage(Dockable.of(tray), owner, tray, true);

    }

    public Stage show(Window owner) {
        Dockable d = DockRegistry.makeDockable(this);
        d.getContext().setAcceptable(false);
        TrashFloatView fp = new TrashFloatView(Dockable.of(this));
        return (Stage) fp.makeStage(Dockable.of(this), owner, this, true);
    }

    public void hide() {
        //DockRegistry.unregisterDockable(this);
        //DockRegistry.unregisterDockLayout(this);
        getItems().clear();
        if (getScene() != null && getScene().getWindow() != null) {
            if (getScene().getWindow() instanceof Stage) {
                ((Stage) getScene().getWindow()).close();
            } else {
                getScene().getWindow().hide();
            }
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TrashTraySkin(this);
    }

    public static class TrayItem {

        private final LongProperty saveTime = new SimpleLongProperty();
        private final ObjectProperty element = new SimpleObjectProperty<>();
        private final StringProperty varName = new SimpleStringProperty("");
        private final ReadOnlyStringWrapper classNameWrapper = new ReadOnlyStringWrapper("");
        private final ReadOnlyObjectWrapper<ImageView> graphicWrapper = new ReadOnlyObjectWrapper("");
        private final ReadOnlyObjectWrapper<TrashTray> trashTrayWrapper = new ReadOnlyObjectWrapper();

        private final SaveRestore saveRestore;

        protected TrayItem(TrashTray trashTray, Object element, SaveRestore saveRestore) {
            this.element.set(element);
            this.saveRestore = saveRestore;
            this.saveTime.set(System.currentTimeMillis());
            this.trashTrayWrapper.setValue(trashTray);
            init();
        }

        public TrayItem(TrashTray trashTray, Object element) {
            this(trashTray, element, null);
        }

        private void init() {
            setClassName(getElement().getClass().getSimpleName());
            ImageView iv = new ImageView();
            Image im = new Image(getClass().getResourceAsStream("/org/vns/javafx/dock/api/resources/question-16x16.png"));
            iv.setImage(im);

            iv.getStyleClass().add("tree-item-node-" + getClassName().toLowerCase());
//            System.err.println("Style: " + "tree-item-node-" + getClassName().toLowerCase());
            setGraphic(iv);

        }

        public long getSaveTime() {
            return saveTime.get();
        }

        public Object getElement() {
            return element.get();
        }

        public void setSaveTime(long time) {
            this.saveTime.set(time);
        }

        public void setElement(Object obj) {
            element.set(obj);
        }

        public StringProperty varNameProperty() {
            return varName;
        }

        public String getVarName() {
            return varName.get();
        }

        public void getVarName(String varName) {
            this.varName.set(varName);
        }

        public ReadOnlyStringProperty classNameProperty() {
            return classNameWrapper.getReadOnlyProperty();
        }

        public String getClassName() {
            return classNameWrapper.getValue();
        }

        private void setClassName(String className) {
            classNameWrapper.setValue(className);
        }

        public ReadOnlyObjectProperty<ImageView> graphicProperty() {
            return graphicWrapper.getReadOnlyProperty();
        }

        public ImageView getGraphic() {
            return graphicWrapper.getValue();
        }

        private void setGraphic(ImageView graphic) {
            graphicWrapper.setValue(graphic);
        }

        public ReadOnlyObjectProperty<TrashTray> trashTrayProperty() {
            return trashTrayWrapper.getReadOnlyProperty();
        }

        public TrashTray getTrashTray() {
            return trashTrayWrapper.getValue();
        }

        private void setTrashTray(TrashTray trashTray) {
            trashTrayWrapper.setValue(trashTray);
        }

    }

    public static class TrashFloatView extends FloatStageView {

        public TrashFloatView(Dockable dockable) {
            super(dockable);
        }

        public Window makeStage(Dockable dockable, Window owner, Parent root, boolean show) {

            setSupportedCursors(DEFAULT_CURSORS);

            Node node = dockable.node();
            //
            // Removes selected and then Removes all MMOUSE_CLICKED event handlers 
            // and filters of type SeectionListener
            //
            Selection.removeListeners(dockable);

            final Stage window = new Stage();
            if (owner != null) {
                window.initOwner(owner);
            }
            window.initStyle(getStageStyle());

            windowRoot = new StackPane(root);

            windowRoot.setStyle("-fx-background-color: transparent");

            windowRoot.getStyleClass().add(FLOAT_WINDOW);
            windowRoot.getStyleClass().add(FLOATVIEW);

            Scene scene = new Scene(windowRoot);
            scene.setFill(null);

            window.setScene(scene);
            window.getScene().setCursor(Cursor.HAND);
            window.sizeToScene();
            markFloating(window);

            node.applyCss();
            windowRoot.applyCss();

            window.setOnShown(e -> {
                DockRegistry.register(window);
            });

            window.setOnHidden(e -> {
                DockRegistry.unregister(window);
            });

            if (show) {
                window.show();
            }
            return window;
        }

        public Window makePopup(Dockable dockable, Window owner, Parent root, boolean show) {

            setSupportedCursors(DEFAULT_CURSORS);

            Node node = dockable.node();
            //
            // Removes selected and then Removes all MMOUSE_CLICKED event handlers 
            // and filters of type SeectionListener
            //
            Selection.removeListeners(dockable);

            Point2D windowPos = node.localToScreen(0, 0);

            if (windowPos == null) {
                windowPos = new Point2D(400, 400);
            }

            final PopupControl window = new PopupControl();
            window.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
            window.setAutoFix(false);

            windowRoot = new StackPane(root);
            windowRoot.setStyle("-fx-background-color: transparent");

            windowRoot.getStyleClass().add(FLOAT_WINDOW);
            windowRoot.getStyleClass().add(FLOATVIEW);
            windowRoot.getStyleClass().add("float-popup-root");

            window.getScene().setRoot(windowRoot);
            window.getScene().setCursor(Cursor.HAND);
            markFloating(window);

            node.applyCss();
            windowRoot.applyCss();

            window.getStyleClass().clear();

            window.setOnShown(e -> {
                DockRegistry.register(window);
            });
            window.setOnHidden(e -> {
                DockRegistry.unregister(window);
            });

            if (show) {
                window.show(owner);
            }
            return window;
        }

    }
}
