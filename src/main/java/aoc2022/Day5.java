package aoc2022;

import com.google.common.base.Joiner;
import common.AocDay;
import common.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day5 implements AocDay<String> {

    private static final Pattern pattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");

    @Override
    public String exercise1(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        ExercisePlan plan = convertToPlan(input);
        while(plan.instructions.size() > 0) {
            executeInstructionForCM9000(plan, plan.instructions.remove(0));
        }
        return Joiner.on("").join(plan.state.stream().map(Stack::pop).collect(Collectors.toList()));
    }

    private ExercisePlan convertToPlan(List<String> input) {
        ExercisePlan plan = new ExercisePlan(new ArrayList<>(), new ArrayList<>());

        int i = 0;
        for (;input.get(i).contains("[");i++) {}

        String base = input.get(i);
        List<Integer> columns = new ArrayList<>();
        List<Character> ignoreChars = List.of(' ', ']', '[');
        for (int j=0;j<base.length();j++) {
            if (!ignoreChars.contains(base.charAt(j))) {
                plan.state.add(new Stack<>());
                columns.add(j);
            }
        }

        for (int l = i - 1;l >= 0;l--) {
            for (int col=0; col<columns.size();col++) {
                if (columns.get(col) < input.get(l).length()) {
                    char nextChar = input.get(l).charAt(columns.get(col));
                    if (nextChar != ' ') {
                        plan.state.get(col).push(nextChar);
                    }
                }
            }
        }

        Matcher m;
        for (i += 2;i<input.size();i++) {
            m = pattern.matcher(input.get(i));
            if (m.find()) {
                plan.instructions.add(new Instruction(m.group(2), m.group(3), m.group(1)));
            }
        }

        return plan;
    }

    private void executeInstructionForCM9000(ExercisePlan cargo, Instruction instruction) {
        cargo.log();
        instruction.log();
        for (int i=0;i<instruction.number;i++) {
            Character ch = cargo.state.get(instruction.fromStack-1).pop();
            cargo.state.get(instruction.toStack-1).push(ch);
            cargo.log();
        }
    }

    @Override
    public String exercise2(String fileName) {
        List<String> input = FileReader.readAllLines(fileName, Function.identity());
        ExercisePlan plan = convertToPlan(input);
        while(plan.instructions.size() > 0) {
            executeInstructionForCM9001(plan, plan.instructions.remove(0));
        }
        return Joiner.on("").join(plan.state.stream().map(Stack::pop).collect(Collectors.toList()));
    }

    private void executeInstructionForCM9001(ExercisePlan cargo, Instruction instruction) {
        cargo.log();
        instruction.log();
        Stack<Character> crateMoverStack = new Stack<>();
        for (int i=0;i<instruction.number;i++) {
            Character ch = cargo.state.get(instruction.fromStack-1).pop();
            crateMoverStack.push(ch);
        }

        while(!crateMoverStack.empty()) {
            Character ch = crateMoverStack.pop();
            cargo.state.get(instruction.toStack-1).push(ch);
        }
    }

    private record ExercisePlan(List<Stack<Character>> state, List<Instruction> instructions) {

        private void log() {
            System.out.println(Joiner.on("").join(state.stream().map(st -> st.empty() ? '-' : st.peek()).collect(Collectors.toList())));
        }
    }

    private record Instruction(int fromStack, int toStack, int number) {
        Instruction(String fromStack, String toStack, String number) {
            this(Integer.parseInt(fromStack), Integer.parseInt(toStack), Integer.parseInt(number));
        }

        private void log() {
            System.out.println(fromStack + " -> " + toStack + " x" + number);
        }
    }
}
