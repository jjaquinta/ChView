package jo.d2k.data.io.sql;

import java.beans.IntrospectionException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.io.IOStarRouteHandler;
import jo.util.dao.sql.SQLBeanHandler2;
import jo.util.dao.sql.SQLConnectionHandler;
import jo.util.utils.DebugUtils;

public class SQLStarRouteHandler extends SQLBeanHandler2<StarRouteBean> implements
        IOStarRouteHandler
{
    public SQLStarRouteHandler(SQLConnectionHandler sqlHandler) throws IntrospectionException, SQLException
    {
        super(sqlHandler, StarRouteBean.class);
        mCaching = true;
    }
    
    public List<StarRouteBean> findAllLinking(List<StarBean> stars)
    {
        StringBuffer cmd = new StringBuffer();
        cmd.append("SELECT * FROM ");
        cmd.append(getTableName());
        cmd.append(" WHERE ");
        cmd.append("( ");       
        for (int i = 0; i < stars.size(); i++)
        {
            if (i > 0)
                cmd.append("OR ");
            StarBean star = stars.get(i);
            if (star.isGenerated())
            {
                cmd.append("( ");       
                cmd.append(mUtils.makeSQLName("star1"));
                cmd.append(" = ");
                cmd.append(star.getOID());
                cmd.append(" AND ");
                cmd.append(mUtils.makeSQLName("star1quad"));
                cmd.append(" = ");
                cmd.append(mUtils.quote(star.getQuadrant()));
                cmd.append(" ");
                cmd.append(") ");       
            }
            else
            {
                cmd.append(mUtils.makeSQLName("star1"));
                cmd.append(" = ");
                cmd.append(star.getOID());
                cmd.append(" ");
            }
        }
        cmd.append(" ) AND ( ");       
        for (int i = 0; i < stars.size(); i++)
        {
            if (i > 0)
                cmd.append("OR ");
            StarBean star = stars.get(i);
            if (star.isGenerated())
            {
                cmd.append("( ");       
                cmd.append(mUtils.makeSQLName("star2"));
                cmd.append(" = ");
                cmd.append(star.getOID());
                cmd.append(" AND ");
                cmd.append(mUtils.makeSQLName("star2quad"));
                cmd.append(" = ");
                cmd.append(mUtils.quote(star.getQuadrant()));
                cmd.append(" ");
                cmd.append(") ");       
            }
            else
            {
                cmd.append(mUtils.makeSQLName("star2"));
                cmd.append(" = ");
                cmd.append(star.getOID());
                cmd.append(" ");
            }
        }
        cmd.append(")");
        cmd.append(";");
        DebugUtils.debug(cmd.toString());
        if (mCaching && mCache.containsKey(cmd.toString()))
        {
            DebugUtils.debug("Retrieved from cache");
            return mCache.get(cmd.toString());
        }
        List<StarRouteBean> ret = new ArrayList<StarRouteBean>();
        for (int i = 3; i >= 0; i--)
        {
            try
            {
                ResultSet result = mSQLConnectionHandler.executeQuery(cmd.toString());
                while (result.next())
                {
                    ret.add(readBean(result));
                }
                if (mCaching)
                    mCache.put(cmd.toString(), ret);
                return ret;
            }
            catch (SQLException ex)
            {
                if (i == 0)
                {
                    DebugUtils.error("Can't find within", ex);
                    ex.printStackTrace();
                    return null;
                }
                ex.printStackTrace();
            }
            ret.clear();
        }
        return ret;
    }
}
