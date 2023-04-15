package getterClasses;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by RGupta on 2/8/2018.
 */
public class GetCalenderDate {

    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static Date getPastDate(int numberOfDays) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -numberOfDays);
        return cal.getTime();
    }
}
