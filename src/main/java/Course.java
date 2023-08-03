import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Course implements Cloneable {
    private String id;
    private Duration duration;
    private Integer lecturesPerWeek;
    private Integer year;

   @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
