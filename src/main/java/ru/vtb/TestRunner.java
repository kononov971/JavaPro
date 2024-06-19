package ru.vtb;

import ru.vtb.annotation.*;
import ru.vtb.exception.TestException;
import ru.vtb.utl.TestClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        runTests(TestClass.class);
    }

    public static void runTests(Class c) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {

        if (checkExcessAnnotations(c)) {
            runBeforeSuiteMethod(c);
            runTestMethods(c);
            runAfterSuiteMethod(c);
        }
    }

    private static boolean checkExcessAnnotations(Class c) {
        int countBefore = 0;
        int countAfter = 0;
        for (Method method : c.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                countBefore++;
            }

            if (method.isAnnotationPresent(AfterSuite.class)) {
                countAfter++;
            }
        }

        if (countBefore <= 1 && countAfter <= 1) {
            return true;
        } else {
            return false;
        }
    }


    private static void runTestMethods(Class c) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Map<Integer, List<Method>> priorityMethodsMap = new TreeMap<>(Comparator.reverseOrder());
        for (Method method : c.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    int key = method.getAnnotation(Test.class).priority();
                    List<Method> value =  priorityMethodsMap.getOrDefault(key, new ArrayList<>());
                    value.add(method);

                    priorityMethodsMap.put(key, value);
                } else {
                    throw new TestException("Method with annotation AfterSuite must be non-static");
                }
            }
        }

        Object object = c.getConstructor(null).newInstance();

        for (Map.Entry<Integer, List<Method>> entry : priorityMethodsMap.entrySet()) {
            for (Method method : entry.getValue()) {
                runBeforeTestMethod(c);
                if (method.isAnnotationPresent(CsvSource.class)) {
                    runCsvSourceMethod(method, object);
                } else {
                    method.invoke(object, new Object[0]);
                }
                runAfterTestMethod(c);
            }
        }
    }

    private static void runCsvSourceMethod(Method method, Object object) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        String[] parameters = method.getAnnotation(CsvSource.class).value().split(", ");
        Class[] parameterTypes = method.getParameterTypes();

        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Object o = parameterTypes[i].getConstructor(String.class).newInstance(parameters[i]);
            args[i] = o;

        }

        method.invoke(object, args);
    }

    private static void runBeforeSuiteMethod(Class c) throws InvocationTargetException, IllegalAccessException {
        runStaticMethodWithAnnotation(c, BeforeSuite.class);
    }

    private static void runAfterSuiteMethod(Class c) throws InvocationTargetException, IllegalAccessException {
        runStaticMethodWithAnnotation(c, AfterSuite.class);
    }
    private static void runBeforeTestMethod(Class c) throws InvocationTargetException, IllegalAccessException {
        runStaticMethodWithAnnotation(c, BeforeTest.class);
    }

    private static void runAfterTestMethod(Class c) throws InvocationTargetException, IllegalAccessException {
        runStaticMethodWithAnnotation(c, AfterTest.class);
    }

    private static void runStaticMethodWithAnnotation(Class c, Class annotationClass) throws InvocationTargetException, IllegalAccessException {
        for (Method method : c.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    method.invoke(null, new Object[0]);
                    return;
                } else {
                    throw new TestException("Method with annotation " + annotationClass.getName() + " must be static");
                }
            }
        }
    }
}
