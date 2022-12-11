package aoc2022;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import common.AocDay;
import common.FileReader;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day11 implements AocDay<Long> {

    @Override
    public Long exercise1(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        return passObjectsBetweenMonkies(input, 20, l -> l/3, false);
    }

    @Override
    public Long exercise2(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        return passObjectsBetweenMonkies(input, 10000, Function.identity(), true);
    }

    private Long passObjectsBetweenMonkies(List<String> input, int iterationCount, Function<Long, Long> fearReduction,
                                           boolean optimizeForBigNumbers) {
        Map<Integer, Monkey> monkies = getMonkeyMap(input, optimizeForBigNumbers);

        for (int i=0;i<iterationCount;i++) {
            for(Monkey monkey: monkies.values()) {
                while(!monkey.items.isEmpty()) {
                    Long item = monkey.items.remove(0);
                    Long newItemValue = monkey.operation.apply(item);
                    newItemValue = fearReduction.apply(newItemValue);
                    if (monkey.condition.test(newItemValue)) {
                        monkies.get(monkey.trueTargetMonkey).items.add(newItemValue);
                    } else {
                        monkies.get(monkey.falseTargetMonkey).items.add(newItemValue);
                    }
                    monkey.inspectionCount++;
                }
            }

            System.out.println("After " + (i+1) + " rounds, inspection counts are:");
            for (Map.Entry<Integer, Monkey> m: monkies.entrySet()) {
                System.out.println("Monkey " + m.getKey() + ": " + m.getValue().inspectionCount);
            }
        }

        return monkies.values().stream()
            .map(m -> m.inspectionCount)
            .sorted(Comparator.reverseOrder())
            .limit(2)
            .reduce(1L, (a, b) -> a * b);
    }

    private Long extractModuloBase(List<String> input, boolean needOptimization) {
        List<Long> allDivisibleCriteria = new ArrayList<>();
        for (String line: input) {
            List<String> tokens = Splitter.on(" ").omitEmptyStrings().splitToList(line);
            if (tokens.size() > 0 && "Test:".equals(tokens.get(0))) {
                allDivisibleCriteria.add(Long.parseLong(tokens.get(3)));
            }
        }
        return needOptimization ? lcm(allDivisibleCriteria.stream().mapToLong(l -> l).toArray()) : 100000;
    }

    private Map<Integer, Monkey> getMonkeyMap(List<String> input, boolean needOptimization) {
        Long lcm = extractModuloBase(input, needOptimization);

        Map<Integer, Monkey> monkies = new HashMap<>();
        Monkey currentMonkey = new Monkey();

        for (String line: input) {
            List<String> tokens = Splitter.on(" ").omitEmptyStrings().trimResults(CharMatcher.anyOf(": ")).splitToList(line);
            if (tokens.size() == 0) {
                continue;
            }

            switch (tokens.get(0)) {
                case "Monkey" -> {
                    currentMonkey = new Monkey();
                    monkies.put(Integer.parseInt(tokens.get(1)), currentMonkey);
                }
                case "Starting" -> currentMonkey.items = tokens.subList(2, tokens.size()).stream()
                    .map(String::trim)
                    .map(token -> StringUtils.strip(token, ","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
                case "Operation" -> currentMonkey.operation = getOperation(tokens, lcm);
                case "Test" -> currentMonkey.condition = x -> x % Integer.parseInt(tokens.get(3)) == 0;
                case "If" -> {
                    if ("true".equals(tokens.get(1))) {
                        currentMonkey.trueTargetMonkey = Integer.parseInt(tokens.get(5));
                    } else {
                        currentMonkey.falseTargetMonkey = Integer.parseInt(tokens.get(5));
                    }
                }
                default -> throw new UnsupportedOperationException();
            }
        }

        return monkies;
    }

    private Function<Long, Long> getOperation(List<String> tokens, Long lcm) {
        BiFunction<Long, Long, Long> method;
        if ("+".equals(tokens.get(4))) {
            method = Long::sum;
        } else {
            method = (a, b) -> a * b;
        }

        String operand1 = tokens.get(3);
        String operand2 = tokens.get(5);

        if ("old".equals(operand1) && "old".equals(operand2)) {
            return x -> method.apply(x, x) % lcm;
        } else if ("old".equals(operand1)) {
            return x -> method.apply(x, Long.parseLong(operand2)) % lcm;
        } else {
            return x -> method.apply(x, Long.parseLong(operand1)) % lcm;
        }
    }

    private static class Monkey {
        long inspectionCount = 0;
        List<Long> items;
        Function<Long, Long> operation;
        Predicate<Long> condition;
        int trueTargetMonkey;
        int falseTargetMonkey;
    }

    private long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    private long lcm(long[] input) {
        long result = input[0];
        for(int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return result;
    }

    private long gcd(long a, long b) {
        while (b > 0)
        {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
