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
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.ContextLookup;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.dragging.view.FloatPopupControlView2;
import org.vns.javafx.dock.api.dragging.view.FloatView;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty(value = "items")
public class DockSideBar extends Control implements Dockable, DockTarget, ListChangeListener {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private DockableContext dockableContext = new DockableContext(this);

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableContext getDockableContext() {
        return this.dockableContext;
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
    private SidePaneContext targetContext;

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
        targetContext = new SidePaneContext(this);
        setOrientation(Orientation.HORIZONTAL);
        getStyleClass().clear();
        getStyleClass().add("dock-side-bar");

        sceneProperty().addListener((v, ov, nv) -> {
            sceneChanged(ov, nv);
        });

        sideProperty.addListener((v, ov, nv) -> {
            targetContext.getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        rotationProperty.addListener((v, ov, nv) -> {
            targetContext.getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        getDelegate().orientationProperty().addListener((v, ov, nv) -> {
            targetContext.getItemMap().values().forEach(d -> {
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
                    targetContext.undock(d.node());
                }

            }
            if (change.wasAdded()) {
                List<? extends Dockable> list = change.getAddedSubList();
                for (Dockable d : list) {
                    //dock(d);
                    targetContext.dock(d);
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
        targetContext.getItemMap().keySet().forEach(g -> {
            Button btn = (Button) g.getChildren().get(0);
            btn.setRotate(rotation.getAngle());
        });
        this.rotationProperty.set(rotation);

    }

    protected void sceneChanged(Scene oldValue, Scene newValue) {
        newValue.windowProperty().addListener((v, ov, nv) -> {
            windowChanged(ov, nv);
        });
        //Stage parent = (Stage) newValue.getWindow();
    }

    protected void windowChanged(Window oldValue, Window newValue) {
        /*        if (!(newValue instanceof Stage)) {
            return;
        }
         */
        Window window = (Window) newValue;
        window.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, this::windowClicked);
        if (window instanceof Stage) {
            ((Stage) window).setTitle("owner");
        }
        getSideItems().values().forEach(d -> {
            d.adjustScreenPos();
        });
    }

    protected void windowClicked(MouseEvent ev) {
        if (localToScreen(getBoundsInLocal()).contains(ev.getScreenX(), ev.getScreenY())) {
            return;
        }

        targetContext.getItemMap().forEach((g, d) -> {
            if (d.getDockable().node().getScene() != null && d.getDockable().node().getScene().getWindow() != null) {
                Window w = d.getDockable().node().getScene().getWindow();
                if (w instanceof Stage) {
                    //??? 11.08((Stage) w).close();
                } else {
                    //??? 11.08 w.hide();
                }
            }
        });
    }

    @Override
    public TargetContext getTargetContext() {
        return this.targetContext;
    }

    protected ObservableMap<Group, Container> getSideItems() {
        return targetContext.getItemMap();
    }

    protected Button getButton(Dockable dockable) {
        Button retval = null;
        for (Map.Entry<Group, Container> en : getSideItems().entrySet()) {
            if (en.getValue().getDockable() == dockable) {
                retval = (Button) en.getKey().getChildren().get(0);
                break;
            }
        }
        return retval;
    }

    protected List<Button> getButtons() {
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
    //    getTargetContext.dock(dockable);
    //07.05return getButton(dockable);
    //}

    /*    public Button dock(Dockable dockable, String btnText) {
        if (btnText != null) {
            dockable.getDockableContext().getProperties().setProperty("user-title", btnText);
        }
        getTargetContext.dock(dockable, null);
        return getButton(dockable);

    }
     */
    @Override
    public Region target() {
        return this;
    }

    protected CustomToolBar getDelegate() {
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

    public static class SidePaneContext extends TargetContext {

        Container container;
        private final ObservableMap<Group, Container> itemMap = FXCollections.observableHashMap();

        public SidePaneContext(Region dockPane) {
            super(dockPane);
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            super.initLookup(lookup);
            lookup.putUnique(PositionIndicator.class,new SideBarPositonIndicator(this));
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

        public ObservableList<Dockable> getDockables() {
            return ((DockSideBar) getTargetNode()).getItems();
        }

        protected void dock(Dockable dockable) {

            System.err.println("SideBar dock dockable=" + dockable);
            System.err.println("SideBar dock dockable.isFloation=" + dockable.getDockableContext().isFloating());
            System.err.println("SideBar dock dockable.dockTarget=" + dockable.getDockableContext().getTargetContext());

            if (doDock(null, dockable.node())) {
                dockable.getDockableContext().setFloating(false);
            }
        }

        protected String getButtonText(Dockable d) {
            String txt = d.getDockableContext().getTitle();
            if (d.getDockableContext().getProperties().getProperty("user-title") != null) {
                txt = d.getDockableContext().getProperties().getProperty("user-title");
            } else if (d.getDockableContext().getProperties().getProperty("short-title") != null) {
                txt = d.getDockableContext().getProperties().getProperty("short-title");
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


        @Override
        protected boolean doDock(Point2D mousePos, Node node) {
            Dockable dockable = Dockable.of(node);

            Window priorWindow = null;
            if (node.getScene() != null && node.getScene().getWindow() != null) {
                priorWindow = (Window) node.getScene().getWindow();
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
                PopupControl popup = container.getPopup();
                if (popup == null) {

                    //popup = (PopupControl) container.getFloatView().createPopupControl(dockable, itemButton.getScene().getWindow());
                    popup = (PopupControl) container.getFloatView().make(dockable, false);
                    //container.getFloatView().addResizer(popup, dockable);
                    container.setPopup(popup);
                    show(itemButton);
                } else if (!popup.isShowing()) {
                    show(itemButton);
                } else {
                    popup.hide();
                }
                for (Container c : getItemMap().values()) {
                    if (c.getPopup() != null && c.getPopup() != popup) {
                        c.getPopup().hide();
                    }
                }
            });

            if (idx >= 0) {
                ((DockSideBar) getTargetNode()).getDelegate().getItems().add(idx, item);
            } else {
                ((DockSideBar) getTargetNode()).getDelegate().getItems().add(item);
            }
            DockableContext nodeHandler = dockable.getDockableContext();
            if (nodeHandler.getTargetContext() == null || nodeHandler.getTargetContext() != this) {
                nodeHandler.setTargetContext(this);
            }
            container.getFloatView().setSupportedCursors(getSupportedCursors());
            if (getTargetNode().getScene() != null && getTargetNode().getScene().getWindow() != null && getTargetNode().getScene().getWindow().isShowing()) {
                container.adjustScreenPos();
            }
            if (priorWindow != null && (priorWindow instanceof Stage)) {
                ((Stage) priorWindow).close();
            } else if (priorWindow != null) {
                priorWindow.hide();
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
            System.err.println("^^^^ remove(Node dockNode)");
            Group r = null;
            for (Map.Entry<Group, Container> en : itemMap.entrySet()) {
                if (en.getValue().getDockable().node() == dockNode) {
                    r = en.getKey();
                    break;
                }
            }
            if (r != null) {
                itemMap.get(r).removeListeners();
                itemMap.get(r).getFloatView().setSupportedCursors(FloatView.DEFAULT_CURSORS);
                itemMap.remove(r);
                ((DockSideBar) getTargetNode()).getDelegate().getItems().remove(r);
                ((DockSideBar) getTargetNode()).getItems().remove(Dockable.of(dockNode));

            }
        }

        protected ObservableMap<Group, Container> getItemMap() {
            return itemMap;
        }

        public void show(Button btn) {
            Group group = (Group) btn.getParent();
            //Dockable dockable = getItemMap().get(group).getDockable();
            Container container = getItemMap().get(group);
            //container.changeSize();
            DockSideBar sb = (DockSideBar) getTargetNode();
            ToolBar tb = sb.getDelegate();
            //Bounds b = tb.localToScreen(tb.getBoundsInLocal());
            if (container.getPopup() != null && !container.getPopup().isShowing()) {
                container.getPopup().show(tb.getScene().getWindow());
            }
            container.changeSize();
        }

/*        @Override
        public PositionIndicator createPositionIndicator() {
            PositionIndicator positionIndicator = new PositionIndicator(this) {
                private Rectangle tabDockPlace;

                @Override
                public void showIndicator(double screenX, double screenY) {
                    getIndicatorPopup().show(getTargetContext().getTargetNode(), screenX, screenY);
                }

                @Override
                protected Pane createIndicatorPane() {
                    Pane p = new Pane();
                    p.getStyleClass().add("drag-pane-indicator");
                    return p;
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
            return positionIndicator;
        }
*/
        @Override
        public Object getRestorePosition(Dockable dockable) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void restore(Dockable dockable, Object restoreposition) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }//class

    public static class Container implements ChangeListener<Number> {

        private PopupControl popup;
        private final Dockable dockable;
        private final FloatView windowBuilder;

        private final BooleanProperty hideOnExitProperty = new SimpleBooleanProperty(false);
        private final BooleanProperty floatingProperty = new SimpleBooleanProperty(false);

        private EventHandler<MouseEvent> mouseExitEventListener;

        public Container(Dockable dockable) {
            this.dockable = dockable;
            //stageBuilder = new StageBuilder(dockable);
            windowBuilder = new FloatPopupControlView2(dockable);
        }

        public PopupControl getPopup() {
            return popup;
        }

        public void setPopup(PopupControl popup) {
            this.popup = popup;
        }

        public void setDocked(boolean docked) {
            DockSideBar sb = getSideBar();
            if (sb.isHideOnExit()) {
                addMouseExitListener();
            }
            hideOnExitProperty.bind(sb.hideOnExitProperty());
            floatingProperty.bind(dockable.getDockableContext().floatingProperty());
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
            return (DockSideBar) dockable.getDockableContext().getTargetContext().getTargetNode();
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
            if (!ev.isPrimaryButtonDown() && !dockable.getDockableContext().isFloating()) {
                dockable.node().getScene().getWindow().hide();
            }
            ev.consume();
        }

        public void adjustScreenPos() {
            SidePaneContext handler = (SidePaneContext) dockable.getDockableContext().getTargetContext();
            Window ownerStage = (Window) ((DockSideBar) handler.getTargetNode()).getScene().getWindow();

            ownerStage.xProperty().addListener(this);
            ownerStage.yProperty().addListener(this);
            ownerStage.widthProperty().addListener(this);
            ownerStage.heightProperty().addListener(this);

            changeSize();

        }

        public void removeListeners() {
            SidePaneContext handler = (SidePaneContext) dockable.getDockableContext().getTargetContext();
            Window ownerStage = (Window) ((DockSideBar) handler.getTargetNode()).getScene().getWindow();
            ownerStage.xProperty().removeListener(this);
            ownerStage.yProperty().removeListener(this);
            ownerStage.widthProperty().removeListener(this);
            ownerStage.heightProperty().removeListener(this);
            //stageBuilder.removeListeners(dockable);
        }

        protected void changeSide() {
            SidePaneContext handler = (SidePaneContext) dockable.getDockableContext().getTargetContext();
            windowBuilder.setSupportedCursors(handler.getSupportedCursors());

        }

        public void changeSize() {
            //System.err.println("+++++ CHANGE SIZE 111 ");
            if (dockable.node().getScene() == null || dockable.node().getScene().getWindow() == null) {
                //return;
            }
            if (getPopup() == null) {
                return;
            }
            //PopupControl popup = (PopupControl) dockable.node().getScene().getWindow();

            DockSideBar sb = (DockSideBar) dockable.getDockableContext().getTargetContext().getTargetNode();
            System.err.println("!!!!!!!!!!!  changeSize SIDE=" + sb.getSide());
            if (!popup.isShowing()) {
                System.err.println("!!!!!!!!!!! popup isShowing=" + popup.isShowing());
                return;
            }
            Pane root = (Pane) popup.getScene().getRoot();
            Point2D pos = sb.localToScreen(0, 0);
            switch (sb.getSide()) {
                case TOP:
                    popup.setAnchorX(pos.getX());
                    popup.setAnchorY(pos.getY() + sb.getHeight());
                    root.setPrefWidth(sb.getWidth());
                    break;
                case BOTTOM:
                    popup.setAnchorX(pos.getX());
                    popup.setAnchorY(pos.getY() - popup.getHeight());
                    root.setPrefWidth(sb.getWidth());
                    break;
                case RIGHT:
                    System.err.println("!!!!!!!!!!! RIGHT changeSize popup isShowing=" + popup.isShowing());

                    popup.setAnchorY(pos.getY());
                    popup.setAnchorX(pos.getX() - popup.getWidth());
                    root.setPrefHeight(sb.getHeight());
                    break;
                case LEFT:
                    System.err.println("!!!!!!!!!!! LEFT changeSize popup isShowing=" + popup.isShowing());

                    popup.setAnchorY(pos.getY());
                    popup.setAnchorX(pos.getX() + sb.getWidth());
                    root.setPrefHeight(sb.getHeight());
                    break;
            }
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            changeSize();
        }

        /*        public StageBuilder getFloatView() {
            return windowBuilder;
        }
         */
        public FloatView getFloatView() {
            return windowBuilder;
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

    public static class SideBarPositonIndicator extends PositionIndicator {

        private Rectangle tabDockPlace;
        private IndicatorPopup indicatorPopup;
        
        public SideBarPositonIndicator(TargetContext context) {
            super(context);
            //this.targetContext = targetContext;
        }
        private IndicatorPopup getIndicatorPopup() {
            if ( indicatorPopup == null) {
                indicatorPopup = getTargetContext().getLookup().lookup(IndicatorPopup.class);
            }
            return indicatorPopup;
        }

        @Override
        public void showIndicator(double screenX, double screenY) {
            getIndicatorPopup().show(getTargetContext().getTargetNode(), screenX, screenY);
        }

        @Override
        protected Pane createIndicatorPane() {
            Pane p = new Pane();
            p.getStyleClass().add("drag-pane-indicator");
            return p;
        }

        //@Override
        /*protected String getStylePrefix() {
                    return "dock-indicator";
                }
         */
        protected Rectangle getTabDockPlace() {
            if (tabDockPlace == null) {
                tabDockPlace = new Rectangle();
                tabDockPlace.getStyleClass().add("dock-place");
                getIndicatorPane().getChildren().add(tabDockPlace);
            }
            return tabDockPlace;
        }

        @Override
        public void hideDockPlace() {
            getDockPlace().setVisible(false);
            getTabDockPlace().setVisible(false);

        }
        protected int indexOf(double x, double y) {
            int idx = -1;
            ToolBar tb = ((DockSideBar) getTargetContext().getTargetNode()).getDelegate();
            Node sb = ((SidePaneContext)getTargetContext()).findNode(tb.getItems(), x, y);
            if (sb != null && (sb instanceof Group)) {
                idx = tb.getItems().indexOf(sb);
            } else if (sb == null && DockUtil.contains(tb, x, y)) {
                idx = tb.getItems().size();
            }
            return idx;
        }

        @Override
        public void showDockPlace(double x, double y) {
            ToolBar tb = ((DockSideBar) getTargetContext().getTargetNode()).getDelegate();

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

    }
}//class
