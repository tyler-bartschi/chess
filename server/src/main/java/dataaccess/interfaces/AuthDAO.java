package dataaccess.interfaces;

import model.AuthData;

public interface AuthDAO {
    /**
     * Clears all AuthTokens in the database
     */
    public void clear();

    /**
     * Adds an AuthData object to the database
     *
     * @param authData AuthData object to add
     */
    public void createAuth(AuthData authData);

    /**
     * Deletes a designated AuthData object from the database
     *
     * @param authToken authToken to delete the requested AuthData
     */
    public void deleteAuth(String authToken);

    /**
     * Finds an AuthData by the specified authToken
     *
     * @param authToken authToken to find the requested AuthData
     * @return an AuthData object if found, null if not
     */
    public AuthData getAuth(String authToken);
}
