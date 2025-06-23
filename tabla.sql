USE FARMAENLACE;
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medication_name VARCHAR(255) NOT NULL,
    medication_type VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    distributor VARCHAR(100) NOT NULL,
    branch_principal BOOLEAN DEFAULT FALSE,
    branch_secondary BOOLEAN DEFAULT FALSE,
    order_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
SELECT * FROM ORDERS;