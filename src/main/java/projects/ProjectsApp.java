package projects;

import java.sql.Connection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.math.BigDecimal;
import projects.entity.Project; 
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	
	private Project curProject;

    // @formatter:off
    private List<String> operations = List.of(
    		"1) Add a project",
    		"2) List projects",
    		"3) Select a project",
    		"4) Update project details",
    		"5) Delete a project"
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
                        
                    case 2:
                        listProjects(); // Call the new method for listing projects
                        System.out.println("You selected 'List projects'.");
                        break;
                    
                    case 3:
                        selectProject(); // Call the new method for selecting a project
                        System.out.println("You selected 'Select a project'.");
                        break;
                        
                    case 4:
                        updateProjectDetails(); // Call method updateProjectDetails()
                        System.out.println("You selected 'Update project details'.");
                        break;
                                                
                    case 5:
                        deleteProject(); // Call the new method for deleting a project
                        System.out.println("You selected 'Delete a project'.");
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

    private void deleteProject() {
        listProjects();
        int projectIdToDelete = getIntInput("Enter the ID of the project to delete");

        try {
            projectService.deleteProjectById(projectIdToDelete);
            System.out.println("Project deleted successfully.");

            // Check if the project ID in the current project is the same as the ID entered by the user
            if (curProject != null && curProject.getProjectId() == projectIdToDelete) {
                curProject = null;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while deleting the project: " + e.getMessage());
        }
    }
    
	private void updateProjectDetails() {
        listProjects();

        // Check if there is a selected project
        if (Objects.isNull(curProject)) {
            System.out.println("\nPlease select a project.");
            return;
        }

        // Update project details
        updateProjectFields();
    }
    
    private void updateProjectFields() {
        Project updatedProject = new Project();

        // Iterate over each field in the Project object
        // Print the current setting and get user input for update
        String projectName = getStringInput("Enter the project name[" + curProject.getProjectName() + "]");
        updatedProject.setProjectName(Objects.nonNull(projectName) ? projectName : curProject.getProjectName());

        BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours[" + curProject.getEstimatedHours() + "]");
        updatedProject.setEstimatedHours(Objects.nonNull(estimatedHours) ? estimatedHours : curProject.getEstimatedHours());

        BigDecimal actualHours = getDecimalInput("Enter the actual hours[" + curProject.getActualHours() + "]");
        updatedProject.setActualHours(Objects.nonNull(actualHours) ? actualHours : curProject.getActualHours());

        Integer difficulty = getIntInput("Enter the project difficulty[" + curProject.getDifficulty() + "]");
        updatedProject.setDifficulty(Objects.nonNull(difficulty) ? difficulty : curProject.getDifficulty());

        String notes = getStringInput("Enter the project notes[" + curProject.getNotes() + "]");
        updatedProject.setNotes(Objects.nonNull(notes) ? notes : curProject.getNotes());

        // Set the project ID field
        updatedProject.setProjectId(curProject.getProjectId());

        // Call projectService to modify project details
        projectService.modifyProjectDetails(updatedProject);

        // Fetch the updated project details
        curProject = projectService.fetchProjectById(curProject.getProjectId());
    }
    
    private void selectProject() {
        listProjects();
        Integer projectId = getIntInput("Enter a project ID to select a project");

        // Unselect the current project
        curProject = null;

       // try {
            // Fetch the project by ID
            curProject = projectService.fetchProjectById(projectId);
       // } catch (NoSuchElementException e) {
        // Check if curProject is null and print a message if needed
        if (curProject == null) {
            System.out.println("Invalid project ID selected.");
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

    private void listProjects() {
        List<Project> projects = projectService.fetchAllProjects();

        System.out.println("\nProjects:");

        projects.forEach(project -> System.out
                .println("     " + project.getProjectId()
                        + ": " + project.getProjectName()));
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
       
        if (Objects.isNull(curProject)) {
			System.out.println("\nYou do not have an active project.");
		} else {
			System.out.println("\n You are viewing: " + curProject);
		}
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
