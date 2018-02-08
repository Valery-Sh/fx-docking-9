/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class TabPaneContext extends TargetContext implements ObjectReceiver {

    public TabPaneContext(Node targetNode) {
        super(targetNode);
        init();
    }

    public TabPaneContext(Dockable dockable) {
        super(dockable);
        init();
    }

    private void init() {
        TabPane pane = (TabPane) getTargetNode();
        pane.getTabs().forEach(tab -> {
            tab.getStyleClass().add("tab-uuid-" + UUID.randomUUID());
        });
        pane.getTabs().addListener(new TabsChangeListener());
    }

    @Override
    public boolean isAcceptable(Dockable dockable) {
        if (dockable instanceof DragContainer) {
            return false;
        }
        boolean retval = false;
        DragContainer dc = dockable.getDockableContext().getDragValue();
        Object v = dc.getValue();

        if (v != null && (dc.isValueDockable())) {
            retval = true;
        } else if ((v instanceof Tab) && !dc.isValueDockable()) {
            retval = true;
        }
        System.err.println("*************************** IS ACEPTABLE retval = " + retval);

        return retval;
    }

    @Override
    public void dock(Point2D mousePos, Dockable dockable) {
        Dockable d = dockable;
        DragContainer dc = dockable.getDockableContext().getDragValue();
        if (dc.getValue() != null) {
            if (!dc.isValueDockable() && (dc.getValue() instanceof Tab)) {
                dock(mousePos, (Tab) dc.getValue(), dockable);
                return;
            }
            d = Dockable.of(dc.getValue());
        }
        if (isDocked(d.node())) {
            System.err.println("TargetContext isDocked == TRUE for node = " + d.node());
            return;
        }
        Node node = d.node();
        Window stage = null;
        if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
            stage = node.getScene().getWindow();
        }

        if (doDock(mousePos, d.node()) && stage != null) {
            //d.getDockableContext().setFloating(false);
            if ((stage instanceof Stage)) {
                ((Stage) stage).close();
            } else {
                stage.hide();
            }
            d.getDockableContext().setTargetContext(this);
        }
    }

    public void dock(Point2D mousePos, Tab tab, Dockable dockable) {
        Window window = dockable.getDockableContext().getDragValue().getFloatingWindow();
        System.err.println("winndow = " + window);
        if (doDock(mousePos, tab) && window != null) {
            if ((window instanceof Stage)) {
                ((Stage) window).close();
            } else {
                window.hide();
            }
            //dockable.getDockableContext().setFloating(false);
            //d.getDockableContext().setTargetContext(this);
        }
    }

    protected boolean doDock(Point2D mousePos, Tab tab) {
        boolean retval = false;
        int idx = -1;
        TabPane pane = (TabPane) getTargetNode();
        if ( getHeaderArea(mousePos.getX(), mousePos.getY()) != null ) {
            idx = pane.getTabs().size();
            Tab t = getTab(mousePos.getX(), mousePos.getY());
            if ( t != null ) {
                idx = pane.getTabs().indexOf(t);
            }
        }
        if ( idx >= 0 ) {
            pane.getTabs().add(idx,tab);
            retval = true;
        }
        return retval;
    }

    @Override
    protected boolean doDock(Point2D mousePos, Node node) {
        boolean retval = true;

        return retval;
    }

    @Override
    public Object getRestorePosition(Dockable dockable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restore(Dockable dockable, Object restoreposition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove(Node dockNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dockObject(Point2D mousePos, Dockable carrier) {
        DragContainer dc = carrier.getDockableContext().getDragValue();
        if (dc.getValue() != null && (dc.getValue() instanceof Tab)) {
            ((TabPane) getTargetNode()).getTabs().add((Tab) dc.getValue());
        }
    }

    @Override
    public void undockObject(Dockable carrier) {
        DragContainer dc = carrier.getDockableContext().getDragValue();
        if (dc.getValue() != null && (dc.getValue() instanceof Tab)) {
            ((TabPane) getTargetNode()).getTabs().remove(dc.getValue());
        }
    }

    //
    //
    //
    public int getTabIndex(Node tabNode) {
        int retval = -1;
        TabPane pane = (TabPane) getTargetNode();
        String style = getUUIDStyle(tabNode);
        if (style != null) {
            for (Tab tab : pane.getTabs()) {
                String tabStyle = getUUIDStyle(tab);
                if (style.equals(tabStyle)) {
                    retval = pane.getTabs().indexOf(tab);
                    break;
                }
            }
        }
        return retval;
    }

    public Tab getTabBy(Node tabNode) {
        int idx = getTabIndex(tabNode);
        if (idx < 0) {
            return null;
        }
        return ((TabPane) getTargetNode()).getTabs().get(idx);
    }

    private String getUUIDStyle(Node node) {
        String retval = null;
        for (String s : node.getStyleClass()) {
            if (s.startsWith("tab-uuid-")) {
                retval = s;
                break;
            }
        }
        return retval;
    }

    private String getUUIDStyle(Tab tab) {
        String retval = null;
        for (String s : tab.getStyleClass()) {
            if (s.startsWith("tab-uuid-")) {
                retval = s;
                break;
            }
        }
        return retval;
    }

/*    private boolean areSame(Tab tab, Node tabNode) {
        String tabStyle = null;
        for (String s : tab.getStyleClass()) {
            if (s.startsWith("tab-uuid-")) {
                tabStyle = s;
                break;
            }
        }
        if (tabStyle == null) {
            return false;
        }
        boolean retval = false;
        for (String s : tabNode.getStyleClass()) {
            if (s.startsWith("tab-uuid-") && s.equals(tabStyle)) {
                retval = true;
                break;
            }
        }
        return retval;

    }
*/    
    protected Node getHeaderArea(double screenX, double screenY) {
        //System.err.println("getHeaderArea");
        Node retval = getTargetNode().lookup(".tab-header-area");
        if (retval == null || ! DockUtil.contains(retval, screenX, screenY) ) {
            retval = null;
        }
        //System.err.println("getHeaderArea retvale=" + retval);
        return retval;
    }

    protected Node getHeadersRegion(double screenX, double screenY) {

        Node retval = getTargetNode().lookup(".headers-region");
        //System.err.println("getHeaderArea node=" + retval);

        if (retval == null || !DockUtil.contains(retval, screenX, screenY )) {
            retval = null;
        }
        //System.err.println("getHeaderArea retval=" + retval);

        return retval;
    }

    protected Tab getTab(double screenX, double screenY) {
        Tab retval = null;
        Set<Node> set = getTargetNode().lookupAll(".tab");
        Node tabNode = null;
        for (Node node : set) {
            if (DockUtil.contains(node, screenX, screenY)) {
                tabNode = node;
                break;
            }
        }
        String style = null;
        if (tabNode != null) {
            for (String s : tabNode.getStyleClass()) {
                if (s.startsWith("tab-uuid-")) {
                    style = s;
                    break;
                }
            }
            TabPane pane = (TabPane) getTargetNode();
/*            for (Tab tab : pane.getTabs()) {
                System.err.println("tab.id = " + tab.getId());
                for (String s : tab.getStyleClass()) {
                    System.err.println("   --- style = " + s);
                }
            }
*/
            if (style != null) {
                for (Tab tab : pane.getTabs()) {
                    for (String s : tab.getStyleClass()) {
                        if (s.equals(style)) {
                            retval = tab;
                            break;
                        }
                    }
                }
            }
        }
        return retval;
    }

    public static class TabsChangeListener implements ListChangeListener<Tab> {

        @Override
        public void onChanged(Change<? extends Tab> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends Tab> list = change.getRemoved();

                    for (Tab tab : list) {
                        String uuidStyle = null;
                        for (String style : tab.getStyleClass()) {
                            if (style.startsWith("tab-uuid-")) {
                                uuidStyle = style;
                                break;
                            }
                        }
                        if (uuidStyle != null) {
                            tab.getStyleClass().remove(uuidStyle);
                        }
                        //targetContext.undock(d.node());
                    }

                }
                if (change.wasAdded()) {
                    List<? extends Tab> list = change.getAddedSubList();
                    for (Tab tab : list) {
                        tab.getStyleClass().add("tab-uuid-" + UUID.randomUUID());
                    }
                }
            }//while
        }
    }
}
