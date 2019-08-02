# PaymentGate
This project is intended to estimate grpc performance.

Payment Gate project represents wallet server which consumes three types of requests from client (https://github.com/Aleksey-S-58/GRPCBasedPaymentGateClient.git):
1) insert some summ of one of three available currencies (USD, EUR, GBP)
2) withdraw some amount from users wallet.
3) get balance for current user.
It consumes income requests and inserts data to database if it is necessary.
If any error occures during request processing it returns transaction error.
Current implemention has a bottleneck:
when a new user wants to insert some amount of money to his wallet, server process this request via serializable transaction isolation level to prevent problem of loosing one of two close inserting transactions made by the same user in two close moments of time. This problem occures only when thre is no such user's wallet registered. Because in other cases we could just use Read commited + read-write lock.
To eliminate the problem specified we could either use insertion via entitymanager (instead of via repository) or forbid to registeer new user with inserting money at the same time.

To build the project just download it and run mvn clean install.
