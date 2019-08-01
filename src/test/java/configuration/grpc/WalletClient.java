package configuration.grpc;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import grpc.Balance;
import grpc.BalanceResponse;
import grpc.Deposit;
import grpc.Response;
import grpc.WalletServiceGrpc;
import grpc.Withdraw;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Component
public class WalletClient {

	private WalletServiceGrpc.WalletServiceBlockingStub walletServiceBlockingStub;
	  @PostConstruct
	  private void init() {
	    ManagedChannel managedChannel = ManagedChannelBuilder
	        .forAddress("localhost", 6565).usePlaintext().build();

	    walletServiceBlockingStub =
	        WalletServiceGrpc.newBlockingStub(managedChannel);
	  }
	  
	  public Response insert(long userId, String currencyName, int amount) {
		  Deposit deposit = Deposit.newBuilder().
				  setUserId(userId).
				  setCurrency(currencyName).
				  setAmount(amount).
				  build();
		Response response = walletServiceBlockingStub.deposit(deposit);
		return response;  
	  }
	  public Response withdraw(long userId, String currencyName, int amount) {
		  Withdraw withdraw = Withdraw.newBuilder()
				  .setUserId(userId)
				  .setCurrency(currencyName)
				  .setAmount(amount)
				  .build();
		  Response response = walletServiceBlockingStub.withdraw(withdraw);
		  return response;
	  }
	  public BalanceResponse balance(long userId) {
		  Balance balance = Balance.newBuilder().setUserId(userId).build();
		  BalanceResponse response = walletServiceBlockingStub.balance(balance);
		  return response;
	  }
	
}
