package com.tu.thesis.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tu.thesis.business.OtherImpl;
import com.tu.thesis.entity.Rooms;
import com.tu.thesis.entity.Subjects;
import com.tu.thesis.entity.Teachers;
import com.tu.thesis.entity.UniTimeSlots;

public class GeneratorImpl {

	List<UniTimeSlots> timeSlots = OtherImpl.retrieveAllTimeSlots();
	Map<DAYS, BusinessObject[][]> schedule = new HashMap<>();

	// spisak na ws lektori za semestara
	List<FEObjectForLecureGeneration> lecturesConstraints = new ArrayList<>();

	public void printRes() {
		for (DAYS d : DAYS.values()) {
			BusinessObject[][] bo = schedule.get(d);
			for (int i = 0; i < bo.length; i++) {
				for (int j = 0; j < bo[i].length; j++) {

					if (bo[i][j] != null) {
						System.out.println(bo[i][j].toString());
					}
				}
			}
			System.out.println("NEW DAY **************8");
		}
	}

	public void computeSchedule(int groups) {

		lecturesConstraints.addAll(generateConstraints());

		// fill in time table for lectures
		for (DAYS d : DAYS.values()) {
			int numOfLecturesToday = 0;
			BusinessObject[][] obj = new BusinessObject[groups][timeSlots.size()];
			// obhojdame wseki den i wsqka grupa - namirame se na Nti red

			List<FEObjectForLecureGeneration> toBeRemovedWhenSet = new ArrayList<FEObjectForLecureGeneration>();

			// za wseki constraint, koito imame - namirame podhodqsht time slot
	LABEL:		for (FEObjectForLecureGeneration o : lecturesConstraints) {
				int desiredRoom = o.getRoom().getId(); // zalata koqto prepodawatelq iska za lekciqta si
				int numberSlotsOfLecture = o.getSubject().getLecture_num(); // za wsqka lekciq kolko sa chasowite
																			// slotowe

				Iterator<UniTimeSlots> iter = o.getAvailableTime().get(d).iterator(); // obhojdame chasowete, k sa
				boolean isThisLectureSEt = false; // swobodni na prepodawatelq
				while (iter.hasNext() && !isThisLectureSEt) {

					UniTimeSlots tempTimeslot = iter.next();
					int currTimeSlot = tempTimeslot.getId();
					int checkSum = 0;
					if (obj[0][currTimeSlot - 1] == null) { // nqma lekciq po towa wreeme za suotwetnata grupa
						// prowerqwame w programata napred slotowete dali imame neobhodimite broiki
						// swobodni slotowe
						if ((currTimeSlot + numberSlotsOfLecture - 1) <= (timeSlots.size() - 1)) {
							for (int searchAvalTimeSlots = currTimeSlot; searchAvalTimeSlots < (currTimeSlot
									+ numberSlotsOfLecture); searchAvalTimeSlots++) {
								if (obj[0][searchAvalTimeSlots] == null) {
									checkSum += 1;
								}
							}

						}
						// ako sumata suwpada znachi imame wuzmojnost da slojim chasa
						if (checkSum == numberSlotsOfLecture) {
							isThisLectureSEt = true;
							toBeRemovedWhenSet.add(o);
							numOfLecturesToday = numOfLecturesToday +1;
							BusinessObject reserved = new BusinessObject(o.getSubject(), o.getRoom(), o.getTeacher());
							for (int searchAvalTimeSlots = (currTimeSlot - 1); searchAvalTimeSlots < (currTimeSlot - 1
									+ numberSlotsOfLecture); searchAvalTimeSlots++) {
								for (int i = 0; i < groups; i++) {
									obj[i][searchAvalTimeSlots] = reserved;
								}
							}

						}
					}

				}

				if(numOfLecturesToday >1)
					break LABEL;
				
			}

			for (FEObjectForLecureGeneration rem : toBeRemovedWhenSet) {
				lecturesConstraints.remove(rem);
			}

			schedule.put(d, obj);

		}

	}

	private Collection<? extends FEObjectForLecureGeneration> generateConstraints() {
		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room = new Rooms(1154, true);
		Subjects subject = new Subjects(1, 1, "MAtematika", 2, 2);
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

		FEObjectForLecureGeneration obj = new FEObjectForLecureGeneration(room, subject, teacher, availableTime);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room1 = new Rooms(1151, true);
		Subjects subject1 = new Subjects(2, 1, "PIK", 3, 2);
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
		FEObjectForLecureGeneration obj1 = new FEObjectForLecureGeneration(room1, subject1, teacher1, availableTime1);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room2 = new Rooms(2200, true);
		Subjects subject2 = new Subjects(3, 1, "OIP", 4, 1);
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

		FEObjectForLecureGeneration obj2 = new FEObjectForLecureGeneration(room2, subject2, teacher2, availableTime2);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room3 = new Rooms(2205, true);
		Subjects subject3 = new Subjects(7, 1, "Ximiq", 1, 3);
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
		FEObjectForLecureGeneration obj3 = new FEObjectForLecureGeneration(room3, subject3, teacher3, availableTime3);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room4 = new Rooms(1151, true);
		Subjects subject4 = new Subjects(8, 1, "Mashinoznane", 1, 1);
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
		FEObjectForLecureGeneration obj4 = new FEObjectForLecureGeneration(room4, subject4, teacher4, availableTime4);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room5 = new Rooms(3152, true);
		Subjects subject5 = new Subjects(2, 1, "Materialoznanie", 4, 4);
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
		FEObjectForLecureGeneration obj5 = new FEObjectForLecureGeneration(room5, subject5, teacher5, availableTime5);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room6 = new Rooms(4412, true);
		Subjects subject6 = new Subjects(2, 1, "BD", 3, 2);
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
		FEObjectForLecureGeneration obj6 = new FEObjectForLecureGeneration(room6, subject6, teacher6, availableTime6);

		// constraint for 1 teacher for 1 subject and for 1 room
		Rooms room7 = new Rooms(1151, true);
		Subjects subject7 = new Subjects(2, 1, "PTS", 2, 1);
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
		FEObjectForLecureGeneration obj7 = new FEObjectForLecureGeneration(room7, subject7, teacher7, availableTime7);

		List<FEObjectForLecureGeneration> lecturesConstraints = new ArrayList<>();
		lecturesConstraints.add(obj);
		lecturesConstraints.add(obj1);
		lecturesConstraints.add(obj2);
		lecturesConstraints.add(obj3);
		lecturesConstraints.add(obj4);
		lecturesConstraints.add(obj5);
		lecturesConstraints.add(obj6);
		lecturesConstraints.add(obj7);

		return lecturesConstraints;
	}

}
