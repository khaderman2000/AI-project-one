import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Chromosome implements Comparable<Chromosome>, Cloneable {
    private List<Day> days;

    public Chromosome(){
        this.days = new ArrayList<>();
        for(DayOfWeek dayOfWeek : DayOfWeek.values()){
            Day day = new Day(dayOfWeek);
            this.days.add(day);
        }
    }

    @Override
    public int compareTo(Chromosome o) {
        return Utils.getFitness(this).compareTo(Utils.getFitness(o));
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        Chromosome clone=(Chromosome)  super.clone();
        List<Day> newDays= new ArrayList<>();
        for(int i=0; i< clone.days.size();i++){
            newDays.add((Day) clone.days.get(i).clone());
        }
        clone.setDays(newDays);

        return clone;
    }



    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}