package server.handlers;

import server.exceptions.UnauthorizedException;

public abstract class AuthVerificationHandler {
    protected void verifyAuth(String authToken) throws UnauthorizedException {
        if (authToken == null || authToken.isEmpty()) {
            throw new UnauthorizedException("No authentication provided");
        }
    }
}
