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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.StackPane;
import org.vns.javafx.dock.DockSideBar;

/**
 *
 * @author Valery
 */
public class DockSideBarSkin extends SkinBase<DockSideBar> {

    private final ToolBar toolBar;// = getSkinnable().getToolBar();
    private final StackPane layout;

    public DockSideBarSkin(DockSideBar control) {
        super(control);

        toolBar = new ToolBar();
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
            if ( nv == Orientation.HORIZONTAL) {
                getSkinnable().setMaxHeight(USE_PREF_SIZE);
                getSkinnable().setMaxWidth(Region.USE_COMPUTED_SIZE);
            } else {
                getSkinnable().setMaxHeight(Region.USE_COMPUTED_SIZE);
                getSkinnable().setMaxWidth(Region.USE_PREF_SIZE);
                
            }
            getTargetContext().getItemMap().values().forEach(d -> {
                d.changeSize();
                d.changeSide();
            });
        });

        getChildren().add(layout);
    }


    /*    protected void sceneChanged(Scene oldValue, Scene newValue) {
        newValue.windowProperty().addListener((v, ov, nv) -> {
            windowChanged(ov, nv);
        });
    }

    protected void windowChanged(Window oldValue, Window newValue) {
        Window window = (Window) newValue;
        window.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, this::windowClicked);
        if (window instanceof Stage) {
            ((Stage) window).setTitle("owner");
        }
        getSideItems().values().forEach(d -> {
            d.adjustScreenPos();
        });
    }

    protected void windowClicked(MouseEvent ev) {
        if (getSkinnable().localToScreen(getSkinnable().getBoundsInLocal()).contains(ev.getScreenX(), ev.getScreenY())) {
            return;
        }

        ((SideBarContext)DockTarget.of(getSkinnable()).getTargetContext()).getItemMap().forEach((g, d) -> {
            if (d.getDockable().node().getScene() != null && d.getDockable().node().getScene().getWindow() != null) {
                Window w = d.getDockable().node().getScene().getWindow();
                if (w instanceof Stage) {
                    //??? 11.08((Stage) w).close();
                } else {
                    //??? 11.08 w.hide();
                }
            }
        });
    }
    protected ObservableMap<Group, SideBarContext.Container> getSideItems() {
        return getTargetContext().getItemMap();
    }
    
     */
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
                System.err.println("LIST size=" + list.size());
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
                System.err.println("DOCKED " + d.node());
            }
        }
    }//while

}
