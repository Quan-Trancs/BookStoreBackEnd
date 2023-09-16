package quantran.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import quantran.api.common.UrlConstant;
import quantran.api.service.UserService;


@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping(UrlConstant.USER)
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping(UrlConstant.LOGIN)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<Boolean> login(@RequestParam String userName, @RequestParam String password) {
        log.info("Start login()");
        boolean loginStatus = userService.login(userName, password);
        log.info("End login()");
        return ResponseEntity.ok(loginStatus);
    }
}
