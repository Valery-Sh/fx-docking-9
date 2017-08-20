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
package org.vns.javafx.dock.api.save;

import javafx.scene.Node;

/**
 * Defines the functionality of classes intended to store and then restore the
 * state of registered objects such as {@code javafx.scene.Node }.
 * <p>
 * Registration of nodes is performed by several overloaded methods with the
 * name {@code register()}. It is assumed that an object can be registered only
 * if the {@link #isLoaded() } method returns {@code false}.
 * </p>
 * <p>
 * The {@link #load() } method checks the initial (default) state of objects to
 * detect inconsistencies between the registered objects and their state, which
 * was saved during the previous execution of the application. If
 * inconsistencies are found, the stored state is considered invalid and the new
 * default state is saved the registration of new objects is not allowed.
 * </p>
 * <p>
 * Applying the {@link #save() } method saves the current state of the
 * registered objects. Running a method for execution can be performed by the
 * application when certain user actions are performed, for example, clicking a
 * button or executing a menu item or closing the main window.
 * </p>
 * <p>
 * The {@link #reset() } method is used to set the state of registered objects
 * to the default state. The method can be applied at any time during the
 * execution of the application. If used when the {@code isLoaded()} method
 * returns {@code false}, then the method should not do anything.
 * </p>
 *
 * @author Valery Shyshkin
 */
public interface StateLoader {

    /**
     * Return the boolean value which specifies has the method load() already
     * finished or not.      
     * @return {@code true} if the method {@code load() } has already finished. 
     *   {@code false } otherwise.
     */
    boolean isLoaded();

    /**
     * Checks whether the specified object was registered early.
     *
     * @param node the object to be checked
     * @return {@code true } if the object was registered. {@code false }
     * otherwise.
     */
    boolean isRegistered(Node node);

    /**
     * The method checks the initial (default) state of objects to detect
     * inconsistencies between the registered objects and their state, which was
     * saved during the previous execution of the application. If
     * inconsistencies are found, the stored state is considered invalid and the
     * new default state is saved the registration of new objects is not
     * allowed.
     */
    void load();

    /**
     * The method is used to set the state of registered objects to the default
     * state. The method can be applied at any time during the execution of the
     * application. If used when the {@code isLoaded()} method returns
     * {@code false}, then the method should not do anything.
     */
    void reset();

    /**
     * Applying the method saves the current state of the registered objects.
     * Running a method for execution can be performed by the application when
     * certain user actions are performed, for example, clicking a button or
     * executing a menu item or closing the main window.
     */
    void save();

    /**
     * Registers the specified object of type {@code javafx.scene.Node} with the
     * given {@code fieldName}.
     *
     * @param fieldName the string value used as identifier for the node to be
     * registered.
     *
     * @param node the object to be registered
     */
    void register(String fieldName, Node node);

    /**
     * Creates a new object of the specified class and registered it with the
     * given name. the given {@code fieldName}.
     *
     * @param fieldName the string value used as identifier for the node to be
     * registered.
     * @param clazz the  class used to create and register an object.
     * @return a new registered object
     */
    Node register(String fieldName, Class<? extends Node> clazz);

}
