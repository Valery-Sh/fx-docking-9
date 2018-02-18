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
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.DockSideBar.Rotation;

/**
 *
 * @author Valery
 */
public class DockSideBarSkin1 extends SkinBase<DockSideBar> {

    private final ToolBar toolBar;
    private final StackPane layout;
    private Group dragNodeGroup;

    public DockSideBarSkin1(DockSideBar control) {
        super(control);

        Dockable dc = DockRegistry.makeDockable(control);
        //dc.getContext().setResizable(false);

        toolBar = new ToolBar();
        dragNodeGroup = new Group();
        toolBar.getItems().add(dragNodeGroup);
        if (getSkinnable().getDragNode() != null) {
            toolBar.getItems().add(0, dragNodeGroup);
        }

        toolBar.setOrientation(getSkinnable().getOrientation());
        SideBarContext targetContext = new SideBarContext(getSkinnable(), toolBar);
        targetContext.getItemMap().keySet().forEach(g -> {
            Button btn = (Button) g.getChildren().get(0);
            btn.setRotate(getSkinnable().getRotation().getAngle());
        });

        DockRegistry.makeDockTarget(getSkinnable(), targetContext);

        layout = new StackPane(toolBar) {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                System.err.println("STACKPANE layout change");
//                updateDragButton();
                //updateLayout(getSkinnable().getScene().getWindow());
                super.layoutChildren();
                updateDragButton();
                if (Dockable.of(control).getContext().isFloating()) {
                    System.err.println("1 STACKPANE layout change");
                    control.getScene().getWindow().setWidth(prefWidth(getHeight()));
                    control.getScene().getWindow().setHeight(prefHeight(getWidth()));
                    System.err.println("   ---  w = " + getWidth());
                    System.err.println("   ---  win w =" + control.getScene().getWindow().getWidth());
                    System.err.println("   ---  h     = " + getWidth());
                    System.err.println("   ---  win h =" + control.getScene().getWindow().getHeight());
                    System.err.println("-----------------------------------");
                    System.err.println("   ---  skin w = " + getSkinnable().getWidth());
                    System.err.println("   ---  win w =" + control.getScene().getWindow().getWidth());
                    System.err.println("   ---  skin h     = " + getSkinnable().getWidth());
                    System.err.println("   ---  win h =" + control.getScene().getWindow().getHeight());
                    System.err.println("===================================-");
                }

            }
        };
        changeItems();
        getSkinnable().getItems().addListener(this::itemsChanged);

        getSkinnable().setMaxWidth(USE_PREF_SIZE);
        getSkinnable().setStyle("-fx-background-color: green");

        getSkinnable().sideProperty().addListener((v, ov, nv) -> {
            getTargetContext().getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        getSkinnable().dragNodeProperty().addListener((v, ov, nv) -> {
            if (ov != null) {
                dragNodeGroup.getChildren().clear();
            }
            if (nv != null) {
                dragNodeGroup.getChildren().add(0, nv);

                nv.setRotate(getSkinnable().getRotation().getAngle());
                Node dn = nv;
                if (!(nv instanceof Region)) {
                    dn = nv.getParent();
                }
                Dockable.of(control).getContext().setDragNode(nv);
            }

        }
        );

        getSkinnable().rotationProperty().addListener((v, ov, nv) -> {
            getTargetContext().getItemMap().keySet().forEach(g -> {
                Button btn = (Button) g.getChildren().get(0);
                btn.setRotate(getSkinnable().getRotation().getAngle());
            });

            getTargetContext().getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });
        getSkinnable().orientationProperty().addListener((v, ov, nv) -> {
            toolBar.setOrientation(nv);
        });
        toolBar.orientationProperty().addListener((v, ov, nv) -> {
            if (Dockable.of(getSkinnable()).getContext().isFloating()) {
                if (nv == Orientation.HORIZONTAL) {
                    getSkinnable().setMaxHeight(Region.USE_PREF_SIZE);
                    getSkinnable().setMaxWidth(Region.USE_PREF_SIZE);
                } else {
                    getSkinnable().setMaxHeight(Region.USE_PREF_SIZE);
                    getSkinnable().setMaxWidth(Region.USE_PREF_SIZE);
                }

            } else {
                if (nv == Orientation.HORIZONTAL) {
                    getSkinnable().setMaxHeight(USE_PREF_SIZE);
                    getSkinnable().setMaxWidth(Region.USE_COMPUTED_SIZE);
                } else {
                    getSkinnable().setMaxHeight(Region.USE_COMPUTED_SIZE);
                    getSkinnable().setMaxWidth(Region.USE_PREF_SIZE);

                }
            }
            getTargetContext().getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });

        getChildren().add(layout);
    }

    protected SideBarContext getTargetContext() {
        return (SideBarContext) DockTarget.of(getSkinnable()).getTargetContext();
    }

    protected void itemsChanged(ListChangeListener.Change<? extends Dockable> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                List<? extends Dockable> list = change.getRemoved();
                for (Dockable d : list) {
                    getTargetContext().undock(d.node());
                }

            }
            if (change.wasAdded()) {
                List<? extends Dockable> list = change.getAddedSubList();
                for (Dockable d : list) {
                    getTargetContext().dock(d);
                }
            }
        }
    }

    private void changeItems() {
        for (Dockable d : getSkinnable().getItems()) {
            if (!getTargetContext().isDocked(d.node())) {
                getTargetContext().dock(d);
            }
        }
    }//while

    protected void updateDragButton() {
        Node dragNode = null;
        if (!dragNodeGroup.getChildren().isEmpty()) {
            dragNode = dragNodeGroup.getChildren().get(0);
        } else {
            return;
        }
        ImageView iv = null;
        Labeled lb = null;

        Group ivGroup = null;//new Group(iv);
        if (dragNode != null && (dragNode instanceof Labeled)) {
            lb = (Labeled) dragNode;
            if (lb.getGraphic() instanceof ImageView) {
                iv = (ImageView) lb.getGraphic();
                ivGroup = new Group(iv);
                lb.setGraphic(ivGroup);
            } else if (lb.getGraphic() instanceof Group) {
                lb = (Labeled) dragNode;
                ivGroup = (Group) lb.getGraphic();
                if (!ivGroup.getChildren().isEmpty()) {
                    iv = (ImageView) ivGroup.getChildren().get(0);
                }
            }
        } else if (dragNode instanceof ImageView) {
            iv = (ImageView) dragNode;
            ivGroup = new Group(iv);
            lb = new Button();
            lb.setGraphic(ivGroup);
            dragNodeGroup.getChildren().clear();
            dragNodeGroup.getChildren().add(lb);
            Dockable.of(getSkinnable()).getContext().setDragNode(lb);
        }
        if (iv == null) {
            return;
        }

        lb.setGraphic(ivGroup);
        Labeled item = null;

        if (toolBar.getItems().size() > 1) {
            item = (Labeled) ((Group) toolBar.getItems().get(1)).getChildren().get(0);
        }

        switch (getSkinnable().getRotation()) {
            case DEFAULT:
                if (toolBar.getOrientation() == Orientation.HORIZONTAL) {
                    lb.setRotate(Rotation.DEFAULT.getAngle());
                    lb.setPadding(new Insets(0, item.getPadding().getRight(), 0, item.getPadding().getLeft()));
                    iv.setRotate(Rotation.DEFAULT.getAngle());
                }
                break;
            case UP_DOWN:
                lb.setRotate(Rotation.UP_DOWN.getAngle());
                iv.setRotate(Rotation.DOWN_UP.getAngle());
                if (item != null) {
                    lb.setPadding(new Insets(item.getPadding().getBottom(), 0, item.getPadding().getTop(), 0));
                }
                break;
            case DOWN_UP:
                lb.setRotate(Rotation.DOWN_UP.getAngle());
                iv.setRotate(Rotation.UP_DOWN.getAngle());
                if (item != null) {
                    lb.setPadding(new Insets(item.getPadding().getTop(), 0, item.getPadding().getBottom(), 0));
                }
                break;
            default:
                break;
        }
    }

    protected void createDragNode() {
        dragNodeGroup.getChildren().add(getSkinnable().getDragNode());
    }

}
