package ru.vtb.utl;

import ru.vtb.annotation.*;

import java.util.Date;

public class TestClass {
    @BeforeSuite
    public static void beforeMethod() {
        System.out.println("before suite");
    }

    @AfterSuite
    public static void afterMethod() {
        System.out.println("after suite");
    }

    @Test
    public void secondMethod() {
        System.out.println("   method with default priority");
    }

    @CsvSource("1993/10/07, Ivanov Ivan, false")
    @Test(priority = 2)
    public void firstMethod(Date birthday, String name, Boolean isProgrammer) {
        System.out.println("   method with priority 2, and parameters " +
                birthday + " " +  name + " " + isProgrammer);
    }

    @CsvSource("10, Java, 20, true")
    @Test
    public void thirdMethod(Integer count, String lang, Integer version, Boolean isStable) {
        System.out.println("   other method with default priority, and parameters " +
                count + " " + lang + " " + version + " " + isStable);
    }

    @Test(priority = 6)
    public void fourthMethod() {
        System.out.println("   method with priority 6");
    }

    @BeforeTest
    public static void beforeTestMethod() {
        System.out.println(" before test");
    }

    @AfterTest
    public static void afterTestMethod() {
        System.out.println(" after test");
    }

    @AfterTest
    public static void anotherAfterTestMethod() {
        System.out.println(" another after test");
    }
}
