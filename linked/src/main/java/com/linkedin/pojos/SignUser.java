package com.linkedin.pojos;

public class SignUser {
    private String name;
    private String surname;
    private String email;
    private String pass;
    private String repass;
    private String birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRepass() {
        return repass;
    }

    public void setRepass(String repass) {
        this.repass = repass;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday){
        this.birthday = birthday;
    }
}
