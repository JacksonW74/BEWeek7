package projects;

import projects.dao.DbConnection;

import java.sql.Connection;

public class ProjectsApp {

    public static void main(String[] args) {
        // Obtain a database connection
        try (Connection connection = DbConnection.getConnection()) {
            // You can perform further operations here
            System.out.println("Database connection successful!");

            // TODO: Add your application logic here

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
