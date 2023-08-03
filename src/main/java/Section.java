import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Section implements Cloneable {
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getInstructor() {
//        return instructor;
//    }
//
//    public void setInstructor(String instructor) {
//        this.instructor = instructor;
//    }
//
//    public Course getCourse() {
//        return course;
//    }
//
//    public void setCourse(Course course) {
//        this.course = course;
//    }

    private Integer id;
    private String instructor;
    private Course course;


    @Override
    protected Object clone() throws CloneNotSupportedException {
        Section clone = (Section) super.clone();
        clone.setCourse((Course) course.clone());
        return clone;
    }


}
