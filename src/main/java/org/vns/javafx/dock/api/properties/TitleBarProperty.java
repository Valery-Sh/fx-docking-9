package org.vns.javafx.dock.api.properties;

import org.vns.javafx.dock.DockTitleBar;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TitleBarProperty<T extends Region> extends ObjectPropertyBase<T> {

    public static final PseudoClass CHOOSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("choosed");

    //private final BooleanProperty choosedProperty = new SimpleBooleanProperty(false);// {

    private final ReadOnlyObjectWrapper<Dockable> ownerWrapper = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyObjectProperty<Dockable> ownerProperty = ownerWrapper.getReadOnlyProperty();

    private boolean activeChoosedPseudoClass;

    public TitleBarProperty(Dockable owner) {
        super();
        ownerWrapper.set(owner);
        init();
    }

    public Region getTitleBar() {
        return get();
    }

    private void init() {
        addListener(this::titlebarChanged);
        //choosedProperty.addListener(this::choosedPropertyInvalidated);
        activeChoosedPseudoClass = false;
    }

    public boolean isActiveChoosedPseudoClass() {
        return activeChoosedPseudoClass;
    }

    public void setActiveChoosedPseudoClass(boolean newValue) {
        if ( get() == null ) {
            return;
        }
        if ( newValue ) {
            turnOnChoosedPseudoClass();
        } else {
            turnOffChoosedPseudoClass();
        }
        this.activeChoosedPseudoClass = newValue;
            
    }

    
/*    protected void choosedPropertyInvalidated(Observable obsv) {
        if (getOwner() == null || ! activeChoosedPseudoClass ) {
            return;
        }
        get().pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, choosedProperty.get());

        if (choosedProperty.get()) {
            getOwner().stateProperty().getNode().toFront();
        }
    }
*/
    protected void turnOffChoosedPseudoClass() {
        //choosedProperty.removeListener(this::choosedPropertyInvalidated);
        //choosedProperty.set(false);
        get().pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, false);                
    }

    protected void turnOnChoosedPseudoClass() {
        if ( activeChoosedPseudoClass ) {
            return;
        }
       // choosedProperty.set(false);
        //choosedProperty.addListener(this::choosedPropertyInvalidated);
        get().pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, true);                
        //choosedProperty.set(true);
        
   }

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public String getName() {
        return "titleBar";
    }

    public ReadOnlyObjectProperty<Dockable> ownerProperty() {
        return ownerProperty;
    }

    public Dockable getOwner() {
        return ownerProperty.get();
    }

    public void changeOwner(Dockable newOwner) {
        ownerWrapper.set(newOwner);
        //get().applyCss();

    }

/*    public BooleanProperty choosedProperty() {
        return choosedProperty;
    }
*/
    public boolean isDefaultTitleBar() {
        return get() != null && (get() instanceof DockTitleBar);
    }

    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
/*        if (oldValue != null && oldValue.getParent() != null && (oldValue.getParent() instanceof Pane)) {
            int idx = ((Pane) oldValue.getParent()).getChildren().indexOf(oldValue);
            if (idx >= 0) {
                Pane pane = (Pane) oldValue.getParent();
                ((Pane) oldValue.getParent()).getChildren().remove(oldValue);
                if (newValue != null) {
                    pane.getChildren().add(idx, newValue);
                }
            }
        }
*/
        if ( oldValue != null ) {
            //Node f = DockUtil.getFocusedDockable(oldValue);
            Node f = DockUtil.getImmediateParent(oldValue, nd -> {
                return (nd instanceof Dockable);
            });
            
            if ( f != null ) {
                ((Dockable)f).stateProperty().titleBarProperty().setActiveChoosedPseudoClass(false);
            }
        }
        if (newValue != null ) {
            //Node f = DockUtil.getFocusedDockable(newValue);
            Node f = DockUtil.getImmediateParent(newValue, nd -> {
                return (nd instanceof Dockable);
            });
            
            if ( f != null ) {
                ((Dockable)f).stateProperty().titleBarProperty().setActiveChoosedPseudoClass(true);
            }

            
        }
    }
}
