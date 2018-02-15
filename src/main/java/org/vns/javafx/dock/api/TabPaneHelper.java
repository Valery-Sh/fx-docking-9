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
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.DockUtil;

/**
 *
 * @author Valery
 */
public class TabPaneHelper {

    private final TabPaneContext context;

    public TabPaneHelper(TabPaneContext context) {
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

    public TabPaneContext getContext() {
        return context;
    }

    public Node getHeaderArea(double screenX, double screenY) {
        Node retval = getContext().getTargetNode().lookup(".tab-header-area");
        if (retval == null || !DockUtil.contains(retval, screenX, screenY)) {
            retval = null;
        }
        return retval;
    }

    public Node getHeadersRegion(double screenX, double screenY) {

        Node retval = getContext().getTargetNode().lookup(".headers-region");
        if (retval == null || !DockUtil.contains(retval, screenX, screenY)) {
            retval = null;
        }
        return retval;
    }

    public Node getTabNode(Tab tab) {
        return tab.getTabPane().lookup("." + getUUIDStyle(tab));
    }

    public Tab getTab(double screenX, double screenY) {
        Tab retval = null;
        Set<Node> set = getContext().getTargetNode().lookupAll(".tab");
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
            TabPane pane = (TabPane) getContext().getTargetNode();
            for (Tab tab : pane.getTabs()) {
                //System.err.println("tab.id = " + tab.getId());
                for (String s : tab.getStyleClass()) {
                    //System.err.println("   --- style = " + s);
                }
            }

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
}
