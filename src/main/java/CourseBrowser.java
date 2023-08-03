import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CourseBrowser {
    private List<Section> sections;
    private static CourseBrowser courseBrowser;

    private CourseBrowser() {
        this.sections = new ArrayList<>();
    }

    public synchronized static CourseBrowser getInstance() {
        if (courseBrowser == null) {
            courseBrowser = new CourseBrowser();
        }
        return courseBrowser;
    }
}
