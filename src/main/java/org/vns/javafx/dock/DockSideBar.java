package org.vns.javafx.dock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockIndicator;

import org.vns.javafx.dock.api.DockableController;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.FloatStageBuilder;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.IndicatorPopup;
import org.vns.javafx.dock.api.StageBuilder;
import org.vns.javafx.dock.api.DockTarget;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty(value = "items")
public class DockSideBar extends Control implements Dockable, DockTarget, ListChangeListener {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private DockableController dockableController = new DockableController(this);

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableController dockableController() {
        return this.dockableController;
    }

    public enum Rotation {
        DEFAULT(0),
        DOWN_UP(-90), // usualy if Side.LEFT
        UP_DOWN(90);  // usually when SideRight

        private double angle;

        Rotation(double value) {
            this.angle = value;
        }

        public double getAngle() {
            return angle;
        }

    }
    private SidePaneController targetController;

    private final ObjectProperty<Side> sideProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<Rotation> rotationProperty = new SimpleObjectProperty<>();

    private final BooleanProperty hideOnExitProperty = new SimpleBooleanProperty(false);

    private final CustomToolBar delegate = new CustomToolBar();

    private final ObservableList<Dockable> items = FXCollections.observableArrayList();

    public DockSideBar() {
        init();
    }

    public DockSideBar(Dockable... items) {
        super();
        this.items.addAll(items);
    }

    private void init() {
        targetController = new SidePaneController(this);
        setOrientation(Orientation.HORIZONTAL);
        getStyleClass().clear();
        getStyleClass().add("dock-side-bar");

        sceneProperty().addListener((v, ov, nv) -> {
            sceneChanged(ov, nv);
        });

        sideProperty.addListener((v, ov, nv) -> {
            targetController.getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        rotationProperty.addListener((v, ov, nv) -> {
            targetController.getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        getDelegate().orientationProperty().addListener((v, ov, nv) -> {
            targetController.getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });

        setSide(Side.TOP);
        setRotation(Rotation.DEFAULT);

        items.addListener(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public ObservableList<Dockable> getItems() {
        return items;
    }

////////////////////////////////////////////////////
    @Override
    public void onChanged(Change change) {
        itemsChanged(change);

    }

    protected void itemsChanged(ListChangeListener.Change<? extends Dockable> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List<? extends Dockable> list = change.getRemoved();
                for (Dockable d : list) {
                    targetController.undock(d.node());
                }

            }
            if (change.wasAdded()) {
                List<? extends Dockable> list = change.getAddedSubList();
                for (Dockable d : list) {
                    //dock(d);
                    targetController.dock(d);
                }
            }
        }//while
    }

    @Override
    protected double computePrefHeight(double h) {
        return delegate.computePrefHeight(h);
    }

    @Override
    protected double computePrefWidth(double w) {
        return delegate.computePrefWidth(w);
    }

    @Override
    protected double computeMinHeight(double h) {
        return delegate.computeMinHeight(h);
    }

    @Override
    protected double computeMinWidth(double w) {
        return delegate.computeMinWidth(w);
    }

    @Override
    protected double computeMaxHeight(double h) {
        return delegate.computeMaxHeight(h);
    }

    @Override
    protected double computeMaxWidth(double w) {
        return delegate.computeMaxWidth(w);
    }

////////////////////////////////////////////////////    
    public ObjectProperty<Rotation> rotationProperty() {
        return rotationProperty;
    }

    public BooleanProperty hideOnExitProperty() {
        return hideOnExitProperty;
    }

    public boolean isHideOnExit() {
        return hideOnExitProperty.get();
    }

    public void setHideOnExit(boolean value) {
        hideOnExitProperty.set(value);
    }

    public Rotation getRotation() {
        return rotationProperty.get();
    }

    public void setRotation(Rotation rotation) {
        targetController.getItemMap().keySet().forEach(g -> {
            Button btn = (Button) g.getChildren().get(0);
            btn.setRotate(rotation.getAngle());
        });
        this.rotationProperty.set(rotation);

    }

    protected void sceneChanged(Scene oldValue, Scene newValue) {
        newValue.windowProperty().addListener((v, ov, nv) -> {
            stageChanged(ov, nv);
        });
        //Stage parent = (Stage) newValue.getWindow();
    }

    protected void stageChanged(Window oldValue, Window newValue) {
        if (!(newValue instanceof Stage)) {
            return;
        }
        Stage stage = (Stage) newValue;
        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, this::stageClicked);
        stage.setTitle("owner");
        getSideItems().values().forEach(d -> {
            d.adjustScreenPos();
        });
    }

    protected void stageClicked(MouseEvent ev) {
        if (localToScreen(getBoundsInLocal()).contains(ev.getScreenX(), ev.getScreenY())) {
            return;
        }

        targetController.getItemMap().forEach((g, d) -> {
            if (d.getDockable().node().getScene() != null && d.getDockable().node().getScene().getWindow() != null) {
                Window w = d.getDockable().node().getScene().getWindow();
                if (w instanceof Stage) {
                    ((Stage) w).close();
                }
            }
        });
    }

    @Override
    public DockTargetController targetController() {
        return this.targetController;
    }

    protected ObservableMap<Group, Container> getSideItems() {
        return targetController.getItemMap();
    }

    public Button getButton(Dockable dockable) {
        Button retval = null;
        for (Map.Entry<Group, Container> en : getSideItems().entrySet()) {
            if (en.getValue().getDockable() == dockable) {
                retval = (Button) en.getKey().getChildren().get(0);
                break;
            }
        }
        return retval;
    }

    public List<Button> getButtons() {
        List<Button> retval = new ArrayList<>();
        for (Group g : getSideItems().keySet()) {
            retval.add((Button) g.getChildren().get(0));
        }
        return retval;
    }

    public Side getSide() {
        return sideProperty.get();
    }

    public void setSide(Side side) {
        sideProperty.set(side);
    }

    public Orientation getOrientation() {
        return getDelegate().getOrientation();
    }

    public void setOrientation(Orientation orientation) {
        getDelegate().setOrientation(orientation);
    }

    //public Button dock(Dockable dockable) {
    //protected void dock(Dockable dockable) {        
    //    targetController.dock(dockable);
    //07.05return getButton(dockable);
    //}

    /*    public Button dock(Dockable dockable, String btnText) {
        if (btnText != null) {
            dockable.dockableController().getProperties().setProperty("user-title", btnText);
        }
        targetController.dock(dockable, null);
        return getButton(dockable);

    }
     */
    @Override
    public Region target() {
        return this;
    }

    public CustomToolBar getDelegate() {
        return delegate;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockSideBarSkin(this);
    }

    public static class DockSideBarSkin extends SkinBase<DockSideBar> {

        public DockSideBarSkin(DockSideBar control) {
            super(control);
            getChildren().add(control.getDelegate());
        }
    }

    public static class SidePaneController extends DockTargetController {

        private final ObservableMap<Group, Container> itemMap = FXCollections.observableHashMap();

        public SidePaneController(Region dockPane) {
            super(dockPane);
            init();
        }

        private void init() {
            //setSidePointerModifier(this::modifyNodeSidePointer);
            setDragPopup(new IndicatorPopup(this));

        }

        @Override
        protected void inititialize() {
            DockRegistry.start();
        }

        protected Container getItem(Dockable d) {
            Container retval = null;
            for (Container c : itemMap.values()) {
                if (c.getDockable() == d) {
                    retval = c;
                    break;
                }
            }
            return retval;
        }

        @Override
        protected boolean isDocked(Node node) {
            boolean retval = false;
            for (Container c : itemMap.values()) {
                if (c.getDockable().node() == node) {
                    retval = true;
                    break;
                }
            }
            return retval;
        }

        /*07.05        @Override
        protected void dock(Point2D mousePos, Dockable dockable) {
            if (isDocked(dockable.node())) {
                return;
            }
            if (dockable.dockableController().isFloating()) {
                if (doDock(mousePos, dockable.node())) {
                    dockable.dockableController().setFloating(false);
                }
            }
        }
         */
        protected void dock(Dockable dockable) {
            if (doDock(null, dockable.node())) {
                dockable.dockableController().setFloating(false);
            }
        }

        protected String getButtonText(Dockable d) {
            String txt = d.dockableController().getTitle();
            if (d.dockableController().getProperties().getProperty("user-title") != null) {
                txt = d.dockableController().getProperties().getProperty("user-title");
            } else if (d.dockableController().getProperties().getProperty("short-title") != null) {
                txt = d.dockableController().getProperties().getProperty("short-title");
            }
            if (txt == null || txt.trim().isEmpty()) {
                txt = "Dockable";
            }
            return txt;
        }

        public boolean hasWindow(Dockable d) {
            boolean retval = false;
            if (d.node().getScene() != null && d.node().getScene().getWindow() != null) {
                retval = true;
            }
            return retval;
        }

        protected int indexOf(double x, double y) {
            int idx = -1;
            ToolBar tb = ((DockSideBar) getTargetNode()).getDelegate();
            Node sb = findNode(tb.getItems(), x, y);
            if (sb != null && (sb instanceof Group)) {
                idx = tb.getItems().indexOf(sb);
            } else if (sb == null && DockUtil.contains(tb, x, y)) {
                idx = tb.getItems().size();
            }
            return idx;
        }

        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            Dockable dockable = DockRegistry.dockable(node);
            Stage nodeStage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                nodeStage = (Stage) node.getScene().getWindow();
            }

            Button itemButton = new Button(getButtonText(dockable));

            itemButton.getStyleClass().add("item-button");

            int idx = -1;
            ToolBar tb = ((DockSideBar) getTargetNode()).getDelegate();
            if (mousePos != null) {
                Node sb = findNode(tb.getItems(), mousePos.getX(), mousePos.getY());
                if (sb != null && (sb instanceof Group)) {
                    idx = tb.getItems().indexOf(sb);
                } else if (sb == null && DockUtil.contains(tb, mousePos.getX(), mousePos.getY())) {
                    idx = tb.getItems().size();
                } else {
                    return false;
                }
            }
            Group item = new Group(itemButton);
            Container container = new Container(dockable);
            getItemMap().put(item, container);

            itemButton.setRotate(((DockSideBar) getTargetNode()).getRotation().getAngle());
            itemButton.setOnAction(a -> {
                a.consume();
                if (dockable.node().getScene().getWindow().isShowing()) {
                    dockable.node().getScene().getWindow().hide();
                    return;
                }
                getItemMap().values().forEach(d -> {
                    d.getDockable().node().getScene().getWindow().hide();
                });
                show(itemButton);
                container.changeSize();
            });
            if (idx >= 0) {
                ((DockSideBar) getTargetNode()).getDelegate().getItems().add(idx, item);
            } else {
                ((DockSideBar) getTargetNode()).getDelegate().getItems().add(item);
            }
            DockableController nodeHandler = dockable.dockableController();
            if (nodeHandler.getTargetController() == null || nodeHandler.getTargetController() != this) {
                nodeHandler.setTargetController(this);
            }
            container.getStageBuilder().setSupportedCursors(getSupportedCursors());
            Stage stage = container.getStageBuilder().createStage(dockable);
            stage.setAlwaysOnTop(true);
            stage.setOnShowing(e -> {
                if (getTargetNode().getScene() != null && getTargetNode().getScene().getWindow() != null) {
                    if (stage.getOwner() == null) {
                        stage.initOwner(getTargetNode().getScene().getWindow());
                    }
                }
            });

            if (getTargetNode().getScene() != null && getTargetNode().getScene().getWindow() != null && getTargetNode().getScene().getWindow().isShowing()) {
                container.adjustScreenPos();
            }

            //container.setDocked(true);
            if (nodeStage != null) {
                nodeStage.close();
            }
            container.setDocked(true);

            return true;
        }

        public Node findNode(List<Node> list, double x, double y) {
            Node retval = null;
            for (Node node : list) {
                if (!(node instanceof Group)) {
                    continue;
                }
                Region r = (Region) ((Group) node).getChildren().get(0);
                if (DockUtil.contains(r, x, y)) {
                    retval = node;
                    break;
                }
            }
            return retval;
        }

        public Cursor[] getSupportedCursors() {
            List<Cursor> list = new ArrayList<>();
            switch (((DockSideBar) getTargetNode()).getSide()) {
                case RIGHT:
                    list.add(Cursor.W_RESIZE);
                    break;
                case LEFT:
                    list.add(Cursor.E_RESIZE);
                    break;
                case BOTTOM:
                    list.add(Cursor.N_RESIZE);
                    break;
                case TOP:
                    list.add(Cursor.S_RESIZE);
                    break;
            }

            return list.toArray(new Cursor[0]);
        }

        protected void rotate(Button btn) {
            //btn.setRotate(0);
            if (btn.getRotate() == 90) {

            }

            switch (((DockSideBar) getTargetNode()).getSide()) {
                case RIGHT:
                    btn.setRotate(90);
                    break;
                case LEFT:
                    btn.setRotate(-90);
                    break;
                default:
                    btn.setRotate(0);
                    break;
            }
        }

        @Override
        public void remove(Node dockNode) {
            Group r = null;
            for (Map.Entry<Group, Container> en : itemMap.entrySet()) {
                if (en.getValue().getDockable().node() == dockNode) {
                    r = en.getKey();
                    break;
                }
            }
            if (r != null) {
                itemMap.get(r).removeListeners();
                itemMap.remove(r);
                ((DockSideBar) getTargetNode()).getDelegate().getItems().remove(r);
                ((DockSideBar) getTargetNode()).getItems().remove(DockRegistry.dockable(dockNode));

            }
        }

        protected ObservableMap<Group, Container> getItemMap() {
            return itemMap;
        }

        public void show(Button btn) {
            Group group = (Group) btn.getParent();

            //container.getStageBuilder().createStage(dockable).setAlwaysOnTop(true);
            Dockable dockable = getItemMap().get(group).getDockable();
            /*            if (!hasWindow(dockable)) {
                getItemMap().get(group).getStageBuilder().createStage(dockable).setAlwaysOnTop(true);
                if (!dockable.node().getScene().getWindow().isShowing()) {
                    if (getTargetNode().getScene() != null && getTargetNode().getScene().getWindow() != null) {
                        ((Stage) dockable.node().getScene().getWindow()).initOwner(getTargetNode().getScene().getWindow());
                    }
                }
            }
             */
            getItemMap().get(group).changeSize();
            ((Stage) dockable.node().getScene().getWindow()).show();
        }

        @Override
        public DockIndicator createDockIndicator() {
            DockIndicator dockIndicator = new DockIndicator(this) {
                private Rectangle tabDockPlace;

                @Override
                public void showIndicator(double screenX, double screenY, Region targetNode) {
                    getIndicatorPopup().show(getPaneController().getTargetNode(), screenX, screenY);
                }

                @Override
                protected Pane createIndicatorPane() {
                    Pane p = new Pane();
                    p.getStyleClass().add("drag-pane-indicator");
                    return p;
                }

                @Override
                protected String getStylePrefix() {
                    return "dock-indicator";
                }

                protected Rectangle getTabDockPlace() {
                    if (tabDockPlace == null) {
                        tabDockPlace = new Rectangle();
                        tabDockPlace.getStyleClass().add("dock-place");
                        getIndicatorPane().getChildren().add(tabDockPlace);
                    }
                    return tabDockPlace;
                }

                public void hideDockPlace() {
                    getDockPlace().setVisible(false);
                    getTabDockPlace().setVisible(false);

                }

                @Override
                public void showDockPlace(double x, double y) {
                    ToolBar tb = ((DockSideBar) getTargetNode()).getDelegate();

                    int idx = indexOf(x, y);
                    if (idx < 0) {
                        return;
                    }
                    double tbHeight = tb.getHeight();

                    Rectangle dockPlace = (Rectangle) getDockPlace();

                    if (tb.getOrientation() == Orientation.HORIZONTAL) {
                        dockPlace.setHeight(tb.getHeight());
                        dockPlace.setWidth(5);
                    } else {
                        dockPlace.setWidth(tb.getWidth());
                        dockPlace.setHeight(5);
                    }
                    //dockPlace.setRotate(90);
                    Point2D p = dockPlace.localToParent(0, 0);

                    dockPlace.setX(p.getX());

                    Node node = null;
                    boolean before = false;

                    if (idx == 0 && tb.getItems().isEmpty()) {
                        dockPlace.setWidth(5);
                    } else if (idx == tb.getItems().size()) {
                        node = tb.getItems().get(idx - 1);
                    } else {
                        node = node = tb.getItems().get(idx);
                        before = true;
                    }
                    double pos = 0;
                    if (node != null) {
                        Bounds bnd = node.getBoundsInParent();
                        if (tb.getOrientation() == Orientation.HORIZONTAL) {
                            if (before) {
                                pos = bnd.getMinX();
                            } else {
                                pos = bnd.getMinX() + bnd.getWidth();
                            }
                            dockPlace.setX(pos);
                            dockPlace.setY(0);
                        } else {
                            if (before) {
                                pos = bnd.getMinY();
                            } else {
                                pos = bnd.getMinY() + bnd.getHeight();
                            }
                            dockPlace.setX(0);
                            dockPlace.setY(pos);

                        }
                    }
                    dockPlace.setVisible(true);
                    dockPlace.toFront();
                }
            };
            return dockIndicator;
        }
    }//class

    public static class Container implements ChangeListener<Number> {

        private final Dockable dockable;
        private final StageBuilder stageBuilder;

        private final BooleanProperty hideOnExitProperty = new SimpleBooleanProperty(false);
        private final BooleanProperty floatingProperty = new SimpleBooleanProperty(false);

        private EventHandler<MouseEvent> mouseExitEventListener;

        public Container(Dockable dockable) {
            this.dockable = dockable;
            stageBuilder = new StageBuilder(dockable.dockableController());
        }

        public void setDocked(boolean docked) {
            DockSideBar sb = getSideBar();
            if (sb.isHideOnExit()) {
                addMouseExitListener();
            }
            hideOnExitProperty.bind(sb.hideOnExitProperty());
            floatingProperty.bind(dockable.dockableController().floatingProperty());
            floatingProperty.addListener((v, oldValue, newValue) -> {
                if (newValue) {
                    removeMouseExitListener();
                }
            });
            hideOnExitProperty.addListener((v, oldValue, newValue) -> {
                if (newValue) {
                    addMouseExitListener();
                } else {
                    removeMouseExitListener();
                }
            });
        }

        public DockSideBar getSideBar() {
            return (DockSideBar) dockable.dockableController().getTargetController().getTargetNode();
        }

        public void addMouseExitListener() {
            mouseExitEventListener = this::mouseExited;
            dockable.node().getScene().getWindow().addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
        }

        public void removeMouseExitListener() {
            if (mouseExitEventListener == null) {
                return;
            }
            dockable.node().getScene().getWindow().removeEventHandler(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
            dockable.node().getScene().getWindow().removeEventFilter(MouseEvent.MOUSE_EXITED_TARGET, mouseExitEventListener);
        }

        public Dockable getDockable() {
            return dockable;
        }

        protected void mouseExited(MouseEvent ev) {
            if ((ev.getSource() instanceof Window) && DockUtil.contains((Window) ev.getSource(), ev.getScreenX(), ev.getScreenY())) {
                return;
            }
            if (!ev.isPrimaryButtonDown() && !dockable.dockableController().isFloating()) {
                dockable.node().getScene().getWindow().hide();
            }
            ev.consume();
        }

        public void adjustScreenPos() {
            SidePaneController handler = (SidePaneController) dockable.dockableController().getTargetController();
            Stage ownerStage = (Stage) ((DockSideBar) handler.getTargetNode()).getScene().getWindow();

            ownerStage.xProperty().addListener(this);
            ownerStage.yProperty().addListener(this);
            ownerStage.widthProperty().addListener(this);
            ownerStage.heightProperty().addListener(this);

            changeSize();

        }

        public void removeListeners() {
            SidePaneController handler = (SidePaneController) dockable.dockableController().getTargetController();
            Stage ownerStage = (Stage) ((DockSideBar) handler.getTargetNode()).getScene().getWindow();
            ownerStage.xProperty().removeListener(this);
            ownerStage.yProperty().removeListener(this);
            ownerStage.widthProperty().removeListener(this);
            ownerStage.heightProperty().removeListener(this);
            stageBuilder.removeListeners(dockable);
        }

        protected void changeSide() {
            SidePaneController handler = (SidePaneController) dockable.dockableController().getTargetController();
            stageBuilder.setSupportedCursors(handler.getSupportedCursors());
        }

        protected void changeSize() {

            Stage stage = (Stage) dockable.node().getScene().getWindow();
            DockSideBar sb = (DockSideBar) dockable.dockableController().getTargetController().getTargetNode();
            if (!stage.isShowing()) {
                return;
            }
            Point2D pos = sb.localToScreen(0, 0);
            switch (sb.getSide()) {
                case TOP:
                    stage.setX(pos.getX());
                    stage.setY(pos.getY() + sb.getHeight());
                    stage.setWidth(sb.getWidth());
                    break;
                case BOTTOM:
                    stage.setX(pos.getX());
                    stage.setY(pos.getY() - stage.getHeight());
                    stage.setWidth(sb.getWidth());
                    break;
                case RIGHT:
                    stage.setY(pos.getY());
                    stage.setX(pos.getX() - stage.getWidth());
                    stage.setHeight(sb.getHeight());
                    break;
                case LEFT:
                    stage.setY(pos.getY());
                    stage.setX(pos.getX() + sb.getWidth());
                    stage.setHeight(sb.getHeight());
                    break;
            }

        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            changeSize();
        }

        public StageBuilder getStageBuilder() {
            return stageBuilder;
        }
    }

    public static class CustomToolBar extends ToolBar {

        public CustomToolBar() {

        }

        public CustomToolBar(Node... items) {
            super(items);
        }

        @Override
        protected double computePrefHeight(double h) {
            return super.computePrefHeight(h);
        }

        @Override
        protected double computePrefWidth(double w) {
            return super.computePrefWidth(w);
        }

        @Override
        protected double computeMinHeight(double h) {
            return super.computeMinHeight(h);
        }

        @Override
        protected double computeMinWidth(double w) {
            return super.computeMinWidth(w);
        }

        @Override
        protected double computeMaxHeight(double h) {
            return super.computeMaxHeight(h);
        }

        @Override
        protected double computeMaxWidth(double w) {
            return super.computeMaxWidth(w);
        }
    }

}//class
