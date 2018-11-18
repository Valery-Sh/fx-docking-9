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
package org.vns.javafx.dock.api.dragging.view;

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Valery
 */
public interface NodeFraming {

/*    public static String ID = "ID-89528991-bd7a-4792-911b-21bf56660bfb";
    public static String CSS_CLASS = "CSS-89528991-bd7a-4792-911b-21bf56660bfb";
    public static final String RECTANGLE_ID = "RECT-" + ID;
    public static final String CIRCLE_ID = "CIRCLE-" + ID;
*/    
    
    void show(Node node);
    void hide();
    
}
