package com.roommate.find.models;

public class UserDo extends BaseDo {

    public String userId = "";
    public String name = "";
    public String email = "";
    public String country = "";
    public String state = "";
    public String gender = "";
    public String password = "";

    public UserDo(){}

    public UserDo(String userId, String name, String email, String country, String state, String gender, String password){
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.country = country;
        this.state = state;
        this.gender = gender ;
        this.password = password ;

    }
}
