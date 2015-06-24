package jo.d2k.data.logic;

import java.util.List;

import jo.d2k.data.data.UserBean;
import jo.d2k.data.io.IOUserHandler;
import jo.util.utils.obj.StringUtils;

public class UserLogic
{
    private static IOUserHandler getHandler()
    {
        return (IOUserHandler)ApplicationLogic.getHandler(ApplicationLogic.USER_HANDLER);
    }
    
    public static UserBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }
    
    public static UserBean getByUsername(String username)
    {
        return getHandler().find("Username", username);
    }

    public static List<UserBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(UserBean bean)
    {
        getHandler().delete(bean);
    }

    public static void delete(List<UserBean> beans)
    {
        getHandler().delete(beans);
    }
    
    public static UserBean create(String userName, String companyName, String password)
    {
        UserBean bean = getByUsername(userName);
        if (bean != null)
        {
            if (StringUtils.compareTo(bean.getPassword(), password) != 0)
                return null;
        }
        else
            bean = getHandler().newInstance();
        bean.setUsername(userName);
        bean.setCompanyName(companyName);
        bean.setPassword(password);
        getHandler().update(bean);
        return bean;
    }

    public static boolean validate(String userName, String password)
    {
        UserBean user = getByUsername(userName);
        if (user == null)
            return false;
        if (StringUtils.compareTo(user.getPassword(), password) != 0)
            return false;
        updateLastLogin(user);
        return true;
    }

    public static void updateLastLogin(UserBean user)
    {
        user.setLastLogin(System.currentTimeMillis());
        getHandler().update(user);
    }

    public static void updatePassword(UserBean user, String password)
    {
        user = getByID(user.getOID());
        user.setPassword(password);
        getHandler().update(user);
    }
    
    public static UserBean updateGameMoney(UserBean user, long adjust)
    {
        user = getByID(user.getOID());
        user.setGameMoney(user.getGameMoney() + adjust);
        getHandler().update(user);
        return user;
    }
}
