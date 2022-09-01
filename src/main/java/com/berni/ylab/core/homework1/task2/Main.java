package com.berni.ylab.core.homework1.task2;

import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;

import static java.lang.System.out;

public class Main {

    private static final int[] TASK_ARRAY = new int[]{5, 6, 3, 2, 5, 1, 4, 9};

    public static void main(String[] args) {
        out.print("Unsorted task array: ");
        out.println(Arrays.toString(TASK_ARRAY));
        out.print("  Sorted task array: ");
        out.println(Arrays.toString(RadixSort.sort(TASK_ARRAY)));
        out.println();

        TestRadixSort.runTest();
    }

    /**
     * {@link <a href="https://en.wikipedia.org/wiki/Radix_sort">Wiki Radix sort</a>}
     * Modified sorting that supports negative numbers.
     * Arranges items in an array based on the value of a specific bit.
     */
    private static class RadixSort {

        public static int[] sort(int[] array) {
            Objects.requireNonNull(array);
            if (array.length == 0) {
                return array;
            }
            int significantBitCount = significantBitsInNumber(getNumberWithMaxSignificantBit(array));

            for (int bitIndex = 0; bitIndex < significantBitCount; bitIndex++) {
                array = countingSort(array, bitIndex);
            }
            return offsetNegativeTailToTop(array);
        }

        private static int significantBitsInNumber(int num) {
            int significantBitCount = 0;
            for (int i = 0; i < 32; i++) {
                if (bitValue(num, i) == 1) {
                    significantBitCount = i;
                }
            }
            return ++significantBitCount;
        }

        private static int getNumberWithMaxSignificantBit(final int[] array) {
            int maxBitsNumber = 0;
            for (int i : array) {
                maxBitsNumber |= i;
            }
            return maxBitsNumber;
        }

        private static int[] countingSort(final int[] array, int bitIndex) {

            // bitCounts[0] saves the number of items with 0 in this bit
            // bitCounts[1] saves the number of items with 1 in this bit
            final int[] bitCounts = new int[]{0, 0};
            for (int item : array) {
                bitCounts[bitValue(item, bitIndex)] += 1;
            }

            // indices[0] saves the index where we should put the next item
            // with 0 in this bit.
            // indices[1] saves the index where we should put the next item
            // with 1 in this bit.
            //
            // the items with 0 in this bit come at the beginning (index 0).
            // the items with 1 in this bit come after all the items with 0.
            final int[] indices = new int[]{0, bitCounts[0]};

            final int[] sortedArray = new int[array.length];

            for (int item : array) {
                final int itemBitValue = bitValue(item, bitIndex);
                // place the item at the next open index for its bit value
                sortedArray[indices[itemBitValue]] = item;
                // the next item with the same bit value goes after this item
                indices[itemBitValue] += 1;
            }
            return sortedArray;
        }

        private static int bitValue(int number, int bitIndex) {
            int mask = 1 << bitIndex;
            if ((number & mask) != 0) {
                return 1;
            }
            return 0;
        }

        /**
         * It's need to support negative numbers sorting. After Counting
         * sorting the sorted array represents array of [0, 1, 2, 3, -3, -2, -1].
         * This method offset the negative tail to the top array [-3, -2, -1, 0, 1, 2, 3].
         */
        private static int[] offsetNegativeTailToTop(final int[] radixSortedArray) {
            final int arraySize = radixSortedArray.length;

            if (radixSortedArray[arraySize - 1] >= 0) {
                return radixSortedArray;
            }

            final int[] sortedArray = new int[arraySize];
            indexOfFirstNegativeItem(radixSortedArray).ifPresent(initialIndexNegativeTail -> {
                for (int i = 0; i < arraySize; i++) {
                    initialIndexNegativeTail = initialIndexNegativeTail == arraySize ? 0 : initialIndexNegativeTail;
                    sortedArray[i] = radixSortedArray[initialIndexNegativeTail];
                    initialIndexNegativeTail++;
                }
            });
            return sortedArray;
        }

        private static OptionalInt indexOfFirstNegativeItem(final int[] array) {
            int indexOfFirstNegativeItem = 0;

            while (indexOfFirstNegativeItem < array.length && array[indexOfFirstNegativeItem] >= 0) {
                indexOfFirstNegativeItem++;
            }
            return indexOfFirstNegativeItem >= array.length ?
                    OptionalInt.empty() :
                    OptionalInt.of(indexOfFirstNegativeItem);
        }
    }

    private static class TestRadixSort {

        private static void runTest() {
            testSortEmptyArray();
            testSortArrayWithOneItem();
            testSortArrayWithIntegerMaxItem();
            testSortArrayWithIntegerMinItem();
            testSortArrayWithIntegerMaxAndMinItems();
            testSortRandomArrayWithIntegerMaxAndMin();
            testSortRandomArrayWithoutIntegerMaxAndMin();
            testSortArrayWithIntegerMaxAndMinItemsDuplicates();
            testSortArrayAllNegativeItems();
            testSortArrayOfEdgeIntVales();
        }

        private static void testSortEmptyArray() {
            int[] emptyArray = new int[]{};
            int[] sortedArray = RadixSort.sort(emptyArray);
            int[] expectedArray = new int[]{};
            assert Arrays.equals(expectedArray, sortedArray);
        }

        private static void testSortArrayWithOneItem() {
            int[] unsortedArray = new int[]{0};
            int[] sortedArray = RadixSort.sort(unsortedArray);
            int[] expectedArray = new int[]{0};
            assert Arrays.equals(expectedArray, sortedArray);
        }

        private static void testSortArrayWithIntegerMaxItem() {
            int[] unsortedArray = new int[]{Integer.MAX_VALUE};
            int[] sorted = RadixSort.sort(unsortedArray);
            int[] expectedArray = new int[]{Integer.MAX_VALUE};
            assert Arrays.equals(expectedArray, sorted);
        }

        private static void testSortArrayWithIntegerMinItem() {
            int[] unsortedArray = new int[]{Integer.MIN_VALUE};
            int[] sorted = RadixSort.sort(unsortedArray);
            int[] expectedArray = new int[]{Integer.MIN_VALUE};
            assert Arrays.equals(expectedArray, sorted);
        }

        private static void testSortArrayWithIntegerMaxAndMinItems() {
            int[] unsortedArray = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
            int[] expectedArray = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
            int[] sorted = RadixSort.sort(unsortedArray);
            assert Arrays.equals(sorted, expectedArray);
        }

        private static void testSortRandomArrayWithIntegerMaxAndMin() {
            int[] unsortedArray = new int[]{1, -2, Integer.MIN_VALUE, -6, 8, 0, 0, Integer.MAX_VALUE, 0, -22};
            int[] expectedArray = new int[]{Integer.MIN_VALUE, -22, -6, -2, 0, 0, 0, 1, 8, Integer.MAX_VALUE};
            int[] sorted = RadixSort.sort(unsortedArray);
            out.println("Test example output:");
            out.println(Arrays.toString(unsortedArray));
            out.println(Arrays.toString(sorted));
            assert Arrays.equals(expectedArray, sorted);
        }

        private static void testSortRandomArrayWithoutIntegerMaxAndMin() {
            int[] unsortedArray = new int[]{1, -2, -6, 8, 0, 0, 0, -22};
            int[] expectedArray = new int[]{-22, -6, -2, 0, 0, 0, 1, 8};
            int[] sorted = RadixSort.sort(unsortedArray);
            assert Arrays.equals(sorted, expectedArray);
        }

        private static void testSortArrayAllNegativeItems() {
            int[] unsortedArray = new int[]{-7, -2, -6, Integer.MIN_VALUE, -2, -22};
            int[] expectedArray = new int[]{Integer.MIN_VALUE, -22, -7, -6, -2, -2};
            int[] sorted = RadixSort.sort(unsortedArray);

            assert Arrays.equals(sorted, expectedArray);
        }

        private static void testSortArrayWithIntegerMaxAndMinItemsDuplicates() {
            int[] unsortedArray = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE};
            int[] expectedArray = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
            int[] sorted = RadixSort.sort(unsortedArray);
            assert Arrays.equals(sorted, expectedArray);
        }

        private static void testSortArrayOfEdgeIntVales() {
            int[] unsortedArray = new int[]{Integer.MAX_VALUE - 2, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, Integer.MIN_VALUE};
            int[] expectedArray = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 2, Integer.MAX_VALUE - 1};
            int[] sorted = RadixSort.sort(unsortedArray);
            assert Arrays.equals(sorted, expectedArray);
        }
    }
}
