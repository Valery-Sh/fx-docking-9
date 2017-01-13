package org.vns.javafx.dock.api.properties;

import javafx.beans.property.BooleanProperty;
import org.vns.javafx.dock.DockTitleBar;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
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

    //private boolean activeChoosedPseudoClass;
    
    private BooleanProperty activeChoosedPseudoClassProperty = new SimpleBooleanProperty();
            
    public TitleBarProperty(Dockable owner) {
        super();
        ownerWrapper.set(owner);
        init();
    }
    public TitleBarProperty(Region owner) {
        super();
        //ownerWrapper.set(owner);
        init();
    }

    public Region getTitleBar() {
        return get();
    }

    private void init() {
        addListener(this::titlebarChanged);
        //choosedProperty.addListener(this::choosedPropertyInvalidated);
        activeChoosedPseudoClassProperty.set(false);
    }

    public boolean isActiveChoosedPseudoClass() {
        return activeChoosedPseudoClassProperty.get();
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
        this.activeChoosedPseudoClassProperty.set(newValue);
            
    }

    public BooleanProperty activeChoosedPseudoClassProperty() {
        return activeChoosedPseudoClassProperty;
    }
    protected void turnOffChoosedPseudoClass() {
        get().pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, false);                
    }

    protected void turnOnChoosedPseudoClass() {
        if ( activeChoosedPseudoClassProperty.get() ) {
            return;
        }
        get().pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, true);                
   }

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public String getName() {
        return "titleBar";
    }


    public boolean isDefaultTitleBar() {
        return get() != null && (get() instanceof DockTitleBar);
    }

    public void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        if ( oldValue != null ) {
            Node f = DockUtil.getImmediateParent(oldValue, nd -> {
                return (nd instanceof Dockable);
            });
            
            if ( f != null ) {
                DockRegistry.dockable(f).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(false);
            }
        }
        if (newValue != null ) {
            Node f = DockUtil.getImmediateParent(newValue, nd -> {
                return (nd instanceof Dockable);
            });
            
            if ( f != null ) {
                DockRegistry.dockable(f).nodeHandler().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        }
    }
}
