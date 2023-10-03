package quantran.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import quantran.api.business.UserBusiness;
import quantran.api.entity.UserEntity;
import quantran.api.model.UserModel;
import quantran.api.service.UserService;

import java.util.List;
import java.security.SecureRandom;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserBusiness userBusiness;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String setUserKey(UserEntity userEntity) {
        StringBuilder key = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            key.append(randomChar);
        }
        userEntity.setKey(key.toString());
        userBusiness.setUserKey(userEntity);
        return key.toString();
    }
    @Override
    public String login(UserModel userModel) {
        log.info("Start login()");
        UserEntity userEntity = new UserEntity(userModel);
        String key = userBusiness.login(userEntity);
            return key;
    }
}
