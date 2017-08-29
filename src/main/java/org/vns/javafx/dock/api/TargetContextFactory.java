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
package org.vns.javafx.dock.api;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.vns.javafx.dock.DockBorderPane;
import org.vns.javafx.dock.HPane;
import org.vns.javafx.dock.VPane;

/**
 *
 * @author Valery
 */
public class TargetContextFactory {
    TargetContext getContext(Node targetNode) {
        if ( DockRegistry.isDockTarget(targetNode) ) {
            return DockRegistry.dockTarget(targetNode).getTargetContext();
        }
        TargetContext retval = null;
        if ( targetNode instanceof StackPane ) {
            retval = getStackPaneContext((StackPane)targetNode);
        } else if ( targetNode instanceof VBox ) {

        } else if ( targetNode instanceof HBox ) {

        }  else if ( targetNode instanceof BorderPane ) {

        }  else if ( targetNode instanceof VPane ) {

        }  else if ( targetNode instanceof HPane ) {

        }  else if ( targetNode instanceof SplitPane ) {
            
        }  else if ( targetNode instanceof Pane ) {
            
        }
        return retval;
    }
    protected TargetContext getStackPaneContext(StackPane pane) {
        TargetContext retval = null;
        //pane.setAlignment(pane, Pos.CENTER);
        return retval;
    }    
    protected TargetContext getBorderPaneContext(BorderPane pane) {
        TargetContext retval = new DockBorderPane.DockBorderPaneContext(pane);
        //pane.setAlignment(pane, Pos.CENTER);
        return retval;
    }    
    
}//class
