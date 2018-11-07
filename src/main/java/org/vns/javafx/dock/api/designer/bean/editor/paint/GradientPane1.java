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
package org.vns.javafx.dock.api.designer.bean.editor.paint;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Nastia
 */
public class GradientPane1 extends Control {

    private final ObjectProperty<Paint> currentPaint = new SimpleObjectProperty<>();
            
    private final ReadOnlyObjectWrapper<Paint> chosenPaint = new ReadOnlyObjectWrapper<>();

    //private final ColorChooserPane colorChooserPane;
    private final ColorChooserPane content;

    private final DoubleProperty topValue = new SimpleDoubleProperty();
    private final DoubleProperty rightValue = new SimpleDoubleProperty();
    private final DoubleProperty bottomValue = new SimpleDoubleProperty();
    private final DoubleProperty leftValue = new SimpleDoubleProperty();    
    
    private  final BooleanProperty proportional = new SimpleBooleanProperty();    
    private final  ObjectProperty<CycleMethod> cycleMethod = new SimpleObjectProperty();     
    
    private final ObjectProperty<Stop[]> stops = new SimpleObjectProperty<>();
    
    public GradientPane1(ColorChooserPane colorChooserPane) {
        this.content = colorChooserPane;
        init();
    }

    private void init() {
        getStyleClass().add("gradient-pane");
        content.getStyleClass().add("content");
    }

    public DoubleProperty topValuePropert() {
        return topValue;
    }
    public double getTopValue() {
        return topValue.get();
    }
    public void setTopValue(double topValue) {
        this.topValue.set(topValue);
    }

    public DoubleProperty rightValuePropert() {
        return rightValue;
    }
    public double getRightValue() {
        return rightValue.get();
    }

    public void setRightValue(double rightValue) {
        this.rightValue.set(rightValue);
    }

    public DoubleProperty bottomValuePropert() {
        return bottomValue;
    }
    public double getBottomValue() {
        return bottomValue.get();
    }

    public void setBottomValue(double bottomValue) {
        this.bottomValue.set(bottomValue);
    }

    public DoubleProperty leftValueProperty() {
        return leftValue;
    }
    public double getLeftValue() {
        return leftValue.get();
    }

    public void setLeftValue(double leftValue) {
        this.leftValue.set(leftValue);
    }

    public BooleanProperty proportionalProperty() {
        return proportional;
    }
    public boolean isProportional() {
        return proportional.get();
    }

    public void setProportional(boolean proportional) {
        this.proportional.set(proportional);
    }

    public ObjectProperty<CycleMethod> cycleMethodProperty() {
        return cycleMethod;
    }
    public CycleMethod getCycleMethod() {
        return cycleMethod.get();
    }

    public void setCycleMethod(CycleMethod cycleMethod) {
        this.cycleMethod.set(cycleMethod);
    }
    public ObjectProperty<Stop[]> stopsProperty() {
        return stops;
    }

    public Stop[] getStops() {
        return stops.get();
    }

    public void setStops(Stop[] stops) {
        this.stops.set(stops);
    }
  
    
    public ObjectProperty<Paint> paintProperty() {
        return currentPaint;
    }
    public Paint getCurrentPaint() {
        return currentPaint.get();
    }

    public void setCurrentPaint(Paint paint) {
        this.currentPaint.set(paint);
    }
    public ReadOnlyObjectProperty<Paint> chosenPaintProperty() {
        return chosenPaint.getReadOnlyProperty();
    }

    public Paint getChosenPaint() {
        return chosenPaint.get();
    }

    private void setChosenPaint(Paint gradient) {
        this.chosenPaint.set(gradient);
    }

    public ColorChooserPane getContent() {
        return content;
    }

    protected void updateGradient(Paint gradient) {
        setChosenPaint(gradient);
    }


    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new GradientPaneSkin(this);
    }

    public static class GradientPaneSkin extends SkinBase<GradientPane1> {

        private final GridPane samplePane;
        private final GridPane propertiesPane;

        private final StopPane stopPane;
        private final GradientPane1 control;
        private final GridPane grid;
        private final Rectangle sampleShape;
        private final ColorChooserPane colorChooserPane;
        CheckBox proportional;
        ComboBox cycleMethodBox;
        
        Slider topSlider;
        Slider rightSlider;
        Slider bottomSlider;
        Slider leftSlider;

        public GradientPaneSkin(GradientPane1 control) {
            super(control);

            this.control = control;
            colorChooserPane = control.getContent();
            grid = colorChooserPane.getContent();
            int row = 5;
            
            stopPane = new StopPane(colorChooserPane);
            
            samplePane = new GridPane();

            propertiesPane = new GridPane();
            propertiesPane.getStyleClass().add("properties-pane");
            
            samplePane.getStyleClass().add("sample-pane");

            sampleShape = new Rectangle(100, 100);
            sampleShape.getStyleClass().addAll("sample-shape", "sample-rect");

//            samplePane.setVgap(5); set in CSS
//            samplePane.setHgap(5); set in CSS
            
            StackPane topPane = new StackPane();
            StackPane rightPane = new StackPane();
            StackPane bottomPane = new StackPane();
            StackPane leftPane = new StackPane();

            samplePane.add(topPane, 1, 0);
            samplePane.add(rightPane, 2, 0, 1, 3);
            samplePane.add(bottomPane, 1, 2);
            samplePane.add(leftPane, 0, 0, 1, 3);

            samplePane.add(sampleShape, 1, 1);
            GridPane.setHgrow(topPane, Priority.NEVER);
            GridPane.setHgrow(rightPane, Priority.ALWAYS);
            GridPane.setVgrow(rightPane, Priority.NEVER);
            GridPane.setHgrow(leftPane, Priority.ALWAYS);
            GridPane.setVgrow(leftPane, Priority.NEVER);
            GridPane.setHgrow(sampleShape, Priority.NEVER);

            topSlider = new Slider();
            topSlider.getStyleClass().add("top-slider");
            rightSlider = new Slider();
            rightSlider.getStyleClass().add("right-slider");
            bottomSlider = new Slider();
            bottomSlider.getStyleClass().add("bottom-slider");            
            leftSlider = new Slider();
            leftSlider.getStyleClass().add("left-slider");
            leftSlider.setOrientation(Orientation.VERTICAL);
            rightSlider.setOrientation(Orientation.VERTICAL);

            topPane.getChildren().add(topSlider);
            rightPane.getChildren().add(rightSlider);
            bottomPane.getChildren().add(bottomSlider);
            leftPane.getChildren().add(leftSlider);

            StackPane.setAlignment(topSlider, Pos.CENTER);
            StackPane.setAlignment(rightSlider, Pos.CENTER_LEFT);
            StackPane.setAlignment(bottomSlider, Pos.CENTER);
            StackPane.setAlignment(leftSlider, Pos.CENTER_RIGHT);

            topSlider.prefWidthProperty().bind(sampleShape.widthProperty());
            rightSlider.prefHeightProperty().bind(sampleShape.heightProperty());
            bottomSlider.prefWidthProperty().bind(sampleShape.widthProperty());
            leftSlider.prefHeightProperty().bind(sampleShape.heightProperty());

            topSlider.maxWidthProperty().bind(sampleShape.widthProperty());
            rightSlider.maxHeightProperty().bind(sampleShape.heightProperty());
            bottomSlider.maxWidthProperty().bind(sampleShape.widthProperty());
            leftSlider.maxHeightProperty().bind(sampleShape.heightProperty());

            //
            // propertiesPane
            //
            
            proportional = new CheckBox();
            proportional.setSelected(true);
            Label title = new Label("proportional");
            propertiesPane.add(title, 0, 0);
            propertiesPane.add(proportional, 1, 0);

            ObservableList<CycleMethod> items = FXCollections.observableArrayList();
            items.addAll(CycleMethod.NO_CYCLE, CycleMethod.REFLECT, CycleMethod.REPEAT);
            cycleMethodBox = new ComboBox(items);
            cycleMethodBox.getSelectionModel().selectFirst();
            title = new Label("cycleMethod");
            propertiesPane.add(title, 0, 1);
            propertiesPane.add(cycleMethodBox, 1, 1,2,1);

            //grid.add(colorChooserPane, 0, 0, 3, 1);
            grid.add(stopPane, 0, row, 2, 1);
            grid.add(samplePane, 0, row + 1, 2, 1);
            grid.add(propertiesPane, 0, row + 2, 2, 1);

            GridPane.setHgrow(stopPane, Priority.ALWAYS);
            //
            // Listeners to update chosenPaint
            //
            stopPane.stopsProperty().addListener((v, ov, nv) -> updateGradient());
            
            topSlider.valueProperty().addListener((v, ov, nv) -> updateGradient());
            rightSlider.valueProperty().addListener((v, ov, nv) -> updateGradient());
            bottomSlider.valueProperty().addListener((v, ov, nv) -> updateGradient());
            leftSlider.valueProperty().addListener((v, ov, nv) -> updateGradient());
            proportional.selectedProperty().addListener((v, ov, nv) -> updateGradient());
            cycleMethodBox.getSelectionModel().selectedItemProperty().addListener((v, ov, nv) -> updateGradient());
            
            control.paintProperty().addListener((v,ov,nv) -> {
                if ( nv != null ) {
                    //updateGradient(nv);
                    System.err.println("GradientPane listener of paintChange: nv = " + nv);
                    stopPane.setCurrentPaint(nv);
                }
            });
            if ( control.getCurrentPaint() != null ) {
                //updateGradient(control.getCurrentPaint());
                stopPane.setCurrentPaint(control.getCurrentPaint());
            }
            
            getChildren().add(colorChooserPane);
        }

        public StopPane getStopPane() {
            return stopPane;
        }

        public GridPane getPropertiesPane() {
            return propertiesPane;
        }

        public CheckBox getProportional() {
            return proportional;
        }

        public ComboBox<CycleMethod> getCycleMethodBox() {
            return cycleMethodBox;
        }

        public Slider getTopSlider() {
            return topSlider;
        }

        public Slider getRightSlider() {
            return rightSlider;
        }

        public Slider getBottomSlider() {
            return bottomSlider;
        }

        public Slider getLeftSlider() {
            return leftSlider;
        }

        protected Paint createGradient() {
            return null;
        }

        private void updateSample(Paint gradient) {
            sampleShape.setFill(gradient);
        }

        protected void updateGradient() {
            Paint gr = createGradient();
            updateSample(gr);
            control.updateGradient(gr);
        }
/*        protected void updateGradient(Paint gradient) {
            updateSample(gradient);
            updateProperties(gradient); 
            updateStops(gradient);
        }
*/
        protected void updateProperties(Paint gradient) {
            
        }
        protected void updateStops(Paint gradient) {
        }
    }//skin GradientPane
}
