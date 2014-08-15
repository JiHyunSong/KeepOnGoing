package com.secsm.keepongoing.Alarm;

/**
 * Created by KMINSU-PC-W1 on 2014-08-11.
 */
public class Contact {
        int id;
        int hour;
        int minute;

        public Contact(){

        }
        public Contact(int id, int name, int phone_number){
            this.id = id;
            this.hour = name;
            this.minute = phone_number;
        }

        public Contact(int name, int phone_number){
            this.hour = name;
            this.minute = phone_number;
        }

        public int getID(){
            return this.id;
        }

        public void setID(int id){
            this.id = id;
        }

        public int gethour(){
            return this.hour;
        }

        public void sethour(int name){
            this.hour = name;
        }

        public int getminute(){
            return this.minute;
        }

        public void setminute(int phone_number){
            this.minute = phone_number;
        }
}
