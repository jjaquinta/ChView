package jo.d2k.data.logic;

import java.util.Map;

public interface IDataSource
{
    public String getName();
    public void initApp(Map<String,Object> app);
    public Object getHandler(Map<String, Object> app, String handlerID) throws Exception;
    public void close();
    public boolean isReadOnly();
    public void setReadOnly(boolean ro);
}
