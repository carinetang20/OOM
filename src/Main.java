import activehub.ActiveHubSystem;

/**
 * Entry point for ActiveHub Sports Centre Management System.
 * Run with the project root as the working directory so {@code data/} is used.
 */
public class Main {
    public static void main(String[] args) {
        ActiveHubSystem system = new ActiveHubSystem();
        system.run();
    }
}
