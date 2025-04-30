package com.naocraftlab.foggypalegarden.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.gui.screens.Screen;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@UtilityClass
public class ReflectUtil {

    public static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SneakyThrows
    public static Screen buildScreen(String className, Screen parent) {
        val screenClass = Class.forName(className);
        val lookup = MethodHandles.lookup();
        val methodType = MethodType.methodType(Screen.class, Screen.class);
        val ofMethodHandle = lookup.findStatic(screenClass, "of", methodType);
        return (Screen) ofMethodHandle.invoke(parent);
    }
}
