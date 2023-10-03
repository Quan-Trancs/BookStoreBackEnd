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
    private final UserService userService;
    @Override
    public void setUserKey(UserEntity userEntity) {
        userRepository.save(userEntity);
    }
    @Override
    public String login(UserEntity userEntity) {
        log.info("Start login()");
        String userName = userEntity.getUserName();
        String password = userEntity.getPassword();
        boolean loginStatus = userRepository.existsByUserNameAndPassword(userName, password);
        String key = "";
        if (loginStatus) {
            UserEntity loginedUserEntity = userRepository.findByUserName(userName);
            String userKey = loginedUserEntity.getKey();
            if (userKey != null && userKey != "") {
                key = userKey;
            }
            else {
                key = userService.setUserKey(loginedUserEntity);
            }
        }
        //List<UserEntity> list = userRepository.findAll();
        //log.info(list);
        log.info("End login()");
        //return key;
        return key;
    }
}
