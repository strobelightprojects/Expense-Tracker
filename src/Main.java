import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    // File name for storing expenses
    private static final String FILENAME = "expenses.txt";
    // Expense categories
    private static final String[] CATEGORIES = {"groceries", "dates", "rent", "other"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Read existing expenses from file
        Map<String, Double> expenses = readExpensesFromFile();

        boolean isRunning = true;

        // Main loop to input new expenses
        while (isRunning) {
            System.out.println("Enter expense category (groceries, dates, rent, other; or 'exit' to finish): ");
            String category = scanner.nextLine().toLowerCase();
            if (category.equalsIgnoreCase("exit")) {
                isRunning = false;
                break;
            }
            if (!isValidCategory(category)) {
                System.out.println("Invalid category. Please choose from groceries, dates, rent, other.");
                continue;
            }
            System.out.println("Enter expense name: ");
            String expenseName = scanner.nextLine();
            System.out.println("Enter expense amount: ");
            double expenseAmount = getValidDouble(scanner);

            // current date and time
            LocalDateTime dateTime = LocalDateTime.now();

            // Add or update expenses 
            expenses.put(category + " - " + expenseName + " - " + dateTime, expenses.getOrDefault(category + " - " + expenseName + " - " + dateTime, 0.0) + expenseAmount);
        }

        // Write updated expenses to file
        writeExpensesToFile(expenses);

        // Display total expenses for each category
        System.out.println("Total expenses:");
        for (String category : CATEGORIES) {
            double totalExpense = expenses.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(category))
                    .mapToDouble(Map.Entry::getValue)
                    .sum();
            System.out.println(category + ": $" + totalExpense);
        }
    }

    // Check if the category is valid
    private static boolean isValidCategory(String category) {
        for (String validCategory : CATEGORIES) {
            if (category.equals(validCategory)) {
                return true;
            }
        }
        return false;
    }

    // Get a valid double value from user input
    private static double getValidDouble(Scanner scanner) {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // Read expenses from file and return as a map
    private static Map<String, Double> readExpensesFromFile() {
        Map<String, Double> expenses = new HashMap<>();
        try {
            Files.lines(Paths.get(FILENAME)).forEach(line -> {
                String[] parts = line.split(":");
                expenses.put(parts[0], Double.parseDouble(parts[1]));
            });
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return expenses;
    }

    // Write expenses to file
    private static void writeExpensesToFile(Map<String, Double> expenses) {
        try {
            Files.write(Paths.get(FILENAME), expenses.entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .toList());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
