package com.secsm.keepongoing.Adapters;

public class FriendScore {
    String score;
    String index;
    String accomplishedTime;
    String date;
    String targetTime;
    String nickname;
    String goalTime;
    @Override
    public String toString() {
//        return super.toString();
        return "score : " + score + "|index : " + index + "|accomplishedTime : " + accomplishedTime +
                "|date : " + date + "|targetTime : " + targetTime + "|nickname : " + nickname;
    }

    public FriendScore(String score, String index, String accomplishedTime, String date, String targetTime, String nickname) {

        this.goalTime = "";
        this.score = score;
        this.index = index;
        if(accomplishedTime != null) {
            if (!accomplishedTime.equals("null")) {
                this.accomplishedTime = accomplishedTime.substring(0, accomplishedTime.length() - 3);
            } else {
                this.accomplishedTime = "00:00";
            }
        }
        this.goalTime += this.accomplishedTime;
        this.goalTime += " / ";
        this.date = date;
        if(targetTime != null) {
            if (!targetTime.equals("null")) {
                this.targetTime = targetTime.substring(0, targetTime.length() - 3);
            } else {
                this.targetTime = "00:00";
            }
        }
        this.goalTime += this.targetTime;
        this.nickname = nickname;
    }

    public String getGoalTime(){

        return goalTime;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getAccomplishedTime() {
        return accomplishedTime;
    }

    public void setAccomplishedTime(String accomplishedTime) {
        this.accomplishedTime = accomplishedTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(String targetTime) {
        this.targetTime = targetTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getScore() {

        return score;
    }

    public int getIntScore(){
        return Integer.parseInt(this.score);
    }

    public int getIntAccomplishedTime(){
        if(accomplishedTime != null)
            return Integer.parseInt(accomplishedTime.substring(0, 2) + accomplishedTime.substring(3,5));

        return 0;
    }

    public int getIntTargetTime(){
        if(targetTime != null)
            return Integer.parseInt(targetTime.substring(0, 2) + targetTime.substring(3,5));
        return 0;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
