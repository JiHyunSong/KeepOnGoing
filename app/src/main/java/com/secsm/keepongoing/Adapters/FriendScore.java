package com.secsm.keepongoing.Adapters;

public class FriendScore {
    String score;
    String index;
    String accomplishedTime;
    String date;
    String targetTime;
    String nickname;

    @Override
    public String toString() {
//        return super.toString();
        return "score : " + score + "|index : " + index + "|accomplishedTime : " + accomplishedTime +
                "|date : " + date + "|targetTime : " + targetTime + "|nickname : " + nickname;
    }

    public FriendScore(String score, String index, String accomplishedTime, String date, String targetTime, String nickname) {

        this.score = score;
        this.index = index;
        this.accomplishedTime = accomplishedTime;
        this.date = date;
        this.targetTime = targetTime;
        this.nickname = nickname;
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

    public void setScore(String score) {
        this.score = score;
    }
}
