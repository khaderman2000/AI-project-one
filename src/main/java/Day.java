import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Day implements Cloneable{
    private DayOfWeek dayOfWeek;
    private List<Slot> slots;

    public Day(DayOfWeek dayOfWeek){
        this.dayOfWeek = dayOfWeek;
        this.slots = new ArrayList<>();

        for(SlotTime slotTime : SlotTime.values()){
            Slot slot = new Slot(slotTime, this);
            this.slots.add(slot);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Day clone= (Day) super.clone();
        List<Slot> newSlots= new ArrayList<>();
        for(int i=0; i< clone.slots.size();i++){
            newSlots.add((Slot) clone.slots.get(i).clone());
        }
        clone.setSlots(newSlots);
        return clone;
    }

//    public DayOfWeek getDayOfWeek() {
//        return dayOfWeek;
//    }
//
//    public void setDayOfWeek(DayOfWeek dayOfWeek) {
//        this.dayOfWeek = dayOfWeek;
//    }
//
//    public List<Slot> getSlots() {
//        return slots;
//    }
//
//    public void setSlots(List<Slot> slots) {
//        this.slots = slots;
//    }
}
