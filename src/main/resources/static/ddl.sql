

CREATE TABLE Semesters(
	id SERIAL PRIMARY KEY,
	name VARCHAR(255)	
);
CREATE TABLE Teachers(
	id SERIAL PRIMARY KEY,
	unavailable_time VARCHAR(255)
);


CREATE TABLE Rooms(
	id INT NOT NULL PRIMARY KEY,
	isLecture BOOLEAN DEFAULT TRUE,
	unavailable_time VARCHAR(255)

);

CREATE TABLE Subjects(
	id SERIAL PRIMARY KEY,
	sem_id INT REFERENCES Semesters(id),
	name VARCHAR(255) NOT NULL,
	lectures_num INT NOT NULL,
	exercises_num INT NOT NULL
);

CREATE TABLE Flow(
	id SERIAL PRIMARY KEY,
	num_groups INT NOT NULL
);
