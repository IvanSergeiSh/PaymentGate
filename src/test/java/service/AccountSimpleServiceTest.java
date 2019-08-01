package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import data.Account;
import data.Currency;
import data.repository.AccountRepository;


@RunWith(MockitoJUnitRunner.class)
public class AccountSimpleServiceTest {
	private static final int AMOUNT = 100;
private static final long ACCOUNT_ID_EMPTY_CASH = 2;
	private static final long ACCOUNT_ID_NOT_EMPTY_CASH = 3;
	private static final long ACCOUNT_ID_NOT_EMPTY_CASH_TO_WITHDRAW = 4;

	@Mock
	private AccountRepository accountRepository;

	private AccountSimpleService service;
	@Before
	public void init() {
		Account accountNotEmptyCash = new Account(ACCOUNT_ID_NOT_EMPTY_CASH, Currency.EUR, AMOUNT);
		Account accountToWithdrawCash = new Account(ACCOUNT_ID_NOT_EMPTY_CASH_TO_WITHDRAW, Currency.EUR, AMOUNT);
		List<Account> accounts = new ArrayList<>();
		accounts.add(accountNotEmptyCash);
		MockitoAnnotations.initMocks(this);
		
		accountRepository = Mockito.mock(AccountRepository.class);
		service = new AccountSimpleService(accountRepository);
		service.setInstance(service);
		
		Mockito.when(accountRepository.getAccountByUserIdAndCurrency(ACCOUNT_ID_EMPTY_CASH, Currency.EUR)).thenReturn(null);
		Mockito.when(accountRepository.getAccountByUserIdAndCurrency(ACCOUNT_ID_NOT_EMPTY_CASH, Currency.EUR)).thenReturn(accountNotEmptyCash);
		Mockito.when(accountRepository.getAccountsByUserId(ACCOUNT_ID_NOT_EMPTY_CASH)).thenReturn(accounts);
	}
	/**
 	 * deposit of 100 EUR to a walet without any should save 100 EUR.
	 */
	@Test
	public void testInsertCashToAnEmptyList() {
		ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
		List<Account> expectedAccounts = Arrays.stream(Currency.values())
				.map(c -> new Account(ACCOUNT_ID_EMPTY_CASH, c, 
						c == Currency.EUR ? AMOUNT : 0)).collect(Collectors.toList());
		service.insertCash(ACCOUNT_ID_EMPTY_CASH, AMOUNT, Currency.EUR);
		Mockito.verify(accountRepository).saveAll(argument.capture());
		Assert.assertTrue(argument.getValue().containsAll(expectedAccounts));
	}
	/**
	 * deposit of 100 EUR to a walet with 100 EUR should save 200 EUR.
	 */
	@Test
	public void testInsertCashWithSummarizing() {
		ArgumentCaptor<Account> argument = ArgumentCaptor.forClass(Account.class);

		service.insertCash(ACCOUNT_ID_NOT_EMPTY_CASH, AMOUNT, Currency.EUR);
		Mockito.verify(accountRepository).save(argument.capture());
		Assert.assertTrue(argument.getValue().getAccountKey().getCurrency() == Currency.EUR &&
				argument.getValue().getAmount() == 2 * AMOUNT);
	}	
	/**
	 * 1) withdraw 100 EUR from wallet without any EUR should return false 
	 */	
	@Test
	public void testWithdrawCashFailAsThereAreNoAccount() {
		
		Assert.assertFalse(service.withdrawCash(ACCOUNT_ID_EMPTY_CASH, AMOUNT, Currency.EUR));
	}
	/**
	 * 2) withdraw 200 EUR from walet with only 100 EUR should return false 
	 */
	@Test
	public void testWithdrawCashFailAsThereAreNotEnoughMoney() {
		
		Assert.assertFalse(service.withdrawCash(ACCOUNT_ID_NOT_EMPTY_CASH, 2 * AMOUNT, Currency.EUR));
	}
	/**
	 * withdrawing 1 EUR from walet eith 100 EUR should return true and save 99 EUR
	 */
	@Test
	public void testWithdrawCashSuccess() {
		ArgumentCaptor<Account> argument = ArgumentCaptor.forClass(Account.class);
		Assert.assertTrue(service.withdrawCash(ACCOUNT_ID_NOT_EMPTY_CASH, 1, Currency.EUR));
		Mockito.verify(accountRepository).save(argument.capture());
		Assert.assertTrue(argument.getValue().getAccountKey().getCurrency() == Currency.EUR &&
				argument.getValue().getAmount() == AMOUNT - 1);		
	}	
	@Test
	public void testGetBalance() {
		service.getBalance(ACCOUNT_ID_NOT_EMPTY_CASH);
		Mockito.verify(accountRepository).getAccountsByUserId(ACCOUNT_ID_NOT_EMPTY_CASH);
	}
	
	@Test
	public void testSaveNewAccounts() {
		ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
		List<Account> expectedAccounts = Arrays.stream(Currency.values())
				.map(c -> new Account(ACCOUNT_ID_EMPTY_CASH, c, 
						c == Currency.EUR ? AMOUNT : 0)).collect(Collectors.toList());
		service.saveNewAccounts(ACCOUNT_ID_EMPTY_CASH, Currency.EUR, AMOUNT);
		Mockito.verify(accountRepository).saveAll(argument.capture());
		Assert.assertTrue(argument.getValue().containsAll(expectedAccounts));		
	}
}
