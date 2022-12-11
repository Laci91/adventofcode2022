package aoc2022;

import common.AocDay;
import common.FileReader;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 implements AocDay<Long> {

    Pattern MONKEY = Pattern.compile("Monkey (\\d):");
    Pattern ITEMS = Pattern.compile("  Starting items: (.*)");
    Pattern OPERATION_ADD = Pattern.compile("  Operation: new = (.*) \\+ (.*)");
    Pattern OPERATION_MULTIPLY = Pattern.compile("  Operation: new = (.*) \\* (.*)");
    Pattern TEST = Pattern.compile("  Test: divisible by (\\d+)");
    Pattern TRUE = Pattern.compile("    If true: throw to monkey (\\d)");
    Pattern FALSE = Pattern.compile("    If false: throw to monkey (\\d)");
    @Override
    public Long exercise1(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        Map<Integer, Monkey> monkies = getMonkeyMap(input, false);

        for (int i=0;i<20;i++) {
            for(Monkey monkey: monkies.values()) {
                while(!monkey.items.isEmpty()) {
                    Long item = monkey.items.remove(0);
                    Long newItemValue = monkey.operation.apply(item);
                    newItemValue /= 3;
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

    private Map<Integer, Monkey> getMonkeyMap(List<String> input, boolean needOptimization) {
        Map<Integer, Monkey> monkies = new HashMap<>();
        Monkey currentMonkey = null;
        List<Long> allDivisibleCriteria = new ArrayList<>();
        for (String line: input) {
            Matcher test = TEST.matcher(line);
            if (test.matches()) {
                allDivisibleCriteria.add(Long.parseLong(test.group(1)));
            }
        }
        Long lcm = needOptimization ? lcm(allDivisibleCriteria.stream().mapToLong(l -> l).toArray()) : 100000;

        for (String line: input) {
            Matcher monkey = MONKEY.matcher(line);
            if (monkey.matches()) {
                currentMonkey = new Monkey();
                monkies.put(Integer.parseInt(monkey.group(1)), currentMonkey);
            }

            Matcher items = ITEMS.matcher(line);
            if (items.matches()) {
                currentMonkey.items = Stream.of(items.group(1).split(",")).map(String::trim).map(Long::parseLong).collect(Collectors.toList());
            }

            Matcher operationAdd = OPERATION_ADD.matcher(line);
            Matcher operationMultiply = OPERATION_MULTIPLY.matcher(line);
            if (operationAdd.matches() || operationMultiply.matches()) {
                BiFunction<Long, Long, Long> method; String operand1; String operand2;
                if (operationAdd.matches()) {
                    method = Long::sum;
                    operand1 = operationAdd.group(1);
                    operand2 = operationAdd.group(2);
                } else {
                    method = (a, b) -> a * b;
                    operand1 = operationMultiply.group(1);
                    operand2 = operationMultiply.group(2);
                }

                if ("old".equals(operand1) && "old".equals(operand2)) {
                    currentMonkey.operation = x -> method.apply(x, x) % lcm;
                } else if ("old".equals(operand1)) {
                    currentMonkey.operation = x -> method.apply(x, Long.parseLong(operand2)) % lcm;
                } else {
                    currentMonkey.operation = x -> method.apply(x, Long.parseLong(operand1)) % lcm;
                }
            }

            Matcher test = TEST.matcher(line);
            if (test.matches()) {
                currentMonkey.condition = x -> x % Integer.parseInt(test.group(1)) == 0;
            }

            Matcher trueMonkey = TRUE.matcher(line);
            if (trueMonkey.matches()) {
                currentMonkey.trueTargetMonkey = Integer.parseInt(trueMonkey.group(1));
            }

            Matcher falseMonkey = FALSE.matcher(line);
            if (falseMonkey.matches()) {
                currentMonkey.falseTargetMonkey = Integer.parseInt(falseMonkey.group(1));
            }
        }

        return monkies;
    }

    @Override
    public Long exercise2(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        Map<Integer, Monkey> monkies = getMonkeyMap(input, true);

        for (int i=0;i<10000;i++) {
            for(Monkey monkey: monkies.values()) {
                while(!monkey.items.isEmpty()) {
                    Long item = monkey.items.remove(0);
                    Long newItemValue = monkey.operation.apply(item);
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
