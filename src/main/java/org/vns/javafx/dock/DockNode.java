package org.vns.javafx.dock;

import javafx.beans.DefaultProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "content")
public class DockNode extends TitledPane implements Dockable {

    DockNodeHandler nodeHandler;
    
    private VBox delegate;

    public DockNode() {
        init(null, null);

    }

    public DockNode(String title) {
        init(null, title);

    }

    

    public DockNode(String id, String title) {
        init(null, title);
    }


    private void init(String id, String title) {
        this.setContent(new StackPane());
        contentProperty().addListener(this::contentChanged);
        getStyleClass().add("dock-node");
        nodeHandler = new DockNodeHandler(this);
        Region titleBar = new DockTitleBar(this);
    
        nodeHandler.setTitleBar(titleBar);

        setTitle(title);
        if (id != null) {
            setId(id);
        }
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    protected void contentChanged(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        if (oldValue != null) {
            getDelegate().getChildren().remove(oldValue);
        } else if (newValue != null) {
            getDelegate().getChildren().clear();
        }
    }

    protected VBox getDelegate() {
        if (delegate == null) {
            delegate = new VBox();
        }
        return delegate;
    }

    public String getTitle() {
        return nodeHandler.getTitle();
    }

    public void setTitle(String title) {
        nodeHandler.setTitle(title);
    }

    public Region getTitleBar() {
        return nodeHandler().getTitleBar();
    }

    public void setTitleBar(Region node) {
        nodeHandler().setTitleBar(node);
    }

    public boolean isRemoveTitleBar() {
        return getTitleBar() == null;
    }

    public void setRemoveTitleBar(boolean remove) {
        if (remove) {
            setTitleBar(null);
        }
    }

/*    public double getDividerPos() {
        return nodeHandler.getDividerPos();
    }

    public void setDividerPos(double divpos) {
        this.nodeHandler.setDividerPos(divpos);
    }
*/
    public Node getDragNode() {
        return nodeHandler.getDragNode();
    }

    public void setDragNode(Node dragSource) {
        nodeHandler.setDragNode(dragSource);
    }

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockNodeHandler nodeHandler() {
        return nodeHandler;
    }

    
    @Override
    protected Skin<?> createDefaultSkin() {
        return new DockNodeSkin(this);
    }

    public static class DockNodeSkin extends SkinBase<DockNode> {

        public DockNodeSkin(DockNode control) {
            super(control);
            if (!getChildren().isEmpty()) {
                getChildren().clear();
            }
            getChildren().add(control.getDelegate());
            if ( control.getTitleBar() != null ) {
                control.getDelegate().getChildren().add(control.getTitleBar());
            }
            control.getDelegate().getChildren().add(control.getContent());
        }
    }
}
