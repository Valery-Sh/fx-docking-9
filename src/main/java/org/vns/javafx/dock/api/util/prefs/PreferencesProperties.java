package org.vns.javafx.dock.api.util.prefs;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public interface PreferencesProperties {
    
    public static String HIDDEN_KEY = "...HIDDEN...";
    public static String HIDDEN_VALUE = "HIDDEN_VALUE";
    
    String[] keys();

    public Preferences getPreferences();

    String getId();

    boolean getBoolean(String key, boolean def);
    
    double getDouble(String key, double def);
    
    float getFloat(String key, float def);
    
    int getInt(String key, int def);

    long getLong(String key, long def);

    String getString(String key, String def);
    
    void putBoolean(String key, boolean value);
    
    void putDouble(String key, double value);

    void putFloat(String key, float value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    PreferencesProperties setProperty(String propName, String value);

    String getProperty(String propName);

    void putString(String key, String value);
    
    byte[] getByteArray(String key, byte[] def);
    
    void putByteArray(String key, byte[] value);
    
    File getFileFromString(String key, Path filePath);
    
    void putFileAsString(String key, File value);
    
    /**
     * Removes all of the preferences (<i>key-value associations</i>) in this 
     * preference node. 
     * This call has no effect on any descendants of 
     * this node.
     * <p>
     * If this implementation supports stored defaults, and this node in the preferences hierarchy contains any such defaults, the stored defaults will be "exposed" by this call, in the sense that they will be returned by succeeding calls to get.
     * @throws IllegalStateException - if this node (or an ancestor) 
     *      has been removed with the removeNode() method.
     * 
     */
    void clear(); 

    void removeKey(String key);

    void removeKeys(Predicate<String> predicate);
    
    void forEach(BiConsumer<String, String> action);
    
    PreferencesProperties copyFrom(Map<String,String> props);
    
    PreferencesProperties copyFrom(Properties props);
    
    Map<String, String> toMap();
    
    Properties toProperties();

    Map<String, String> filter(BiPredicate<String, String> predicate);
    Stream<String> keyStream();   
    boolean remove();
    
    int size();
}
