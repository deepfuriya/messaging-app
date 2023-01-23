package edu.uncc.hw08;

import com.google.firebase.Timestamp;

public class MessageList {
    public String msg;
    public String user_id;
    public String user_name;
    public String single_message_id;
    public com.google.firebase.Timestamp timestamp;

    public MessageList() {
    }

    public String getSingle_message_id() {
        return single_message_id;
    }

    public void setSingle_message_id(String single_message_id) {
        this.single_message_id = single_message_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTimestamp() {
        final String preConverted = this.timestamp.toString();
        final int _seconds = Integer.parseInt(preConverted.substring(18, 28)); // 1621176915
        final int _nanoseconds = Integer.parseInt(preConverted.substring(42, preConverted.lastIndexOf(')'))); // 276147000
        final com.google.firebase.Timestamp postConverted = new com.google.firebase.Timestamp(_seconds, _nanoseconds);

        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (postConverted.getSeconds()*1000));

        return date;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MessageList{" +
                "msg='" + msg + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
