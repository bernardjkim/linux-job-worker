package ljworker.server;

import ljworker.AuthenticateRequest;
import ljworker.AuthenticateResponse;
import ljworker.AuthenticationServiceGrpc.AuthenticationServiceImplBase;
import io.grpc.stub.StreamObserver;

/** AuthenticationService handles authentication RPCs. */
public class AuthenticationServiceImpl extends AuthenticationServiceImplBase {
    // TODO: Use a more secure method of managing authenticated users. Store
    // authorized usernames and encrypted passwords in a DB.
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    @Override
    public void authenticate(AuthenticateRequest req, StreamObserver<AuthenticateResponse> responseObserver) {
        System.out.println();
        // TODO: handle authenticate RPC
    }
}
