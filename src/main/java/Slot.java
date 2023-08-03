import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"day"})
public class Slot implements Cloneable {
    private SlotTime slotTime;
    private List<Section> sections;
    private Day day;



    @Override
    protected Object clone() throws CloneNotSupportedException {
        Slot clone = (Slot) super.clone();
        List<Section> clonedSections =new ArrayList<>();
       for(int i=0; i< clone.sections.size();i++){
           clonedSections.add((Section) clone.sections.get(i).clone());
       }
       clone.setSections(clonedSections);
       //clone.setDay((Day) day.clone());

      return clone;

    }

    public Slot(SlotTime slotTime, Day day) {
        this.slotTime = slotTime;
        this.day = day;
        this.sections = new ArrayList<>();
    }

//    public SlotTime getSlotTime() {
//        return slotTime;
//    }
//
//    public void setSlotTime(SlotTime slotTime) {
//        this.slotTime = slotTime;
//    }
//
//    public List<Section> getSections() {
//        return sections;
//    }
//
//    public void setSections(List<Section> sections) {
//        this.sections = sections;
//    }
//
//    public Day getDay() {
//        return day;
//    }
//
//    public void setDay(Day day) {
//        this.day = day;
//    }
}
