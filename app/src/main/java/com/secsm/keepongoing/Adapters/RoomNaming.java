package com.secsm.keepongoing.Adapters;


//    String type, rule, roomname, max_holiday_count, start_time, duration_time, showup_time, meet_days;

public class RoomNaming {
    String type;
    String rid;
    String rule;
    String roomname;
    String max_holiday_count;
    String start_time;
    String duration_time;
    String showup_time;
    String meet_days;
    String quiz_num;

    public RoomNaming(String type, String rid, String rule, String roomname, String max_holiday_count, String start_time, String duration_time, String showup_time, String meet_days, String quiz_num) {
        this.type = type;
        this.rid = rid;
        this.rule = rule;
        this.roomname = roomname;
        this.max_holiday_count = max_holiday_count;
        if(start_time != null) {
            if (start_time.length() > 4) {
                this.start_time = start_time.substring(0, 5);
            }
        }
        this.duration_time = duration_time;
        this.showup_time = showup_time;
        this.meet_days = meet_days;
        this.quiz_num = quiz_num;
    }

    public String getQuiz_num() {return quiz_num;}
    public String getRid() {
        return rid;
    }

    public String getRoomname() {
        return roomname;
    }

    public String getMeet_days() {
        return meet_days;
    }

    public String getShowup_time() {

        return showup_time;
    }

    public String getDuration_time() {

        return duration_time;
    }

    public String getStart_time() {

        return start_time;
    }

    public String getRule() {

        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getMax_holiday_count() {

        return max_holiday_count;
    }

    public void setMax_holiday_count(String max_holiday_count) {
        this.max_holiday_count = max_holiday_count;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
