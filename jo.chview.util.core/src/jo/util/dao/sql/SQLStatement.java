package jo.util.dao.sql;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import jo.util.dao.data.DAOProperty;

public class SQLStatement
{
    private StringBuffer        mStatement;
    private List<Object>        mValues;
    private List<DAOProperty>   mParams;
    
    public SQLStatement()
    {
        mValues = new ArrayList<Object>();
        mStatement = new StringBuffer();
        mParams = new ArrayList<DAOProperty>();
    }
    
    public void append(String txt)
    {
        mStatement.append(txt);
    }
    
    public void append(Object value, DAOProperty param)
    {
        mValues.add(value);
        mParams.add(param);
        mStatement.append("?");
    }
    
    public String getStatement()
    {
        return mStatement.toString();
    }

    public void prepare(PreparedStatement statement) throws Exception
    {
        for (int j = 0; j < mParams.size(); j++)
            mParams.get(j).getObjectValue(mValues.get(j), statement, j+1);
    }
}
