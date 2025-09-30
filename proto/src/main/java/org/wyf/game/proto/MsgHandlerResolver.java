package org.wyf.game.proto;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class MsgHandlerResolver {

    public static <T extends M, M> Class<?> resolveGenericType(Class<T> clazz, Class<M> targetInterface) {
        Map<TypeVariable<?>, Type> typeMap = new HashMap<>();
        return resolveGenericTypeRecursive(clazz, targetInterface, typeMap);
    }

    private static <T extends M, M> Class<?> resolveGenericTypeRecursive(Type type, Class<T> targetInterface, Map<TypeVariable<?>, Type> typeMap) {
        if (type instanceof Class<?> clazz) {

            // 查找接口
            for (Type iface : clazz.getGenericInterfaces()) {
                Class<?> result = resolveGenericTypeRecursive(iface, targetInterface, typeMap);
                if (result != null) {
                    return result;
                }
            }

            // 查找父类
            Type superClass = clazz.getGenericSuperclass();
            if (superClass != null) {
                return resolveGenericTypeRecursive(superClass, targetInterface, typeMap);
            }

        } else if (type instanceof ParameterizedType pt) {
            Type rawType = pt.getRawType();

            if (rawType instanceof Class<?> rawClass && targetInterface.isAssignableFrom(rawClass)) {
                TypeVariable<?>[] typeParams = rawClass.getTypeParameters();
                Type[] actualArgs = pt.getActualTypeArguments();

                for (int i = 0; i < typeParams.length; i++) {
                    typeMap.put(typeParams[i], actualArgs[i]);
                }

                if (rawClass.equals(targetInterface)) {
                    TypeVariable<?>[] targetTypeParams = targetInterface.getTypeParameters();
                    Type resolved = typeMap.get(targetTypeParams[0]);
                    int i = 0;
                    while (true) {
                        switch (resolved) {
                            case null -> throw new IllegalArgumentException("can not find generic type");
                            case Class<?> aClass -> {
                                Class<?> result = aClass;
                                for (int j = 0; j < i; j++) {
                                    result = result.arrayType();
                                }
                                return result;
                            }
                            case GenericArrayType genericArrayType -> {
                                resolved = genericArrayType.getGenericComponentType();
                                i++;
                            }
                            default -> {
                            }
                        }
                        resolved = typeMap.get(resolved);
                    }
                }

                // 继续向上查
                for (Type iface : rawClass.getGenericInterfaces()) {
                    Class<?> result = resolveGenericTypeRecursive(iface, targetInterface, typeMap);
                    if (result != null) {
                        return result;
                    }
                }

                return resolveGenericTypeRecursive(rawClass.getGenericSuperclass(), targetInterface, typeMap);
            }
        }

        return null;
    }
}
