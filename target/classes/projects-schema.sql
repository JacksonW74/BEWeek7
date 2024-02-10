USE projects;

-- Drop tables if they exist
DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS project;

-- Create project table
CREATE TABLE project (
    project_id INT AUTO_INCREMENT NOT NULL,
    project_name VARCHAR(128) NOT NULL,
    estimated_hours DECIMAL(7,2),
    actual_hours DECIMAL(7,2),
    difficulty INT,
    notes TEXT,
    PRIMARY KEY (project_id)
); 

-- Create material table
CREATE TABLE material (
    material_id INT AUTO_INCREMENT NOT NULL,
    project_id INT NOT NULL,
    material_name VARCHAR(128) NOT NULL,
    num_required INT,
    cost DECIMAL(7,2),
    PRIMARY KEY (material_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);

-- Create step table
CREATE TABLE step (
    step_id INT AUTO_INCREMENT NOT NULL,
    project_id INT NOT NULL,
    step_text TEXT NOT NULL,
    step_order INT NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    PRIMARY KEY (step_id)
);

-- Create category table
CREATE TABLE category (
    category_id INT AUTO_INCREMENT NOT NULL,
    category_name VARCHAR(128) NOT NULL,
    PRIMARY KEY (category_id)
);

-- Create project_category table
CREATE TABLE project_category (
    project_id INT NOT NULL,
    category_id INT NOT NULL,
    UNIQUE KEY (project_id, category_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE
);
