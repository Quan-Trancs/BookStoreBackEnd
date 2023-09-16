package quantran.api.entity;


import quantran.api.model.UserModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user2")
public class UserEntity {

    @Id
    @Column(name = "userName")
    private String userName;
    @Column(name = "password")
    private String password;

    public UserEntity() {}
    public UserEntity(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    public UserEntity(UserModel userModel) {
        this.userName = userModel.getUserName();
        this.password = userModel.getPassword();
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
