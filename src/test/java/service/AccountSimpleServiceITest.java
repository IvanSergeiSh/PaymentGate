package service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import configuration.database.DBTestConfiguration;
import data.Account;
import data.Currency;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DBTestConfiguration.class})
public class AccountSimpleServiceITest {
	
	@Autowired
	private AccountService service;
	
	@Test
	public void testDeposit() {
		service.insertCash(1, 300, Currency.EUR);
	}
	@Test
	public void testSeriesOfRequests() {
		Assert.assertFalse(service.withdrawCash(1, 200, Currency.USD));
		service.insertCash(1, 100, Currency.USD);
		List<Account> accounts = service.getBalance(1);
		Assert.assertTrue(accounts.size() == 3);
		Assert.assertTrue(accounts.contains(new Account(1, Currency.USD, 100)));
		Assert.assertFalse(service.withdrawCash(1, 200, Currency.USD));
		service.insertCash(1, 100, Currency.EUR);
		accounts = service.getBalance(1);
		Assert.assertTrue(accounts.contains(new Account(1, Currency.USD, 100)));
		Assert.assertTrue(accounts.contains(new Account(1, Currency.EUR, 100)));
		Assert.assertTrue(accounts.size() == 3);
		Assert.assertFalse(service.withdrawCash(1, 200, Currency.USD));
		service.insertCash(1, 100, Currency.USD);
		accounts = service.getBalance(1);
		Assert.assertTrue(accounts.contains(new Account(1, Currency.USD, 200)));
		Assert.assertTrue(accounts.contains(new Account(1, Currency.EUR, 100)));	
		Assert.assertTrue(accounts.size() == 3);
		Assert.assertTrue(service.withdrawCash(1, 200, Currency.USD));
		accounts = service.getBalance(1);
		Assert.assertTrue(accounts.contains(new Account(1, Currency.USD, 0)));
		Assert.assertTrue(accounts.contains(new Account(1, Currency.EUR, 100)));
		Assert.assertFalse(service.withdrawCash(1, 200, Currency.USD));
	}
	/**
	 * 
	 * 1. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
2. Make a deposit of USD 100 to user with id 1.
3. Check that all balances are correct
4. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
5. Make a deposit of EUR 100 to user with id 1.

6. Check that all balances are correct

7. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".

8. Make a deposit of USD 100 to user with id 1.

9. Check that all balances are correct

10. Make a withdrawal of USD 200 for user with id 1. Must return "ok".

11. Check that all balances are correct

12. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
	 */
}
