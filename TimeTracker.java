//Moegamat Samsodien
//6 May 2025
//Class that records and logs performance metrics in csv files
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class TimeTracker {
    private int entityCount;
    private int schedulerType;
    private long totalBusyTime;
    private long totalWaitTime = 0;
    private long totalTurnaroundTime = 0;
    private long[] switchTimes;
    private final String baseFolder;
    private static final Object lock = new Object();
    private static int entitiesCompleted = 0;
    private static long endTime;
    private long firstRequestTime;
    private static final double ns_to_s = 1000000000.0;

    private final String[] metricLabels = {"Waiting_Time", "Turnaround_Time", "Response_Time", "CPU_Burst", "Content_Switch_Time", "Total"};

    public TimeTracker(int entityCount,  String baseFolderPath) {
        this.entityCount = entityCount;
        this.baseFolder = baseFolderPath;

        switchTimes = new long[entityCount];

        for (int i = 0; i < entityCount; i++) {
         
            switchTimes[i] = 0;
        }
        if (baseFolderPath == null || baseFolderPath.trim().isEmpty())
        {baseFolderPath = "output";}

        File folder = new File(baseFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        
        }
        
        createAndOverwriteCSV();
    }

    private void createAndOverwriteCSV() {
        for (String label : metricLabels) {
            File file = new File(baseFolder + "/" + label + ".csv");
            try (FileWriter writer = new FileWriter(file, false)) {
               if (label.equals("Total")){writer.write("Task,Time,Metric\n");}
               else{
                writer.write("Entity,Task,Time in nanoseconds,Metric\n");
                  }
            } catch (IOException e) {
                System.err.println("Error creating CSV: " + e.getMessage());
            }
        }
    }

    public void markFirstRequest(long time) {
        this.firstRequestTime = time;
    }

    public void markFirstResponse(int entityId) throws IOException {
        long now = System.nanoTime();
        long responseTime = now - firstRequestTime;
        writeToCSV(responseTime, entityId, "Response_Time", "");
    }

    public static void markEntityCompleted(int totalEntities, long firstRequestTime) {
        synchronized (lock) {
            entitiesCompleted++;
            if (entitiesCompleted == totalEntities) {
                endTime = System.nanoTime();
                long totalTurnaround = endTime - firstRequestTime;
            }
        }
    }

    public void logSimulationSummary(long simStart, long simEnd) throws IOException {
        long duration = simEnd - simStart;
        logUtilization(duration);
        logThroughput(duration);

        writeSummary("Waiting_Time", totalWaitTime);
        writeSummary("Turnaround_Time", totalTurnaroundTime);
    }

    private void writeSummary(String metric, long timeNs) throws IOException {
        double seconds = (double) timeNs / ns_to_s;
        String value = String.format(Locale.US, "%.2f", seconds);
        writeToCSV(value, metric, "Total in seconds");
    }

    public void logThroughput(long durationNs) throws IOException {
        double seconds = (double) durationNs / ns_to_s;
        double throughput = entityCount / seconds;
        String value = String.format(Locale.US, "%.2f", throughput);
        writeToCSV(value, "Throughput", "Total");
    }

    public void logUtilization(long durationNs) throws IOException {
        int numCores = Runtime.getRuntime().availableProcessors();
         double utilization = (double) totalBusyTime * 100 / (durationNs * numCores);
        String value = String.format(Locale.US, "%.2f", utilization);
        writeToCSV(value,  "CPU Utilization", "Total");
    }

    public long calculateWaitingTime(int id, long turnaround, long burst) {
        return turnaround - burst - switchTimes[id];
    }

    public void logFullTaskTime(int id, long start, long end, String task) throws IOException {
        long turnaround = end - start;
        totalTurnaroundTime += turnaround;
        writeToCSV(turnaround, id, "Turnaround_Time", task);

        long wait = calculateWaitingTime(id, turnaround, cpuBursts[id]);
        totalWaitTime += wait;
        writeToCSV(wait, id, "Waiting_Time", task);
    }

    public void logBurstTime(int id, long start, long end, String task) throws IOException {
        long burst = end - start;
        totalBusyTime += burst;
        writeToCSV(burst, id, "CPU_Burst", task);
    }

    public void logSwitchTime(int id, long start, long end) throws IOException {
        long switchTime = end - start;
        switchTimes[id] = switchTime;
        writeToCSV(switchTime, id, "Content_Switch_Time", "");
    }

    private void writeToCSV(long time, int id, String metric, String task) throws IOException {
        String filePath = baseFolder + "/" + metric + ".csv";
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(id + "," + task + "," + time + "," + metric + "\n");
        }
    }

    private void writeToCSV(String value, String metric, String task) throws IOException {
        String filePath = baseFolder + "/Total.csv";
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write( task + "," + value + "," + metric + "\n");
        }
    }
}
