import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {

        CSVFileReader csvFileReader = CSVFileReader.getInstance();
        csvFileReader.readFile("Book2.csv");
        CourseBrowser courseBrowser = CourseBrowser.getInstance();

        List<Chromosome> population = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            Chromosome chromosome = new Chromosome();
            for (Section section : courseBrowser.getSections()) {
                ImmutablePair<Slot, Slot> slot = Utils.getRandomSlot(chromosome, section);
                if (section.getCourse().getDuration().equals(Duration.LAB)) {
                    slot.getLeft().getSections().add(section);
                } else {
                    slot.getLeft().getSections().add(section);
                    slot.getRight().getSections().add(section);
                }
            }
            population.add(chromosome);
        }
        Collections.sort(population);
        System.out.print("enter the number of iterations : ");
        Scanner scan = new Scanner(System.in);
        int numberOfIterations= scan.nextInt();
        double fitnessBefore = Utils.getFitness(population.get(9));
        for (int i = 0; i < numberOfIterations; i++) {
            if(i%500==0) System.out.println("working in it .... :" + i + " iteration Done!");
            List<Chromosome> chromosomes = null;
            if (i % 10 == 4 || i % 10 == 9) {
                Chromosome oldChromosome = population.get(9);
                Chromosome chromosome = Utils.mutation(oldChromosome);
                if (chromosome != null) {
                    population.remove(oldChromosome);
                    population.add(chromosome);
                    Collections.sort(population);
                }
            }

            chromosomes = Utils.crossOver(population.get(8), population.get(9));

            population.add(chromosomes.get(0));
            population.add(chromosomes.get(1));
            Collections.sort(population);
            population.remove(1);
            population.remove(0);


        }

        System.out.println("number of iterations :" + numberOfIterations);
        System.out.println("before :" + fitnessBefore);
        System.out.println("after :" + Utils.getFitness(population.get(9)));
        Utils.printChromosome(population.get(9));

    }


}

