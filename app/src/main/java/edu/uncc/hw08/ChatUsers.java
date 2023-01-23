package edu.uncc.hw08;

public class ChatUsers {
    public String user_id,user_name;
    public Boolean is_online;

    public ChatUsers() {
    }

    public String getUser_id() {
        return user_id;
    }

    @Override
    public String toString() {
        return "ChatUsers{" +
                "user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", is_online=" + is_online +
                '}';
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

    public Boolean getIs_online() {
        return is_online;
    }

    public void setIs_online(Boolean is_online) {
        this.is_online = is_online;
    }
}
