package quantran.api.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import quantran.api.business.UserBusiness;
import quantran.api.entity.BookEntity;
import quantran.api.entity.UserEntity;
import quantran.api.repository.UserRepository;
import quantran.api.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserBusinessImpl implements UserBusiness {
    private final UserRepository userRepository;
    @Override
    public String login(UserEntity userEntity, String newKey) {
        String key = newKey;
        log.info("Start login()");
        String userName = userEntity.getUserName();
        String password = userEntity.getPassword();
        boolean loginStatus = userRepository.existsByUserNameAndPassword(userName, password);
        userRepository.save(userEntity);
        if (loginStatus) {
            UserEntity loginedUserEntity = userRepository.findByUserName(userName);
            String userKey = loginedUserEntity.getKey();
            if (userKey != null && userKey != "") {
                key = userKey;
            }
            else {
                userEntity.setKey(key);
            }
        }
        else return "false";
        //List<UserEntity> list = userRepository.findAll();
        //log.info(list);
        log.info("End login()");
        //return key;
        return key;
    }
}
