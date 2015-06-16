package jo.d2k.data.data;

import jo.util.beans.Bean;

public class UserBean extends Bean
{
    private String  mUsername;
    private String  mCompanyName;
    private long    mLastLogin;
    private String  mPassword;
    private long    mGameMoney;
    
    public String toString()
    {
        return mUsername;
    }
    
    public String getUsername()
    {
        return mUsername;
    }
    public void setUsername(String username)
    {
        mUsername = username;
    }
    public String getCompanyName()
    {
        return mCompanyName;
    }
    public void setCompanyName(String companyName)
    {
        mCompanyName = companyName;
    }
    public long getLastLogin()
    {
        return mLastLogin;
    }
    public void setLastLogin(long lastLogin)
    {
        mLastLogin = lastLogin;
    }
    public String getPassword()
    {
        return mPassword;
    }
    public void setPassword(String password)
    {
        mPassword = password;
    }

    public long getGameMoney()
    {
        return mGameMoney;
    }

    public void setGameMoney(long gameMoney)
    {
        mGameMoney = gameMoney;
    }
}
