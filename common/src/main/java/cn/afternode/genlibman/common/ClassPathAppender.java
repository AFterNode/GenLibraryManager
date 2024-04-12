package cn.afternode.genlibman.common;

import cn.afternode.genlibman.common.agent.GenLibManagerAgent;
import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class ClassPathAppender {
    private static MethodHandles.Lookup hndLookup;
    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.trySetAccessible();
            unsafe = (Unsafe) field.get(null);

            Field f = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            hndLookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void append(File file, ClassLoader loader) {
        try {
            appendA(file, loader);
        } catch (Throwable t) {
            try {
                appendB(file, loader);
            } catch (Throwable th) {
                throw new RuntimeException("All append functions failed (A: %s)".formatted(t.getCause()), th);
            }
        }
    }

    /**
     * URLClassLoader appender
     */
    public static void appendA(File file, ClassLoader ldr) {
        try {
            Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            m.trySetAccessible();
            m.invoke(ldr, file.toURI().toURL());
        } catch (Throwable t) {
            throw new RuntimeException("appendA failed", t);
        }
    }

    /**
     * AppClassLoader appender
     */
    public static void appendB(File file, ClassLoader ldr) {
        try {
            Field ucp;
            try {
                try {
                    ucp = URLClassLoader.class.getDeclaredField("ucp");
                } catch (NoSuchFieldException e) {
                    ucp = ldr.getClass().getDeclaredField("ucp");
                }
            } catch (NoSuchFieldException ex) {
                ucp = ldr.getClass().getSuperclass().getDeclaredField("ucp");
            }

            addURL(ldr, ucp, file.toURI().toURL());
        } catch (Throwable t) {
            throw new RuntimeException("appendB failed", t);
        }
    }

    /**
     * SystemClassLoader appender
     */
    public static void appendZ(File file, ClassLoader ldr) throws IOException {
        GenLibManagerAgent.getInstrumentation().appendToSystemClassLoaderSearch(new JarFile(file));
    }

    private static void addURL(ClassLoader loader, Field ucp, URL url) throws Throwable {
        Object ucpObj = unsafe.getObject(loader, unsafe.objectFieldOffset(ucp));
        MethodHandle hnd = hndLookup.findVirtual(loader.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
        hnd.invoke(loader, url);
    }
}
