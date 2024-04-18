package models;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<User> members;
    private List<Expense> expenses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public Group(String name) {
        this.name = name;
        members= new ArrayList<>();
        expenses = new ArrayList<>();
    }
}
