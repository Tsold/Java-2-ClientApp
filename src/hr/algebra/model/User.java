package hr.algebra.model;


public class User {
    private int idUser;
    private String name;
    private String surName;
    private int role;

    public User(){};
    public User(int idUser, String name, String surName, int role) {
        this.idUser = idUser;
        this.name = name;
        this.surName = surName;
        this.role = role;
    }

    public User(String name, String surName, int role) {
        this.name = name;
        this.surName = surName;
        this.role = role;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }


    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", name='" + name + '\'' +
                ", surName='" + surName + '\'' +
                ", role=" + role +
                '}';
    }


    public String toFullName(){return name +" "+ surName;}
}
