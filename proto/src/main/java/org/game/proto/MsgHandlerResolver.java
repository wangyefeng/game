package org.game.proto;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class MsgHandlerResolver {

    public static Class<?> resolveGenericType(Class<?> clazz, Class<?> targetInterface) {
        Map<TypeVariable<?>, Type> typeMap = new HashMap<>();
        return resolveGenericTypeRecursive(clazz, targetInterface, typeMap);
    }

    private static Class<?> resolveGenericTypeRecursive(Type type, Class<?> targetInterface, Map<TypeVariable<?>, Type> typeMap) {
        if (type instanceof Class<?> clazz) {

            // 查找接口
            for (Type iface : clazz.getGenericInterfaces()) {
                Class<?> result = resolveGenericTypeRecursive(iface, targetInterface, typeMap);
                if (result != null) return result;
            }

            // 查找父类
            Type superClass = clazz.getGenericSuperclass();
            if (superClass != null) {
                return resolveGenericTypeRecursive(superClass, targetInterface, typeMap);
            }

        } else if (type instanceof ParameterizedType pt) {
            Type rawType = pt.getRawType();

            if (rawType instanceof Class && targetInterface.isAssignableFrom((Class<?>) rawType)) {
                Class<?> rawClass = (Class<?>) rawType;
                TypeVariable<?>[] typeParams = rawClass.getTypeParameters();
                Type[] actualArgs = pt.getActualTypeArguments();

                for (int i = 0; i < typeParams.length; i++) {
                    typeMap.put(typeParams[i], actualArgs[i]);
                }

                if (rawClass.equals(targetInterface)) {
                    TypeVariable<?>[] targetTypeParams = targetInterface.getTypeParameters();
                    Type resolved = typeMap.get(targetTypeParams[0]);
                    while (!(resolved instanceof Class<?>)) {
                        resolved = typeMap.get(resolved);
                    }
                    return (Class) resolved;
                }

                // 继续向上查
                for (Type iface : rawClass.getGenericInterfaces()) {
                    Class<?> result = resolveGenericTypeRecursive(iface, targetInterface, new HashMap<>(typeMap));
                    if (result != null) return result;
                }

                return resolveGenericTypeRecursive(rawClass.getGenericSuperclass(), targetInterface, new HashMap<>(typeMap));
            }
        }

        return null;
    }
}
