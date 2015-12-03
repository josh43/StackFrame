package com.rhcloud.stackframe;

/**
 * Created by Desone on 11/12/2015.
 */
public class Message {
    String username;
    String token;
    Type type;
    String date;
    String text;
    String serverid;
    String avatar;

    public Message(String username, String token, String type, String date, String text, String serverid, String avatar)
    {
        this.username = username;
        this.token = token;
        this.date = date;
        this.text = text;
        this.serverid = serverid;
        this.avatar = avatar;

        if(type.equals("text"))
            this.type = Type.TEXT;
        else if(type.equals("image"))
            this.type = Type.IMAGE;
        else if(type.equals("video"))
            this.type = Type.VIDEO;
    }

    public String getUsername()
    {
        return username;
    }

    public String getToken()
    {
        return token;
    }

    public Type getType()
    {
        return type;
    }

    public String getDate()
    {
        return date;
    }

    public String getText()
    {
        return text;
    }

    public String getServerid()
    {
        return serverid;
    }

    public String getAvatar() {return avatar; }
}

enum Type
{
    TEXT, IMAGE, VIDEO
}
