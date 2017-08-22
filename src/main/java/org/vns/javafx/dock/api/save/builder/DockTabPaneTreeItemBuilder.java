/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api.save.builder;

import java.util.Properties;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.DockTabPane;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.save.AbstractDockTreeItemBuilder;
import org.vns.javafx.dock.api.save.DockTreeItemBuilder;
import static org.vns.javafx.dock.api.save.DockTreeItemBuilder.FIELD_NAME_ATTR;
import static org.vns.javafx.dock.api.save.DockTreeItemBuilder.OBJECT_ATTR;

public class DockTabPaneTreeItemBuilder extends AbstractDockTreeItemBuilder {

        public DockTabPaneTreeItemBuilder(DockTarget tabPane) {
            super(tabPane);
        }

        @Override
        public Node restore(TreeItem<Properties> targetRoot) {
            Properties props = targetRoot.getValue();
            if (!(props.get(OBJECT_ATTR) instanceof DockTabPane)) {
                return null;
            }
            DockTabPane pane = (DockTabPane) props.get(OBJECT_ATTR);
            targetRoot.setExpanded(true);

            pane.getTabs().clear();
            for (int i = 0; i < targetRoot.getChildren().size(); i++) {

                TreeItem<Properties> item = targetRoot.getChildren().get(i);
                //for (TreeItem<Properties> item : targetRoot.getChildren()) {
                Tab tab = (Tab) item.getValue().get(OBJECT_ATTR);

                if (tab == null) {
                    tab = buildTab(item);
                    //pane.getTabs().add(tab);
                }

                if (tab.getContent() == null) {
                    pane.getTabs().add(i, tab);
                    return pane;
                }

                if (DockRegistry.instanceOfDockTarget(tab.getContent())) {
                    tab.setContent(restore(item));
                    pane.getTabs().add(i, tab);
                } else if (DockRegistry.instanceOfDockable(tab.getContent())) {
                    if (DockRegistry.dockable(tab.getContent()).getDockableContext().getTargetContext() != null) {
                        TargetContext c = DockRegistry.dockable(tab.getContent()).getDockableContext().getTargetContext();
                        if (c != getDockTarget().getTargetContext()) {
                            c.undock(tab.getContent());
                        }
                    }
                    String fnm = item.getValue().getProperty(FIELD_NAME_ATTR);
                    pane.dock(i, DockRegistry.dockable(tab.getContent()));
                    //pane.getTabs().add(tab);
                } else {
                    pane.getTabs().add(i, tab);
                }
            }
            return pane;
        }

        protected Tab buildTab(TreeItem<Properties> tabItem) {
            Tab tab = (Tab) tabItem.getValue().get(OBJECT_ATTR);
            if (tab == null) {
                tab = buildTabInstance(tabItem);
                tabItem.getValue().put(OBJECT_ATTR, tab);
            }
            if (tabItem.getChildren().isEmpty()) {
                return tab;
            }
            TreeItem<Properties> item = tabItem.getChildren().get(0);
            item.setExpanded(true);
            Node node = (Node) item.getValue().get(OBJECT_ATTR);
            if (node == null) {
                return tab;
            }
            if (DockRegistry.instanceOfDockTarget(node)) {
                node = restore(item);
                tab.setContent(node);
            } else if (DockRegistry.instanceOfDockable(node)) {
                tab.setContent(node);
            }
            return tab;
        }

        protected Tab buildTabInstance(TreeItem<Properties> sourceItem) {
            sourceItem.setExpanded(true);
            Tab tab = new Tab();
            tab.setId(sourceItem.getValue().getProperty("id"));
            return tab;
        }

        /*
        private void setProperties(PreferencesItem it) {
            DockTabPane tabPane = (DockTabPane) it.getItemObject();
            //it.getProperties().put("tabPane-pref-width", String.valueOf(tabPane.getWidth()));
            //it.getProperties().put("tabPane-pref-height", String.valueOf(tabPane.getHeight()));
            it.getProperties().put("tabPane-min-width", String.valueOf(tabPane.getMinWidth()));
            it.getProperties().put("tabPane-max-width", String.valueOf(tabPane.getMaxWidth()));
        }

        public Map<String, String> getProperties(Object node) {
            Map<String, String> props = FXCollections.observableHashMap();
            if (node instanceof DockTabPane) {
                //props.put("tabPane-pref-width", String.valueOf(((DockTabPane) tab).getWidth()));
                //props.put("tabPane-pref-height", String.valueOf(((DockTabPane) tab).getHeight()));
                props.put("tabPane-min-width", String.valueOf(((DockTabPane) node).getMinWidth()));
                props.put("tabPane-max-width", String.valueOf(((DockTabPane) node).getMaxWidth()));
            }
            return props;
        }
         */
        @Override
        public TreeItem<Properties> build(String fieldName) {
            Node node = getDockTarget().target();
            TreeItem<Properties> retval = DockTreeItemBuilder.build(fieldName, node);
            retval.setExpanded(true);
            int sz = retval.getChildren().size();
            //setObjectProperties(retval.getValue());        
            buildChildren(retval);
            return retval;

        }

        protected void buildChildren(TreeItem<Properties> root) {
            //DockPane tab = (DockPane) getTargetContext().getTargetNode();
            DockTabPane pane = (DockTabPane) getDockTarget().target();
            for (int i = 0; i < pane.getTabs().size(); i++) {
                TreeItem ti = DockTreeItemBuilder.build(pane.getTabs().get(i));
                root.getChildren().add(ti);
                ti.setExpanded(true);

                notifyOnBuidItem(ti);

                if (DockRegistry.instanceOfDockTarget(pane.getTabs().get(i).getContent())) {
                    Node node = pane.getTabs().get(i).getContent();
                    TreeItem contentItem = getDockTreeItemBuilder(node).build();
                    ti.getChildren().add(contentItem);
                } else if (DockRegistry.instanceOfDockable(pane.getTabs().get(i).getContent())) {
                    TreeItem contentItem = DockTreeItemBuilder.build(pane.getTabs().get(i).getContent());
                    ti.getChildren().add(contentItem);
                }
            }
        }
    }
