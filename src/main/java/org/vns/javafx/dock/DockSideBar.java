package org.vns.javafx.dock;

import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.DockRegistry;

import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockSideBarSkin;
import org.vns.javafx.dock.api.SideBarContext;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty(value = "items")
public class DockSideBar extends Control implements Dockable { // ListChangeListener {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");

    private DockableContext context = new DockableContext(this);

    private SideBarContext targetContext;
    
    private Orientation orientation = Orientation.VERTICAL;

    private final ObjectProperty<Side> sideProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<Rotation> rotationProperty = new SimpleObjectProperty<>();

    private final BooleanProperty hideOnExitProperty = new SimpleBooleanProperty(false);

    private final ToolBar toolBar = new ToolBar();

    private final ObservableList<Dockable> items = FXCollections.observableArrayList();
    

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

    public DockSideBar() {
        init();
    }

/*    public DockSideBar(Dockable... items) {
        super();
        this.items.addAll(items);
        init();
    }
*/
    private void init() {
        
        DockRegistry.makeDockable(this);
        context = Dockable.of(this).getContext();
        
        targetContext = new SideBarContext(this);
        DockRegistry.makeDockTarget(this, targetContext);
        
        setOrientation(Orientation.VERTICAL);
        getStyleClass().clear();
        getStyleClass().add("dock-side-bar");

/*        sceneProperty().addListener((v, ov, nv) -> {
            sceneChanged(ov, nv);
        });
*/
/*        sideProperty.addListener((v, ov, nv) -> {
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
        getToolBar().orientationProperty().addListener((v, ov, nv) -> {
            targetContext.getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
*/        
        setSide(Side.TOP);
        setRotation(Rotation.DEFAULT);

        items.addListener(this::itemsChanged);
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public ObservableList<Dockable> getItems() {
        return items;
    }
    public void addItems(Node... node) {
        for ( Node n : node ) {
            getItems().add( Dockable.of(n));
        }
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
        //addMouseExitListener();
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


/*    @Override
    public TargetContext getTargetContext() {
        return this.targetContext;
    }
*/

/*    protected Button getButton(Dockable dockable) {
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
*/    
    public ObjectProperty<Side> sideProperty() {
        return sideProperty;
    }
    public Side getSide() {
        return sideProperty.get();
    }

    public void setSide(Side side) {
        sideProperty.set(side);
    }

    public Orientation getOrientation() {
        return toolBar.getOrientation();
    }

    public void setOrientation(Orientation orientation) {
        toolBar.setOrientation(orientation);
    }
    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableContext getContext() {
        return this.context;
    }

/*    @Override
    public Region target() {
        return this;
    }
*/

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockSideBarSkin(this);
    }


    @Override
    protected double computePrefHeight(double h) {
        return toolBar.prefHeight(h);
    }

    @Override
    protected double computePrefWidth(double w) {
        return toolBar.prefWidth(w);
    }

    @Override
    protected double computeMinHeight(double h) {
        return toolBar.minHeight(h);
    }

    @Override
    protected double computeMinWidth(double w) {
        return toolBar.minWidth(w);
    }

    @Override
    protected double computeMaxHeight(double h) {
        return toolBar.maxHeight(h);
    }

    @Override
    protected double computeMaxWidth(double w) {
        return toolBar.maxWidth(w);
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

}//class
