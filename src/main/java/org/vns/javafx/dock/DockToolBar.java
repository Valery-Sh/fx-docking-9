package org.vns.javafx.dock;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DockToolBar extends ToolBar implements Dockable{

    StringProperty title = new SimpleStringProperty("Tool Bar Enabled");
    DockableContext dockableContext = new DockableContext(this);
    
    public DockToolBar() {
        init();
    }
    private void init() {
        Separator titleBar = new Separator();
        titleBar.setPrefWidth(USE_PREF_SIZE);
        dockableContext.setTitleBar(titleBar);
        Button b1 = new Button("",new Circle(0, 0, 4));
        Button b2 = new Button("", new Rectangle(0,0,8,8));
        getItems().addAll(b1,b2, new Separator(), titleBar);
    }
    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }
    
    public StringProperty titleProperty() {
        return title;
    }
    public String getTitle() {
        return title.get();
    }
    public void setTitle(String title) {
        this.title.set(title);
    }
    
   
    public void useAsTitleBar(Region titleBar) {
        dockableContext.setTitleBar(titleBar);
    }

    @Override
    public Region node() {
        return this;
    }

    @Override
    public DockableContext getContext() {
        return dockableContext;
    }
}
