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
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.layout.HBox;

import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestTransformation extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button startAnime = new Button("start resize");
        Rectangle rect1 = new Rectangle(100, 50, Color.AQUA);
        rect1.setX(150);
        rect1.setStroke(Color.BLUE);
        rect1.setOpacity(0.5);

        Rectangle rect2 = new Rectangle(100, 50, Color.LIGHTGRAY);
        rect2.setX(0);
        rect2.setStroke(Color.RED);
// Apply a scale on rect2. The origin of the local coordinate system
// of rect4 is the pivot point
        double v = 0.5;
        Scale scale = new Scale(v, 1, 0, 10);

        rect2.getTransforms().addAll(scale);
        rect2.setTranslateX(25);
        //rect2.getTransforms().clear();
        //Pane root = new Pane(rect1, rect2);
        HBox root = new HBox(startAnime, rect2);
        root.setPrefSize(300, 60);
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Applying the Scale Transformation");
        SizeAnimation szAnim = new SizeAnimation(stage);
        startAnime.setOnAction(a -> {
            szAnim.start();
            //stage.setWidth(stage.getWidth() - 10);
            //stage.setX(stage.getX() + 10);
            
        });
        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);
    }

    public class SizeAnimation {

        private AnimationTimer timer;
        private double increment = 10.0;
        private boolean running;
        private Stage stage;

        public SizeAnimation(Stage stage) {
            this.stage = stage;
            init();
        }

        private void init() {
            timer = new AnimationTimer() {

                private long lastUpdate = -1;

                @Override
                public void handle(long now) {
                    if (lastUpdate <= 0) {
                        System.err.println("now=" + now);
                        lastUpdate = now;
                    }

                    if (stage.getWidth() <= 150) {
                        System.err.println("STOP 2");
                        stop();
                        running = false;
                    }
                    //System.err.println("max = " + stage.getWidth());
                    if (stage.getWidth() - increment <= 0) {
                        System.err.println("STOP 1");
                        stop();
                    }
                    //System.err.println("dif=" + now);
                    if ( now - lastUpdate > 12000000 ) {
                        stage.setWidth(stage.getWidth() - increment);
                        if ( stage.getWidth() > stage.getMinWidth()) {
                            stage.setX(stage.getX() + increment);
                        }
                        lastUpdate = now;
                    }

                    
                }
            };
        }

        public double getIncrement() {
            return increment;
        }

        public void setIncrement(double increment) {
            this.increment = increment;
        }

        public void start() {
            if (isRunning()) {
                System.err.println("RUNNING");
                return;
            }
            //stage.setMinWidth(stage.getMinWidth() - 5);

            timer.start();
            running = true;
        }

        public boolean isRunning() {
            return running;
        }

        public void stop() {
            timer.stop();
            running = false;
        }

    }

}
