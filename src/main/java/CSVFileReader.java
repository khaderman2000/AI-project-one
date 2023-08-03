import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.SneakyThrows;

import java.io.FileReader;
import java.util.Collections;
import java.util.List;

public class CSVFileReader {
    private static CSVFileReader CSVFileReader;

    private CSVFileReader(){
    }

    public synchronized static CSVFileReader getInstance(){
        if(CSVFileReader == null){
            CSVFileReader = new CSVFileReader();
        }

        return CSVFileReader;
    }

    @SneakyThrows
    public void readFile(String file){
        // Create an object of file reader
        // class with CSV file as a parameter.
        FileReader filereader = new FileReader(file);

        // create csvReader object and skip first Line
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        List<String[]> allData = csvReader.readAll();

        CourseBrowser courseBrowser = CourseBrowser.getInstance();

        for(String[] line : allData){
            String courseName = line[0];
            Integer numberOfLecturesPerWeek = Integer.valueOf(line[1]);
            Integer year = Integer.valueOf(line[2]);
            List<String> instructors = List.of(line[3].split("/"));

            Integer sectionId = 1;
            for(String instructor : instructors){
                Course course = new Course()
                        .setId(courseName)
                        .setYear(year)
                        .setLecturesPerWeek(numberOfLecturesPerWeek)
                        .setDuration(numberOfLecturesPerWeek == 1 ? Duration.LAB : Duration.LECTURE);
                Section section = new Section(sectionId, instructor, course);
                courseBrowser.getSections().add(section);
                sectionId++;
            }

        }

    }
}
