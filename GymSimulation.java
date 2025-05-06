//Moegamat Samsodien
//6 May 2025
//This simulates a gym where clients do exercises through multithreading
import java.io.IOException;
public class GymSimulation {
    public static void main(String[] args) throws InterruptedException {
        int numClients = 5;
        String folderPath = "simulation_results";
        TimeTracker tracker = new TimeTracker(numClients, folderPath);

        long simulationStart = System.nanoTime();
        tracker.markFirstRequest(simulationStart); // Mark when the first request to start simulation is made
        System.out.println("---------Gym has open for the clients---------");

        Thread[] clients = new Thread[numClients];

        // Create and start client threads
        for (int i = 0; i < numClients; i++) {
            final int clientId = i;
            clients[i] = new Thread(() -> {
                try {
                    Client client = new Client(clientId, tracker);
                    client.performExercises(); // Perform exercises for this client
                } catch (Exception e) {
                    System.err.println("Client " + clientId + " error: " + e.getMessage());
                }
            });

            clients[i].start();
        }

        // Wait for all clients to finish
        for (Thread t : clients) {
            t.join();
        }
         System.out.println("---------Gym closing---------");
         System.out.println("---------Gym closed---------");

        long simulationEnd = System.nanoTime();
        try {
            tracker.logSimulationSummary(simulationStart, simulationEnd); // Log throughput, CPU utilization, etc.
        } catch (IOException e) {
            System.err.println("Error logging simulation summary: " + e.getMessage());
        }
    }
}