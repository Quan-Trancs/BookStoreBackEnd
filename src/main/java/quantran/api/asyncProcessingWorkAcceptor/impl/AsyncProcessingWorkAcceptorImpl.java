package quantran.api.asyncProcessingWorkAcceptor.impl;

import org.springframework.stereotype.Component;
import quantran.api.asyncProcessingWorkAcceptor.AsyncProcessingWorkAcceptor;
import quantran.api.model.UserModel;

@Component
public class AsyncProcessingWorkAcceptorImpl implements AsyncProcessingWorkAcceptor {
    @Override
    public String[] workAcceptor(UserModel userModel) {
        String[] requestStatus = new String[2];
        String userName = userModel.getUserName();
        if (userName == null) {
            requestStatus[0] = "404";
            requestStatus[1] = "Unauthorized";
            return requestStatus;
        }

        String key = userModel.getKey();
        // Perform authorization checks here with the key value.

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