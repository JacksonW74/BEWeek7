package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";
    
    public List<Project> getAllProjects() {
        //@formatter:off
        String sql = "SELECT * FROM " + PROJECT_TABLE;
        //@formatter:on

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            List<Project> projects = new ArrayList<>();

            while (resultSet.next()) {
                Project project = new Project();
                project.setProjectId(resultSet.getInt("project_id"));
                project.setProjectName(resultSet.getString("project_name"));
                project.setEstimatedHours(resultSet.getBigDecimal("estimated_hours"));
                project.setActualHours(resultSet.getBigDecimal("actual_hours"));
                project.setDifficulty(resultSet.getInt("difficulty"));
                project.setNotes(resultSet.getString("notes"));

                projects.add(project);
            }

            return projects;
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }
    
    public Optional<Project> fetchProjectById(Integer projectId) {
        //@formatter:off
        String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
        //@formatter:on

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try {
            	Project project = null;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, projectId, Integer.class);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        project = extract(rs, Project.class);
                    }
                }
            }

            if (Objects.nonNull(project)) {
            	    project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
                    project.getSteps().addAll(fetchStepsForProject(conn, projectId));
                    project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
            }

            commitTransaction(conn);

            return Optional.ofNullable(project);
        } 
        catch (Exception e) {
            rollbackTransaction(conn);
            throw new DbException("Error fetching additional details for the project", e);
        }
    }
    catch (SQLException e) {
        throw new DbException("Error fetching project from the database", e);
        }
    }

    public List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
    	// @formatter:off
    			String sql = ""
    				+ "Select c.* FROM " + CATEGORY_TABLE + " c "
    				+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
    				+ "WHERE project_id = ?";
    			// @formatter:on

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Category> categories = new ArrayList<>();

                while (rs.next()) {
                    Category category = extract(rs, Category.class);
                    categories.add(category);
                }

                return categories;
            }
        }
    }

	public List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
	    String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        setParameter(stmt, 1, projectId, Integer.class);

	        try (ResultSet rs = stmt.executeQuery()) {
	            List<Step> steps = new ArrayList<>();

	            while (rs.next()) {
	                Step step = extract(rs, Step.class);
	                steps.add(step);
	            }

	            return steps;
	        }
	    }
	}

	public List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
	    String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        setParameter(stmt, 1, projectId, Integer.class);

	        try (ResultSet rs = stmt.executeQuery()) {
	            List<Material> materials = new ArrayList<>();

	            while (rs.next()) {
	                Material material = extract(rs, Material.class);
	                materials.add(material);
	            }

	            return materials;
	        }
	    }
	}

	public List<Project> fetchAllProjects() {
        //@formatter:off
        String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
        //@formatter:on

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                List<Project> projects = new ArrayList<>();

                while (rs.next()) {
                    projects.add(extract(rs, Project.class));
                }

                commitTransaction(conn);
                return projects;
            } catch (SQLException e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }
    
    public Project insertProject(Project project) {
        //@formatter:off
        String sql = ""
                + "INSERT INTO " + PROJECT_TABLE 
                + " (project_name, estimated_hours, actual_hours, difficulty, notes) " 
                + "VALUES " 
                + "(?, ?, ?, ?, ?)";
        //@formatter:on

        try {
            Connection conn = DbConnection.getConnection();
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                // Execute the update
                stmt.executeUpdate();
                
                // Obtain the project ID (primary key)
                Integer projectId = getLastInsertId(conn, PROJECT_TABLE);

                // Commit the transaction
                commitTransaction(conn);
                
                project.setProjectId(projectId);                
                return project;
            } catch (SQLException e) {
                rollbackTransaction(conn);
                throw new DbException("Error inserting project into the database", e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

	public void updateProject(Project project) {
        String sql = "UPDATE project SET project_name = ?, estimated_hours = ?, actual_hours = ?, " +
                "difficulty = ?, notes = ? WHERE project_id = ?";

        try (Connection conn = DbConnection.getConnection();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {

        	// Set parameters for the update statement
        	setParameter(stmt, 1, project.getProjectName(), String.class);
        	setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
        	setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
        	setParameter(stmt, 4, project.getDifficulty(), Integer.class);
        	setParameter(stmt, 5, project.getNotes(), String.class);
        	setParameter(stmt, 6, project.getProjectId(), Integer.class);

        	// Execute the update statement
        	stmt.executeUpdate();

        } catch (SQLException e) {
        	throw new DbException("Error updating project details: " + e.getMessage(), e);
        }
    }
        
    public void deleteProjectById(Integer projectId) {
        // Implement the logic to delete a project by its ID
        String deleteProjectSql = "DELETE FROM project WHERE project_id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteProjectSql)) {

        	setParameter(stmt, 1, projectId, Integer.class);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DbException("Error deleting project: " + e.getMessage(), e);
        }
    }
}

