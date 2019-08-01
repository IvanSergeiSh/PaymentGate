package data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * 
 * @author Aleksey-S-58
 *
 */
@Embeddable
public class AccountKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4752811881807990929L;

	@Column(name = "user_id")
	private long userId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "currency")
	private Currency currency;
	
	public AccountKey() {}
	
	public AccountKey (long userId, Currency currency) {
		this.userId = userId;
		this.currency = currency;
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + (int) (userId ^ (userId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (! (obj instanceof AccountKey))
			return false;
		AccountKey other = (AccountKey) obj;
		if (currency != other.currency)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}
	
}
