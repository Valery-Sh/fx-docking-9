package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.vns.javafx.dock.api.ContextLookup;
import org.vns.javafx.dock.api.DefaultContextLookup;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DockSideBarSkin;

/**
 *
 * @author Valery Shyshkin
 */
@DefaultProperty(value = "items")
public class DockSideBar extends Control { // ListChangeListener {

    public static final PseudoClass TABOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("tabover");
    
    private final ContextLookup lookup;
    
    private final ObjectProperty<Side> side = new SimpleObjectProperty<>();

    private final ObjectProperty<Rotation> rotation = new SimpleObjectProperty<>();
    
    private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>();
    
    private final BooleanProperty hideOnExit = new SimpleBooleanProperty(false);


    private final ObservableList<Dockable> items = FXCollections.observableArrayList();

    private final ObjectProperty<Node> dragNode = new SimpleObjectProperty<>();
    
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
        lookup = new DefaultContextLookup();
        init();
    }

    private void init() {
        
        setOrientation(Orientation.VERTICAL);
        getStyleClass().clear();
        getStyleClass().add("dock-side-bar");

        setSide(Side.TOP);
        setRotation(Rotation.DEFAULT);
        
        Dockable dc = DockRegistry.makeDockable(this);
        dc.getContext().setDragNode(null);
        //DockSideBarContext layoutContext = new DockSideBarContext(this, toolBar);
        //DockRegistry.makeDockLayout(control, layoutContext);
        
        
    }

    public ContextLookup getLookup() {
        return lookup;
    }
    
    public ObjectProperty<Node> dragNodeProperty() {
        return dragNode;
    }
    public Node getDragNode() {
        return dragNode.get();
    }
    /**
     * Try to use nodes of type {@code javafx.scene.control.Labeled } or 
     * {@code javafx.scene.image.ImageView }.
     * 
     * @param dragNode the node to become drag node 
     */
    public void setDragNode(Node dragNode) {
        this.dragNode.set(dragNode);
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

    public ObjectProperty<Rotation> rotationProperty() {
        return rotation;
    }

    public BooleanProperty hideOnExitProperty() {
        return hideOnExit;
    }

    public boolean isHideOnExit() {
        return hideOnExit.get();
    }

    public void setHideOnExit(boolean value) {
        //addMouseExitListener();
        hideOnExit.set(value);
    }

    public Rotation getRotation() {
        return rotation.get();
    }

    public void setRotation(Rotation rotation) {
        this.rotation.set(rotation);
    }

    public ObjectProperty<Side> sideProperty() {
        return side;
    }
    public Side getSide() {
        return side.get();
    }

    public void setSide(Side side) {
        this.side.set(side);
    }
    
    public ObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }
    public Orientation getOrientation() {
        return orientation.get();
    }

    public void setOrientation(Orientation orientation) {
        this.orientation.set(orientation);
        
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockSideBarSkin(this);
    }
}//class
