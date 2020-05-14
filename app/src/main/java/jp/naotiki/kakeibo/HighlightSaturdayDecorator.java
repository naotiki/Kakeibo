package jp.naotiki.kakeibo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import org.threeten.bp.DayOfWeek;

/**
 * Highlight Saturdays and Sundays with a background
 */
public class HighlightSaturdayDecorator implements DayViewDecorator {

    private final Drawable highlightDrawable;
    private static int color = Color.parseColor("#5f0000ff");

    public HighlightSaturdayDecorator() {
        highlightDrawable = new ColorDrawable(color);
    }

    @Override public boolean shouldDecorate(final CalendarDay day) {
        final DayOfWeek weekDay = day.getDate().getDayOfWeek();
        return weekDay == DayOfWeek.SATURDAY;
    }

    @Override public void decorate(final DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable);
    }
}
