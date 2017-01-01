package org.vns.javafx.dock.api;

import java.util.Properties;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.DockTitleBar;
import org.vns.javafx.dock.api.properties.DockFloatingProperty;
import org.vns.javafx.dock.api.properties.DockPaneHandlerProperty;
import org.vns.javafx.dock.api.properties.DockResizableProperty;
import org.vns.javafx.dock.api.properties.DockedProperty;
import org.vns.javafx.dock.api.properties.TitleBarProperty;

/**
 *
 * @author Valery
 */
public class DockNodeHandler  {
    
    private final TitleBarProperty<Region> titleBarProperty;

    private final StringProperty titleProperty = new SimpleStringProperty("");
    private Dockable dockable;
    private final DockFloatingProperty floatingProperty = new DockFloatingProperty(false);
    private final DockedProperty dockedProperty = new DockedProperty(false);
    private final DockResizableProperty resizableProperty = new DockResizableProperty(true);

    private boolean usedAsDockTarget = true;
    
    private DragTransformer dragTransformer;

    //private PaneDelegate paneDelegate;
    /**
     * Last dock target pane
     */
    //private DockPaneHandler originalPaneHandler;

    private final DockPaneHandlerProperty<DockPaneHandler> paneHandler = new DockPaneHandlerProperty<>();

    private String dockPos;
    
    private Properties properties;

    public DockNodeHandler(Dockable dockable) {
        this.dockable = dockable;
        titleBarProperty = new TitleBarProperty(dockable.node());
        init();
    }

    private void init() {
        dockedProperty.addListener(this::dockedChanged);
        dragTransformer = getDragTransformer();
        titleBarProperty.addListener(this::titlebarChanged);
        //paneHandler.addListener(this::paneHandlerChanged);
    }
    public Node getDragSource() {
        return getDragTransformer().getDragSource();
    }
    public void setDragSource(Node dragSource) {
        getDragTransformer().setDragSource(dragSource);
    }    
    protected DragTransformer getDragTransformer() {
        if ( dragTransformer == null ) {
            dragTransformer = new DragTransformer(dockable);
        }
        return dragTransformer;
    }
    public boolean isUsedAsDockTarget() {
        return usedAsDockTarget;
    }
    
    public void setUsedAsDockTarget(boolean usedAsDockTarget) {
        this.usedAsDockTarget = usedAsDockTarget;
    }

/*    protected void paneHandlerChanged(ObservableValue<? extends DockPaneHandler> observable, DockPaneHandler oldValue, DockPaneHandler newValue) {
        originalPaneHandler = oldValue;
    }
*/
    public String getDockPos() {
        return dockPos;
    }

    public Properties getProperties() {
        if ( properties == null ) {
            properties = new Properties();
        }
        return properties;
    }

    public void setDockPos(String dockPos) {
        this.dockPos = dockPos;
    }

/*    public DockPaneHandler getOrigionalPaneDelegate() {
        return originalPaneHandler;
    }
*/
    public TitleBarProperty<Region> titleBarProperty() {
        return titleBarProperty;
    }
    public StringProperty titleProperty() {
        return titleProperty;
    }
    public String getTitle() {
        return titleProperty.get();
    }
    public void setTitle(String title) {
        this.titleProperty.set(title);
    }
    public DockPaneHandler getPaneHandler() {
        return paneHandler.get();
    }

    public void setPaneHandler(DockPaneHandler paneHandler) {
        this.paneHandler.set(paneHandler);
    }

    protected void dockedChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            getPaneHandler().remove(node());
        }
    }


    public Dockable dockable() {
        return this.dockable;
    }

    public Region node() {
        return dockable.node();
    }

    public Region getTitleBar() {
        return titleBarProperty.get();
    }

    public void setTitleBar(Region node) {
        titleBarProperty.set(node);
    }

    public boolean addTitleBar(int idx, Region newTitleBar, ObservableList children) {
        if (titleBarProperty.get() != null) {
            return false;
        }
        children.add(idx, newTitleBar);
        titleBarProperty.set(newTitleBar);
        return true;
    }

    public boolean replaceTitleBar(int idx, Region newTitleBar, ObservableList children) {
        if (titleBarProperty.get() == null) {
            return false;
        }
        Node oldNode = titleBarProperty.get();
        int oldIdx = children.indexOf(oldNode);
        if (oldIdx < 0) {
            return false;
        }
        children.set(idx, newTitleBar);
        titleBarProperty.set(newTitleBar);
        return true;
    }

    public DockFloatingProperty floatingProperty() {
        return floatingProperty;
    }

    public boolean isFloating() {
        return this.floatingProperty.get();
    }

    public void undock() {
        if (!isDocked()) {
            return;
        }
        setDocked(false);
        /*        if (owner != null && (owner instanceof MultiTab)) {
            ((MultiTab) owner).undock(dockable());
        } else {
            parent.remove(node());
        }
         */
        getPaneHandler().remove(node());

        //!!!!!!!! must we assign null to owner ?????
    }
    
    
    public void setFloating(boolean floating) {
        if (!isFloating() && floating) {
            StateTransformer t = getStateTransformer();
            t.makeFloating();
            floatingProperty.set(floating);
        } else if ( ! floating ) {
            floatingProperty.set(floating);
        }
    }
    public StateTransformer getStateTransformer() {
        return new StateTransformer(this);
    }
    public DockedProperty dockedProperty() {
        return dockedProperty;
    }

    public boolean isResizable() {
        return resizableProperty.get();
    }

    public void setResizable(boolean resizable) {
        resizableProperty.set(resizable);
    }

    public boolean isDocked() {

        if (!isFloating() && getPaneHandler() == null) {
            return false;
        }
        if (isFloating()) {
            return false;
        }
        if (!dockedProperty.get()) {
            return false;
        }
        return true;
        //return !isFloating() && parent.parentSplitPane(node()) != null;
    }

    public void setDocked(boolean docked) {
//        owner = null;
        this.dockedProperty.set(docked);
    }


    public Region createDefaultTitleBar(String title) {
        DockTitleBar tb = new DockTitleBar(dockable());
        tb.setId("FIRST");
        tb.getLabel().textProperty().bind(titleProperty);
        titleProperty.set(title);
        titleBarProperty().set(tb);
        return tb;
    }

    public Dockable getImmediateParent(Node node) {
        Dockable retval = dockable();
        if (immediateParent != null) {
            retval = immediateParent.apply(node);
        }
        return retval;
    }

    private Function<Node, Dockable> immediateParent = null;

    public void setImmediateParentFunction(Function<Node, Dockable> f) {
        immediateParent = f;
    }

    protected void titlebarChanged(ObservableValue ov, Node oldValue, Node newValue) {
        dragTransformer.titlebarChanged(ov, oldValue, newValue);
        /*        if ( oldValue != null && newValue == null ) {
           // getChildren().remove(oldValue);
        } else if ( oldValue != null && newValue != null ) {
            //getChildren().set(0,newValue);
        } else if ( oldValue == null && newValue != null ) {
            //getChildren().add(0,newValue);
        }
         */
    }

}
