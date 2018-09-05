package com.tu.thesis.generator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.tu.thesis.business.OtherImpl;
import com.tu.thesis.business.RoomsImpl;
import com.tu.thesis.entity.Rooms;
import com.tu.thesis.entity.Subjects;
import com.tu.thesis.entity.Teachers;
import com.tu.thesis.entity.UniTimeSlots;
import com.tu.thesis.helpers.CSVHelper;
import com.tu.thesis.helpers.ConvertToObjectForLecture;

public class GeneratorImpl {

	public static List<UniTimeSlots> timeSlots = OtherImpl.retrieveAllTimeSlots();


	static Map<DAYS, BusinessObject[][]> schedule = new HashMap<>(); // krainiqt sedmichen razpis

	// s tezi 2 lista durjim lekciite, doakto gi obrabotwame
	static List<FEObjectForLecureGeneration> forFurtherProcessLectures = new ArrayList<>(); // struktura, w koiqto pazim
																						// wremenno
	// izwadenite predmeti pri razmestwane

	static List<FEObjectForLecureGeneration> constraintsLectures = new ArrayList<>(); // polucheniq ot FE spisak na predmeti i
																				// lektori s tehnite chasowi pretencii

	// izpolzwame za uprajneniq - za wsqka grupa powtarqme logikata za lekciite
	Map<Integer, List<FEObjectForLecureGeneration>> forFurtherProcessExercises = new HashMap<>(); // struktura, w koiqto
																									// pazim
	// wremenno izwadenite predmeti pri razmestwane

	static Map<Integer, List<FEObjectForLecureGeneration>> constraintsExercises = new HashMap<>(); // polucheniq ot FE spisak
																							// na predmeti i
	// lektori s tehnite chasowi pretencii

	static Map<Teachers, Map<DAYS, Set<UniTimeSlots>>> allTeachers = new HashMap<>(); // spisak ot wsichki lektori s tehnite
																				// chasowi pretencii

	// zalite za uprajneniq
	static Map<DAYS, Map<UniTimeSlots, List<Rooms>>> roomsForEx = new HashMap<>();

	public static void putRooms() throws CloneNotSupportedException, ClassNotFoundException, SQLException {

		Map<UniTimeSlots, List<Rooms>> slots = new HashMap<>();

		List<Rooms> allExercisesRooms = RoomsImpl.retrieveAllRooms();

		for (UniTimeSlots uts : timeSlots) {

			List<Rooms> rooms = new ArrayList<>();
			for (Rooms r : allExercisesRooms) {
				rooms.add((Rooms) r.clone());
			}

			slots.put(uts, rooms);
		}

		for (DAYS d : DAYS.values()) {

			HashMap<UniTimeSlots, List<Rooms>> copy = new HashMap<>();

			for (Map.Entry<UniTimeSlots, List<Rooms>> entry : slots.entrySet()) {
				copy.put(entry.getKey(), new ArrayList<Rooms>(entry.getValue()));
			}
			roomsForEx.put(d, copy);
		}
	}

	/**
	 * popalwame spisaka na prepodawatelite
	 */
	public static void evaluateAllTeachersTimesConstraints() {
		for (FEObjectForLecureGeneration l : constraintsLectures) {
			allTeachers.put(l.getTeacher(), l.getAvailableTime());
		}
	}

	public void printRes() {
		for (DAYS d : DAYS.values()) {
			BusinessObject[][] bo = schedule.get(d);
			for (int i = 0; i < bo.length; i++) {
				for (int j = 0; j < bo[i].length; j++) {

					if (bo[i][j] != null) {
						System.out.println("CHAS: " + timeSlots.get(j).getName() + ", LEKCIQ: " + bo[i][j].isLecture()
								+ "   " + bo[i][j].toString());
					}
				}
				System.out.println("--");
			}
			System.out.println("NEW DAY **************8");
		}

		System.out.println("nakraq FURTHER PROCCESS- " + forFurtherProcessLectures.toString());
		System.out.println("nakraq LECTURES- " + constraintsLectures.toString());

		System.out.println("nakraq UPRAJN FURTHER PROCCESS- " + forFurtherProcessExercises.toString());
		System.out.println("nakraq UPRAJN LECTURES- " + constraintsExercises.toString());
	}

	/**
	 * izwurshwa izchislenieto na lekciite za N na broi grupi
	 * 
	 * @param groups
	 * @param jsonStructure 
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void computeSchedule(int groups, String jsonStructure)
			throws IOException, CloneNotSupportedException, ClassNotFoundException, SQLException {

		System.out.println("GROUPS!!!!!!!!!!!!11 " + groups);
		System.out.println("JSON!!!!!!!!!!!!11 " + jsonStructure);
		
		
	//	List<FEObjectForLecureGeneration> takenfromFe = ConvertToObjectForLecture.convertor(jsonStructure);
		List<FEObjectForLecureGeneration> takenfromFe = generateConstraints();
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++" + takenfromFe.toString());
		
		constraintsLectures.addAll(takenfromFe); // dobawqne na danni
		// pulnim uprajneniqta za wsqka grupa
		for (int i = 0; i < groups; i++) {
			constraintsExercises.put(i, takenfromFe);
		}
		evaluateAllTeachersTimesConstraints();
		putRooms();

		// razpredelqne na lekcii
		computeLecturesSchedule(groups);
		if (!constraintsLectures.isEmpty()) {
			tryToReconfigureSchedule(groups);
		}

		// razpredelqne na uprajneniq
		computeExercisesSchedule(groups);

		CSVHelper.exportDataToExcel("C:\\Users\\User\\Desktop\\program.xlsx", schedule);
	}

	/**
	 * purwonachalnoto zapulwane na programata za uprajneniq
	 * 
	 * 
	 * @param groups
	 */
	private static void computeExercisesSchedule(int groups) {

		for (int i = 0; i < groups; i++) {
			System.out.println("GROUPS:" + i);
			// fill in time table for lectures
			for (DAYS d : DAYS.values()) {
				System.out.println("PULNIM 1WI PUT I SI BESHE OK - 5 EXECUTIONS");
				BusinessObject[][] currentProgram = schedule.get(d); // programata do tekushtiq moment

				List<FEObjectForLecureGeneration> toBeRemovedWhenSet = new ArrayList<FEObjectForLecureGeneration>();

				// za wseki constraint, koito imame - namirame podhodqsht time slot
				LABEL: for (FEObjectForLecureGeneration o : constraintsExercises.get(i)) {
					System.out.println("PREDMETI: " + o.getSubject().getName());

					currentProgram[i] = addEachLectureToSchedule(o, d, currentProgram[i], toBeRemovedWhenSet, false,
							currentProgram, i);

					if (getNumberOfExercisesForDay(currentProgram[i]) > 1)
						break LABEL;
				}

				for (FEObjectForLecureGeneration rem : toBeRemovedWhenSet) {
					constraintsExercises.get(i).remove(rem);
				}

				// schedule.put(d, obj);
			}
		}
	}

	/**
	 * zapochwame razmestwaneto na lekciite
	 */
	private static void tryToReconfigureSchedule(int groups) {

		int numOfTriesToPutLectures = 0;
		while (numOfTriesToPutLectures < 10) {
			System.out.println("TUK LI E PROBLEMA?!" + numOfTriesToPutLectures);
			numOfTriesToPutLectures++;
			List<FEObjectForLecureGeneration> toBeRemovedWhenSet = new ArrayList<FEObjectForLecureGeneration>(); // ako
																													// uspeem
			// da
			// resetnem
			// elementa
			// ->
			// slagame
			// go
			// tuk
			// razmestwane ako ostane nqkoq lekciq na kraq na sedmicata

			if (!constraintsLectures.isEmpty()) {
				BusinessObject[][] obj = new BusinessObject[groups][timeSlots.size()];
				FEObjectForLecureGeneration tempLeftOver = constraintsLectures.get(0); // predmeta koito ne e slojen
																						// nikade

				for (DAYS d : DAYS.values()) {

					BusinessObject[][] scheduleToday = schedule.get(d);

					Set<UniTimeSlots> timeSlots = tempLeftOver.getAvailableTime().get(d); // chasowete na
																							// prepodawatelq za edin
																							// den

					Iterator<UniTimeSlots> it = timeSlots.iterator(); // iterirame warhu swobodnite chasowe na
																		// prepodawatelite samo
					boolean isThisLectureSEt = false;
					while (it.hasNext() && !isThisLectureSEt) {
						int tempId = it.next().getId();

						if (scheduleToday[0][tempId - 1] == null) {

							if (getNumberOfLecturesForDay(scheduleToday[0]) < 2) {

								isThisLectureSEt = addLectures(scheduleToday[0], tempId, tempLeftOver,
										toBeRemovedWhenSet, isThisLectureSEt);
							}
						} else { // ako ne e null shte prowerim obekta kolko time slota zaema i ako zaema
									// poweche
									// ot ili rawen broi razmenqme gi, kato nowoizkaraniq obekt otiwa w dr
									// kolekciq

							System.out.println("Swap ot lecturesConstraints");
							isThisLectureSEt = swapLectures(scheduleToday[0], tempId, tempLeftOver, d,
									toBeRemovedWhenSet, isThisLectureSEt);

						}

						for (int p = 1; p < groups; p++) { // towa replikira razpredelenieto za
							// edna grupa na wsichki grupi
							// scheduleToday[p] = scheduleToday[0];

							for (int x = 0; x < scheduleToday[0].length; x++) {
								if (scheduleToday[0][x] != null) {
									BusinessObject temp = new BusinessObject(scheduleToday[0][x].getSub(),
											scheduleToday[0][x].getRoom(), scheduleToday[0][x].getTeacher(),
											scheduleToday[0][x].isLecture());
									scheduleToday[p][x] = temp;
									temp = null;
								} else {
									scheduleToday[p][x] = null;
								}
							}
						}
					}
				}

			} else if (!forFurtherProcessLectures.isEmpty()) {

				BusinessObject[][] obj = new BusinessObject[groups][timeSlots.size()];
				FEObjectForLecureGeneration tempLeftOver = forFurtherProcessLectures.get(0); // predmeta koito ne e
																								// slojen
				// nikade
				for (DAYS d : DAYS.values()) {
					BusinessObject[][] scheduleToday = schedule.get(d);

					Set<UniTimeSlots> timeSlots = tempLeftOver.getAvailableTime().get(d);

					Iterator<UniTimeSlots> it = timeSlots.iterator(); // iterirame warhu swobodnite chasowe na
																		// prepodawatelite samo
					boolean isThisLectureSEt = false;
					while (it.hasNext() && !isThisLectureSEt) {
						int tempId = it.next().getId();
						int checkSum = 0;

						if (scheduleToday[0][tempId - 1] == null) {

							if (getNumberOfLecturesForDay(scheduleToday[0]) < 2) {

								isThisLectureSEt = addLectures(scheduleToday[0], tempId, tempLeftOver,
										toBeRemovedWhenSet, isThisLectureSEt);

							}
						} else { // ako ne e null shte prowerim obekta kolko time slota zaema i ako zaema
									// poweche
									// ot ili rawen broi razmenqme gi, kato nowoizkaraniq obekt otiwa w dr
									// kolekciq

							System.out.println("Swap ot further process");
							isThisLectureSEt = swapLectures(scheduleToday[0], tempId, tempLeftOver, d,
									toBeRemovedWhenSet, isThisLectureSEt);

							for (int p = 1; p < groups; p++) { // towa replikira razpredelenieto za
								// edna grupa na wsichki grupi

								for (int x = 0; x < scheduleToday[0].length; x++) {
									if (scheduleToday[0][x] != null) {
										BusinessObject temp = new BusinessObject(scheduleToday[0][x].getSub(),
												scheduleToday[0][x].getRoom(), scheduleToday[0][x].getTeacher(),
												scheduleToday[0][x].isLecture());
										scheduleToday[p][x] = temp;
										temp = null;
									} else {
										scheduleToday[p][x] = null;
									}
								}
							}
						}
					}
				}
			}

			for (FEObjectForLecureGeneration o : toBeRemovedWhenSet) {
				constraintsLectures.remove(o);
				forFurtherProcessLectures.remove(o);

			}

			toBeRemovedWhenSet.clear();

		}

	}

	private static boolean addLectures(BusinessObject[] scheduleToday, int tempId, FEObjectForLecureGeneration tempLeftOver,
			List<FEObjectForLecureGeneration> toBeRemovedWhenSet, boolean isThisLectureSEt) {

		int sizeOfTempLeftOver = tempLeftOver.getSubject().getLecture_num(); // razmera mu
		int numOfLecturesToday = 0;
		int checkSum = 0;
		// powtarqme logikata ot namestwaneto na
		// chasowete
		if ((tempId - 1 + sizeOfTempLeftOver) < timeSlots.size()) {
			for (int k = 0; k < (tempId - 1 + sizeOfTempLeftOver); k++) {
				if (scheduleToday[k] == null) {
					checkSum += 1;
				}

			}

			if (checkSum == sizeOfTempLeftOver) {
				isThisLectureSEt = true;
				toBeRemovedWhenSet.add(tempLeftOver);
				numOfLecturesToday = numOfLecturesToday + 1;
				BusinessObject reserved = new BusinessObject(tempLeftOver.getSubject(), tempLeftOver.getRoom(),
						tempLeftOver.getTeacher(), true);
				for (int searchAvalTimeSlots = (tempId - 1); searchAvalTimeSlots < (tempId - 1
						+ sizeOfTempLeftOver); searchAvalTimeSlots++) {
					scheduleToday[searchAvalTimeSlots] = reserved;
				}
			}
		}
		return isThisLectureSEt;
	}

	private static boolean swapLectures(BusinessObject[] scheduleToday, int tempId, FEObjectForLecureGeneration tempLeftOver,
			DAYS d, List<FEObjectForLecureGeneration> toBeRemovedWhenSet, boolean isThisLectureSEt) {

		BusinessObject forSwap = null;
		boolean flag = true;
		int sizeOfTempLeftOver = tempLeftOver.getSubject().getLecture_num();

		if (scheduleToday[tempId - 1].getSub().getLecture_num() >= sizeOfTempLeftOver && !tempLeftOver.isSet()) {
			forSwap = scheduleToday[tempId - 1];
			Set<UniTimeSlots> slotsForTeacher = tempLeftOver.getAvailableTime().get(d);// wsichki chasowe na
																						// prepodawatelq

			// prowerka dali nqma da izlezem izwan ramkite na wuzmojnite time slotowe
			if ((tempId - 1 + forSwap.getSub().getLecture_num()) < scheduleToday.length) {
				// dali na prepodawateq chasowete sa swobodni kogato e tozi forSwap predmet
				for (int v = tempId - 1; v < (tempId - 1 + sizeOfTempLeftOver); v++) {

					if (!slotsForTeacher.contains(timeSlots.get(v))) {
						flag = false;
						break; // prepodawatelq ne e swoboden w nqkoi ot slotowete - nqma da
								// prawim razmestwaneto
					}

				}

				if (flag) {

					isThisLectureSEt = true;

					for (int v = tempId - 1; v < (tempId - 1 + forSwap.getSub().getLecture_num()); v++) {
						BusinessObject forProceess = scheduleToday[v];
						if (forProceess != null) {
							FEObjectForLecureGeneration fe = new FEObjectForLecureGeneration(forProceess.getRoom(),
									forProceess.getSub(), forProceess.getTeacher(),
									allTeachers.get(forProceess.getTeacher()), false);
							if (!forFurtherProcessLectures.contains(fe)) {

								forFurtherProcessLectures.add(fe);
							}
						}
						if (v < (tempId - 1 + sizeOfTempLeftOver)) {
							scheduleToday[v] = new BusinessObject(tempLeftOver.getSubject(), tempLeftOver.getRoom(),
									tempLeftOver.getTeacher(), true);

						} else {
							scheduleToday[v] = null;
						}
					}
					tempLeftOver.setSet(true);

					toBeRemovedWhenSet.add(tempLeftOver);
				}
			}
		}
		return isThisLectureSEt;
	}

	/**
	 * purwonachalnoto zapulwane na programata
	 * 
	 * 
	 * @param groups
	 */
	private static void computeLecturesSchedule(int groups) {

		// fill in time table for lectures
		for (DAYS d : DAYS.values()) {
			int numOfLecturesToday = 0;
			BusinessObject[][] obj = new BusinessObject[groups][timeSlots.size()]; // obhojdame wseki den i wsqka grupa
																					// - namirame se na Nti red

			List<FEObjectForLecureGeneration> toBeRemovedWhenSet = new ArrayList<FEObjectForLecureGeneration>();

			// za wseki constraint, koito imame - namirame podhodqsht time slot
			LABEL: for (FEObjectForLecureGeneration o : constraintsLectures) {

				obj[0] = addEachLectureToSchedule(o, d, obj[0], toBeRemovedWhenSet, true, null, 0);

				if (getNumberOfLecturesForDay(obj[0]) > 1)
					break LABEL;
			}

			for (int i = 1; i < groups; i++) { // towa replikira razpredelenieto za edna grupa na wsichki grupi
				// edna grupa na wsichki grupi
				// scheduleToday[p] = scheduleToday[0];

				for (int x = 0; x < obj[0].length; x++) {
					if (obj[0][x] != null) {
						BusinessObject temp = new BusinessObject(obj[0][x].getSub(), obj[0][x].getRoom(),
								obj[0][x].getTeacher(), obj[0][x].isLecture());
						obj[i][x] = temp;
						temp = null;
					} else {
						obj[i][x] = null;
					}
				}
			}

			for (FEObjectForLecureGeneration rem : toBeRemovedWhenSet) {
				constraintsLectures.remove(rem);
			}

			schedule.put(d, obj);

		}
	}

	private static BusinessObject[] addEachLectureToSchedule(FEObjectForLecureGeneration o, DAYS d, BusinessObject[] obj,
			List<FEObjectForLecureGeneration> toBeRemovedWhenSet, boolean isLecture, BusinessObject[][] currentProgram,
			int i) {

		// int desiredRoom = o.getRoom().getId(); // zalata koqto prepodawatelq iska za
		// lekciqta si
		int numberSlots = 0;
		Iterator<UniTimeSlots> iter = null;// obhojdame chasowete, k saswobodni na
		// prepodawatelq
		if (isLecture == true) {
			numberSlots = o.getSubject().getLecture_num(); // za wsqka lekciq kolko sa chasowite slotowe
			iter = o.getAvailableTime().get(d).iterator(); // obhojdame chasowete, k saswobodni na
			// prepodawatelq
		} else {
			numberSlots = o.getSubject().getExercises_num(); // za wsqko uprajnenie kolko sa chasowite slotowe
			iter = timeSlots.iterator(); // obhojdame chasowete, k saswobodni na prepodawatelq
		}

		boolean isThisLectureSEt = false;

		while (iter.hasNext() && !isThisLectureSEt) {

			UniTimeSlots tempTimeslot = iter.next();
			int currTimeSlot = tempTimeslot.getId();
			int checkSum = 0;
			if (obj[currTimeSlot - 1] == null) { // nqma lekciq po towa wreeme za suotwetnata grupa
				// prowerqwame w programata napred slotowete dali imame neobhodimite broiki
				// swobodni slotowe

				if ((currTimeSlot + numberSlots - 1) <= (timeSlots.size() - 1)) {
					for (int searchAvalTimeSlots = currTimeSlot - 1; searchAvalTimeSlots < (currTimeSlot - 1
							+ numberSlots); searchAvalTimeSlots++) {
						if (isLecture) {
							if (obj[searchAvalTimeSlots] == null
									&& o.getAvailableTime().get(d).contains(tempTimeslot)) {
								checkSum += 1;
							}
						} else {
							if (obj[searchAvalTimeSlots] == null) {
								boolean conflict = false;
								for (int temp = 0; temp < i; temp++) {
									if ((currentProgram[temp][searchAvalTimeSlots] != null)
											&& currentProgram[temp][searchAvalTimeSlots].getSub().getName()
													.equals(o.getSubject().getName())) {
										conflict = true;
									}
								}
								if (conflict == false) {
									checkSum += 1;
								}
							}
						}
					}
				}
				// ako sumata suwpada znachi imame wuzmojnost da slojim chasa
				if (checkSum == numberSlots) {
					isThisLectureSEt = true;
					toBeRemovedWhenSet.add(o);

					Rooms toBeSet = new Rooms();

					if (isLecture == false) {

						UniTimeSlots ts = new UniTimeSlots(currTimeSlot);
						Map<UniTimeSlots, List<Rooms>> slots = roomsForEx.get(d);
						List<Rooms> rooms = slots.get(ts);
						toBeSet = rooms.get(0);

						roomsForEx.get(d).get(ts).remove(toBeSet);

					} else {
						toBeSet = o.getRoom();
					}

					BusinessObject reserved = new BusinessObject(o.getSubject(), toBeSet, o.getTeacher(), isLecture);

					for (int searchAvalTimeSlots = (currTimeSlot - 1); searchAvalTimeSlots < (currTimeSlot - 1
							+ numberSlots); searchAvalTimeSlots++) {
						obj[searchAvalTimeSlots] = reserved;
					}

				}
			}

		}
		return obj;

	}

	private static int getNumberOfLecturesForDay(BusinessObject[] scheduleToday) {
		Set<String> lectures = new HashSet<>();

		for (int i = 0; i < scheduleToday.length; i++) {
			if (scheduleToday[i] != null && scheduleToday[i].isLecture()) {
				lectures.add(scheduleToday[i].getSub().getName());
			}
		}

		return lectures.size();
	}

	private static int getNumberOfExercisesForDay(BusinessObject[] scheduleToday) {
		Set<String> lectures = new HashSet<>();

		for (int i = 0; i < scheduleToday.length; i++) {
			if (scheduleToday[i] != null && !scheduleToday[i].isLecture()) {
				lectures.add(scheduleToday[i].getSub().getName());
			}
		}

		return lectures.size();
	}

	private static List<FEObjectForLecureGeneration> generateConstraints() {
		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room = new Rooms(1154, true);
		Subjects subject = new Subjects(1, 1, "Висша математика – I", 2, 2);
		Teachers teacher = new Teachers(5, "Dragan Draganov");

		Map<DAYS, Set<UniTimeSlots>> availableTime = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON = new HashSet<UniTimeSlots>();
		timeSlotsMON.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsMON.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsMON.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsMON.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON.add(new UniTimeSlots(5, "11:30-12:15"));

		availableTime.put(DAYS.MON, timeSlotsMON);

		Set<UniTimeSlots> timeSlotsTUE = new HashSet<UniTimeSlots>();
		timeSlotsTUE.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsTUE.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsTUE.add(new UniTimeSlots(6, "12:30-13:15"));
		timeSlotsTUE.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsTUE.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsTUE.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime.put(DAYS.TUE, timeSlotsTUE);

		Set<UniTimeSlots> timeSlotsWED = new HashSet<UniTimeSlots>();
		timeSlotsWED.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsWED.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsWED.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsWED.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime.put(DAYS.WED, timeSlotsWED);

		Set<UniTimeSlots> timeSlotsTHU = new HashSet<UniTimeSlots>();

		timeSlotsTHU.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsTHU.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsTHU.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsTHU.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime.put(DAYS.THU, timeSlotsTHU);

		Set<UniTimeSlots> timeSlotsFRI = new HashSet<UniTimeSlots>();
		timeSlotsFRI.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsFRI.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsFRI.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime.put(DAYS.FR, timeSlotsFRI);

		FEObjectForLecureGeneration obj = new FEObjectForLecureGeneration(room, subject, teacher, availableTime, false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room1 = new Rooms(1151, true);
		Subjects subject1 = new Subjects(2, 1, "ПИК – II", 3, 2);
		Teachers teacher1 = new Teachers(2, "Petar Petrov");

		Map<DAYS, Set<UniTimeSlots>> availableTime1 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON1 = new HashSet<UniTimeSlots>();
		timeSlotsMON1.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON1.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsMON1.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsMON1.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsMON1.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime1.put(DAYS.MON, timeSlotsMON1);

		Set<UniTimeSlots> timeSlotsTUE1 = new HashSet<UniTimeSlots>();
		timeSlotsTUE1.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE1.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTUE1.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsTUE1.add(new UniTimeSlots(6, "12:30-13:15"));
		timeSlotsTUE1.add(new UniTimeSlots(7, "13:45-14:30"));

		availableTime1.put(DAYS.TUE, timeSlotsTUE1);

		Set<UniTimeSlots> timeSlotsWED1 = new HashSet<UniTimeSlots>();
		timeSlotsWED1.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsWED1.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED1.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED1.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsWED1.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED1.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsWED1.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime1.put(DAYS.WED, timeSlotsWED);

		Set<UniTimeSlots> timeSlotsTHU1 = new HashSet<UniTimeSlots>();

		timeSlotsTHU1.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU1.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU1.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime1.put(DAYS.THU, timeSlotsTHU1);

		Set<UniTimeSlots> timeSlotsFRI1 = new HashSet<UniTimeSlots>();
		timeSlotsFRI1.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI1.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI1.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsFRI1.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI1.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI1.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI1.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI1.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsFRI1.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsFRI1.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI1.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI1.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime1.put(DAYS.FR, timeSlotsFRI1);
		FEObjectForLecureGeneration obj1 = new FEObjectForLecureGeneration(room1, subject1, teacher1, availableTime1,
				false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room2 = new Rooms(2200, true);
		Subjects subject2 = new Subjects(3, 1, "Технологичен практикум", 4, 1);
		Teachers teacher2 = new Teachers(8, "Todor Todorov");

		Map<DAYS, Set<UniTimeSlots>> availableTime2 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON2 = new HashSet<UniTimeSlots>();

		timeSlotsMON2.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsMON2.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsMON2.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsMON2.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsMON2.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsMON2.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime2.put(DAYS.MON, timeSlotsMON2);

		Set<UniTimeSlots> timeSlotsTUE2 = new HashSet<UniTimeSlots>();
		timeSlotsTUE2.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE2.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTUE2.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime2.put(DAYS.TUE, timeSlotsTUE2);

		Set<UniTimeSlots> timeSlotsWED2 = new HashSet<UniTimeSlots>();
		timeSlotsWED2.add(new UniTimeSlots(6, "12:30-13:15"));
		timeSlotsWED2.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsWED2.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED2.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED2.add(new UniTimeSlots(11, "17:45-18:30"));

		availableTime2.put(DAYS.WED, timeSlotsWED);

		Set<UniTimeSlots> timeSlotsTHU2 = new HashSet<UniTimeSlots>();

		timeSlotsTHU2.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU2.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU2.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsTHU2.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsTHU2.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsTHU2.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsTHU2.add(new UniTimeSlots(13, "19:45-20:30"));

		availableTime2.put(DAYS.THU, timeSlotsTHU2);

		Set<UniTimeSlots> timeSlotsFRI2 = new HashSet<UniTimeSlots>();
		timeSlotsFRI2.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI2.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI2.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI2.add(new UniTimeSlots(14, "20:45-21:30"));
		timeSlotsFRI2.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI2.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI2.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime2.put(DAYS.FR, timeSlotsFRI2);

		FEObjectForLecureGeneration obj2 = new FEObjectForLecureGeneration(room2, subject2, teacher2, availableTime2,
				false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room3 = new Rooms(2205, true);
		Subjects subject3 = new Subjects(7, 1, "Полупроводникови елементи", 1, 3);
		Teachers teacher3 = new Teachers(5, "AAAA. AAAA.");

		Map<DAYS, Set<UniTimeSlots>> availableTime3 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON3 = new HashSet<UniTimeSlots>();
		timeSlotsMON3.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON3.add(new UniTimeSlots(5, "11:30-12:15"));

		availableTime3.put(DAYS.MON, timeSlotsMON3);

		Set<UniTimeSlots> timeSlotsTUE3 = new HashSet<UniTimeSlots>();
		timeSlotsTUE3.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE3.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTUE3.add(new UniTimeSlots(7, "13:45-14:30"));

		availableTime3.put(DAYS.TUE, timeSlotsTUE3);

		Set<UniTimeSlots> timeSlotsWED3 = new HashSet<UniTimeSlots>();
		timeSlotsWED3.add(new UniTimeSlots(7, "13:45-14:30"));

		timeSlotsWED3.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED3.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsWED3.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime3.put(DAYS.WED, timeSlotsWED3);

		Set<UniTimeSlots> timeSlotsTHU3 = new HashSet<UniTimeSlots>();

		timeSlotsTHU3.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU3.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime3.put(DAYS.THU, timeSlotsTHU3);

		Set<UniTimeSlots> timeSlotsFRI3 = new HashSet<UniTimeSlots>();
		timeSlotsFRI3.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI3.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI3.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI3.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI3.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI3.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI3.add(new UniTimeSlots(13, "19:45-20:30"));

		availableTime3.put(DAYS.FR, timeSlotsFRI3);
		FEObjectForLecureGeneration obj3 = new FEObjectForLecureGeneration(room3, subject3, teacher3, availableTime3,
				false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room4 = new Rooms(1151, true);
		Subjects subject4 = new Subjects(8, 1, "Техническа  механика", 1, 1);
		Teachers teacher4 = new Teachers(2, "BBBBB. BBBBBBBb.");

		Map<DAYS, Set<UniTimeSlots>> availableTime4 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON4 = new HashSet<UniTimeSlots>();
		timeSlotsMON4.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON4.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsMON4.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsMON4.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime4.put(DAYS.MON, timeSlotsMON4);

		Set<UniTimeSlots> timeSlotsTUE4 = new HashSet<UniTimeSlots>();
		timeSlotsTUE4.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE4.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTUE4.add(new UniTimeSlots(6, "12:30-13:15"));

		availableTime4.put(DAYS.TUE, timeSlotsTUE4);

		Set<UniTimeSlots> timeSlotsWED4 = new HashSet<UniTimeSlots>();
		timeSlotsWED4.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED4.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED4.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED4.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsWED4.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime4.put(DAYS.WED, timeSlotsWED4);

		Set<UniTimeSlots> timeSlotsTHU4 = new HashSet<UniTimeSlots>();

		timeSlotsTHU4.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU4.add(new UniTimeSlots(2, "8:30-9:15"));

		availableTime4.put(DAYS.THU, timeSlotsTHU4);

		Set<UniTimeSlots> timeSlotsFRI4 = new HashSet<UniTimeSlots>();
		timeSlotsFRI4.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI4.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI4.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI4.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI4.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI4.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI4.add(new UniTimeSlots(12, "18:45-19:30"));

		availableTime4.put(DAYS.FR, timeSlotsFRI4);
		FEObjectForLecureGeneration obj4 = new FEObjectForLecureGeneration(room4, subject4, teacher4, availableTime4,
				false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room5 = new Rooms(3152, true);
		Subjects subject5 = new Subjects(2, 1, "Основи на инженерното проектиране II", 4, 4);
		Teachers teacher5 = new Teachers(2, "PPPPPPP PPPPP");

		Map<DAYS, Set<UniTimeSlots>> availableTime5 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON5 = new HashSet<UniTimeSlots>();
		timeSlotsMON5.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON5.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsMON5.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsMON5.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime5.put(DAYS.MON, timeSlotsMON5);

		Set<UniTimeSlots> timeSlotsTUE5 = new HashSet<UniTimeSlots>();
		timeSlotsTUE5.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE5.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTUE5.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime5.put(DAYS.TUE, timeSlotsTUE5);

		Set<UniTimeSlots> timeSlotsWED5 = new HashSet<UniTimeSlots>();
		timeSlotsWED5.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED5.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsWED5.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED5.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsWED5.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime5.put(DAYS.WED, timeSlotsWED5);

		Set<UniTimeSlots> timeSlotsTHU5 = new HashSet<UniTimeSlots>();

		timeSlotsTHU5.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU5.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU5.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime5.put(DAYS.THU, timeSlotsTHU5);

		Set<UniTimeSlots> timeSlotsFRI5 = new HashSet<UniTimeSlots>();
		timeSlotsFRI5.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsFRI5.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI5.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI5.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI5.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI5.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI5.add(new UniTimeSlots(13, "19:45-20:30"));

		availableTime5.put(DAYS.FR, timeSlotsFRI5);
		FEObjectForLecureGeneration obj5 = new FEObjectForLecureGeneration(room5, subject5, teacher5, availableTime5,
				false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room6 = new Rooms(4412, true);
		Subjects subject6 = new Subjects(2, 1, "Физика – II", 3, 2);
		Teachers teacher6 = new Teachers(2, "GGGGG GGGGGG");

		Map<DAYS, Set<UniTimeSlots>> availableTime6 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON6 = new HashSet<UniTimeSlots>();
		timeSlotsMON6.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsMON6.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsMON6.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime6.put(DAYS.MON, timeSlotsMON6);

		Set<UniTimeSlots> timeSlotsTUE6 = new HashSet<UniTimeSlots>();
		timeSlotsTUE6.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE6.add(new UniTimeSlots(6, "12:30-13:15"));
		timeSlotsTUE6.add(new UniTimeSlots(7, "13:45-14:30"));

		availableTime6.put(DAYS.TUE, timeSlotsTUE6);

		Set<UniTimeSlots> timeSlotsWED6 = new HashSet<UniTimeSlots>();
		timeSlotsWED6.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsWED6.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED6.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime6.put(DAYS.WED, timeSlotsWED6);

		Set<UniTimeSlots> timeSlotsTHU6 = new HashSet<UniTimeSlots>();

		timeSlotsTHU6.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU6.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU6.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime6.put(DAYS.THU, timeSlotsTHU6);

		Set<UniTimeSlots> timeSlotsFRI6 = new HashSet<UniTimeSlots>();
		timeSlotsFRI6.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI6.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI6.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsFRI6.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI6.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI6.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI6.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI6.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsFRI6.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsFRI6.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI6.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI6.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime6.put(DAYS.FR, timeSlotsFRI6);
		FEObjectForLecureGeneration obj6 = new FEObjectForLecureGeneration(room6, subject6, teacher6, availableTime6,
				false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room7 = new Rooms(1151, true);
		Subjects subject7 = new Subjects(2, 1, "Теоретична електротехника и електрически измервания", 2, 1);
		Teachers teacher7 = new Teachers(2, "DDDDAAA DDEEERRD");

		Map<DAYS, Set<UniTimeSlots>> availableTime7 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON7 = new HashSet<UniTimeSlots>();
		timeSlotsMON7.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON7.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsMON7.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime7.put(DAYS.MON, timeSlotsMON7);

		Set<UniTimeSlots> timeSlotsTUE7 = new HashSet<UniTimeSlots>();
		timeSlotsTUE7.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE7.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsTUE7.add(new UniTimeSlots(6, "12:30-13:15"));

		availableTime7.put(DAYS.TUE, timeSlotsTUE7);

		Set<UniTimeSlots> timeSlotsWED7 = new HashSet<UniTimeSlots>();
		timeSlotsWED7.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED7.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED7.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED7.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime7.put(DAYS.WED, timeSlotsWED7);

		Set<UniTimeSlots> timeSlotsTHU7 = new HashSet<UniTimeSlots>();

		timeSlotsTHU7.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU7.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU7.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime7.put(DAYS.THU, timeSlotsTHU7);

		Set<UniTimeSlots> timeSlotsFRI7 = new HashSet<UniTimeSlots>();
		timeSlotsFRI7.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI7.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI7.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsFRI7.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI7.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI7.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI7.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI7.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsFRI7.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsFRI7.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI7.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI7.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime7.put(DAYS.FR, timeSlotsFRI7);
		FEObjectForLecureGeneration obj7 = new FEObjectForLecureGeneration(room7, subject7, teacher7, availableTime7,
				false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room8 = new Rooms(3151, true);
		Subjects subject8 = new Subjects(11, 1, "Bulgarski", 1, 2);
		Teachers teacher8 = new Teachers(2, "Georgi Georgiev");

		Map<DAYS, Set<UniTimeSlots>> availableTime8 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON8 = new HashSet<UniTimeSlots>();
		timeSlotsMON8.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON8.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsMON8.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsMON8.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsMON8.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime8.put(DAYS.MON, timeSlotsMON8);

		Set<UniTimeSlots> timeSlotsTUE8 = new HashSet<UniTimeSlots>();
		timeSlotsTUE8.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE8.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTUE8.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsTUE8.add(new UniTimeSlots(6, "12:30-13:15"));
		timeSlotsTUE8.add(new UniTimeSlots(7, "13:45-14:30"));

		availableTime8.put(DAYS.TUE, timeSlotsTUE8);

		Set<UniTimeSlots> timeSlotsWED8 = new HashSet<UniTimeSlots>();
		timeSlotsWED8.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsWED8.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED8.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED8.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsWED8.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED8.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsWED8.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime8.put(DAYS.WED, timeSlotsWED8);

		Set<UniTimeSlots> timeSlotsTHU8 = new HashSet<UniTimeSlots>();

		timeSlotsTHU8.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU8.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU8.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime8.put(DAYS.THU, timeSlotsTHU8);

		Set<UniTimeSlots> timeSlotsFRI8 = new HashSet<UniTimeSlots>();
		timeSlotsFRI8.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI8.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI8.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsFRI8.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI8.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI8.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI8.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI8.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsFRI8.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsFRI8.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI8.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI8.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime8.put(DAYS.FR, timeSlotsFRI8);
		FEObjectForLecureGeneration obj8 = new FEObjectForLecureGeneration(room8, subject8, teacher8, availableTime8,
				false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room9 = new Rooms(4151, true);
		Subjects subject9 = new Subjects(11, 1, "Физика – I", 2, 3);
		Teachers teacher9 = new Teachers(2, "Iwanka Iwanowa");

		Map<DAYS, Set<UniTimeSlots>> availableTime9 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON9 = new HashSet<UniTimeSlots>();
		timeSlotsMON9.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON9.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsMON9.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsMON9.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsMON9.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime9.put(DAYS.MON, timeSlotsMON9);

		Set<UniTimeSlots> timeSlotsTUE9 = new HashSet<UniTimeSlots>();
		timeSlotsTUE9.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE9.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTUE9.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsTUE9.add(new UniTimeSlots(6, "12:30-13:15"));
		timeSlotsTUE9.add(new UniTimeSlots(7, "13:45-14:30"));

		availableTime9.put(DAYS.TUE, timeSlotsTUE9);

		Set<UniTimeSlots> timeSlotsWED9 = new HashSet<UniTimeSlots>();
		timeSlotsWED9.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsWED9.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED9.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED9.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsWED9.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED9.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsWED9.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime9.put(DAYS.WED, timeSlotsWED9);

		Set<UniTimeSlots> timeSlotsTHU9 = new HashSet<UniTimeSlots>();

		timeSlotsTHU9.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU9.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU9.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime9.put(DAYS.THU, timeSlotsTHU9);

		Set<UniTimeSlots> timeSlotsFRI9 = new HashSet<UniTimeSlots>();
		timeSlotsFRI9.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI9.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI9.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsFRI9.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI9.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI9.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI9.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI9.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsFRI9.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsFRI9.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI9.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI9.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime9.put(DAYS.FR, timeSlotsFRI9);
		FEObjectForLecureGeneration obj9 = new FEObjectForLecureGeneration(room9, subject9, teacher9, availableTime9,
				false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room10 = new Rooms(1151, true);
		Subjects subject10 = new Subjects(6, 1, "Въведение в специалността", 3, 2);
		Teachers teacher10 = new Teachers(2, "Tswetelinka Petrowa");

		Map<DAYS, Set<UniTimeSlots>> availableTime10 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON10 = new HashSet<UniTimeSlots>();
		timeSlotsMON10.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON10.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsMON10.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsMON10.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsMON10.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime10.put(DAYS.MON, timeSlotsMON10);

		Set<UniTimeSlots> timeSlotsTUE10 = new HashSet<UniTimeSlots>();
		timeSlotsTUE10.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE10.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTUE10.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsTUE10.add(new UniTimeSlots(6, "12:30-13:15"));
		timeSlotsTUE10.add(new UniTimeSlots(7, "13:45-14:30"));

		availableTime10.put(DAYS.TUE, timeSlotsTUE10);

		Set<UniTimeSlots> timeSlotsWED10 = new HashSet<UniTimeSlots>();
		timeSlotsWED10.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsWED10.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED10.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED10.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsWED10.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED10.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsWED10.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime10.put(DAYS.WED, timeSlotsWED10);

		Set<UniTimeSlots> timeSlotsTHU10 = new HashSet<UniTimeSlots>();

		timeSlotsTHU10.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU10.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU10.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime10.put(DAYS.THU, timeSlotsTHU10);

		Set<UniTimeSlots> timeSlotsFRI10 = new HashSet<UniTimeSlots>();
		timeSlotsFRI10.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI10.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI10.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsFRI10.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI10.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI10.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI10.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI10.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsFRI10.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsFRI10.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI10.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI10.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime10.put(DAYS.FR, timeSlotsFRI10);
		FEObjectForLecureGeneration obj10 = new FEObjectForLecureGeneration(room10, subject10, teacher10,
				availableTime10, false);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room11 = new Rooms(1152, true);
		Subjects subject11 = new Subjects(6, 1, "ПИК – II", 2, 2);
		Teachers teacher11 = new Teachers(11, "Petar Petrow");

		Map<DAYS, Set<UniTimeSlots>> availableTime11 = new HashMap<>();

		Set<UniTimeSlots> timeSlotsMON11 = new HashSet<UniTimeSlots>();
		timeSlotsMON11.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsMON11.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsMON11.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsMON11.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsMON11.add(new UniTimeSlots(10, "16:45-17:30"));

		availableTime11.put(DAYS.MON, timeSlotsMON11);

		Set<UniTimeSlots> timeSlotsTUE11 = new HashSet<UniTimeSlots>();
		timeSlotsTUE11.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTUE11.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTUE11.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsTUE11.add(new UniTimeSlots(6, "12:30-13:15"));
		timeSlotsTUE11.add(new UniTimeSlots(7, "13:45-14:30"));

		availableTime11.put(DAYS.TUE, timeSlotsTUE11);

		Set<UniTimeSlots> timeSlotsWED11 = new HashSet<UniTimeSlots>();
		timeSlotsWED11.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsWED11.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsWED11.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsWED11.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsWED11.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsWED11.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsWED11.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime11.put(DAYS.WED, timeSlotsWED11);

		Set<UniTimeSlots> timeSlotsTHU11 = new HashSet<UniTimeSlots>();

		timeSlotsTHU11.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsTHU11.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsTHU11.add(new UniTimeSlots(3, "9:30-10:15"));

		availableTime11.put(DAYS.THU, timeSlotsTHU11);

		Set<UniTimeSlots> timeSlotsFRI11 = new HashSet<UniTimeSlots>();
		timeSlotsFRI11.add(new UniTimeSlots(1, "7:30-8:15"));
		timeSlotsFRI11.add(new UniTimeSlots(2, "8:30-9:15"));
		timeSlotsFRI11.add(new UniTimeSlots(3, "9:30-10:15"));
		timeSlotsFRI11.add(new UniTimeSlots(4, "10:30-11:15"));
		timeSlotsFRI11.add(new UniTimeSlots(5, "11:30-12:15"));
		timeSlotsFRI11.add(new UniTimeSlots(7, "13:45-14:30"));
		timeSlotsFRI11.add(new UniTimeSlots(9, "15:45-16:30"));
		timeSlotsFRI11.add(new UniTimeSlots(10, "16:45-17:30"));
		timeSlotsFRI11.add(new UniTimeSlots(11, "17:45-18:30"));
		timeSlotsFRI11.add(new UniTimeSlots(12, "18:45-19:30"));
		timeSlotsFRI11.add(new UniTimeSlots(13, "19:45-20:30"));
		timeSlotsFRI11.add(new UniTimeSlots(14, "20:45-21:30"));

		availableTime11.put(DAYS.FR, timeSlotsFRI11);
		FEObjectForLecureGeneration obj11 = new FEObjectForLecureGeneration(room11, subject11, teacher11,
				availableTime11, false);

		List<FEObjectForLecureGeneration> lecturesConstraints = new ArrayList<>();
		lecturesConstraints.add(obj);
		lecturesConstraints.add(obj1);
		lecturesConstraints.add(obj2);
		lecturesConstraints.add(obj3);
		lecturesConstraints.add(obj4);
		lecturesConstraints.add(obj5);
		lecturesConstraints.add(obj6);
		lecturesConstraints.add(obj7);
		lecturesConstraints.add(obj8);

		
		String json = new Gson().toJson(lecturesConstraints);
		System.out.println("GOOGLE? " + json);
		
		return lecturesConstraints;
	}

}
