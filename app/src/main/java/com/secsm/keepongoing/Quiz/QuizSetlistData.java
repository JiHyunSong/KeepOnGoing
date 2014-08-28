package com.secsm.keepongoing.Quiz;

/**
 * Created by kim on 2014-08-26.
 */
public class QuizSetlistData {
    public String name;
    public String date;
    public String subject;
    public String solution;
    public String question;
    public QuizSetlistData(String name,String date,String subject,String solution,String question) {
            this.name = name;
            this.date = date;
            this.subject=subject;
             this.solution=solution;
            this.question=question;
        }
}

