
CREATE TABLE Semesters(
	id SERIAL PRIMARY KEY,
	year INT NOT NULL,
	name VARCHAR(255)	
);


CREATE TABLE Subjects(
	id SERIAL PRIMARY KEY,
	sem_id INT REFERENCES Semesters(id),
	name VARCHAR(255) NOT NULL,
	lectures_num INT NOT NULL,
	exercises_num INT NOT NULL
);

-- still no difference between days?! 
CREATE TABLE UniTimeSlots(
	id INT PRIMARY KEY,
	name VARCHAR NOT NULL
);

DROP TABLE UniTime;

--unavailabe time is varchar but should keep values 1-7 for the time slots in the program; should time slots still be a table?!
--leads lecturs and exercises are not FK due to 1 row containing all of the subjects lead by this person, should stay this way? 
CREATE TABLE Teachers(
	id SERIAL PRIMARY KEY,
	name VARCHAR(255) NOT NULL
);

DROP TABLE Teachers CASCADE;
--unavailabe time is varchar but should keep values 1-7 for the time slots in the program; should time slots still be a table?!
CREATE TABLE Rooms(
	id INT NOT NULL PRIMARY KEY,
	isLecture BOOLEAN DEFAULT TRUE
);

INSERT INTO Semesters(year, name) VALUES (1, 'Year one - semester one'), (1, 'Year one - semester two'),
					(2, 'Year two - semester three'), (2, 'Year two - semester four'),
					 (3, 'Year three - semester five'), (3, 'Year three - semester six'),
					  (4, 'Year four - semester seven'), (4, 'Year four - semester eight');

					  
INSERT INTO Subjects(sem_id, name, lectures_num, exercises_num) VALUES (1, 'Wywedenie w Specialnostta', 1, 0),
						(1, 'Wissha matematika 1', 3, 2), (1, 'Fizika 1', 2, 2), (1, 'Himiq', 1, 1), 
						(1,  'PIK 1', 2, 2), (1, 'OIP', 1, 2), (1, 'Materialoznanie', 2, 2), 
						(1, 'Angliiski ezik', 0, 2);

INSERT INTO Subjects(sem_id, name, lectures_num, exercises_num) VALUES (2, 'Wissha matematika 2', 3, 2),
						(2, 'Fizika 2', 2, 2), (2, 'Tehnologiq na materialite', 3, 2), (2, 'PIK 2', 2, 2), 
						(2,  'Tehnologichen praktikum', 0, 3), (2, 'OIP 2', 1, 2), (2, 'Angliiski ezik', 0, 2);

INSERT INTO UniTimeSlots(id, name) VALUES (1, '7:30-8:15'), (2, '8:30-9:15'), (3, '9:30-10:15'), (4, '10:30-11:15'),  (5, '11:30-12:15'),
								(6, '12:30-13:15'), (7, '13:45-14:30'), (8, '14:45-15:30'), (9, '15:45-16:30'), (10, '16:45-17:30'),
											(11, '17:45-18:30'),(12, '18:45-19:30'), (13, '19:45-20:30'), (14, '20:45-21:30');

INSERT INTO Teachers(name) VALUES ('Dragan Draganov'),
								('Petkan Petkanov'), ('Todor Todorov');

INSERT INTO Rooms(id, isLecture) VALUES (1151, true), (1152, true), (1153, true), (1154, true), (1211, false), (1216, false);


CREATE TABLE Teachers_Subjects (
	teachers_id INT NOT NULL REFERENCES Teachers(id),
	subjects_id INT NOT NULL REFERENCES Subjects(id),
	isLecture BOOLEAN DEFAULT FALSE,
	UNIQUE (teachers_id, subjects_id, isLecture)
);

INSERT INTO Teachers_Subjects (teachers_id, subjects_id, isLecture) VALUES (1, 3, true), (1, 5, true), (2, 3, true), (2, 3, false);

