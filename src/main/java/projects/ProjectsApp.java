package projects;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.math.BigDecimal;
import projects.entity.Project; 
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

    // @formatter:off
    private List<String> operations = List.of(
    		"1) Add a project"
    );
    //@formatter:on
    private Scanner scanner = new Scanner(System.in);
    
    private ProjectService projectService = new ProjectService();
    
    public static void main(String[] args) {
        ProjectsApp app = new ProjectsApp();
        app.processUserSelections();
    }

    private void processUserSelections() {
        boolean done = false;

        while (!done) {
            try {
                int selection = getUserSelection();
                // Handle the user's selection
                switch (selection) {
                    case 1:
                        // Handle the "Add a project" operation
                        createProject();  // Call createProject() method
                        System.out.println("You selected 'Add a project'.");
                        break;
                    // Add more cases for other operations as needed

                    case -1:
                        // Exit the application
                        done = true;
                        System.out.println("Enter key selected. Have a nice day!!");
                        break;

                    default:
                        printOperations();
                        System.out.println("\n     " + selection + " is not a valid selection. Try again.");
                        break;
                }
            } catch (Exception e) {
                printOperations();
                System.out.println("\n     An error occurred: " + e.getMessage());
                // Optionally, you can log the exception or take additional actions
            }
        }
    }

    private void createProject() {
        String projectName = getStringInput("Enter the project name");
        BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
        BigDecimal actualHours = getDecimalInput("Enter the actual hours");
        Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
        String notes = getStringInput("Enter the project notes");

        Project project = new Project();
        project.setProjectName(projectName);
        project.setEstimatedHours(estimatedHours);
        project.setActualHours(actualHours);
        project.setDifficulty(difficulty);
        project.setNotes(notes);

        Project dbProject = projectService.addProject(project);

        System.out.println("You have successfully created project: " + dbProject);
    }

    private int getUserSelection() {
        printOperations();
        Integer input = getIntInput("Enter a menu selection");
        return (input == null) ? -1 : input;
    }

    private BigDecimal getDecimalInput(String prompt) {
        String input = getStringInput(prompt);

        if (Objects.isNull(input)) {
            return null;
        }

        try {
            return new BigDecimal(input).setScale(2);
        } catch (NumberFormatException e) {
            throw new DbException("" + input + " is not a valid number. Try again.");
        } catch (Exception e) {
            throw new DbException("" + input + " is not a valid decimal. Try again.");
        }
    }

    private void printOperations() {
        System.out.println("\nThese are the available selections. Press the Enter key to quit:");
        operations.forEach(operation -> System.out.println("     " + operation));
    }

    private Integer getIntInput(String prompt) {
        String input = getStringInput(prompt);

        if (Objects.isNull(input)) {
            return null;
        }

        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            throw new DbException("" + input + " is not a valid number. Try again.");
        } catch (Exception e) {
            throw new DbException("" + input + " is not a valid integer. Try again.");
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt + ": ");
        String input = scanner.nextLine();

        return input.isBlank() ? null : input.trim();
    }
}
