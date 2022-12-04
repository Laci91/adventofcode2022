package aoc2022;

import common.AocDay;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public abstract class AocTest {
    protected final AocDay testObject;
    private final int dayNumber;
    private final long firstExpected;
    private final long secondExpected;

    public AocTest(int dayNumber, long firstExpected) {
        this.dayNumber = dayNumber;
        this.firstExpected = firstExpected;
        this.secondExpected = Long.MIN_VALUE;
        try {
            testObject = (AocDay) Class.forName("aoc2022.Day" + dayNumber).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public AocTest(int dayNumber, long firstExpected, long secondExpected) {
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
    public void testExercise1() {
        long actual = testObject.exercise1("test" + dayNumber + ".txt");
        Assertions.assertEquals(firstExpected, actual);
    }

    @Test
    public void testExercise2() {
        if (secondExpected == Long.MIN_VALUE) {
            return;
        }
        long actual = testObject.exercise2("test" + dayNumber + ".txt");
        Assertions.assertEquals(secondExpected, actual);
    }

    @Test
    public void testReal1() {
        long actual = testObject.exercise1("real" + dayNumber + ".txt");
        System.out.println(actual);
    }

    @Test
    public void testReal2() {
        long actual = testObject.exercise2("real" + dayNumber + ".txt");
        System.out.println(actual);
    }
}