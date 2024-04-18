package services;

import dtos.Transaction;
import models.*;
import repositories.GroupRepository;
import repositories.UserExpenseRepository;
import startegies.SettleUpStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private GroupRepository groupRepository;
    private UserExpenseRepository userExpenseRepository;
    private SettleUpStrategy settleUpStrategy;

    public UserService(GroupRepository groupRepository, UserExpenseRepository userExpenseRepository, SettleUpStrategy settleUpStrategy) {
        this.groupRepository = groupRepository;
        this.userExpenseRepository = userExpenseRepository;
        this.settleUpStrategy = settleUpStrategy;
    }

    public List<Transaction> settleUser(String userName, String groupName){
        /*
            1. Get all expenses of a group
            2. Filter for REGULAR expenses
            3. for every expense we need to get UserExpenses(List)
            4. from these, +who paid and - had to pay and update them to the map
                    (calculating extra amount)
            5. from map, add them to 2 heaps, givers(-ve) and receivers(+ve)
                this is passed to different class
            6. max from both heaps and note the transaction and return this

         */

        Map<User, Integer> extraAmountMap = new HashMap<>();
        List<Expense> expenses = groupRepository.findExpensesByGroup(groupName);
        for(Expense expense: expenses){
            if(expense.getExpenseType() == ExpenseType.REGULAR){
                List<UserExpense> userExpenses =
                        userExpenseRepository.findUserExpenseByExpense(expense.getDescription());
                for (UserExpense userExpense: userExpenses){
                    User user = userExpense.getUser();
                    if(!extraAmountMap.containsKey(user)){
                        extraAmountMap.put(user,0);
                    }
                    int amt = extraAmountMap.get(user);
                    if(userExpense.getUserExpenseType()== UserExpenseType.PAID_BY){
                        amt+=userExpense.getAmount();
                    }
                    if(userExpense.getUserExpenseType()== UserExpenseType.HAD_TO_PAY){
                        amt-=userExpense.getAmount();
                    }
                    extraAmountMap.put(user,amt);
                }
            }
        }
        //finding transactions for extra amount for every user.
        List<Transaction> groupTransactions =
                settleUpStrategy.settleUpUsers(extraAmountMap);
        List<Transaction> userTransaction = new ArrayList<>();
        for(Transaction transaction: groupTransactions){
            if(transaction.getFrom().equals(userName) ||
                   transaction.getTo().equals(userName)){
                userTransaction.add(transaction);
            }
        }

        return userTransaction;
    }
}
