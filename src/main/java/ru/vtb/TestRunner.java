package ru.vtb;

import ru.vtb.annotation.*;
import ru.vtb.exception.TestException;
import ru.vtb.utl.TestClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        runTests(TestClass.class);
    }

    public static void runTests(Class c) throws Exception {

        Map<String, List<Method>> necessaryMethods = getNecessaryMethods(c);

        runStaticMethods(necessaryMethods.get("BeforeSuite"));
        runTestMethods(c, necessaryMethods.get("Test"), necessaryMethods.get("BeforeTest"), necessaryMethods.get("AfterTest"));
        runStaticMethods(necessaryMethods.get("AfterSuite"));

    }

    private static void runTestMethods(Class c, List<Method> testMethods,
                                       List<Method> beforeTestMethods, List<Method> afterTestMethods) throws Exception {
        Collections.sort(testMethods, (o1, o2) -> o2.getAnnotation(Test.class).priority() - o1.getAnnotation(Test.class).priority());

        Object object = c.getConstructor(null).newInstance();


        for (Method method : testMethods) {
            runStaticMethods(beforeTestMethods);
            if (method.isAnnotationPresent(CsvSource.class)) {
                runCsvSourceMethod(method, object);
            } else {
                method.invoke(object, new Object[0]);
            }
            runStaticMethods(afterTestMethods);
        }

    }

    private static void runCsvSourceMethod(Method method, Object object) throws Exception {
        String[] parameters = method.getAnnotation(CsvSource.class).value().split(", ");
        Class[] parameterTypes = method.getParameterTypes();

        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Object o = parameterTypes[i].getConstructor(String.class).newInstance(parameters[i]);
            args[i] = o;

        }

        method.invoke(object, args);
    }


    private static Map<String, List<Method>> getNecessaryMethods(Class c) {
        Map<String, List<Method>> methodsMap = new HashMap<>();
        for (Method method : c.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    if (methodsMap.containsKey("BeforeSuite")) {
                        throw new TestException("Method with annotation BeforeSuite must be single");

                    } else {
                        methodsMap.put("BeforeSuite", Arrays.asList(method));
                    }
                } else {
                    throw new TestException("Method with annotation BeforeSuite must be static");
                }
            } else if (method.isAnnotationPresent(AfterSuite.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    if (methodsMap.containsKey("AfterSuite")) {
                        throw new TestException("Method with annotation AfterSuite must be single");
                    } else {
                        methodsMap.put("AfterSuite", Arrays.asList(method));
                    }
                } else {
                    throw new TestException("Method with annotation AfterSuite must be static");
                }
            } else if (method.isAnnotationPresent(BeforeTest.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    List<Method> value = methodsMap.getOrDefault("BeforeTest", new ArrayList<>());
                    value.add(method);
                    methodsMap.put("BeforeTest", value);
                } else {
                    throw new TestException("Methods with annotation BeforeTest must be static");
                }
            } else if (method.isAnnotationPresent(AfterTest.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    List<Method> value = methodsMap.getOrDefault("AfterTest", new ArrayList<>());
                    value.add(method);
                    methodsMap.put("AfterTest", value);
                } else {
                    throw new TestException("Methods with annotation AfterTest must be static");
                }
            } else if (method.isAnnotationPresent(Test.class)) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    List<Method> value = methodsMap.getOrDefault("Test", new ArrayList<>());
                    value.add(method);
                    methodsMap.put("Test", value);
                } else {
                    throw new TestException("Methods with annotation Test must be non-static");
                }
            }

        }
        return methodsMap;
    }

    public static void runStaticMethods(List<Method> methods) throws Exception {
        if (methods != null) {
            for (Method method : methods) {
                method.invoke(null, new Object[0]);
            }
        }
    }


}
