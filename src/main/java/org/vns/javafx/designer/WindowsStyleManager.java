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
package org.vns.javafx.designer;

/**
 *
 * @author Valery
 */
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class WindowsStyleManager {

    private final ObservableList<String> mainList = FXCollections.observableArrayList();

    public WindowsStyleManager() {
        init();
    }

    private void init() {
        mainList.addListener(this::mainListChanged);
        //ObservableList<Window> windows = Window.getWindows();
        ObservableList<Window> windows = getWindows();
        windows.addListener(this::windowsChanged);
    }

    private void mainListChanged(Change<? extends String> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(url -> {
                    //Window.getWindows().forEach(w -> {
                    getWindows().forEach(w -> {
                        if (w.getScene() != null) {
                            w.getScene().getStylesheets().remove(url);
                        }
                    });
                });
            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(url -> {
                    //Window.getWindows().forEach(w -> {
                    getWindows().forEach(w -> {    
                        if (w.getScene() != null && !w.getScene().getStylesheets().contains(url)) {
                            w.getScene().getStylesheets().add(url);
                        }
                    });
                });
            }
        }//while                
    }

    private void windowsChanged(Change<? extends Window> change) {
        while (change.next()) {

            if (change.wasRemoved()) {
                change.getRemoved().forEach((Window w) -> {
                    if (w.getScene() != null) {
                        mainList.forEach(url -> {
                            w.getScene().getStylesheets().remove(url);
                        });
                    }
                });

            }
            if (change.wasAdded()) {
                change.getAddedSubList().forEach((Window w) -> {
                    if (w.getScene() != null) {
                        mainList.forEach(url -> {
                            if (!w.getScene().getStylesheets().contains(url)) {
                                w.getScene().getStylesheets().add(url);
                            }
                        });
                    }
                });

            }
        }//while      
    };

    private ObservableList<Window> getWindows() {
        return null;
    }
    public static WindowsStyleManager getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public void addUserAgentStylesheet(String... urls) {
        for ( String url : urls) {
            if (!mainList.contains(url)) {
                mainList.add(url);
            }
        }
    }

    public void removeUserAgentStylesheet(String... urls) {
        mainList.removeAll(urls);
    }

    private static class SingletonInstance {

        private static final WindowsStyleManager INSTANCE = new WindowsStyleManager();
    }
}
