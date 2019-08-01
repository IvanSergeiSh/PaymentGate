package data.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import data.Account;
import data.AccountKey;
import data.Currency;

/**
 * 
 * @author Aleksey-S-58
 *
 */
@Transactional
public interface AccountRepository extends CrudRepository<Account, AccountKey> {
	
	/**
	 * this query returns list of accounts for specified userId
	 * @param userId
	 * @return
	 */
	@Query("select a from Account a where a.accountKey.userId =?1")
	public List<Account> getAccountsByUserId(long userId);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Account a where a.accountKey.userId =?1 and a.accountKey.currency =?2")
	public Account getAccountByUserIdAndCurrency(long userId, Currency currency);
	
}
