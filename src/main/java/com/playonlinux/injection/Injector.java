package com.playonlinux.injection;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Injector {
    private String packageName;

    public Injector(String packageName) {
        this.packageName = packageName;
    }

    public Set<Class<?>> getComponentClasses() {
        Reflections reflections = new Reflections(this.packageName);
        return reflections.getTypesAnnotatedWith(Component.class);
    }

    public <T extends Annotation> Boolean isAnnotatedWith(Field field, Class<T> annotation) {
        return field.getAnnotation(annotation) != null;
    }

    public <T extends Annotation> Boolean isAnnotatedWith(Method method, Class<T> annotation) {
        return method.getAnnotation(annotation) != null;
    }

    public <T extends Annotation> List<Field> getAnnotatedFields(Class<?> annotatedClass, Class<T> annotation) {
        List<Field> fields = new ArrayList<>();
        for(Field field: annotatedClass.getDeclaredFields()) {
            if(isAnnotatedWith(field, annotation)) {
                fields.add(field);
            }
        }
        return fields;
    }

    public <T extends Annotation> List<Method> getAnnotatedMethods(Class<?> annotatedClass, Class<T> annotation) {
        List<Method> methods = new ArrayList<>();
        for(Method method: annotatedClass.getDeclaredMethods()) {
            if(isAnnotatedWith(method, annotation)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public HashMap<Class<?>, Object> loadAllBeans(AbstractConfigFile configFile) throws InjectionException {
        List<Method> methods = this.getAnnotatedMethods(configFile.getClass(), Bean.class);

        HashMap<Class<?>, Object> beans = new HashMap<>();

        for(Method method: methods) {
            method.setAccessible(true);
            try {
                beans.put(method.getReturnType(), method.invoke(configFile));
            } catch (IllegalAccessException e) {
                throw new InjectionException(String.format("Unable to inject dependencies (IllegalAccessException): %s", e));
            } catch (InvocationTargetException e) {
                throw new InjectionException(String.format("Unable to inject dependencies (InvocationTargetException): %s", e));
            }
        }
        return beans;
    }

    public void injectAllBeans(Boolean strictLoadingPolicy, HashMap<Class<?>, Object> beans) throws InjectionException {
        Set<Class<?>> componentClasses = this.getComponentClasses();

        for(Class<?> componentClass: componentClasses) {
            List<Field> fields = this.getAnnotatedFields(componentClass, Inject.class);
            for(Field field: fields){
                if(strictLoadingPolicy && !beans.containsKey(field.getType())) {
                    throw new InjectionException(String.format("Unable to inject %s. Check your config file",
                            field.getType().toString()));
                } else if(beans.containsKey(field.getType())){
                    try {
                        field.setAccessible(true);
                        field.set(null, beans.get(field.getType()));
                    } catch (Throwable e) {
                        e.printStackTrace();
                        throw new InjectionException(String.format("Unable to inject %s. Error while injecting: %s",
                                field.getType().toString(), e));
                    }
                }
            }
        }

    }
}
