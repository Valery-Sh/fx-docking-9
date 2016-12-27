package org.vns.javafx.dock.api;

import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;

/**
 *
 * @author Valery Shyshkin
 */
public class DockTab extends DockNodeBase implements DockConverter{
    
    private final Dockable content;
    private double titleBarMinHeight;
    private double titleBarPrefHeight;
    private boolean titleBarVisible;
            
    public DockTab(Dockable content) {
        this.content = content; 
        init();
    }
    public void hideContentTitleBar() {
        Region tb = content.nodeHandler().getTitleBar();
        tb.setVisible(false);
        tb.setMinHeight(0);
        tb.setPrefHeight(0);
    }
    public void showContentTitleBar() {
        Region tb = content.nodeHandler().getTitleBar();
        tb.setVisible(titleBarVisible);
        tb.setMinHeight(titleBarMinHeight);
        tb.setPrefHeight(titleBarPrefHeight);
    }
    
    private void init() {
        setTitle(content.nodeHandler().getTitle());
        Region tb = content.nodeHandler().getTitleBar();
        titleBarMinHeight = tb.getMinHeight();
        titleBarPrefHeight = tb.getPrefHeight();
        titleBarVisible = tb.isVisible();
        
        nodeHandler().dockedProperty().addListener(this::dockedChanged);
    }
    public Dockable getContent() {
        return content;
    }
    
    protected void dockedChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (! newValue) {
            content.nodeHandler().titleBarProperty().get().setVisible(true);
            getChildren().remove(content.node());
        } else {
            content.nodeHandler().titleBarProperty().get().setVisible(false);
        }
    }

    @Override
    public Dockable convert(Dockable source, int when) {
        Dockable retval = source;
        switch(when) {
            case BEFORE_DOCK: 
                retval = content;
                content.nodeHandler().getTitleBar().setVisible(true);
                break;
        }
        return retval;
    }
    
    
}
