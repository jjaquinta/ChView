package jo.util.dao.logic;

import java.util.List;

import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.intro.PropInfo;

public interface ISchemaCreator
{
    public List<DAOProperty> getSchema(IDBUtil utils, PropInfo info);
}
