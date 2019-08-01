package data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @author Aleksey-S-58
 *
 */
@Entity
@Table(name = "account")
public class Account {
	
	/**
	 * UserId
	 */
	@EmbeddedId
	private AccountKey accountKey;
	
	/**
	 * amount should be regarded as amount of a minimal pieces of currency,
	 * as double is not an appropriate type for this case.
	 */
	@Column(name = "amount")
	private int amount;	
	
	/**
	 * amount of 
	 */
	public Account() {}
	
	public Account(AccountKey key, int amount) {
		this.accountKey = key;
		this.amount = amount;
	}
	
	public Account(long userId, Currency currency, int amount) {
		this.accountKey = new AccountKey(userId, currency);
		this.amount = amount;
	}
	
	public int getAmount() {
		return amount;
	}

	public AccountKey getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(AccountKey accountKey) {
		this.accountKey = accountKey;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountKey == null) ? 0 : accountKey.hashCode());
		result = prime * result + amount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (! (obj instanceof Account)) 
			return false;
		Account other = (Account) obj;
		if (accountKey == null) {
			if (other.accountKey != null)
				return false;
		} else if (!accountKey.equals(other.accountKey))
			return false;
		if (amount != other.amount)
			return false;
		return true;
	}
	
}
