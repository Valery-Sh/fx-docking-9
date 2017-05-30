/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.api;

import com.sun.javafx.stage.StageHelper;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 *
 * @author Valery
 */
public class platform {
    public static ObservableList<Stage> getStages() {
        return StageHelper.getStages();
    }
    
}
