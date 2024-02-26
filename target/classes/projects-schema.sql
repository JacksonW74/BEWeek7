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

select * from project;

-- Add sample projects

INSERT INTO project (project_id, project_name, estimated_hours, actual_hours, difficulty, notes)
VALUES 
    (1, 'Hang door', 3, 4, 3, 'Get door hangers from Home Depot'),
   	(2, 'Build birdhouse', 3, 4, 3, 'This is for a small bird');

INSERT INTO material (project_id, material_name, num_required, cost) 
VALUES 
    (1, 'Door in frame material', 1, 200),
    (1, 'Package of door hangers from Home Depot', 1, 6.99),
    (1, '2-inch screws', 20, 5.99),
   	(2, '1 x 6 x 8 pine board', 2, 15.99),
  	(2, 'waterproof wood glue', 1, 6.99),
 	(2, 'paint', 1, 25.99);

-- Add sample materials
INSERT INTO category (category_name) 
VALUES 
    ('Doors and Windows'),
    ('Repairs'),
   	('Hobby builds');

-- Add sample steps
INSERT INTO step (project_id, step_text, step_order) 
VALUES 
    (1, 'Screw door hangers on the top and bottom of each side of the door', 1),
    (1, 'Screw hangers into frame', 2),
   	(2, 'Cut boards to length, 4 sides, a bottom and 2 top pieces', 1),
   	(2, 'Glue baords together and hold in place with clamps until dry', 2),
   	(2, 'Apply paint until all parts are adequately coated', 3);
    
-- Add sample categories
INSERT INTO category (category_name) VALUES ('Category 1'), ('Category 2');
