package jo.d2k.data.logic;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.data.ScoreBean;
import jo.d2k.data.io.IOScoreHandler;
import jo.util.dao.sql.SQLBeanHandler2;

public class ScoreLogic
{
    private static IOScoreHandler getHandler()
    {
        return (IOScoreHandler)ApplicationLogic.getHandler(ApplicationLogic.SCORE_HANDLER);
    }
    
    public static ScoreBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }
    
    public static ScoreBean getByListIDAndUsername(String listID, String username)
    {
        return getHandler().find("listID", listID, "username", username);
    }
    
    public static List<ScoreBean> getByListPrefixAndUsername(String listID, String username)
    {
        return getHandler().findByListPrefixAndUsername(listID, username);
    }
    
    public static List<ScoreBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(ScoreBean bean)
    {
        getHandler().delete(bean);
    }

    public static void delete(List<ScoreBean> beans)
    {
        getHandler().delete(beans);
    }
    
    public static boolean create(String listID, String username, long score)
    {
        ScoreBean bean = getByListIDAndUsername(listID, username);
        if (bean != null)
        {
            if (bean.getScore() > score)
                return false;
            delete(bean);
        }
        bean = getHandler().newInstance();
        bean.setListID(listID);
        bean.setUsername(username);
        bean.setScore(score);
        bean.setDate(System.currentTimeMillis());
        getHandler().update(bean);
        return true;
    }
    
    public static List<ScoreBean> getScores(String listID, int offset, int limit)
    {
        try
        {
            return getHandler().find(new String[] { "listID" }, new String[] { listID }, 
                    false, SQLBeanHandler2.REVERSE+"score", false, offset, limit);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<ScoreBean>();
        }
    }

    public static int getRank(String listID, String username, int limit)
    {
        List<ScoreBean> top = getScores(listID, 0, limit);
        for (int i = 0; i < limit; i++)
            if (top.get(i).getUsername().equals(username))
                return i;
        return -1;
    }
}
