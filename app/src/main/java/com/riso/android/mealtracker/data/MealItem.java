package com.riso.android.mealtracker.data;

/**
 * Created by richard.janitor on 23-Sep-18.
 */

public class MealItem {
    public final String id;
    public final String typeItem;
    public final String descItem;
    public final String dateItem;
    public final String timeItem;
    public final String locationItem;
    public final String customItem;
    public String gCalendarItem;
    public  String colorItem;

    public MealItem(String id, String typeItem, String descItem, String dateItem,
                    String timeItem, String locationItem, String customItem,
                    String gCalendarItem, String colorItem) {
        this.typeItem = typeItem;
        this.descItem = descItem;
        this.dateItem = dateItem;
        this.timeItem = timeItem;
        this.locationItem = locationItem;
        this.customItem = customItem;
        this.gCalendarItem = gCalendarItem;
        this.colorItem = colorItem;
        this.id=id;

    }

    public void setGCalendar(String value){
        this.gCalendarItem=value;
    }

}

