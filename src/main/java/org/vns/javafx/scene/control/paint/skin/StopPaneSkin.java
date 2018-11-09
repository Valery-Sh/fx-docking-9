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
package org.vns.javafx.scene.control.paint.skin;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.vns.javafx.scene.control.paint.Util;
import org.vns.javafx.scene.control.paint.ColorPane;
import org.vns.javafx.scene.control.paint.StopPane;

/**
 *
 * @author Nastia
 */
public class StopPaneSkin  extends SkinBase<StopPane> {

        double startMousePos = 0;
        boolean dragDetected = false;
//        boolean update = false;

        private final ToggleButton stop0Button;
        private final ToggleButton stop1Button;
        private final ToggleGroup toggleGroup;
        private final StopPane control;
        private AnchorPane anchor;
        private Shape selPointer;
        private Text stopValue;

        public StopPaneSkin(StopPane control) {
            super(control);
            this.control = control;
            toggleGroup = new ToggleGroup();
            stop0Button = createStopButon(Color.BLACK);
            stop0Button.getStyleClass().addAll("stop0");
            stop0Button.setContextMenu(null);

            stop1Button = createStopButon(Color.WHITE);
            stop1Button.getStyleClass().addAll("stop1");
            stop1Button.setContextMenu(null);

            toggleGroup.getToggles().add(stop0Button);
            toggleGroup.getToggles().add(stop1Button);

            anchor = new AnchorPane();
            anchor.setStyle("-fx-background-color: yellow");
            anchor.getStyleClass().add("anchor-pane");
            anchor.setId("anchor-pane");

            GridPane grid = new GridPane();
            grid.getStyleClass().add("content");
            grid.getStyleClass().add("grid-pane");

            anchor.getChildren().add(stop0Button);
            anchor.getChildren().add(stop1Button);
            AnchorPane.setLeftAnchor(stop0Button, 0d);
            AnchorPane.setRightAnchor(stop1Button, 0d);

            grid.add(anchor, 0, 1);

            selPointer = Util.createUpTriangle();
            selPointer.setVisible(false);
            selPointer.setManaged(false);

            stopValue = new Text("0.00");
            stopValue.setManaged(false);
            stopValue.setFont(Font.font("System", 9));
            stopValue.setVisible(false);

            Pane selPointerPane = new Pane(selPointer);
            Pane stopValuePane = new Pane(stopValue);
            stopValuePane.setPrefHeight(stopValue.getFont().getSize() + 3);
            stopValuePane.setMaxHeight(stopValue.getFont().getSize() + 3);
            stopValuePane.setMinHeight(stopValue.getFont().getSize() + 3);

            grid.add(stopValuePane, 0, 0);

            stopValue.getStyleClass().clear();
            grid.add(selPointerPane, 0, 2);
            GridPane.setHgrow(selPointerPane, Priority.ALWAYS);
            GridPane.setHgrow(anchor, Priority.ALWAYS);

            toggleGroup.selectedToggleProperty().addListener((v, ov, nv) -> {
                if (ov != null) {
                    ToggleButton btn = (ToggleButton) ov;
                    //((Shape)btn.getGraphic()).fillProperty().unbindBidirectional( control.getColorChooserPane().getColorPane().chosenColorProperty() );
                    //control.getColorChooserPane().getColorPane().chosenColorProperty().unbindBidirectional(((Shape)btn.getGraphic()).fillProperty());
                }
                if (nv == null) {
                    selPointer.setVisible(false);
                } else {
                    updateSelPointerPosition((ToggleButton) nv);
                    ToggleButton btn = (ToggleButton) nv;
                    updateValues((Color) ((Shape) btn.getGraphic()).getFill());
                    //updateStops();

                    //((Shape)btn.getGraphic()).fillProperty().bindBidirectional( control.getColorChooserPane().getColorPane().chosenColorProperty() );
                    //control.getColorChooserPane().getColorPane().chosenColorProperty().bindBidirectional(((Shape)btn.getGraphic()).fillProperty());                    
                }
            });

            /*            control.getColorChooserPane().getColorPane().chosenColorProperty().addListener((v, oldColor, newColor) -> {
                ToggleButton btn = (ToggleButton) toggleGroup.getSelectedToggle();
                if (btn == null) {
                    return;
                }
                ((Rectangle) btn.getGraphic()).setFill(newColorBy((Color) newColor));
                if (btn != null) {
                //    setButtonColor(btn, (Color) newColor);
                }
            });
             */
            EventHandler<MouseEvent> anchorMouseHandler = event -> {

                if (event.isSecondaryButtonDown()) {
                    return;
                }
                double pos = event.getX();
                if (pos < 0) {
                    pos = 0;
                }
                ToggleButton btn = createStopButon();

              
                anchor.getChildren().add(btn);
                AnchorPane.setLeftAnchor(btn, pos);
                System.err.println("LEFT ANCHOR " + AnchorPane.getLeftAnchor(btn));
                toggleGroup.getToggles().add(btn);
                toggleGroup.selectToggle(btn);
            };

            anchor.setOnMouseClicked(anchorMouseHandler);

            Label txt = new Label("Click to add stop");
            txt.getStyleClass().add("title");
            anchor.getChildren().add(txt);
            txt.toBack();
            AnchorPane.setLeftAnchor(txt, 0d);
            AnchorPane.setRightAnchor(txt, 0d);
            AnchorPane.setTopAnchor(txt, 0d);
            AnchorPane.setBottomAnchor(txt, 0d);
            txt.setAlignment(Pos.CENTER);

            anchor.getChildren().addListener(this::toggleButonsChanged);

            control.getColorChooserPane().getColorPane().chosenColorProperty().addListener((v, oldColor, newColor) -> {
                ToggleButton btn = (ToggleButton) toggleGroup.getSelectedToggle();
                if (btn != null) {
                    setButtonColor(btn, (Color) newColor);
                    updateStops();
                }
            });

            control.currentPaintProperty().addListener((v, ov, nv) -> {
                updateCurrentStops(nv);
            });
            //           System.err.println("StopPane: currentpaint = " + control.getCurrentPaint());
            Platform.runLater(() -> updateCurrentStops(control.getCurrentPaint()));
            getChildren().add(grid);

        }//skin constructor

        private void updateValues(Color c) {
            ColorPane cp = control.getColorChooserPane().getColorPane();
            cp.setHue(c.getHue());
            cp.setSaturation(c.getSaturation() * 100);
            cp.setBrightness(c.getBrightness() * 100);
            cp.setAlpha(c.getOpacity() * 100);
            cp.setChosenColor(Color.hsb(cp.getHue(), ColorPane.clamp(cp.getSaturation() / 100),
                    ColorPane.clamp(cp.getBrightness() / 100), ColorPane.clamp(cp.getAlpha() / 100)));
        }

        private void updateCurrentStops(Paint paint) {
            System.err.println("stopPane updateCurrentStops  of paintChange paint = " + paint);
            List<Stop> stops;
            if (paint == null) {
                return;
            }
            if (paint instanceof LinearGradient) {
                stops = ((LinearGradient) paint).getStops();
            } else if (paint instanceof RadialGradient) {
                stops = ((RadialGradient) paint).getStops();
            } else {
                return;
            }
            if (stops.isEmpty()) {
                return;
            }
            //update = true;
            Stop last = stops.get(stops.size() - 1);
            //
            // Clear stop buttons collection
            //

            List btns = new ArrayList();
            anchor.getChildren().forEach(node -> {
                if ((node instanceof ToggleButton) && node != stop0Button && node != stop1Button) {
                    btns.add(node);
                }
            });
            System.err.println("StopPane: list of btns size = " + btns.size());
            btns.forEach(btn -> {
                System.err.println(" --- btn = " + btn);
                toggleGroup.getToggles().remove(btn);
                anchor.getChildren().remove(btn);
            });
            stops.forEach(stop -> {
                if (stop.getOffset() == 0 || stop.getOffset() == 1) {
                    ToggleButton btn = stop.getOffset() == 0 ? stop0Button : stop1Button;
                    setButtonColor(btn, stop.getColor());
                } else {
                    ToggleButton btn = createStopButon(stop.getColor());
                    toggleGroup.getToggles().add(btn);
                    anchor.getChildren().add(btn);
                    AnchorPane.setLeftAnchor(btn, stop.getOffset() * (anchor.getWidth() - stop1Button.getWidth()));
                }
                if (stop == last) {
                    updateStops();
                }

            });

        }

        /*        private void updateCurrentStops(Stop[] newStops) {
            ObservableList<Stop> stops = FXCollections.observableArrayList();
            stops.addAll(newStops);
            System.err.println("stopPane updateCurrentStops  of paintChange stops = " + stops);
            if (stops.isEmpty()) {
                return;
            }
            //update = true;
            Stop last = stops.get(stops.size() - 1);
            //
            // Clear stop buttons collection
            //

            List<ToggleButton> btns = new ArrayList();
            anchor.getChildren().forEach(node -> {
                if ((node instanceof ToggleButton) && node != stop0Button && node != stop1Button) {
                    btns.add((ToggleButton) node);
                }
            });
            System.err.println("StopPane: list of btns size = " + btns.size());
            //
            // Check whether we need modify anchor pane which contains buttons
            //
            if (btns.size() + 2 == stops.size()) {
                for (ToggleButton tb : btns) {
                    int count = 0;
                    for (Stop stop : stops) {
                        //if ( tb.)
                    }
                }
            }
            btns.forEach(btn -> {
                System.err.println(" --- btn = " + btn);
                toggleGroup.getToggles().remove(btn);
                anchor.getChildren().remove(btn);
            });
            stops.forEach(stop -> {
                if (stop.getOffset() == 0 || stop.getOffset() == 1) {
                    ToggleButton btn = stop.getOffset() == 0 ? stop0Button : stop1Button;
                    setButtonColor(btn, stop.getColor());
                } else {
                    ToggleButton btn = createStopButon(stop.getColor());
                    toggleGroup.getToggles().add(btn);
                    anchor.getChildren().add(btn);
                    AnchorPane.setLeftAnchor(btn, stop.getOffset() * (anchor.getWidth() - stop1Button.getWidth()));
                }
                if (stop == last) {
                    updateStops();
                }

            });

        }
         */
        private ToggleButton createStopButon() {
            return createStopButon(newColorBy(control.getChosenColor()));
        }

        private ToggleButton createStopButon(Color color) {
            ToggleButton btn = new ToggleButton();
            btn.getStyleClass().add("stop-button");
//            btn.setPrefWidth(28);

            Rectangle inner = new Rectangle(12, 12);
            inner.setFill(color);
            inner.getStyleClass().clear();
            inner.getStyleClass().add("color-rect");
            btn.setGraphic(inner);
            addContextMenu(btn);
            btn.layoutXProperty().addListener((v, ox, nx) -> {
                updateSelPointerPosition(btn);
                updateStops();
            });

            btn.addEventHandler(MouseEvent.MOUSE_PRESSED, this::buttonMouseHandler);
            btn.addEventHandler(MouseEvent.MOUSE_RELEASED, this::buttonMouseHandler);
            btn.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::buttonMouseHandler);
            btn.addEventHandler(MouseEvent.DRAG_DETECTED, this::buttonMouseHandler);

            return btn;
        }

        private void buttonMouseHandler(MouseEvent ev) {
            ToggleButton btn = (ToggleButton) ev.getSource();
            if (btn == stop0Button || btn == stop1Button) {
                return;
            }
            if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                dragDetected = false;
                startMousePos = ev.getX();
                selPointer.setVisible(true);
                updateSelPointerPosition(btn);
                //control.getColorChooserPane().currentPaintChanged( ((Shape)btn.getGraphic()).getFill());
                if (ev.getButton() == MouseButton.SECONDARY) {
                    btn.getContextMenu().show(btn, btn.getLayoutX(), btn.getLayoutY());
                }

            } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                Platform.runLater(() -> {
                    if (dragDetected) {
                        btn.setSelected(true);
                    }
                    dragDetected = false;
                });
                updateSelPointerPosition(btn);
            } else if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                double newAnchor;// = -1;
                double mouseOffset = (ev.getX() - startMousePos);
                double right = stop1Button.getLayoutX();

                if (btn.getLayoutX() + btn.getWidth() + mouseOffset >= anchor.getWidth()) {
                    newAnchor = right;
                } else if (btn.getLayoutX() + mouseOffset <= 0) {
                    newAnchor = 0;
                } else {
                    newAnchor = mouseOffset + btn.getLayoutX();
                }
                if (newAnchor >= 0) {
                    AnchorPane.setLeftAnchor(btn, newAnchor);
                    updateSelPointerPosition(btn);
                    updateStops();
                }

            } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
                dragDetected = true;
            }
        }

        private Color newColorBy(Color c) {
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity());
        }

        private Color newColorBy(ToggleButton btn) {
            Color c = (Color) ((Rectangle) btn.getGraphic()).getFill();
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity());
        }

        private void setButtonColor(ToggleButton btn, Color c) {
            Color newColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity());
            ((Rectangle) btn.getGraphic()).setFill(newColor);
        }

        private void addContextMenu(ToggleButton button) {
            MenuItem item = new MenuItem("Remove");
            ContextMenu menu = new ContextMenu(item);
            button.setContextMenu(menu);
            item.setOnAction(e -> {
                toggleGroup.getToggles().remove(button);
                anchor.getChildren().remove(button);
            });
        }

        private double getOffset(ToggleButton btn) {
            return btn.getLayoutX() / (anchor.getWidth() - stop1Button.getWidth());
        }

        private void toggleButonsChanged(ListChangeListener.Change<? extends Node> change) {
            while (change.next()) {
                if (change.wasAdded()) {
//                    ToggleButton btn = (ToggleButton) change.getAddedSubList().get(change.getAddedSubList().size() - 1);
//                    selPointer.setVisible(false);
//                    stopValue.setVisible(false);

                    Platform.runLater(() -> {
                        //toggleGroup.selectToggle(btn);

//                        updateSelPointerPosition(btn);
//                        updateStops();
                    });
                } else if (change.wasRemoved()) {
                    //ToggleButton btn = (ToggleButton) change.getRemoved().get(change.getRemovedSize() - 1);
                    //Platform.runLater(() -> {
                    //    updateStops();
                    //});
                    selPointer.setVisible(false);
                    stopValue.setVisible(false);
                }
                Platform.runLater(() -> {
                    updateStops();
                });

            }
        }

        private void updateSelPointerPosition(ToggleButton btn) {
            if ( ! btn.isSelected() ) {
                return;
            }
            double width = selPointer.getLayoutBounds().getWidth();
            double height = selPointer.getLayoutBounds().getHeight();

            selPointer.setVisible(true);
            selPointer.setLayoutX(btn.getLayoutX() + (btn.getWidth() - width) / 2);
            selPointer.setLayoutY(height + 1);

            width = stopValue.getLayoutBounds().getWidth();
            height = stopValue.getFont().getSize();

            stopValue.setVisible(true);

            double offset = getOffset(btn);

            String txt = Double.toString(offset);
            if (txt.length() > 4) {
                txt = txt.substring(0, 4);
            }

            stopValue.setText(txt);
            ((Pane) stopValue.getParent()).setPrefHeight(height + 3);
            ((Pane) stopValue.getParent()).setMaxHeight(height + 3);
            ((Pane) stopValue.getParent()).setMinHeight(height + 3);
            stopValue.setLayoutX(btn.getLayoutX() + (btn.getWidth() - width) / 2);
            stopValue.setLayoutY(height);
        }
        private void updateSelPointerPosition1(ToggleButton btn) {

            double width = selPointer.getLayoutBounds().getWidth();
            double height = selPointer.getLayoutBounds().getHeight();

            selPointer.setVisible(true);
            selPointer.setLayoutX(btn.getLayoutX() + (btn.getWidth() - width) / 2);
            selPointer.setLayoutY(height + 1);

            width = stopValue.getLayoutBounds().getWidth();
            height = stopValue.getFont().getSize();

            stopValue.setVisible(true);

            double offset = getOffset(btn);

            String txt = Double.toString(offset);
            if (txt.length() > 4) {
                txt = txt.substring(0, 4);
            }

            stopValue.setText(txt);
            ((Pane) stopValue.getParent()).setPrefHeight(height + 3);
            ((Pane) stopValue.getParent()).setMaxHeight(height + 3);
            ((Pane) stopValue.getParent()).setMinHeight(height + 3);
            stopValue.setLayoutX(btn.getLayoutX() + (btn.getWidth() - width) / 2);
            stopValue.setLayoutY(height);
        }

        private void updateStops() {

            List<Stop> sl = FXCollections.observableArrayList();

            Stop stop0 = new Stop(0, newColorBy(stop0Button));
            sl.add(stop0);

            List list1 = anchor.getChildren().filtered(it -> {
                return (it instanceof ToggleButton);
            });
            List<ToggleButton> list = list1;
            for (ToggleButton btn : list) {
                sl.add(new Stop(getOffset(btn), newColorBy(btn)));
            }
            Stop stop1 = new Stop(1, newColorBy(stop1Button));
            sl.add(stop1);
            control.setStops(sl.toArray(new Stop[0]));
        }

    }//StopPaneSkin