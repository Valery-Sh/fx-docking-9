package org.vns.javafx.dock.api.editor.bean;

import java.security.AllPermission;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.SecureClassLoader;
import java.security.PrivilegedExceptionAction;
import java.security.CodeSource;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedActionException;


/*
 * Create a trampoline class.
 */
public final class MethodUtil extends SecureClassLoader {

    private static final String INVOKER = "org.vns.javafx.dock.api.editor.MethodUtil$ActualInvoker";
    private static final Method actualInvoker = getActualInvoker();

    private MethodUtil() {
        super();
    }

    public static Method getMethod(Class<?> cls, String name, Class<?>[] args)
            throws NoSuchMethodException {
        //My ReflectUtil.checkPackageAccess(cls);
        ReflecttionUtil.checkPackageAccess(cls);
        return cls.getMethod(name, args);
    }

    public static Method[] getMethods(Class<?> cls) {
        //ReflectUtil.checkPackageAccess(cls);
        ReflecttionUtil.checkPackageAccess(cls);
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
        }/* catch (IllegalAccessException iae) {
            // this can't happen
            throw new Error("Unexpected invocation error", iae);
        }*/
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

    public static class ActualInvoker {
        private static Object invoke(Method m, Object obj, Object[] params)
                throws InvocationTargetException, IllegalAccessException {
            //ensureInvocableMethod(m);
            return m.invoke(obj, params);
        }
    }

}
