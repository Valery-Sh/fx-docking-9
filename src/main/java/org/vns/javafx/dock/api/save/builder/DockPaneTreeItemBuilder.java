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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;
import org.vns.javafx.dock.api.DockPaneContext;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.DockSplitPane;
import org.vns.javafx.dock.api.DockTarget;
import org.vns.javafx.dock.api.TargetContext;
import org.vns.javafx.dock.api.save.AbstractDockTreeItemBuilder;
import org.vns.javafx.dock.api.save.DockTreeItemBuilder;
import static org.vns.javafx.dock.api.save.DockTreeItemBuilder.CLASS_NAME_ATTR;
import static org.vns.javafx.dock.api.save.DockTreeItemBuilder.OBJECT_ATTR;

/**
 *
 * @author Valery
 */
public class DockPaneTreeItemBuilder  extends AbstractDockTreeItemBuilder {

        public static final String DIVIDER_POSITIONS = "dividerPositions";
        public static final String ORIENTATION = "orientation";

        public DockPaneTreeItemBuilder(DockTarget dockTarget) {
            super(dockTarget);
        }
        private DockSplitPane getRoot() {
            return ((DockPaneContext)getDockTarget().getTargetContext()).getRoot();
        }
        @Override
        public TreeItem<Properties> build(String fieldName) {

            //Node node = getTargetContext().getTargetNode();
            Node node = getDockTarget().target();
            TreeItem<Properties> retval = DockTreeItemBuilder.build(fieldName, node);
            setObjectProperties(retval.getValue());
            buildChildren(retval);
            return retval;
        }

        protected void buildChildren(TreeItem<Properties> root) {
            //DockPane pane = (DockPane) getTargetContext().getTargetNode();
            DockPane pane = (DockPane) getDockTarget().target();
            for (int i = 0; i < pane.getItems().size(); i++) {
                TreeItem ti = DockTreeItemBuilder.build(pane.getItems().get(i));

                root.getChildren().add(ti);
                ti.setExpanded(true);

                notifyOnBuidItem(ti);

                if (pane.getItems().get(i) instanceof DockSplitPane) {
                    setObjectProperties((Properties) ti.getValue());
                    buildPane((SplitPane) pane.getItems().get(i), root, ti);
                } else if (DockRegistry.instanceOfDockTarget(pane.getItems().get(i))) {
                    DockTarget t = DockRegistry.dockTarget(pane.getItems().get(i));
                    TreeItem it = getDockTreeItemBuilder(t.target()).build();
                    ti.getChildren().addAll(it.getChildren());
                }
            }
        }

        protected void buildPane(SplitPane pane, TreeItem<Properties> root, TreeItem<Properties> parent) {
            Properties props;
            for (int i = 0; i < pane.getItems().size(); i++) {
                System.err.println("1) buildPane i = " + pane.getItems().get(i));
                TreeItem ti;
                if (DockRegistry.instanceOfDockTarget(pane.getItems().get(i))) {
                    System.err.println("2) buildPane = " + pane.getItems().get(i));
                    Node node = DockRegistry.dockTarget(pane.getItems().get(i)).target();
                    ti = getDockTreeItemBuilder(node).build();
                    System.err.println("1) ti = " + ti.getChildren().size());
                } else {
                    System.err.println("3) buildPane i = " + pane.getItems().get(i));
                    ti = DockTreeItemBuilder.build(pane.getItems().get(i));
                    System.err.println("2) ti = " + ti.getChildren().size());
                }

                props = (Properties) ti.getValue();
                parent.getChildren().add(ti);

                ti.setExpanded(true);

                notifyOnBuidItem(ti);

                if (pane.getItems().get(i) instanceof DockSplitPane) {
                    setObjectProperties(props);
                    buildPane((SplitPane) pane.getItems().get(i), root, ti);
                }
            }
        }

        protected void setObjectProperties(Properties props) {
            SplitPane sp;
            if (props.get(OBJECT_ATTR) instanceof DockPane) {
                sp = getRoot();
            } else {
                sp = (SplitPane) props.get(OBJECT_ATTR);
            }
            String[] strDp = new String[sp.getDividerPositions().length];

            props.put(ORIENTATION, sp.getOrientation().toString());
            for (int i = 0; i < sp.getDividerPositions().length; i++) {
                strDp[i] = String.valueOf(sp.getDividerPositions()[i]);
            }
            if (strDp.length > 0) {
                props.put(DIVIDER_POSITIONS, String.join(",", strDp));
            }
        }

        public Map<String, String> getProperties(Object obj) {
            Map<String, String> props = FXCollections.observableHashMap();
            if (obj instanceof SplitPane) {
                SplitPane sp = (SplitPane) obj;
                props.put(ORIENTATION, sp.getOrientation().toString());
                if (sp.getDividerPositions().length != 0) {
                    String[] s = new String[sp.getDividerPositions().length];
                    Arrays.setAll(s, (idx) -> {
                        return String.valueOf(sp.getDividerPositions()[idx]);
                    });
                    String dp = String.join(",", s);
                    props.put(DIVIDER_POSITIONS, dp);
                }
            }
            return props;
        }

        private void addDividersListener(SplitPane splitPane) {

            ListChangeListener<Node> itemsListener = (ListChangeListener.Change<? extends Node> change) -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends Node> list = change.getRemoved();
                        for (Node node : list) {
                            //getDockLoader().layoutChanged(dockPane);
                        }
                    }
                    if (change.wasAdded()) {
                        //System.err.println("2 addDividersListener added");
                        List<? extends Node> list = change.getAddedSubList();
                        for (Node node : list) {
                            //getDockLoader().layoutChanged(dockPane);
                        }
                    }
                }//while
            };

            ChangeListener<Number> posListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                //getDockLoader().layoutChanged(dockPane);
            };

            ListChangeListener<SplitPane.Divider> divListListener;
            divListListener = (ListChangeListener.Change<? extends SplitPane.Divider> change) -> {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        List<? extends SplitPane.Divider> list = change.getRemoved();
                        if (!list.isEmpty()) {
                        }
                        for (SplitPane.Divider dvd : list) {
                            dvd.positionProperty().removeListener(posListener);
                            //getDockLoader().layoutChanged(dockPane);
                        }
                    }
                    if (change.wasAdded()) {
                        List<? extends SplitPane.Divider> list = change.getAddedSubList();
                        for (SplitPane.Divider dvd : list) {
                            dvd.positionProperty().addListener(posListener);
                            //getDockLoader().layoutChanged(dockPane);
                        }
                    }
                }//while
            };
            splitPane.getDividers().addListener(divListListener);
            splitPane.getItems().addListener(itemsListener);
        }

        @Override
        public Node restore(TreeItem<Properties> targetRoot) {
            Properties props = targetRoot.getValue();
            if (!(props.get(OBJECT_ATTR) instanceof DockPane)) {
                return null;
            }
            DockPane pane = (DockPane) props.get(OBJECT_ATTR);
            targetRoot.setExpanded(true);
            //addDividersListener(pane, getRoot());
            addDividersListener(getRoot());

            pane.getItems().clear();

            String dp = props.getProperty(DIVIDER_POSITIONS);
            if (dp != null && !dp.trim().isEmpty()) {
                String[] dps = dp.split(",");
                double[] dpd = new double[dps.length];
                Arrays.setAll(dpd, i -> {
                    return Double.valueOf(dps[i]);
                });
                getRoot().setDividerPositions(dpd);
            }
            for (TreeItem<Properties> item : targetRoot.getChildren()) {
                Node node = (Node) item.getValue().get(OBJECT_ATTR);
                if (node == null || (node instanceof DockSplitPane) && !DockRegistry.instanceOfDockable(node)) {
                    node = buildSplitPane(item);
                    System.err.println("++++++ node=" + node);
                    pane.getItems().add(node);
                } else if (DockRegistry.instanceOfDockTarget(node)) {
                    node = getDockTreeItemBuilder(node)
                            .restore(item);
                    System.err.println("1) ++++++ node=" + node);

                    pane.getItems().add(node);

                } else if (DockRegistry.instanceOfDockable(node)) {
                    if (DockRegistry.dockable(node).getDockableContext().getTargetContext() != null) {
                        TargetContext c = DockRegistry.dockable(node).getDockableContext().getTargetContext();
                        if (c != getDockTarget().getTargetContext()) {
                            c.undock(node);
                        }
                    }
                    pane.getItems().add(node);
                } else {
                    System.err.println("4) --- node = " + node);
                    pane.getItems().add(node);
                }
            }
            return pane;
        }

        protected Node buildSplitPane(TreeItem<Properties> splitPaneItem) {
            DockSplitPane pane = (DockSplitPane) splitPaneItem.getValue().get(OBJECT_ATTR);
            if (pane == null) {
                pane = buildSplitPaneInstance(splitPaneItem);
                splitPaneItem.getValue().put(OBJECT_ATTR, pane);
            }
            pane.getItems().clear();
            for (TreeItem<Properties> item : splitPaneItem.getChildren()) {
                Node node = (Node) item.getValue().get(OBJECT_ATTR);
                if (node == null) {
                    node = buildSplitPane(item);
                    item.getValue().put(OBJECT_ATTR, node);
                    pane.getItems().add(node);
                } else if ((node instanceof DockSplitPane) && !DockRegistry.instanceOfDockable(node)) {
                    node = buildSplitPane(item);
                    pane.getItems().add(node);
                } else if (DockRegistry.instanceOfDockTarget(node)) {
                    node = getDockTreeItemBuilder(node)
                            .restore(item);
                    System.err.println("1) buildSplitPane ++++++ node=" + node);

//                    node = restore(item);
                    pane.getItems().add(node);
                } else {
                    pane.getItems().add(node);
                }
            }
            return pane;
        }

        protected DockSplitPane buildSplitPaneInstance(TreeItem<Properties> sourceItem) {
            Properties props = sourceItem.getValue();
            sourceItem.setExpanded(true);
            DockSplitPane pane = null;
            String className = sourceItem.getValue().getProperty(CLASS_NAME_ATTR);
            if (VPane.class.getName().equals(className)) {
                pane = new VPane();
            } else if (HPane.class.getName().equals(className)) {
                pane = new HPane();
            } else if (DockSplitPane.class.getName().equals(className)) {
                pane = new DockSplitPane();
            }
            System.err.println("!!!!!!!! BUIKD SplitPane " + pane);
            if (pane == null) {
                return null; // ????
            }

            pane.setId(sourceItem.getValue().getProperty("id"));

            //addDividersListener((DockPane) DockPaneContext.this.getTargetNode(), pane);
            addDividersListener(pane);

            String p = props.getProperty(ORIENTATION);
            if (p == null || "HORIZONTAL".equals(p)) {
                pane.setOrientation(Orientation.HORIZONTAL);
            } else {
                pane.setOrientation(Orientation.VERTICAL);
            }

            p = props.getProperty(DIVIDER_POSITIONS);
            if (p != null && !p.trim().isEmpty()) {
                String[] dps = p.split(",");
                double[] dpd = new double[dps.length];
                Arrays.setAll(dpd, i -> {
                    return Double.valueOf(dps[i]);
                });
                pane.setDividerPositions(dpd);
            }
            return pane;
        }

    }