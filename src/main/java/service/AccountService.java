package service;

import java.util.List;
import data.Account;
import data.Currency;

public interface AccountService {
	/**
	 * this method deposits money on user account in a corresponding currency.
	 * If there is no account with specified currency a new one will be created.
	 */
	public void insertCash(long userId, int amount, Currency currency);
	/**
	 * This method withdraws money from the specified user account.
	 * If there is no account with a specified currency of there is insufficient amount of it,
	 * the method returns false.
	 * Otherwise it returns true.  
	 */
	public boolean withdrawCash(long userId, int amount, Currency currency);

	public List<Account> getBalance(long userId);
	
	public void saveNewAccounts(long userId, Currency currency, int amount);
}
