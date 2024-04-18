package runner;

import controller.UserController;
import dtos.Transaction;
import models.*;
import repositories.ExpenseRepository;
import repositories.GroupRepository;
import repositories.UserExpenseRepository;
import repositories.UserRepository;
import services.UserService;
import startegies.HeapSettleUpStrategy;

import java.util.ArrayList;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class App {
    public static void main(String[] args) {
        //creating users
        User Sanjana = new User("Sanjana", "12334", "Sanju");
        User Vinay = new User("Vinay", "12334", "Vinay");
        User Komala = new User("Komala", "12334", "Komala");
        User Vihanya = new User("Vihanya", "12334", "Vihanya");

        List<User> goaGuys=new ArrayList<>();
        goaGuys.add(Sanjana);
        goaGuys.add(Vinay);
        goaGuys.add(Komala);
        goaGuys.add(Vihanya);

        //create group
        Group goaTrip = new Group("GoaTrip");
        goaTrip.setMembers(goaGuys);

        // add an expense of dinner
        Expense dinner
                = new Expense("Dinner", 4000, ExpenseType.REGULAR);

        //we need to add UserExpense
        UserExpense SanjuShare
                = new UserExpense(Sanjana, dinner, 1000, UserExpenseType.HAD_TO_PAY);
        UserExpense VinayShare
                = new UserExpense(Vinay, dinner, 1000, UserExpenseType.HAD_TO_PAY);
        UserExpense KomiShare
                = new UserExpense(Komala, dinner, 1000, UserExpenseType.HAD_TO_PAY);
        UserExpense VihuShare
                = new UserExpense(Vihanya, dinner, 1000, UserExpenseType.HAD_TO_PAY);
        UserExpense SanjuPaid
                = new UserExpense(Sanjana, dinner, 2000, UserExpenseType.PAID_BY);
        UserExpense VinayPaid
                = new UserExpense(Vinay, dinner, 2000, UserExpenseType.PAID_BY);

        //manually adding to DB, here we add to repo
        UserRepository userRepository = new UserRepository();
        GroupRepository groupRepository = new GroupRepository();
        UserExpenseRepository userExpenseRepository = new UserExpenseRepository();
        ExpenseRepository expenseRepository = new ExpenseRepository();

        userRepository.setUsers(goaGuys);
        goaTrip.getExpenses().add(dinner);
        groupRepository.getGroups().add(goaTrip);
        expenseRepository.getExpenses().add(dinner);

        userExpenseRepository.getUserExpenses().add(SanjuShare);
        userExpenseRepository.getUserExpenses().add(VinayShare);
        userExpenseRepository.getUserExpenses().add(VihuShare);
        userExpenseRepository.getUserExpenses().add(KomiShare);
        userExpenseRepository.getUserExpenses().add(SanjuPaid);
        userExpenseRepository.getUserExpenses().add(VinayPaid);

        UserController userController = new UserController
                (new UserService
                        (groupRepository,userExpenseRepository, new HeapSettleUpStrategy()));
        List<Transaction> userTransaction = userController.settleUser("Sanjana", "GoaTrip");
        for(Transaction transaction: userTransaction){
            System.out.println(transaction.getFrom()+
                    " -> " + transaction.getTo()+" = "+ transaction.getAmount());
        }
        List<Transaction> vinayTransaction = userController.settleUser("Vinay","GoaTrip");
        for(Transaction transaction: vinayTransaction){
            System.out.println(transaction.getFrom()+
                    " -> " + transaction.getTo()+" = "+ transaction.getAmount());
        }

    }
}