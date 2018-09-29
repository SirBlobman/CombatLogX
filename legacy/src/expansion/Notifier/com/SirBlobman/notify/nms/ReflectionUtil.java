package com.SirBlobman.notify.nms;

import com.SirBlobman.combatlogx.utility.Util;

public class ReflectionUtil extends Util {
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(T instance) {
        return (Class<T>) instance.getClass();
    }
    
    public static Class<?> getInnerClass(Class<?> original, String innerClassName) {
        try {
            Class<?>[] classes = original.getClasses();
            for(Class<?> clazz : classes) {
                String name = clazz.getSimpleName();
                if(name.equals(innerClassName)) return clazz;
            }
            return null;
        } catch(Throwable ex) {
            print("An error occured getting an inner class!");
            ex.printStackTrace();
            return null;
        }
    }
}