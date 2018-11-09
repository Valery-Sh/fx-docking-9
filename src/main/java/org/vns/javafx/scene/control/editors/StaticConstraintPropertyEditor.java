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
package org.vns.javafx.scene.control.editors;

import java.lang.reflect.Method;
import javafx.scene.Parent;

/**
 *
 * @author Valery Shyshkin
 */
public interface StaticConstraintPropertyEditor {


    String getName();

    void bindConstraint(Parent node, Method... setMethods);

    void unbind();


    boolean isBound();
    
    HyperlinkTitle getTitle();

}
