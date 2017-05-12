/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api.demo.panes;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.vns.javafx.dock.api.DockTargetController;
import org.vns.javafx.dock.api.PositionIndicator;

/**
 *
 * @author Valery
 */
public class BorderPanePositionIndicator extends PositionIndicator {

    public BorderPanePositionIndicator(DockTargetController targetController) {
        super(targetController);
    }


    @Override
    protected Pane createIndicatorPane() {
        BorderPane borderPane = (BorderPane) getTargetController().getTargetNode();
        Label topNode = new Label("Top");
        Label rightNode = new Label("Right");
        Label bottomNode = new Label("Bottom");
        Label leftNode = new Label("Left");
        Label centerNode = new Label("Center");
        //borderPane.setTop(topNode);
        

        topNode.prefWidthProperty().bind(borderPane.widthProperty());
        topNode.prefHeightProperty().bind(borderPane.heightProperty().divide(3));
        topNode.setStyle("-fx-border-color: black; -fx-border-width=2.0; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
        rightNode.setStyle("-fx-border-color: black; -fx-border-width=2.0; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
        bottomNode.setStyle("-fx-border-color: black; -fx-border-width=2.0; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
        leftNode.setStyle("-fx-border-color: black; -fx-border-width=2.0; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
        centerNode.setStyle("-fx-border-color: black; -fx-border-width=2.0; -fx-opacity: 0.3; -fx-background-color: lightgray; -fx-text-fill: black");
        
        bottomNode.prefWidthProperty().bind(borderPane.widthProperty());
        bottomNode.prefHeightProperty().bind(borderPane.heightProperty().divide(3));        
        leftNode.prefWidthProperty().bind(borderPane.widthProperty().divide(3));
        rightNode.prefWidthProperty().bind(borderPane.widthProperty().divide(3));
        leftNode.prefHeightProperty().bind(borderPane.heightProperty().divide(3));
        rightNode.prefHeightProperty().bind(borderPane.heightProperty().divide(3));
        centerNode.prefHeightProperty().bind(borderPane.heightProperty().divide(3));
        centerNode.prefWidthProperty().bind(borderPane.widthProperty().divide(3));
        
        BorderPane indicator = new BorderPane(centerNode, topNode, rightNode, bottomNode, leftNode);
        topNode.setAlignment(Pos.CENTER);
        rightNode.setAlignment(Pos.CENTER);
        bottomNode.setAlignment(Pos.CENTER);
        leftNode.setAlignment(Pos.CENTER);
        centerNode.setAlignment(Pos.CENTER);
        
/*        BorderPane.setAlignment(topNode, Pos.CENTER);
        BorderPane.setAlignment(rightNode, Pos.CENTER);
        BorderPane.setAlignment(bottomNode, Pos.CENTER);
        BorderPane.setAlignment(leftNode, Pos.CENTER);
        BorderPane.setAlignment(centerNode, Pos.CENTER);
*/        
        indicator.setStyle("-fx-border-width: 2px; -fx-border-color: red");
        return indicator;
    }

    @Override
    protected String getStylePrefix() {
        return "dock-borderpane-indicator";
    }

    @Override
    public void showIndicator(double screenX, double screenY) {
        getTargetController().getIndicatorPopup().show(getTargetController().getTargetNode(), screenX, screenY);
    }
    
}
