package quantran.api.model;
import quantran.api.entity.UserEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


public class UserModel {
    @NotNull (message = "UserName is required!")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{7,14}$", message = "Invalid UserName")
    private String userName;
    @NotNull (message = "Password is required!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-zA-Z0-9]{5,20}$", message = "Invalid Password")
    private String password;

    public UserModel() {}
    public UserModel(UserEntity userEntity) {
        this.userName = userEntity.getUserName();
        this.password = userEntity.getPassword();
    }
    public UserModel(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
