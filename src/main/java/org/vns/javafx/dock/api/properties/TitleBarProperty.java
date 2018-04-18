package org.vns.javafx.dock.api.properties;

import javafx.beans.property.BooleanProperty;
import org.vns.javafx.dock.DockTitleBar;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */

public class TitleBarProperty extends ObjectPropertyBase<Node> {

    public static final PseudoClass CHOOSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("choosed");

    private final ReadOnlyObjectWrapper<Dockable> ownerWrapper = new ReadOnlyObjectWrapper<>();

    private final BooleanProperty activeChoosedPseudoClass = new SimpleBooleanProperty();
            
    public TitleBarProperty(Dockable owner) {
        super();
        ownerWrapper.set(owner);
        init();
    }
    public TitleBarProperty(Node owner) {
        super();
        init();
    }

    public Node getTitleBar() {
        return get();
    }

    private void init() {
        addListener(this::titlebarChanged);
        activeChoosedPseudoClass.set(false);
    }
    
    public BooleanProperty activeChoosedPseudoClassProperty() {
        return activeChoosedPseudoClass;
    }
    
    public boolean isActiveChoosedPseudoClass() {
        return activeChoosedPseudoClass.get();
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
        this.activeChoosedPseudoClass.set(newValue);
            
    }

    protected void turnOffChoosedPseudoClass() {
        get().pseudoClassStateChanged(CHOOSED_PSEUDO_CLASS, false);                
    }

    protected void turnOnChoosedPseudoClass() {
        if ( activeChoosedPseudoClass.get() ) {
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
                return (Dockable.of(nd) != null);
            });
            
            if ( f != null ) {
                Dockable.of(f).getContext().titleBarProperty().setActiveChoosedPseudoClass(false);
            }
        }
        if (newValue != null ) {
            Node f = DockUtil.getImmediateParent(newValue, nd -> {
                return (Dockable.of(nd) != null);
            });
            
            if ( f != null ) {
                //Dockable.of(f).getContext().titleBarProperty().setActiveChoosedPseudoClass(true);
            }
        }
    }
}
