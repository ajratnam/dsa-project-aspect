package dsa.ajay;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GlobalContext extends BaseContext {
    public int globalVariable;

    public GlobalContext(){}

    public GlobalContext(int globalVariable) {
        this.globalVariable = globalVariable;
    }

    public int performOperation(int a, String b) {
        globalVariable += a;
        System.out.println("Global variable updated: " + globalVariable);
        return globalVariable + b.length();
    }

    public String anotherMethod(String str) {
        System.out.println("Received string: " + str);
        return str.toUpperCase();
    }

    public void yetAnotherMethod() {
        System.out.println("No parameters method called.");
    }

    public void calculateAverageViews(String csvFile) {
        long totalViews = 0;
        int viewCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("datasets/" + csvFile + ".csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 8)
                    continue;
                long views = Long.parseLong(values[7]);
                totalViews += views;
                viewCount++;

                for (long i = 0; i< 1000000L; i++);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            return;
        }

        double avg = (double) totalViews / viewCount;
        System.out.println("Average views of region " + csvFile + " is calculated as - " + avg);
    }

    public void calculateAverageViewsOfChannel(String csvFile, String channelName) {
        long totalViews = 0;
        int viewCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("datasets/" + csvFile + ".csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 8)
                    continue;
                long views = Long.parseLong(values[7]);
                if (values[3].equals(channelName)) {
                    totalViews += views;
                    viewCount++;
                }

                for (long i = 0; i< 1000000L; i++);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            return;
        }

        double avg = (double) totalViews / viewCount;
        System.out.println("Average views of channel " + channelName + " is calculated as - " + avg);
    }
}