import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static ImmutablePair<Slot, Slot> getRandomSlot(Chromosome chromosome, Section section) {
        Integer numberOfDays = chromosome.getDays().size();
        Integer numberOfSlots = chromosome.getDays().get(0).getSlots().size();

        while (true) {
            Integer randomDay = ThreadLocalRandom.current().nextInt(0, numberOfDays);
            Integer randomSlot = ThreadLocalRandom.current().nextInt(0, numberOfSlots);

            Slot slot = chromosome.getDays().get(randomDay).getSlots().get(randomSlot);
            Slot correspondingSlot1 = null;
            Slot correspondingSlot2 = null;

            // Check if slot is suitable for Lab
            if (section.getCourse().getDuration().equals(Duration.LAB)) {
                if (isSlotSuitable(chromosome, section, slot)) {
                    return new ImmutablePair<>(slot, null);
                }

                continue;
            }

            // Check if slot is suitable for Lecture
            if (slot.getDay().getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
                correspondingSlot1 = getCorrespondingSlot(chromosome, slot, DayOfWeek.THURSDAY);
            } else if (slot.getDay().getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
                correspondingSlot1 = getCorrespondingSlot(chromosome, slot, DayOfWeek.TUESDAY);
            } else if (slot.getDay().getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                correspondingSlot1 = getCorrespondingSlot(chromosome, slot, DayOfWeek.MONDAY);
                correspondingSlot2 = getCorrespondingSlot(chromosome, slot, DayOfWeek.WEDNESDAY);
            } else if (slot.getDay().getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                correspondingSlot1 = getCorrespondingSlot(chromosome, slot, DayOfWeek.SATURDAY);
                correspondingSlot2 = getCorrespondingSlot(chromosome, slot, DayOfWeek.WEDNESDAY);
            } else if (slot.getDay().getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
                correspondingSlot1 = getCorrespondingSlot(chromosome, slot, DayOfWeek.MONDAY);
                correspondingSlot2 = getCorrespondingSlot(chromosome, slot, DayOfWeek.SATURDAY);
            }

            if (correspondingSlot2 == null) {
                if (isSlotSuitable(chromosome, section, slot) && isSlotSuitable(chromosome, section, correspondingSlot1)) {
                    return new ImmutablePair<>(slot, correspondingSlot1);
                }
                continue;
            } else {
                if (isSlotSuitable(chromosome, section, slot) && isSlotSuitable(chromosome, section, correspondingSlot1)) {
                    return new ImmutablePair<>(slot, correspondingSlot1);
                } else if (isSlotSuitable(chromosome, section, slot) && isSlotSuitable(chromosome, section, correspondingSlot2)) {
                    return new ImmutablePair<>(slot, correspondingSlot2);
                }

                continue;
            }


        }

    }

    /*
    Returns if slot is suitable for instructor and for section duration
     */
    public static Boolean isSlotSuitable(Chromosome chromosome, Section section, Slot slot) {

        // If duration for this slot is suitable for the section
        if (!section.getCourse().getDuration().equals(slot.getSlotTime().getDuration())) {
            return false;
        }

        // If there is conflict in time for instructor
        for (Day day : chromosome.getDays()) {
            for (Slot daySlot : day.getSlots()) {
                if (slot.getDay().getDayOfWeek().equals(daySlot.getDay().getDayOfWeek())) {
                    for (Section slotSection : daySlot.getSections()) {
                        if (slotSection.getInstructor().equals(section.getInstructor())) {

                            Integer daySlotFrom = daySlot.getSlotTime().getFrom();
                            Integer daySlotTo = daySlot.getSlotTime().getTo();
                            Boolean intersection = isThereIntersection(daySlotFrom, daySlotTo,
                                    slot.getSlotTime().getFrom(), slot.getSlotTime().getTo());
                            if (intersection) {
                                return false;
                            }
                        }
                    }
                }

            }
        }

        return true;
    }

    public static Slot getCorrespondingSlot(Chromosome chromosome, Slot slot, DayOfWeek dayOfWeek) {
        for (Day day : chromosome.getDays()) {
            if (day.getDayOfWeek().equals(dayOfWeek)) {
                for (Slot daySlot : day.getSlots()) {
                    if (daySlot.getSlotTime().equals(slot.getSlotTime())) {
                        return daySlot;
                    }
                }
            }
        }
        return null;
    }

    private static Boolean isThereIntersection(Integer a1, Integer a2, Integer b1, Integer b2) {
        return !(a2 <= b1 || b2 <= a1);
    }

    public static Double getFitness(Chromosome chromosome) {
        return getGeneralDiversityScore(chromosome) +
                getDiversityForCourses(chromosome) * 1.5 +
                getDiversityForStudentYears(chromosome) * 2;
    }

    /*
    gets a score of uniformity of the sections in the chromosome
     */
    private static Double getGeneralDiversityScore(Chromosome chromosome) {
        Double uniformityScore = 0D;

        for (Day day : chromosome.getDays()) {
            for (Slot slot : day.getSlots()) {
                if (slot.getSections().size() == 0)
                    continue;

                uniformityScore += (double) (1 / (slot.getSections().size()));
            }
        }

        return uniformityScore;
    }

    private static Double getDiversityForCourses(Chromosome chromosome) {
        Double coursesDiversityScore = 0D;
        for (Day day : chromosome.getDays()) {
            Map<String, Integer> courseOccurrence = new HashMap<>(); // How many occurrence this course in same day
            for (Slot slot : day.getSlots()) {
                for (Section section : slot.getSections()) {
                    String courseId = section.getCourse().getId();
                    courseOccurrence.put(courseId, courseOccurrence.getOrDefault(courseId, 0) + 1);
                }
            }

            // find a score for occurrence
            for (Integer value : courseOccurrence.values()) {
                coursesDiversityScore += (double) (1 / value);
            }
        }

        return coursesDiversityScore;
    }

    private static Double getDiversityForStudentYears(Chromosome chromosome) {
        Double studentsDiversityScore = 0D;
        for (Day day : chromosome.getDays()) {
            Map<Integer, Integer> yearsOccurrence = new HashMap<>(); // How many courses of same year in the same day
            for (Slot slot : day.getSlots()) {
                for (Section section : slot.getSections()) {
                    Integer year = section.getCourse().getYear();
                    yearsOccurrence.put(year, yearsOccurrence.getOrDefault(year, 0) + 1);
                }
            }

            // find a score for occurrence
            for (Integer value : yearsOccurrence.values()) {
                studentsDiversityScore += (double) (1 / value);
            }
        }

        return studentsDiversityScore;
    }

    public static void printChromosome(Chromosome chromosome) {

        List<CourseBrowserOutput> courseBrowserOutputs = new ArrayList<>();

        for (Day day : chromosome.getDays()) {
            for (Slot slot : day.getSlots()) {
                for (Section section : slot.getSections()) {
                    CourseBrowserOutput courseBrowserOutput = new CourseBrowserOutput()
                            .setCourseId(section.getCourse().getId())
                            .setInstructor(section.getInstructor())
                            .setSectionId(section.getId())
                            .setDay(day.getDayOfWeek().toString())
                            .setFrom((double) slot.getSlotTime().getFrom() / 100)
                            .setTo((double) slot.getSlotTime().getTo() / 100);

                    courseBrowserOutputs.add(courseBrowserOutput);
                }
            }
        }

        Collections.sort(courseBrowserOutputs);

        for (int i = 0; i < courseBrowserOutputs.size() - 1; i++) {

            String separator = "";
            if (!courseBrowserOutputs.get(i + 1).getCourseId().equals(courseBrowserOutputs.get(i).getCourseId())) {
                separator = "\n---------------------------------------------------------------------------------\n";
            } else {
                if (courseBrowserOutputs.get(i + 1).getSectionId().equals(courseBrowserOutputs.get(i).getSectionId())) {
                    courseBrowserOutputs.get(i + 1).setDay(courseBrowserOutputs.get(i).getDay() + " - " + courseBrowserOutputs.get(i + 1).getDay());
                    continue;
                }
            }
            System.out.println(courseBrowserOutputs.get(i));
            System.out.print(separator);

        }

    }

    public static Boolean isThereConflict(Chromosome chromosome) {
        for (Day day : chromosome.getDays()) {
            Map<String, List<SlotTime>> instructorSlotsForDay = new HashMap<>();
            for (Slot slot : day.getSlots()) {
                for (Section section : slot.getSections()) {
                    if (instructorSlotsForDay.get(section.getInstructor()) == null) {
                        instructorSlotsForDay.put(section.getInstructor(), new ArrayList<>());
                    }
                    instructorSlotsForDay.get(section.getInstructor()).add(slot.getSlotTime());
                }
            }

            for (String instructor : instructorSlotsForDay.keySet()) {
                List<SlotTime> slotTimes = instructorSlotsForDay.get(instructor);
                for (int i = 0; i < slotTimes.size() - 1; i++) {
                    for (int j = i + 1; j < slotTimes.size(); j++) {
                        SlotTime slot1 = slotTimes.get(i);
                        SlotTime slot2 = slotTimes.get(j);

                        if (isThereIntersection(slot1.getFrom(), slot1.getTo(), slot2.getFrom(), slot2.getTo())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static List<Chromosome> crossOver(Chromosome chromosome1, Chromosome chromosome2) throws CloneNotSupportedException {
        Chromosome chromosomeA = new Chromosome();
        Chromosome chromosomeB = new Chromosome();

        List<Chromosome> childesChromosomes = new ArrayList<>();


        Integer numberOfDays = chromosome1.getDays().size();
        Integer numberOfSlots = chromosome1.getDays().get(0).getSlots().size();
        Integer numberOfDays1 = chromosome2.getDays().size();
        Integer numberOfSlots1 = chromosome2.getDays().get(0).getSlots().size();


        while (true) {
            chromosomeA = (Chromosome) chromosome1.clone();
            chromosomeB = (Chromosome) chromosome2.clone();

            Integer randomDay = ThreadLocalRandom.current().nextInt(0, numberOfDays);
            Integer randomSlot = ThreadLocalRandom.current().nextInt(0, numberOfSlots);
            Integer randomDay1 = ThreadLocalRandom.current().nextInt(0, numberOfDays1);
            Integer randomSlot1 = ThreadLocalRandom.current().nextInt(0, numberOfSlots1);
            int daySection11 = 0;
            int slotSection11 = 0;

            int i = 0;

            if (chromosomeA.getDays().get(randomDay).getSlots().get(randomSlot).getSections().isEmpty()) continue;
            if (chromosomeB.getDays().get(randomDay1).getSlots().get(randomSlot1).getSections().isEmpty()) continue;
            Section section1 = chromosome1.getDays().get(randomDay).getSlots().get(randomSlot).getSections().get(0);
            Section section2 = chromosome2.getDays().get(randomDay1).getSlots().get(randomSlot1).getSections().get(0);
            if (!section2.getCourse().getDuration().equals(section1.getCourse().getDuration())) continue;


            //remove section1 from its slot in chromosomeA
            for (Day day : chromosomeA.getDays()) {
                for (Slot slot : day.getSlots()) {
                    for (Section section : slot.getSections()) {
                        if (section.getId().equals(section1.getId())
                                && section.getCourse().getId().equals(section1.getCourse().getId())
                                && section.getInstructor().equals(section1.getInstructor())) {
                            i = 1;
                            break;
                        }

                    }
                    if (i == 1) break;

                    slotSection11++;
                }
                if (i == 1) break;
                slotSection11 = 0;
                daySection11++;
            }
            chromosomeA.getDays().get(daySection11).getSlots().get(slotSection11).getSections().remove(section1);

            int daySection12 = 0;
            int slotSection12 = 0;
            i=0;
            if (section1.getCourse().getDuration().equals(Duration.LECTURE)) {
                //remove section1 from its slot in chromosomeA
                for (Day day : chromosomeA.getDays()) {
                    for (Slot slot : day.getSlots()) {
                        for (Section section : slot.getSections()) {
                            if (section.getId().equals(section1.getId())
                                    && section.getCourse().getId().equals(section1.getCourse().getId())
                                    && section.getInstructor().equals(section1.getInstructor())) {
                                i = 1;
                                break;
                            }

                        }
                        if (i == 1) break;
                        slotSection12++;
                    }
                    if (i == 1) break;
                    slotSection12 = 0;
                    daySection12++;
                }
                chromosomeA.getDays().get(daySection12).getSlots().get(slotSection12).getSections().remove(section1);

            }

            //remove section2 from its slot in chromosomeB
            int daySection21 = 0;
            int slotSection21 = 0;
            i=0;
            for (Day day : chromosomeB.getDays()) {
                for (Slot slot : day.getSlots()) {
                    for (Section section : slot.getSections()) {
                        if (section.getId().equals(section2.getId())
                                && section.getCourse().getId().equals(section2.getCourse().getId())
                                && section.getInstructor().equals(section2.getInstructor())) {
                            i++;
                            break;
                        }

                    }
                    if (i == 1) break;
                    slotSection21++;
                }
                if (i == 1) break;
                slotSection21 = 0;
                daySection21++;
            }
            chromosomeB.getDays().get(daySection21).getSlots().get(slotSection21).getSections().remove(section2);
            int daySection22 = 0;
            int slotSection22 = 0;
            i=0;
            if (section2.getCourse().getDuration().equals(Duration.LECTURE)) {
                for (Day day : chromosomeB.getDays()) {
                    for (Slot slot : day.getSlots()) {
                        for (Section section : slot.getSections()) {
                            if (section.getId().equals(section2.getId())
                                    && section.getCourse().getId().equals(section2.getCourse().getId())
                                    && section.getInstructor().equals(section2.getInstructor())) {
                                i++;
                                break;
                            }

                        }
                        if (i == 1) break;
                        slotSection22++;
                    }
                    if (i == 1) break;
                    slotSection22 = 0;
                    daySection22++;
                }
                chromosomeB.getDays().get(daySection22).getSlots().get(slotSection22).getSections().remove(section2);

            }

            chromosomeA.getDays().get(daySection21).getSlots().get(slotSection21).getSections().add(section1);
            if (section1.getCourse().getDuration().equals(Duration.LECTURE)) {
                chromosomeA.getDays().get(daySection22).getSlots().get(slotSection22).getSections().add(section1);
            }

            chromosomeB.getDays().get(daySection11).getSlots().get(slotSection11).getSections().add(section2);
            if (section2.getCourse().getDuration().equals(Duration.LECTURE)) {
                chromosomeB.getDays().get(daySection12).getSlots().get(slotSection12).getSections().add(section2);
            }
/*
            if (section1 == null || section2 == null) continue;
            d = 0;
            s = 0;
            // add section1 in chromosomeA in its slot in chromosomeB
            for (Day day : chromosomeB.getDays()) {
                for (Slot daySlot : day.getSlots()) {
                    for (Section section : daySlot.getSections()) {
                        if (section.getId().equals(section1.getId())
                                && section.getCourse().getId().equals(section1.getCourse().getId())
                                && section.getInstructor().equals(section1.getInstructor())) {
                            chromosomeA.getDays().get(d).getSlots().get(s).getSections().add(section1);

                        }

                    }
                    s++;
                }
                s = 0;
                d++;
            }

            d = 0;
            s = 0;
            // add section2 in chromosomeB in its slot in chromosomeA
            for (Day day : chromosomeA.getDays()) {
                for (Slot daySlot : day.getSlots()) {
                    for (Section section : daySlot.getSections()) {
                        if (section.getId().equals(section2.getId())
                                && section.getCourse().getId().equals(section2.getCourse().getId())
                                && section.getInstructor().equals(section2.getInstructor())) {
                            chromosomeA.getDays().get(d).getSlots().get(s).getSections().add(section2);

                        }

                    }
                    s++;
                }
                s = 0;

                d++;
            }
*/
            if (!isThereConflict(chromosomeA) && isThereConflict(chromosomeB)) continue;
            childesChromosomes.add(chromosomeA);
            childesChromosomes.add(chromosomeB);

            break;


        }


        return childesChromosomes;
    }


    public static Chromosome mutation(Chromosome chromosome) throws CloneNotSupportedException {
        Chromosome newChromosome = new Chromosome();
        Section section1=null;
        Section section2=null;
        int k=0;

        while (true) {
            newChromosome = (Chromosome) chromosome.clone();
            Integer numberOfDays = chromosome.getDays().size();
            Integer numberOfSlots = chromosome.getDays().get(0).getSlots().size();

            Integer randomDay = ThreadLocalRandom.current().nextInt(0, numberOfDays);
            Integer randomSlot = ThreadLocalRandom.current().nextInt(0, numberOfSlots);

            if(newChromosome.getDays().get(randomDay).getSlots().isEmpty())continue;
            if(newChromosome.getDays().get(randomDay).getSlots().get(randomSlot).getSections().isEmpty()) continue;
             section1 = newChromosome.getDays().get(randomDay).getSlots().get(randomSlot).getSections().get(0);

            int daySection11 = 0;
            int slotSection11 = 0;
            int i = 0;


            // remove section1 from its slot
            for (Day day : newChromosome.getDays()) {
                for (Slot slot : day.getSlots()) {
                    for (Section section : slot.getSections()) {
                        if (section.getId().equals(section1.getId())
                                && section.getCourse().getId().equals(section1.getCourse().getId())
                                && section.getInstructor().equals(section1.getInstructor())) {
                            i++;
                            break;
                        }

                    }
                    if (i == 1) break;
                    slotSection11++;
                }
                if (i == 1) break;
                slotSection11 = 0;
                daySection11++;
            }
            newChromosome.getDays().get(daySection11).getSlots().get(slotSection11).getSections().remove(section1);

            int daySection12 = 0;
            int slotSection12 = 0;
            i = 0;

            if (section1.getCourse().getDuration().equals(Duration.LECTURE)) {

                for (Day day : newChromosome.getDays()) {
                    for (Slot slot : day.getSlots()) {
                        for (Section section : slot.getSections()) {
                            if (section.getId().equals(section1.getId())
                                    && section.getCourse().getId().equals(section1.getCourse().getId())
                                    && section.getInstructor().equals(section1.getInstructor())) {
                                i++;
                                break;
                            }

                        }
                        if (i == 1) break;
                        slotSection12++;
                    }
                    if (i == 1) break;
                    slotSection12 = 0;
                    daySection12++;
                }
                newChromosome.getDays().get(daySection12).getSlots().get(slotSection12).getSections().remove(section1);
            }

            Integer randomDay1 = ThreadLocalRandom.current().nextInt(0, numberOfDays);
            Integer randomSlot1 = ThreadLocalRandom.current().nextInt(0, numberOfSlots);

            if(newChromosome.getDays().get(randomDay1).getSlots().isEmpty())continue;
            if(newChromosome.getDays().get(randomDay1).getSlots().get(randomSlot1).getSections().isEmpty()) continue;
             section2 = newChromosome.getDays().get(randomDay1).getSlots().get(randomSlot1).getSections().get(0);

            if (!section2.getCourse().getDuration().equals(section1.getCourse().getDuration())) continue;

            int daySection21 = 0;
            int slotSection21 = 0;
            i = 0;

            for (Day day : newChromosome.getDays()) {
                for (Slot slot : day.getSlots()) {
                    for (Section section : slot.getSections()) {
                        if (section.getId().equals(section2.getId())
                                && section.getCourse().getId().equals(section2.getCourse().getId())
                                && section.getInstructor().equals(section2.getInstructor())) {

                            i++;
                            break;
                        }

                    }
                    if (i == 1) break;
                    slotSection21++;
                }
                if (i == 1) break;
                slotSection21=0;
                daySection21++;
            }

            newChromosome.getDays().get(daySection21).getSlots().get(slotSection21).getSections().remove(section2);

            int daySection22 = 0;
            int slotSection22 = 0;
            i=0;
            if (section2.getCourse().getDuration().equals(Duration.LECTURE)) {
                for (Day day : newChromosome.getDays()) {
                    for (Slot slot : day.getSlots()) {
                        for (Section section : slot.getSections()) {
                            if (section.getId().equals(section2.getId())
                                    && section.getCourse().getId().equals(section2.getCourse().getId())
                                    && section.getInstructor().equals(section2.getInstructor())) {

                                i++;
                                break;
                            }

                        }
                        if (i == 1) break;
                        slotSection22++;
                    }
                    if (i == 1) break;
                    slotSection22=0;
                    daySection22++;
                }

                newChromosome.getDays().get(daySection22).getSlots().get(slotSection22).getSections().remove(section2);

            }






            newChromosome.getDays().get(daySection21).getSlots().get(slotSection21).getSections().add(section1);
            newChromosome.getDays().get(daySection11).getSlots().get(slotSection11).getSections().add(section2);

            if(section1.getCourse().getDuration().equals(Duration.LECTURE)){
                newChromosome.getDays().get(daySection22).getSlots().get(slotSection22).getSections().add(section1);
                newChromosome.getDays().get(daySection12).getSlots().get(slotSection12).getSections().add(section2);
            }


            if (!isThereConflict(newChromosome)) break;
            k++;
            if (k==1000){
              //  System.out.println(k);
                return null;
            }

        }




       // System.out.println(k);
        return newChromosome;
    }


}
