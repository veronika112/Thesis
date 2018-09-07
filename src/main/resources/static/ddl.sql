
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

CREATE TABLE UniTimeSlots(
	id INT PRIMARY KEY,
	name VARCHAR NOT NULL
);



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

INSERT INTO Semesters(year, name) VALUES (1, 'Първи семестър'), (1, 'Втори семестър'),
					(2, 'Трети семестър'), (2, 'Четвърти семестър'),
					 (3, 'Пети семестър'), (3, 'Шести семестър'),
					  (4, 'Седми семестър'), (4, 'Осми семестър');

					  
INSERT INTO Subjects(sem_id, name, lectures_num, exercises_num) VALUES (1, 'Въведение в специалността', 1, 0),
						(1, 'Висша математика – I', 3, 2), (1, 'Физика – I', 2, 2), (1, 'Химия', 1, 1), 
						(1,  'ПИК – I', 2, 2), (1, 'Основи на инженерното проектиране – I', 1, 2), (1, 'Материалознание', 2, 2), 
						(1, 'Чужд език', 0, 2);
						
INSERT INTO Subjects(sem_id, name, lectures_num, exercises_num) VALUES (2, 'Висша математика – II', 3, 2),
						(2, 'Физика – II', 2, 2), (2, 'Технология на материалите', 3, 2), (2, 'ПИК – II', 2, 2), 
						(2,  'Технологичен практикум', 0, 3), (2, 'Основи на инженерното проектиране II', 1, 2), (2, 'Чужд език', 0, 2);

						  Техническа  механика  3 0 2 5 5 10 1   1 BITI21 5 21. Полупроводникови елементи
						
INSERT INTO Subjects(sem_id, name, lectures_num, exercises_num) VALUES (3, 'Висша математика – III', 3, 2),
						(3, 'Теоретична електротехника и електрически измервания', 2, 2), (3, 'Техническа  механика', 3, 2), (2, 'ПИК – III', 2, 2), 
						(3,  'Полупроводникови елементи', 0, 3), (3, 'Чужд език', 0, 2);
						
INSERT INTO UniTimeSlots(id, name) VALUES (1, '7:30-8:15'), (2, '8:30-9:15'), (3, '9:30-10:15'), (4, '10:30-11:15'),  (5, '11:30-12:15'),
								(6, '12:30-13:15'), (7, '13:45-14:30'), (8, '14:45-15:30'), (9, '15:45-16:30'), (10, '16:45-17:30'),
											(11, '17:45-18:30'),(12, '18:45-19:30'), (13, '19:45-20:30'), (14, '20:45-21:30');

INSERT INTO Teachers(name) VALUES (''),
								('Petkan Petkanov'), ('Todor Todorov');

INSERT INTO Rooms(id, isLecture) VALUES (1151, true), (1152, true), (1153, true), (1154, true), (1211, false), (1216, false);


CREATE TABLE Teachers_Subjects (
	teachers_id INT NOT NULL REFERENCES Teachers(id),
	subjects_id INT NOT NULL REFERENCES Subjects(id),
	isLecture BOOLEAN DEFAULT FALSE,
	UNIQUE (teachers_id, subjects_id, isLecture)
);

INSERT INTO Teachers_Subjects (teachers_id, subjects_id, isLecture) VALUES (1, 3, true), (1, 5, true), (2, 3, true), (2, 3, false);

INSERT INTO teachers (name) VALUES ('Александър Александров'), ('Петър Петров'), ('Стоян Стоянов') , ('Симеон Симеонов'), ('Драган Драганов'), ('Боян Боянов'), ('Цветомир Цвветомиров'), ('Георги Георгиев');


INSERT INTO teachers_subjects VALUES (4,1, true), (4,2, true), (5,5, true), (6,3, true),(6,7, true), (7,5, true), (8,3, true),(8,2, true), (9,2, true),(9,1, true), (10,4, true),(10,8, true),(10,6, true), (11,1, true),(11,2, true), (11,3, true),(11,6, true);
