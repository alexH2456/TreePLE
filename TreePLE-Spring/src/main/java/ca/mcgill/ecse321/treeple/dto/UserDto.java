package ca.mcgill.ecse321.treeple.dto;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse321.treeple.model.User.*;

public class UserDto {

    private String username;
    private String password;
    private UserRole role;
    private List<String> myAddresses;
    private List<Integer> myTrees;

    public UserDto() {
    }

    public UserDto(String username, String password, UserRole role, ArrayList<String> myAddresses) {
        this(username, password, role, myAddresses, new ArrayList<Integer>());
    }

    public UserDto(String username, String password, UserRole role, ArrayList<String> myAddresses, ArrayList<Integer> myTrees) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.myAddresses = myAddresses;
        this.myTrees = myTrees;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public List<String> getMyAddresses() {
        return myAddresses;
    }

    public List<Integer> getMyTrees() {
        return myTrees;
    }

    public void setMyAddresses(List<String> myAddresses) {
        this.myAddresses = myAddresses;
    }

    public void setMyTrees(List<Integer> myTrees) {
        this.myTrees = myTrees;
    }
}
