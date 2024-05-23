package dsa.ajay;

import java.util.Arrays;
import java.util.Random;

public class PerformanceMeasurement {

    public static void main(String[] args) {
        int arraySize = 1000000;
        int[] array = generateRandomArray(arraySize);

        long startTime = System.nanoTime();

        // Task to measure: sorting the array
        Arrays.sort(array);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);

        System.out.println("Time taken to sort the array: " + duration + " nanoseconds");
    }

    private static int[] generateRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size);
        }

        return array;
    }
}
