package aoc2022;

import common.AocDay;
import org.junit.jupiter.api.*;

import java.lang.reflect.InvocationTargetException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AocTest<T> {
    protected final AocDay<T> testObject;
    private final int dayNumber;
    private final T firstExpected;
    private final T secondExpected;

    public AocTest(int dayNumber, T firstExpected) {
        this.dayNumber = dayNumber;
        this.firstExpected = firstExpected;
        this.secondExpected = null;
        try {
            testObject = (AocDay) Class.forName("aoc2022.Day" + dayNumber).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public AocTest(int dayNumber, T firstExpected, T secondExpected) {
        this.dayNumber = dayNumber;
        this.firstExpected = firstExpected;
        this.secondExpected = secondExpected;
        try {
            testObject = (AocDay) Class.forName("aoc2022.Day" + dayNumber).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    public void testExercise1() {
        T actual = testObject.exercise1("test" + dayNumber + ".txt");
        Assertions.assertEquals(firstExpected, actual);
    }

    @Test
    @Order(2)
    public void testExercise2() {
        if (secondExpected == null) {
            return;
        }
        T actual = testObject.exercise2("test" + dayNumber + ".txt");
        Assertions.assertEquals(secondExpected, actual);
    }

    @Test
    @Order(3)
    public void testReal1() {
        T actual = testObject.exercise1("real" + dayNumber + ".txt");
        System.out.println(actual);
    }

    @Test
    @Order(4)
    public void testReal2() {
        T actual = testObject.exercise2("real" + dayNumber + ".txt");
        System.out.println(actual);
    }
}