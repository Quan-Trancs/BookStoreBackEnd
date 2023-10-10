package quantran.api.asyncProcessingWorkAcceptor.impl;

import quantran.api.model.UserModel;

public class AsyncProcessingWorkAcceptorImpl {
    public String[] asyncProcessingWorkAcceptor(UserModel userModel) {
        String[] requestStatus = new String[2];
        String userName = userModel.getUserName();
        if (userName == null) {
            requestStatus[0] = "404";
            requestStatus[1] = "Unauthorized";
            return requestStatus;
        }

        String key = userModel.getKey();
        // Perform authorization checks here with the authorizationHeader.

        if (key == null) {
            requestStatus[0] = "404";
            requestStatus[1] = "Bad Request";
            return requestStatus;
        }

        String requestId = java.util.UUID.randomUUID().toString();
        requestStatus[0] = "400";
        requestStatus[1] = requestId;
        return requestStatus;
    }
}