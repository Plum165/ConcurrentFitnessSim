# ConcurrentFitnessSim
A Java-based multithreaded simulation that models clients performing exercises in a gym. Each client completes a random set of exercises, and performance metrics (e.g., waiting time, turnaround time, CPU utilization) are tracked using the TimeTracker class.
It educational base and purpose is to show my TimeTracker class that I made. The GymSimulation.java just to illustrate how it is used and where to call System calls.

GymSimulation.java: Main class that runs the simulation with multiple client threads.

TimeTracker.java: Handles tracking and logging of performance metrics to CSV files

# To run simulation
javac TimeTracker.java GymSimulation.java Client.java
java GymSimulation

# or
You can open GymSimulation.java and run it
