package service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import data.Account;
import data.Currency;
import data.repository.AccountRepository;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class AccountSimpleService implements AccountService {
	
	private AccountService instance;
	
	private AccountRepository accountRepository;
	
	public AccountSimpleService(AccountRepository accountRepository) {
		super();
		this.accountRepository = accountRepository;
	}

	@Autowired
	public void setInstance(AccountService instance) {
		this.instance = instance;
	}

	@Override
	public void insertCash(long userId, int amount, Currency currency) {
		Account account = accountRepository.getAccountByUserIdAndCurrency(userId, currency);
		if (account == null) {
			instance.saveNewAccounts(userId, currency, amount);
		} else {
			account.setAmount(account.getAmount() + amount);
			accountRepository.save(account);
		}
	}
	
	/** 
	 * cash.amount is a positive value
	 */
	@Override
	public boolean withdrawCash(long userId, int amount, Currency currency) {
		Account account = accountRepository.getAccountByUserIdAndCurrency(userId, currency);
		if (account == null) {
			return false;
		}
		if (account.getAmount() < amount) {
			return false;
		}
		account.setAmount(account.getAmount() - amount);
		accountRepository.save(account);
		return true;
	}

	@Override
	public List<Account> getBalance(long userId) {		
		return accountRepository.getAccountsByUserId(userId);
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
	public void saveNewAccounts(long userId, Currency currency, int amount) {
		accountRepository.saveAll(createNewAccounts(userId, currency, amount));
	}	
	private List<Account> createNewAccounts(long userId, Currency currency, int amount) {
		return Arrays.stream(Currency.values())
		.map(c -> new Account(userId, c, currency == c ? amount : 0))
		.collect(Collectors.toList());
	}

}
