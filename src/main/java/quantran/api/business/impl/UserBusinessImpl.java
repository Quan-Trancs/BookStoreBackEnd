package quantran.api.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import quantran.api.business.UserBusiness;
import quantran.api.repository.UserRepository;


@Service
@Log4j2
@RequiredArgsConstructor
public class UserBusinessImpl implements UserBusiness {
    private final UserRepository userRepository;
    @Override
    public boolean login(String userName, String password) {
        log.info("Start login()");
        boolean loginStatus = userRepository.existsByUserNameAndPassword(userName, password);
        //List<UserEntity> list = userRepository.findAll();
        //log.info(list);
        log.info("End login()");
        return loginStatus;
        //return true;
    }
}
