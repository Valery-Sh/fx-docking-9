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
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockUtil;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.event.DockEvent;

/**
 *
 * @author Valery
 */
public class TestFlowPaneTargetContext  extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane primaryRoot = new FlowPane();
        primaryRoot.getChildren().addAll(new Button("Btn1"), new Label("Label1"), new Button("Btn2"));
        DockRegistry.getInstance().registerAsDockTarget(primaryRoot);
        
        primaryRoot.addEventFilter(DockEvent.NODE_DOCKED, ev -> {
            System.err.println("DockPane eventFilter: getSource()       = " + ev.getSource());
            System.err.println("DockPane eventFilter: getDockedNode()   = " + ev.getDockedNode());            
            System.err.println("DockPane eventFilter: getTarget() = " + ev.getTarget());                        
            System.err.println("DockPane eventFilter: getTargetNode() = " + ev.getTargetNode());                                    
            System.err.println("DockPane eventFilter: getDockPosition()  0 = " + ev.getDockPosition()[0] + "; 1 = " + ev.getDockPosition()[1] );                                                
        });
        primaryRoot.addEventFilter(DockEvent.NODE_UNDOCKED, ev -> {
            System.err.println("DockPane eventFilter: getDockedNode()   = " + ev.getDockedNode());            
            System.err.println("DockPane eventFilter: getTargetNode() = " + ev.getTargetNode());                                    
            System.err.println("DockPane eventFilter: getDockPosition().length = " + ev.getDockPosition().length);
        });
        
        //dockPane.getTargetContext().setDragType(DragType.DRAG_AND_DROP);
        //dockPane.addEventHandler( new );
        primaryRoot.setId("PRIMARY ROOT");
        //DockEvent ev = new DockEvent(null, primaryRoot,DockEvent.NODE_DOCKED);
        Button b1 = new Button("Add or Remove TitleBar");
        Button b2 = new Button("b2r");
        //b1.setGraphic(b2);
        Pane p1 = new HBox(b1);
        DockNode custom = new DockNode();
        primaryRoot.getChildren().add(custom);
        custom.setId("custom");
        DockNode custom1 = new DockNode();
        custom1.setTitle("CUSTOM 1");
        primaryRoot.getChildren().add(custom1);
        custom1.setId("custom1");        
        //custom1.setContent(b2);                
        b1.setOnAction(a->{
            custom1.setContent(b2);
            if ( custom.getTitleBar() == null ) {
                custom1.setContent(b2);                
                //custom.getContext().createDefaultTitleBar("Now Not Null");
            } else {
                //custom.setTitleBar(null);
                //custom.setRemoveTitleBar(true);
            }
            //b1.getScene().getWindow().setX(40);
            //b1.getScene().getWindow().setY(40);
            DockUtil.print(primaryRoot);
            
        });
        
        //TitledPane tp = new TitledPane();
        //dockPane.getChildren().add(tp);
        //tp.setContent(p1);
//        p1.getChildren().add(b1);
        //custom.setPrefSize(100, 100);
        custom.setContent(p1);
        
        p1.setId("pane p1");
        DockUtil.print(primaryRoot);
        //dockPane.dock(p1, Side.TOP).getContext().setTitle("Pane p1");
        Scene primaryScene = new Scene(primaryRoot);
        
        primaryStage.setTitle("JavaFX and Maven");
        primaryStage.setScene(primaryScene);
        
        primaryStage.setOnShown(s -> {
            //((Pane)custom.getContent()).getChildren().forEach(n -> {System.err.println("custom node=" + n);});
            //System.err.println("tp.lookup(arrowRegion)" + tp.);
            DockUtil.print(b1);
        });
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);
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

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }
}
