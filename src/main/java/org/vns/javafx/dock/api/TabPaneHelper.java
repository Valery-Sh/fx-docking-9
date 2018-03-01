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

import java.util.Set;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery Shyshkin
 */
public class TabPaneHelper {

    private final LayoutContext context;

    public TabPaneHelper(LayoutContext context) {
        this.context = context;
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

    public LayoutContext getContext() {
        return context;
    }

    public Node getHeaderArea(double screenX, double screenY) {
        Node retval = getContext().getLayoutNode().lookup(".tab-header-area");
        if (retval == null || !DockUtil.contains(retval, screenX, screenY)) {
            retval = null;
        }
        return retval;
    }

    public Node getHeaderArea() {
        return getContext().getLayoutNode().lookup(".tab-header-area");
    }

    public Node getHeadersRegion() {
        return getContext().getLayoutNode().lookup(".headers-region");
    }

    public Node getHeadersRegion(double screenX, double screenY) {

        Node retval = getContext().getLayoutNode().lookup(".headers-region");
        if (retval == null || !DockUtil.contains(retval, screenX, screenY)) {
            retval = null;
        }
        return retval;
    }

    public Node getTabNode(Tab tab) {
        if (tab == null) {
            return null;
        }
        return tab.getTabPane().lookup("." + getUUIDStyle(tab));
    }

    public Tab getTab(double screenX, double screenY) {
        Tab retval = null;
        Set<Node> set = getContext().getLayoutNode().lookupAll(".tab");
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
            TabPane pane = (TabPane) getContext().getLayoutNode();
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

    public Node getControlButtonsTab() {
        return getContext().getLayoutNode().lookup(".control-buttons-tab ");
    }

    public Node getControlButtonsTab(double screenX, double screenY) {

        Node retval = getContext().getLayoutNode().lookup(".control-buttons-tab ");
        if (retval == null || !DockUtil.contains(retval, screenX, screenY)) {
            retval = null;
        }
        return retval;
    }

    public Bounds tabBounds(Tab tab) {
        if (tab == null) {
            return null;
        }
        if (!(getContext().getLayoutNode() instanceof TabPane)) {
            return null;
        }
        Node tabNode = getTabNode(tab);

        if (!((TabPane) getContext().getLayoutNode()).getTabs().contains(tab) || tabNode == null) {
            return null;
        }
        return tabNode.localToScreen(tabNode.getBoundsInLocal());

    }

    public Bounds tabBounds(double screenX, double screenY) {
        Tab tab = getTab(screenX, screenY);
        return tabBounds(tab);
    }

    public Bounds headerAreaBounds() {
        Node node = getHeaderArea();
        if (node == null) {
            return null;
        }
        return node.localToScreen(node.getBoundsInLocal());
    }

    public Bounds headerRegionBounds() {
        Node node = getHeadersRegion();
        if (node == null) {
            return null;
        }
        return node.localToScreen(node.getBoundsInLocal());
    }

    public Bounds headersRegionBounds(double screenX, double screenY) {
        Node node = getHeadersRegion(screenX, screenY);
        if (node == null) {
            return null;
        }
        return node.localToScreen(node.getBoundsInLocal());
    }

    public Bounds headerAreaBounds(double screenX, double screenY) {
        Node node = getHeaderArea(screenX, screenY);
        if (node == null) {
            return null;
        }
        return node.localToScreen(node.getBoundsInLocal());
    }

    public Bounds controlButtonBounds() {
        Node node = getControlButtonsTab();
        if (node == null) {
            return null;
        }
        return node.localToScreen(node.getBoundsInLocal());
    }

    public Bounds controlButtonBounds(double screenX, double screenY) {
        Node node = getControlButtonsTab(screenX, screenY);
        if (node == null) {
            return null;
        }
        return node.localToScreen(node.getBoundsInLocal());
    }

/*    public int getTabIndex(Node tabNode) {
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
*/
}
