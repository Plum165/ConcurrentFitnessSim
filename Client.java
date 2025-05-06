//Moegamat Samsodien
//6 May 2025
//The client class that keep track of what excercises they have done and need to do.
import java.io.IOException;
import java.util.Random;

public class Client {
    private int clientId;
    private TimeTracker tracker;
     private final String[] exercises = {"Push-ups", "Squats", "Plank", "Lunges", "Jumping Jacks"};

    public Client(int clientId, TimeTracker tracker) {
        this.clientId = clientId;
        this.tracker = tracker;
    }

    public void performExercises() throws IOException, InterruptedException {
        tracker.markFirstResponse(clientId); // Measure time until this client first responds
        long clientStart = System.nanoTime(); // Start of full exercise session

        int numExercises = new Random().nextInt(3) + 3; // Client will do 3 to 5 exercises

        for (int j = 0; j < numExercises; j++) {
            // Simulate context switching delay BEFORE the exercise (like waiting for gym equipment)
            System.out.println("Client " + clientId + " is doing " + exercises[j]);
            long switchStart = System.nanoTime();
            Thread.sleep(new Random().nextInt(100) + 50); // 50-150ms "switching" time
            long switchEnd = System.nanoTime();
            tracker.logSwitchTime(clientId, switchStart, switchEnd); // Log context switch time

            // Simulate exercise (the CPU burst)
            long exerciseStart = System.nanoTime();
            Thread.sleep(new Random().nextInt(400) + 100); // 100-500ms workout
            long exerciseEnd = System.nanoTime();
            System.out.println("Client " + clientId + " finished doing " + exercises[j]);
            tracker.logBurstTime(clientId, exerciseStart, exerciseEnd, exercises[j]);
        }

        long clientEnd = System.nanoTime();
        tracker.logFullTaskTime(clientId, clientStart, clientEnd, "All-Exercises");

        TimeTracker.markEntityCompleted(1, System.nanoTime()); // Global mark for simulation done
    }
}