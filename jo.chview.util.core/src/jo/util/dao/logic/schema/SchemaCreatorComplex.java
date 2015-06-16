package jo.util.dao.logic.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jo.util.beans.Bean;
import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.dao.data.props.DAOPropertyComplex;
import jo.util.dao.data.props.DAOPropertyObject;
import jo.util.dao.logic.ISchemaCreator;
import jo.util.dao.logic.SchemaLogic;
import jo.util.intro.PropInfo;

public class SchemaCreatorComplex implements ISchemaCreator
{
    @Override
    public List<DAOProperty> getSchema(IDBUtil utils, PropInfo info)
    {
        List<DAOProperty> props = new ArrayList<DAOProperty>();
        if (Bean.class.isAssignableFrom(info.getType()))
        {
            List<DAOProperty> subs = SchemaLogic.getSchema(utils, info.getType());
            for (DAOProperty sub : subs)
            {
                DAOProperty prop = new DAOPropertyComplex(utils, info, sub);
                props.add(prop);
            }
        }
        else if (Map.class.isAssignableFrom(info.getType()))
        {
            DAOProperty prop = new DAOPropertyObject(utils, info);
            props.add(prop);
        }
        else if (Collection.class.isAssignableFrom(info.getType()))
        {
            DAOProperty prop = new DAOPropertyObject(utils, info);
            props.add(prop);
        }
        return props;
    }
}
