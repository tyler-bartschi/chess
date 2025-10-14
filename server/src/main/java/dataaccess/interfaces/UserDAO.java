package dataaccess.interfaces;

import model.UserData;

public interface UserDAO {
    /**
     * Clears all users from the database
     */
    public void clear();

    /**
     * Creates a new user in the database
     *
     * @param user UserData to be stored in the database
     */
    public void createUser(UserData user);

    /**
     * Retrieves a user from the database
     *
     * @param username username to find the user
     * @return UserData if found, null otherwise
     */
    public UserData getUser(String username);
}
