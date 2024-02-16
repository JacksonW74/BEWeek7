package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;
import projects.dao.DbConnection;

public class ProjectDao extends DaoBase {

    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";

    public List<Project> getAllProjects() {
        // Implementation for retrieving all projects
        return null;
    }

    public Project insertProject(Project project) {
    	//@formatter:off
        String sql = ""
        	+ "INSERT INTO " + PROJECT_TABLE 
            + " (project_name, estimated_hours, actual_hours, difficulty, notes) " 
            + "VALUES " 
            + "(?, ?, ?, ?, ?)";
        //@formatter:on
        
        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            	setParameter(stmt, 1, project.getProjectName(), String.class);
            	setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
            	setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
            	setParameter(stmt, 4, project.getDifficulty(), Integer.class);
            	setParameter(stmt, 5, project.getNotes(), String.class);


                // Execute the update
                stmt.executeUpdate();
                
                //Obtain the project ID (primary key)
                Integer projectId = getLastInsertId(conn, PROJECT_TABLE);


                // Commit the transaction
                commitTransaction(conn);
                
                project.setProjectId(projectId);                
                return project;
            } 
            catch (Exception e) {
                // Rollback the transaction on exception
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } 
        catch (SQLException e) {
            throw new DbException(e);
        }
    }
}
