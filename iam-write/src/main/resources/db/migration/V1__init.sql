CREATE TABLE subjects (
    id BINARY(16) NOT NULL PRIMARY KEY,
    subject_email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);
CREATE INDEX subject_email_idx ON subjects (subject_email);

CREATE TABLE subject_attributes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id BINARY(16) NOT NULL,
    attribute_key VARCHAR(128) NOT NULL,
    attribute_value VARCHAR(128) NOT NULL
);
CREATE INDEX subject_attribute_idx ON subject_attributes (subject_id);

CREATE TABLE resources (
    id BINARY(16) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
CREATE INDEX resource_name_idx ON resources (name);

CREATE TABLE resource_attributes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resource_id BINARY(16) NOT NULL,
    attribute_key VARCHAR(128) NOT NULL,
    attribute_value VARCHAR(128) NOT NULL
);
CREATE INDEX resource_attributes_idx ON resource_attributes (resource_id);

CREATE TABLE policies (
    id BINARY(16) NOT NULL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    policy_rule TEXT NOT NULL
);

CREATE TABLE resource_policy (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resource_id BINARY(16) NOT NULL,
    policy_id BINARY(16) NOT NULL,
    FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE,
    FOREIGN KEY (policy_id) REFERENCES policies(id)  ON DELETE CASCADE
);
CREATE INDEX resource_policy_id ON resource_policy(resource_id);
