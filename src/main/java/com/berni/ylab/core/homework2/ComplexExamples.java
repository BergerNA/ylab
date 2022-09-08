package com.berni.ylab.core.homework2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.out;

public class ComplexExamples {

    static class Person {
        final int id;

        final String name;

        Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person person)) return false;
            return getId() == person.getId() && getName().equals(person.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), getName());
        }
    }

    private static Person[] RAW_DATA = new Person[]{
            new Person(0, "Harry"),
            new Person(0, "Harry"), // дубликат
            new Person(1, "Harry"), // тёзка
            new Person(2, "Harry"),
            new Person(3, "Emily"),
            new Person(4, "Jack"),
            new Person(4, "Jack"),
            new Person(5, "Amelia"),
            new Person(5, "Amelia"),
            new Person(6, "Amelia"),
            new Person(7, "Amelia"),
            new Person(8, "Amelia"),
    };
        /*  Raw data:

        0 - Harry
        0 - Harry
        1 - Harry
        2 - Harry
        3 - Emily
        4 - Jack
        4 - Jack
        5 - Amelia
        5 - Amelia
        6 - Amelia
        7 - Amelia
        8 - Amelia

        **************************************************

        Duplicate filtered, grouped by name, sorted by name and id:

        Amelia:
        1 - Amelia (5)
        2 - Amelia (6)
        3 - Amelia (7)
        4 - Amelia (8)
        Emily:
        1 - Emily (3)
        Harry:
        1 - Harry (0)
        2 - Harry (1)
        3 - Harry (2)
        Jack:
        1 - Jack (4)
     */

    public static void main(String[] args) {
        out.println("Raw data:");
        out.println();

        for (Person person : RAW_DATA) {
            out.println(person.id + " - " + person.name);
        }

        out.println();
        out.println("**************************************************");
        out.println();
        out.println("Duplicate filtered, grouped by name, sorted by name and id:");
        out.println();

        /*
        Task1
            Убрать дубликаты, отсортировать по идентификатору, сгруппировать по имени

            Что должно получиться
                Key: Amelia
                Value:4
                Key: Emily
                Value:1
                Key: Harry
                Value:3
                Key: Jack
                Value:1
         */

        out.println("Task1:\n");

        countGroupedNames(RAW_DATA).forEach((name, quantityByName) ->
                out.format("Key: %s%nValue:%d%n", name, quantityByName));

        /*
        Task2

            [3, 4, 2, 7], 10 -> [3, 7] - вывести пару менно в скобках, которые дают сумму - 10
         */

        out.println("\nTask2:\n");

        final int[] taskArray = new int[]{3, 4, 2, 7};
        final int taskRequireSum = 10;

        out.println(findFirstPairNumberGivingRequiredSum(taskArray, taskRequireSum)
                .stream()
                .map(Arrays::toString)
                .findAny()
                .orElseGet(() -> "There are no pairs of numbers in the array that gives a required sum."));

        FindFirstPairNumberGivingRequiredSumTest.runTest();

        /*
        Task3
            Реализовать функцию нечеткого поиска

                    fuzzySearch("car", "ca6$$#_rtwheel"); // true
                    fuzzySearch("cwhl", "cartwheel"); // true
                    fuzzySearch("cwhee", "cartwheel"); // true
                    fuzzySearch("cartwheel", "cartwheel"); // true
                    fuzzySearch("cwheeel", "cartwheel"); // false
                    fuzzySearch("lw", "cartwheel"); // false
         */

        out.println("\nTask3:\n");

        Stream.of(fuzzySearch("car", "ca6$$#_rtwheel"),
                        fuzzySearch("cwhl", "cartwheel"),
                        fuzzySearch("cwhee", "cartwheel"),
                        fuzzySearch("cartwheel", "ca6$$#_rtwheel"),
                        fuzzySearch("cwheeel", "cartwheel"),
                        fuzzySearch("lw", "cartwheel"))
                .forEach(out::println);

        FuzzySearchTest.runTest();
    }

    private static TreeMap<String, Integer> countGroupedNames(final Person... persons) {
        requireNonNull(persons);
        return Arrays.stream(persons)
                .filter(Objects::nonNull)
                .filter(person -> Objects.nonNull(person.name))
                .distinct()
                .collect(Collectors.toMap(Person::getName, x -> 1, Math::addExact, TreeMap::new));
    }

    private static void requireNonNull(Object... objects) {
        Arrays.stream(objects).forEach(Objects::requireNonNull);
    }

    private static Optional<int[]> findFirstPairNumberGivingRequiredSum(final int[] ints, final int requiredSum) {
        requireNonNull(ints);
        final Set<Integer> numbersGivingRequiredSum = new HashSet<>();

        for (int anInt : ints) {
            if (numbersGivingRequiredSum.contains(anInt)) {
                return Optional.of(new int[]{requiredSum - anInt, anInt});
            }
            numbersGivingRequiredSum.add(requiredSum - anInt);
        }
        return Optional.empty();
    }

    private static boolean fuzzySearch(final String searchingSuffix, final String expression) {
        requireNonNull(searchingSuffix, expression);
        final AtomicInteger lastFoundCharIndex = new AtomicInteger(0);

        return searchingSuffix.chars()
                .dropWhile(ch -> {
                    lastFoundCharIndex.set(expression.indexOf(ch, lastFoundCharIndex.get()));
                    return lastFoundCharIndex.getAndIncrement() != -1;
                })
                .findFirst()
                .isEmpty();
    }

    private static class FindFirstPairNumberGivingRequiredSumTest {

        private static void runTest() {
            final int requiredSum = 10;
            BiPredicate<int[], int[]> pairFinderTest = (givenArray, expectedArray) ->
                    Arrays.equals(
                            findFirstPairNumberGivingRequiredSum(givenArray, requiredSum).orElseGet(() -> new int[]{}),
                            expectedArray);

            assert pairFinderTest.test(new int[]{3, 4, 2, 7}, new int[]{3, 7});
            assert pairFinderTest.test(new int[]{2, 5, 8}, new int[]{2, 8});
            assert pairFinderTest.test(new int[]{5, 2, 8}, new int[]{2, 8});
            assert pairFinderTest.test(new int[]{5, 2, 8, 5}, new int[]{2, 8});
            assert pairFinderTest.test(new int[]{5, 2, 5, 8}, new int[]{5, 5});
            assert pairFinderTest.test(new int[]{5, 5, 2, 8}, new int[]{5, 5});
            assert pairFinderTest.test(new int[]{2, 8, 5, 5}, new int[]{2, 8});
            assert pairFinderTest.test(new int[]{8, 2, 5, 5}, new int[]{8, 2});
            assert pairFinderTest.test(new int[]{8, 1, 3, 5}, new int[]{});
        }
    }

    private static class FuzzySearchTest {

        private static void runTest() {
            assert fuzzySearch("car", "ca6$$#_rtwheel");
            assert fuzzySearch("cwhl", "cartwheel");
            assert fuzzySearch("cwhee", "cartwheel");
            assert fuzzySearch("cartwheel", "ca6$$#_rtwheel");
            assert !fuzzySearch("cwheeel", "cartwheel");
            assert !fuzzySearch("lw", "cartwheel");
            assert fuzzySearch("a", "a");
            assert fuzzySearch("a", "ba");
            assert fuzzySearch("a", "ab");
            assert !fuzzySearch("a", "b");
            assert !fuzzySearch("a", "  ");
            assert !fuzzySearch("a", "");
            assert fuzzySearch("", "a");
        }
    }
}
