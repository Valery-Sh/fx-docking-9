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
package org.vns.javafx.dock.api.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Valery
 */
public class ReflectHelper {

    public static final String PROXY_PACKAGE = "com.sun.proxy";

    public static Class<?> getGetterReturnType(Class<?> forClass, String key) {
        Class<?> retval = null;
        try {

            Method method = MethodUtil.getMethod(forClass, "get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[0]);
            retval = method.getReturnType();
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ReflectHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    public static Type getGetterGenericReturnType(Class<?> forClass, String key) {
        Type retval = null;
        try {
            Method method = MethodUtil.getMethod(forClass, "get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[0]);
            if (method == null) {
                method = MethodUtil.getMethod(forClass, "is" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[0]);
            }
            retval = method.getGenericReturnType();
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ReflectHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    public static Class<?> getListGenericType(Class<?> forClass, String key) {
        Type tp = ReflectHelper.getGetterGenericReturnType(forClass, key);
        return (Class) BeanAdapter.getGenericListItemType(tp);
    }

    public static List<Class<?>> getInterfaces(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        List<Class<?>> list = new ArrayList<>();
        return list;
    }

    public static void getInterfaces(Class<?> clazz, List<Class<?>> list) {
        while (clazz != null) {
            for (Class<?> c : clazz.getInterfaces()) {
                if (!list.contains(c)) {
                    list.add(c);
                    getInterfaces(c, list);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public static Field getField(Class<?> clazz, String name)
            throws NoSuchFieldException {
        checkPackageAccess(clazz);
        return clazz.getField(name);
    }

    public static Field[] getFields(Class<?> clazz) {
        checkPackageAccess(clazz);
        return clazz.getFields();
    }

    public static Field getDeclaredField(Class<?> clazz, String name)
            throws NoSuchFieldException {
        checkPackageAccess(clazz);
        return clazz.getDeclaredField(name);
    }

    public static Field[] getDeclaredFields(Class<?> clazz) {
        checkPackageAccess(clazz);
        return clazz.getDeclaredFields();
    }

    /**
     * Checks package access on the given class.
     *
     * If it is a {@link Proxy#isProxyClass(java.lang.Class)} that implements a
     * non-public interface (i.e. may be in a non-restricted package), also
     * check the package access on the proxy interfaces.
     *
     * @param clazz ???
     */
    public static void checkPackageAccess(Class<?> clazz) {
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            privateCheckPackageAccess(s, clazz);
        }
    }

    /**
     * Checks package access on the given classname. This method is typically
     * called when the Class instance is not available and the caller attempts
     * to load a class on behalf the true caller (application).
     *
     * @param name ???
     */
    public static void checkPackageAccess(String name) {
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            String cname = name.replace('/', '.');
            if (cname.startsWith("[")) {
                int b = cname.lastIndexOf('[') + 2;
                if (b > 1 && b < cname.length()) {
                    cname = cname.substring(b);
                }
            }
            int i = cname.lastIndexOf('.');
            if (i != -1) {
                s.checkPackageAccess(cname.substring(0, i));
            }
        }
    }

    /**
     * NOTE: should only be called if a SecurityManager is installed
     */
    private static void privateCheckPackageAccess(SecurityManager s, Class<?> clazz) {
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }

        String pkg = getPackageName(clazz, false);

        if (pkg != null && !pkg.isEmpty()) {
            s.checkPackageAccess(pkg);
        }

        if (isNonPublicProxyClass(clazz)) {
            privateCheckProxyPackageAccess(s, clazz);
        }
    }

    /**
     * Check package access on the proxy interfaces that the given proxy class
     * implements.
     *
     * @param clazz Proxy class object
     */
    public static void checkProxyPackageAccess(Class<?> clazz) {
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            privateCheckProxyPackageAccess(s, clazz);
        }
    }

    public static boolean isPublic(Class<?> clazz) {
        return clazz.getModifiers() == Modifier.PUBLIC;
    }    
    /**
     * NOTE: should only be called if a SecurityManager is installed
     */
    private static void privateCheckProxyPackageAccess(SecurityManager s, Class<?> clazz) {
        if (Proxy.isProxyClass(clazz)) {
            for (Class<?> intf : clazz.getInterfaces()) {
                privateCheckPackageAccess(s, intf);
            }
        }
    }

    /**
     * Test if the given class is a proxy class that implements non-public
     * interface. Such proxy class may be in a non-restricted package that
     * bypasses checkPackageAccess.
     *
     * @param cls ??
     * @return ??
     */
    public static boolean isNonPublicProxyClass(Class<?> cls) {
        if (!Proxy.isProxyClass(cls)) {
            return false;
        }
        String pkg = getPackageName(cls, false);
        return pkg == null || !pkg.startsWith(PROXY_PACKAGE);
    }

    public static String getPackageName(Class clazz, boolean considerArray) {
        if (!considerArray) {
            return clazz.getPackage().getName();
        }
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        return clazz.getPackage().getName();
    }

    public static class MethodUtil extends SecureClassLoader {

        private static final String INVOKER = "org.vns.javafx.dock.api.bean.ReflectHelper$MethodUtil$ActualInvoker";
        private static final Method actualInvoker = getActualInvoker();

        protected MethodUtil() {
            super();
        }

        public static Method getMethod(Class<?> cls, String name, Class<?>[] args) throws NoSuchMethodException {
            ReflectHelper.checkPackageAccess(cls);
            return cls.getMethod(name, args);
        }

        public static Method[] getMethods(Class<?> cls) {
            ReflectHelper.checkPackageAccess(cls);
            return cls.getMethods();
        }

        public static Object invoke(Method m, Object obj, Object[] parms)
                throws InvocationTargetException, IllegalAccessException {
            try {
                return actualInvoker.invoke(null, new Object[]{m, obj, parms});
            } catch (InvocationTargetException ex) {

                if (ex.getCause() instanceof InvocationTargetException) {
                    throw (InvocationTargetException) ex.getCause();
                } else if (ex.getCause() instanceof IllegalAccessException) {
                    throw (IllegalAccessException) ex.getCause();
                } else if (ex.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) ex.getCause();
                } else if (ex.getCause() instanceof Error) {
                    throw (Error) ex.getCause();
                } else {
                    throw new Error("Unexpected invocation error", ex.getCause());
                }
            }
        }

        private static Method getActualInvoker() {
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<Method>) () -> {
                    Class<?> clazz = null; //getTrampolineClass();
                    try {
                        clazz = Class.forName(INVOKER, true, new MethodUtil());
                    } catch (ClassNotFoundException e) {
                    }

                    Class<?>[] types = {
                        Method.class, Object.class, Object[].class
                    };
                    Method b = clazz.getDeclaredMethod("invoke", types);
                    b.setAccessible(true);
                    return b;
                });
            } catch (PrivilegedActionException | NullPointerException e) {
                throw new InternalError("Cannot load actual invoker", e);
            }
        }

        @Override
        protected PermissionCollection getPermissions(CodeSource codesource) {
            PermissionCollection perms = super.getPermissions(codesource);
            perms.add(new AllPermission());
            return perms;
        }

        private static void ensureInvocableMethod(Method m)
                throws InvocationTargetException {
            Class<?> clazz = m.getDeclaringClass();
            if (clazz.equals(AccessController.class)
                    || clazz.equals(Method.class)
                    || clazz.getName().startsWith("java.lang.invoke.")) {
                throw new InvocationTargetException(
                        new UnsupportedOperationException("invocation not supported"));
            }
        }

        public static class ActualInvoker {

            private static Object invoke(Method m, Object obj, Object[] params)
                    throws InvocationTargetException, IllegalAccessException {
                ensureInvocableMethod(m);
                return m.invoke(obj, params);
            }
        }
    }

}
