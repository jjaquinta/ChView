package jo.d2k.data.io.derby;

import java.beans.IntrospectionException;
import java.sql.SQLException;
import java.util.List;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.io.IOStarRouteHandler;
import jo.util.dao.derby.DerbyBeanHandler2;
import jo.util.dao.derby.DerbyConnectionHandler;
import jo.util.utils.DebugUtils;

public class DerbyStarRouteHandler extends DerbyBeanHandler2<StarRouteBean> implements
        IOStarRouteHandler
{
    public DerbyStarRouteHandler(DerbyConnectionHandler sqlHandler) throws IntrospectionException, SQLException
    {
        super(sqlHandler, StarRouteBean.class);
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
        try
        {
            return query(cmd.toString(), 0, -1);
        }
        catch (SQLException e)
        {
            DebugUtils.error("Can't find within", e);
            e.printStackTrace();
            return null;
        }
    }
}
