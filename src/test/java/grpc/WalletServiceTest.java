package grpc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import configuration.database.DBTestConfiguration;
import configuration.grpc.WalletClient;
import configuration.grpc.WalletServiceConfiguration;
import data.Currency;

/**
 * 
 * @author Aleksey-S-58
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DBTestConfiguration.class, WalletServiceConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = {DBTestConfiguration.class, WalletServiceConfiguration.class})
@EnableAutoConfiguration
public class WalletServiceTest {
	private static final long USER_ID = 101;
	private static final long USER_ID_BALANCE = 102;
	private static final long USER_ID_IT = 1;
	@Autowired
	private WalletClient client;

	@Test
	public void depositTest() {
		Response response = client.insert(USER_ID, Currency.EUR.name(), 100);
		Assert.assertTrue(StringUtils.isEmpty(response.toString()));
		response = client.insert(1, "RUB", 100);
		Assert.assertEquals(response.getError(), "Unknown currency");
	}
	
	@Test
	public void withdrawTest() {
		Response response = client.withdraw(USER_ID, Currency.EUR.name(), Integer.MAX_VALUE);
		Assert.assertEquals(response.getError(), "Insufficient funds");
		response = client.withdraw(USER_ID, "RUB", 1);
		Assert.assertEquals(response.getError(), "Unknown currency");
		response = client.withdraw(USER_ID, Currency.EUR.name(), 1);
		Assert.assertTrue(StringUtils.isEmpty(response.toString()));
	}
	
	@Test
	public void balanceTest() {
		BalanceResponse balance = client.balance(USER_ID_BALANCE);
		Assert.assertTrue(balance.containsWallet(Currency.EUR.name()));
		Assert.assertTrue(balance.containsWallet(Currency.USD.name()));
		Assert.assertTrue(balance.containsWallet(Currency.GBP.name()));
		Assert.assertTrue(balance.getWalletMap().get((Currency.EUR.name())) == 100);
		Assert.assertTrue(balance.getWalletMap().get((Currency.USD.name())) == 100);
		Assert.assertTrue(balance.getWalletMap().get((Currency.GBP.name())) == 100);
	}
	
	/**
	* 1. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
	* 2. Make a deposit of USD 100 to user with id 1.
	* 3. Check that all balances are correct
	* 4. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
	* 5. Make a deposit of EUR 100 to user with id 1.
	* 6. Check that all balances are correct
	* 7. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
	* 8. Make a deposit of USD 100 to user with id 1.
	* 9. Check that all balances are correct
	* 10. Make a withdrawal of USD 200 for user with id 1. Must return "ok".
	* 11. Check that all balances are correct
	* 12. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
	 **/	
	@Test
	public void integrationTest() {
		Response response = client.withdraw(USER_ID_IT, Currency.USD.name(), 200);
		Assert.assertEquals(response.getError(), "Insufficient funds");
		response = client.insert(USER_ID_IT, Currency.USD.name(), 100);
		
		BalanceResponse balance = client.balance(USER_ID_IT);
		Assert.assertTrue(balance.getWalletMap().get((Currency.USD.name())) == 100);
		Assert.assertTrue(balance.getWalletMap().get((Currency.EUR.name())) == 0);
		Assert.assertTrue(balance.getWalletMap().get((Currency.GBP.name())) == 0);
		
		response = client.withdraw(USER_ID_IT, Currency.USD.name(), 200);
		Assert.assertEquals(response.getError(), "Insufficient funds");
		response = client.insert(USER_ID_IT, Currency.EUR.name(), 100);
		Assert.assertTrue(StringUtils.isEmpty(response.toString()));		

		balance = client.balance(USER_ID_IT);
		Assert.assertTrue(balance.getWalletMap().get((Currency.USD.name())) == 100);
		Assert.assertTrue(balance.getWalletMap().get((Currency.EUR.name())) == 100);
		Assert.assertTrue(balance.getWalletMap().get((Currency.GBP.name())) == 0);
		
		response = client.withdraw(USER_ID_IT, Currency.USD.name(), 200);
		Assert.assertEquals(response.getError(), "Insufficient funds");
		
		response = client.insert(USER_ID_IT, Currency.USD.name(), 100);
		Assert.assertTrue(StringUtils.isEmpty(response.toString()));
		balance = client.balance(USER_ID_IT);
		Assert.assertTrue(balance.getWalletMap().get((Currency.USD.name())) == 200);
		Assert.assertTrue(balance.getWalletMap().get((Currency.EUR.name())) == 100);
		Assert.assertTrue(balance.getWalletMap().get((Currency.GBP.name())) == 0);
		
		response = client.withdraw(USER_ID_IT, Currency.USD.name(), 200);
		Assert.assertTrue(StringUtils.isEmpty(response.toString()));

		balance = client.balance(USER_ID_IT);
		Assert.assertTrue(balance.getWalletMap().get((Currency.USD.name())) == 0);
		Assert.assertTrue(balance.getWalletMap().get((Currency.EUR.name())) == 100);
		Assert.assertTrue(balance.getWalletMap().get((Currency.GBP.name())) == 0);

		response = client.withdraw(USER_ID_IT, Currency.USD.name(), 200);
		Assert.assertEquals(response.getError(), "Insufficient funds");
	}
	
}
