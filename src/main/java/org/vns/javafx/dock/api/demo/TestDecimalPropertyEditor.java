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
package org.vns.javafx.dock.api.demo;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.designer.bean.editor.DecimalPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestDecimalPropertyEditor extends Application {

    int sz = 100;

    @Override
    public void start(Stage stage) throws ClassNotFoundException {

        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");

        GridPane grid = new GridPane();

        grid.setHgap(10);

        StackPane root = new StackPane(grid);

        DecimalPropertyEditor tf1 = new DecimalPropertyEditor(-100d, 200d, 0);
        //DecimalPropertyEditor tf1 = new DecimalPropertyEditor();
        btn2.setPrefWidth(12.1);
        tf1.bindBidirectional(btn2.prefWidthProperty());

        btn1.setOnAction(e -> {
            btn2.setPrefWidth(120.25);
            System.err.println("3 btn2.prefWidthProperty() = " + btn2.getPrefWidth());
        });

        btn2.setOnAction(e -> {
            btn2.setPrefWidth(sz = sz + 10);
        });
        grid.add(btn1, 0, 0);
        grid.add(tf1, 0, 1);
        grid.add(btn2, 0, 2);

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);
        System.err.println("minWidth = " + split("minWidth"));
        System.err.println("Point2D = " + split("Point2D"));
        System.err.println("P2D = " + split("P2D"));
        System.err.println("PDC = " + split("PDC"));
        System.err.println("PD = " + split("PD"));
        System.err.println("iPD = " + split("iPD"));
//        System.err.println("R = " + getClass().getResource("resources/demo-styles.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("resources/demo-styles.css").toExternalForm());

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    private static final String CAMELCASE_OR_UNDERSCORE
            = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_";

    /*public static List<String> split(String string) {
        List<String> words = new ArrayList<String>();
        for (String word : string.split(RE_CAMELCASE_OR_UNDERSCORE)) {
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        return words;
    }
     */
    public static String split(String string, String... except) {
        StringBuilder sb = new StringBuilder();
        String[] split = string.split(CAMELCASE_OR_UNDERSCORE);
        for (String word : split) {
            if (!word.isEmpty()) {
                if ( sb.length() == 2 ) {
                    sb.deleteCharAt(1);
                    if ( Character.isLowerCase(string.charAt(0))) {
                        sb.deleteCharAt(0);
                        sb.append(string.charAt(0));
                    }
                    sb.append(word)
                            .append(' ');
                } else if (word.length() == 1) {
                    if (sb.length() != 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    sb.append(word.toUpperCase())
                            .append(' ');
                } else {
                    sb.append(word.substring(0, 1).toUpperCase())
                            .append(word.substring(1))
                            .append(' ');
                }
            }
        }

        return sb.toString().trim();
    }
}
