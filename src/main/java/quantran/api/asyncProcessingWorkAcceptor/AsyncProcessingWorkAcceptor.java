package quantran.api.asyncProcessingWorkAcceptor;

import org.springframework.stereotype.Component;
import quantran.api.model.UserModel;

@Component
public interface AsyncProcessingWorkAcceptor {
    String[] workAcceptor(UserModel userModel);
}
