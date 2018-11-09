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
package org.vns.javafx.scene.control.paint;

import javafx.scene.control.Skin;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import org.vns.javafx.scene.control.paint.skin.LinearGradientPaneSkin;

/**
 *
 * @author Nastia
 */
public class LinearGradientPane extends GradientPane {

    public LinearGradientPane() {
        this(null);
    }

    public LinearGradientPane(LinearGradient currentPaint) {
        super(currentPaint);
        init();
    }

    private void init() {
        getStyleClass().add("linear-gradient");
        if ( getCurrentPaint() == null ) {
            setCurrentPaint(createDefaultGradient());
        }
        updateValues();
    }
    public void currentPaintChanged(Paint paint) {
        if ( ! (paint instanceof LinearGradient) ) {
            return;
        }
        System.err.println("linearGradientPane: currentPaintChanged paint =  " +((LinearGradient) paint).getStops());
        setCurrentPaint(paint);
        updateValues();
        
        
    }
    @Override
    protected Paint createDefaultGradient() {
        return new LinearGradient(getTopValue(), getLeftValue(),
                getBottomValue(), getRightValue(),
                isProportional(),
                getCycleMethod(),
                getStops());
    }
    
    @Override
    public void updateValues() {
        LinearGradient g = (LinearGradient) getCurrentPaint();
        setTopValue(g.getStartX());
        setBottomValue(g.getEndX());
        setLeftValue(g.getStartY());
        setRightValue(g.getEndY());
        setProportional(g.isProportional());
        setCycleMethod(g.getCycleMethod());
        Stop[] stops = new Stop[0];
        setStops(g.getStops().toArray(stops));
        
        setChosenPaint(new LinearGradient(g.getStartX(),g.getStartY(),
                g.getEndX(), g.getEndY(),g.isProportional(), g.getCycleMethod(), g.getStops()));
        System.err.println("UPDATE Values stpos = " + ((LinearGradient)getChosenPaint()).getStops());
    }
    
    @Override
    protected Skin<?> createDefaultSkin() {
        return new LinearGradientPaneSkin(this);
    }

/*    public static class LinearGradientPaneSkin extends GradientPaneSkin {

        private final LinearGradientPane control;

        public LinearGradientPaneSkin(LinearGradientPane control) {
            super(control);
            this.control = control;
            init();

        }

        private void init() {
            GridPane propPane = getPropertiesPane();
            getTopSlider().setMin(0);
            getTopSlider().setMax(1);

            getRightSlider().setMin(0);
            getRightSlider().setMax(1);
            getBottomSlider().setMin(0);
            getBottomSlider().setMax(1);
            getLeftSlider().setMin(0);
            getLeftSlider().setMax(1);

            getTopSlider().setValue(0);
            getRightSlider().setValue(1);
            getBottomSlider().setValue(1);
            getLeftSlider().setValue(0);

            Tooltip toolTip1 = new Tooltip();
            toolTip1.setOnShowing(e -> {
                toolTip1.setText("startX = " + getTopSlider().getValue());
            });
            getTopSlider().setTooltip(toolTip1);

            Tooltip toolTip2 = new Tooltip();
            toolTip2.setOnShowing(e -> {
                toolTip2.setText("endY = " + getRightSlider().getValue());
            });
            getRightSlider().setTooltip(toolTip2);

            Tooltip toolTip3 = new Tooltip();
            toolTip3.setOnShowing(e -> {
                toolTip3.setText("endX = " + getBottomSlider().getValue());
            });
            getBottomSlider().setTooltip(toolTip3);

            Tooltip toolTip4 = new Tooltip();

            toolTip4.setOnShowing(e -> {
                toolTip4.setText("startY = " + getLeftSlider().getValue());
            });

            getLeftSlider().setTooltip(toolTip4);
        }

        @Override
        protected Paint createGradient() {
            return new LinearGradient(topSlider.getValue(), leftSlider.getValue(),
                    bottomSlider.getValue(), rightSlider.getValue(),
                    getProportional().isSelected(),
                    getCycleMethodBox().getSelectionModel().getSelectedItem(),
                    getStopPane().getStops());
        }

        @Override
        protected void updateProperties(Paint linearGradient) {
            LinearGradient gr = (LinearGradient) linearGradient;
            topSlider.setValue(gr.getStartX());
            bottomSlider.setValue(gr.getEndX());

            leftSlider.setValue(gr.getStartY());
            rightSlider.setValue(gr.getEndY());
            cycleMethodBox.setValue(gr.getCycleMethod());
            proportional.setSelected(gr.isProportional());
        }

        @Override
        protected void updateStops(Paint gradient) {
            LinearGradient gr = (LinearGradient) gradient;

        }
    }//skin GradientPane
*/
}
