package quantran.api.service;

import quantran.api.entity.UserEntity;
import quantran.api.model.UserModel;

import java.util.List;

public interface UserService {
    String login(UserModel userModel);

    String setUserKey(UserEntity userEntity);
}
