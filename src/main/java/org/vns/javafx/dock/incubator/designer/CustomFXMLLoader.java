/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.javafx.dock.incubator.designer;

/**
 *
 * @author Valery
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.util.BuilderFactory;
import javafx.util.Callback;

import javafx.fxml.FXMLLoader;
import sun.reflect.CallerSensitive;

/**
 * Loads an object hierarchy from an XML document.
 *
 * @since JavaFX 2.0
 */
public class CustomFXMLLoader extends FXMLLoader {

    // Indicates permission to get the ClassLoader
    private static final RuntimePermission GET_CLASSLOADER_PERMISSION
            = new RuntimePermission("getClassLoader");

    /**
     * Creates a new FXMLLoader instance.
     */
    public CustomFXMLLoader() {
        this((URL) null);
    }

    /**
     * Creates a new FXMLLoader instance.
     *
     * @param location ??
     * @since JavaFX 2.1
     */
    public CustomFXMLLoader(URL location) {
        this(location, null);
    }

    /**
     * Creates a new FXMLLoader instance.
     *
     * @param location ??
     * @param resources ??
     * @since JavaFX 2.1
     */
    public CustomFXMLLoader(URL location, ResourceBundle resources) {
        this(location, resources, null);
    }

    /**
     * Creates a new FXMLLoader instance.
     *
     * @param location ??
     * @param resources ??
     * @param builderFactory ??
     * @since JavaFX 2.1
     */
    public CustomFXMLLoader(URL location, ResourceBundle resources, BuilderFactory builderFactory) {
        this(location, resources, builderFactory, null);
    }

    /**
     * Creates a new FXMLLoader instance.
     *
     * @param location ??
     * @param resources ??
     * @param builderFactory ??
     * @param controllerFactory ??
     * @since JavaFX 2.1
     */
    public CustomFXMLLoader(URL location, ResourceBundle resources, BuilderFactory builderFactory,
            Callback<Class<?>, Object> controllerFactory) {
        this(location, resources, builderFactory, controllerFactory, Charset.forName(DEFAULT_CHARSET_NAME));
    }

    /**
     * Creates a new FXMLLoader instance.
     *
     * @param charset ??
     */
    public CustomFXMLLoader(Charset charset) {
        this(null, null, null, null, charset);
    }

    /**
     * Creates a new FXMLLoader instance.
     *
     * @param location ??
     * @param resources ??
     * @param builderFactory ??
     * @param controllerFactory ??
     * @param charset ??
     * @since JavaFX 2.1
     */
    public CustomFXMLLoader(URL location, ResourceBundle resources, BuilderFactory builderFactory,
            Callback<Class<?>, Object> controllerFactory, Charset charset) {
        this(location, resources, builderFactory, controllerFactory, charset,
                new LinkedList<FXMLLoader>());
    }

    /**
     * Creates a new FXMLLoader instance.
     *
     * @param location ??
     * @param resources ?? 
     * @param builderFactory ??
     * @param controllerFactory ??
     * @param charset ??
     * @param loaders ??
     * @since JavaFX 2.1
     */
    public CustomFXMLLoader(URL location, ResourceBundle resources, BuilderFactory builderFactory,
            Callback<Class<?>, Object> controllerFactory, Charset charset,
            LinkedList<FXMLLoader> loaders) {
        super(location, resources, builderFactory, controllerFactory, charset, loaders);
    }
    @CallerSensitive
    @Override
    public <T> T load(InputStream inputStream) throws IOException {
        return super.load(inputStream);
    }
}
