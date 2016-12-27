package org.vns.javafx.dock;

import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.api.DockNodeHandler;
import org.vns.javafx.dock.api.DockPaneHandler;
import org.vns.javafx.dock.api.DockPaneTarget;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.StageRegistry;

/**
 *
 * @author Valery Shyshkin
 */
public class DockSideBar extends StackPane implements DockPaneTarget {

    private DockPaneHandler paneHandler;
    private Side side = Side.RIGHT;
    private Orientation orientation = Orientation.VERTICAL;
    
    public DockSideBar() {
        init();
        
    }

    private void init() {
        paneHandler = new SideBarDelegate(this);
    }

    @Override
    public DockPaneHandler paneHandler() {
        return this.paneHandler;
    }
    
    public ObservableMap<Group,Dockable> getItems() {
        return ((SideBarDelegate)paneHandler).getItems();
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
    
    public Dockable dock(Node dockable, Side dockPos) {
         //!!! inherit DockPaneTarget.super.dock(dockable, dockPos); 
         return dock(dockable);
    }

    public Dockable dock(Node dockable) {
        return paneHandler.dock(dockable, null);
        //!!! inherit DockPaneTarget.super.dock(dockable);
    }

    @Override
    public Pane pane() {
        return this;
    }

    public static class SideBarDelegate extends DockPaneHandler {
        
        private final  ObservableMap<Group,Dockable> items = FXCollections.observableHashMap();
        
        private final ToolBar toolBar;
        
        public SideBarDelegate(DockSideBar dockPane) {
            super(dockPane);
            toolBar = new ToolBar();
            dockPane.getChildren().add(toolBar);
            toolBar.setOrientation(dockPane.getOrientation());
/*            Button b = new Button("VALERA");
            if ( dockPane.getOrientation() == Orientation.VERTICAL && dockPane.getSide() == Side.RIGHT) {
                b.setRotate(90);
            }
            toolBar.getItems().add(new Group(b));
*/
        }

        @Override
        protected void inititialize() {
            StageRegistry.start();
        }

        @Override
        public boolean isUsedAsDockTarget() {
            return false;
        }

        @Override
        protected boolean isDocked(Node node) {
            return items.values().contains((Dockable)node);
        }

        @Override
        public void undock(Node node) {
            if (!isDocked(node)) {
                return;
            }
            if (node instanceof Dockable) {
                ((Dockable) node).nodeHandler().undock();
            }
        }
        
        @Override
        public Dockable dock(Node node, Side dockPos) {
            if (true) return null;
            if (isDocked(node)) {
                return null;
            }
            ((Dockable)node).nodeHandler().setFloating(true);
            if ( (node instanceof Dockable)&& ((Dockable)node).nodeHandler().isDocked()) {
                //return;
            }
            //getDockPane().setVisible(false);
/*            if (node.getScene() != null && node.getScene().getWindow() != null && (node.getScene().getWindow() instanceof Stage)) {
                ((Stage) node.getScene().getWindow()).close();
            }
*/            
            if (node instanceof Dockable) {
                //((Dockable) node).getDockState().setFloating(false);
            }
            
            if (node instanceof Dockable) {
                Button btn = new Button();
                btn.setText(getButtonText((Dockable) node));
                Node grf = getButtonIcon((Dockable) node);
                if ( grf != null ) {
                    btn.setGraphic(node);
                }
                Group group = new Group(btn);
                toolBar.getItems().add(group);
                items.put(group,(Dockable)node);
                if ( toolBar.getOrientation() == Orientation.VERTICAL && ((DockSideBar)getDockPane()).getSide() == Side.RIGHT) {
                   btn.setRotate(90);
                }
            
                
                DockNodeHandler state = ((Dockable) node).nodeHandler();
                if (state.getPaneHandler() == null || state.getPaneHandler() != this) {
                    state.setPaneHandler(this);
                }
                state.setDocked(true);
            }
            return null;
        }
        protected String getButtonText(Dockable d ) {
            String txt = d.nodeHandler().getTitle();
            if ( d.nodeHandler().getProperties().getProperty("short-title") != null ) {
                txt = d.nodeHandler().getProperties().getProperty("short-title");
            }
            return txt;
        }
        protected Node getButtonIcon(Dockable d) {
            return null;
        }
        

        @Override
        public void remove(Node dockNode) {
            Group r = null;
            
            for ( Map.Entry<Group,Dockable>  en : items.entrySet()) {
                if ( en.getValue() == dockNode ) {
                    r = en.getKey();
                    break;
                }
            }
            if ( r != null ) {
                items.remove(r);
                toolBar.getItems().remove(r);
            }
        }
        protected ObservableMap<Group,Dockable> getItems() {
            return items;
        }    
    }//class

}//class
