package quantran.api.business;

public interface UserBusiness {
    default boolean login(String userName, String password) { return false; };
}
