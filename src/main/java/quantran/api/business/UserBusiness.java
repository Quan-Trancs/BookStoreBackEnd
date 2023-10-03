package quantran.api.business;

import quantran.api.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public interface UserBusiness {
    default String login(UserEntity userEntity) {
        return "";
    };

    void setUserKey(UserEntity userEntity);
}
