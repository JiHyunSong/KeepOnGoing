package com.secsm.keepongoing.Adapters;

public class InviteRoom {
    String roomID;
    String roomName;
    String message;

    public InviteRoom(String _roomID, String _roomName, String _message)
    {
        this.roomID = _roomID;
        this.roomName = _roomName;
        this.message = _message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoomName() {

        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomID() {

        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
