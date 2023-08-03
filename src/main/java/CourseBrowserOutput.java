import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class CourseBrowserOutput implements Comparable<CourseBrowserOutput> {
    private String courseId;
    private Integer sectionId;
    private String instructor;
    private String day;
    private Double from;
    private Double to;

    @Override
    public int compareTo(CourseBrowserOutput o) {
        if(courseId.compareTo(o.getCourseId()) == 0){
            return sectionId.compareTo(o.sectionId);
        }else{
            return courseId.compareTo(o.getCourseId());
        }
    }
}
