package jihuayu.patchoulitask.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectHelper {

    public static void setField(Class<?> clazz,String name,Object value,Object t) {
        try {
            Field target = clazz.getField(name);
            target.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            int modify = target.getModifiers() & ~Modifier.FINAL;
            modifiersField.setInt(target, modify);
            target.set(t, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}