package quantran.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import quantran.api.business.UserBusiness;
import quantran.api.service.UserService;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserBusiness userBusiness;
    @Override
    public boolean login(String userName, String password) {
        log.info("Start updateBook()");
        boolean loginStatus = userBusiness.login(userName, password);
        log.info("End updateBook()");
        return loginStatus;
    }
}
