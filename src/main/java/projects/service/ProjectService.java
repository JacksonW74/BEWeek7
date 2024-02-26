package projects.service;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {

    private ProjectDao projectDao = new ProjectDao();

    public Project addProject(Project project) {
        return projectDao.insertProject(project);
    }

    public List<Project> fetchAllProjects() {
        return projectDao.fetchAllProjects();
    }
    
    public Project fetchProjectById(Integer projectId) {
        return projectDao.fetchProjectById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist."));
    }

    public void modifyProjectDetails(Project project) {
        // Validate that the project with the given ID exists
        if (!projectExists(project.getProjectId())) {
            throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
        }

        // Update the project details
        projectDao.updateProject(project);
    }

    public void deleteProjectById(int projectId) {
        // Validate that the project with the given ID exists
        if (!projectExists(projectId)) {
            throw new DbException("Project with ID=" + projectId + " does not exist.");
        }

        // Delete the project by ID
        projectDao.deleteProjectById(projectId);
    }

    // Add this method to check if a project with a given ID exists
    private boolean projectExists(int projectId) {
        try {
            fetchProjectById(projectId);
            return true; // Project exists
        } catch (NoSuchElementException e) {
            return false; // Project doesn't exist
        }
    }
}

