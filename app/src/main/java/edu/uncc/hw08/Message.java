package edu.uncc.hw08;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Message implements Serializable {
    public String last_msg,user_one,user_one_name,user_two,user_two_name;
    public com.google.firebase.Timestamp last_msg_timestamp;

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String message_id;

    public Message() {

    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public String getUser_one() {
        return user_one;
    }

    public void setUser_one(String user_one) {
        this.user_one = user_one;
    }

    public String getUser_one_name() {
        return user_one_name;
    }

    public void setUser_one_name(String user_one_name) {
        this.user_one_name = user_one_name;
    }

    public String getUser_two() {
        return user_two;
    }

    public void setUser_two(String user_two) {
        this.user_two = user_two;
    }

    public String getUser_two_name() {
        return user_two_name;
    }

    public void setUser_two_name(String user_two_name) {
        this.user_two_name = user_two_name;
    }

    public Timestamp getLast_msg_timestamp_real(){
        return this.last_msg_timestamp;
    }

    public String getLast_msg_timestamp() {
        final String preConverted = this.last_msg_timestamp.toString();
        final int _seconds = Integer.parseInt(preConverted.substring(18, 28)); // 1621176915
        final int _nanoseconds = Integer.parseInt(preConverted.substring(42, preConverted.lastIndexOf(')'))); // 276147000
        final com.google.firebase.Timestamp postConverted = new com.google.firebase.Timestamp(_seconds, _nanoseconds);

        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (postConverted.getSeconds()*1000));

        return date;
    }

    public void setLast_msg_timestamp(Timestamp last_msg_timestamp) {
        this.last_msg_timestamp = last_msg_timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "last_msg='" + last_msg + '\'' +
                ", user_one='" + user_one + '\'' +
                ", user_one_name='" + user_one_name + '\'' +
                ", user_two='" + user_two + '\'' +
                ", user_two_name='" + user_two_name + '\'' +
                ", last_msg_timestamp=" + last_msg_timestamp +
                '}';
    }
}
