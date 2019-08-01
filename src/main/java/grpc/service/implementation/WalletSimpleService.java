package grpc.service.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import data.Account;
import data.Currency;
import grpc.BalanceResponse;
import grpc.Response;
import grpc.WalletServiceGrpc;
import grpc.Response.Builder;
import service.AccountService;

/**
 * Service is exposed on port 6565.
 * All necessary classes will be generated during compilation phase.
 * 
 * @author Aleksey-S-58
 *
 */
@GRpcService
public class WalletSimpleService extends WalletServiceGrpc.WalletServiceImplBase {
	
	private static final String UNKNOWN_CURRENCY = "Unknown currency";
	private static final String INSUFFICIENT_FUNDS = "Insufficient funds";
	private static final String TRANSACTION_FAILED = "Transaction failed";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WalletSimpleService.class);
	
	@Autowired
	private AccountService service;
	
	@Override
	public void deposit(grpc.Deposit request,
	        io.grpc.stub.StreamObserver<grpc.Response> responseObserver) {
		Builder builder = Response.newBuilder();
		try {
			service.insertCash(request.getUserId(), request.getAmount(), Currency.valueOf(request.getCurrency()));
			LOGGER.info("deposit request {}", request);
		} catch (IllegalArgumentException e) {
			builder.setError(UNKNOWN_CURRENCY);
			LOGGER.error(UNKNOWN_CURRENCY + " deposit currency {}", request.getCurrency());
		} catch (Exception e) {
			builder.setError(TRANSACTION_FAILED);
			LOGGER.error(TRANSACTION_FAILED + " deposit request {}, ERROR: {}", request, e);
		}
	    responseObserver.onNext(builder.build());
	    responseObserver.onCompleted();
	}
	
	@Override
	public void withdraw(grpc.Withdraw request,
	        io.grpc.stub.StreamObserver<grpc.Response> responseObserver) {
		Builder builder = Response.newBuilder();
		try {
			if (!service.withdrawCash(request.getUserId(), request.getAmount(), Currency.valueOf(request.getCurrency()))) {
				builder.setError(INSUFFICIENT_FUNDS);
				LOGGER.info(INSUFFICIENT_FUNDS + " withdraw request {}", request);
			}
		} catch (IllegalArgumentException e) {
			builder.setError(UNKNOWN_CURRENCY);
			LOGGER.error(UNKNOWN_CURRENCY + " withdraw currency {}", request.getCurrency());
		} catch (Exception e) {
			builder.setError(TRANSACTION_FAILED);
			LOGGER.error(TRANSACTION_FAILED + " withdraw request {}, ERROR: {}", request, e);
		}
	    responseObserver.onNext(builder.build());
	    responseObserver.onCompleted();
	    LOGGER.info("withdraw {}", request);
	}
	
	@Override
	public void balance(grpc.Balance request,
	        io.grpc.stub.StreamObserver<grpc.BalanceResponse> responseObserver) {
		BalanceResponse.Builder builder = BalanceResponse.newBuilder();
		try {
			List<Account> accounts = service.getBalance(request.getUserId());
			builder.putAllWallet(accounts.stream()
					.collect(Collectors
							.toMap(account -> account.getAccountKey().getCurrency().name(), 
									account -> account.getAmount())));
		} catch (Exception e) {
			LOGGER.error(TRANSACTION_FAILED + " balance {}", request );
		}
		LOGGER.debug("balance request {}", request);
	    responseObserver.onNext(builder.build());
	    responseObserver.onCompleted();
	}

}
