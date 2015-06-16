package jo.util.dao.logic.schema;

import java.util.ArrayList;
import java.util.List;

import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.dao.data.props.DAOPropertyBoolean;
import jo.util.dao.data.props.DAOPropertyByte;
import jo.util.dao.data.props.DAOPropertyChar;
import jo.util.dao.data.props.DAOPropertyDouble;
import jo.util.dao.data.props.DAOPropertyFloat;
import jo.util.dao.data.props.DAOPropertyInt;
import jo.util.dao.data.props.DAOPropertyLong;
import jo.util.dao.data.props.DAOPropertyShort;
import jo.util.dao.data.props.DAOPropertyString;
import jo.util.dao.logic.ISchemaCreator;
import jo.util.intro.PropInfo;

public class SchemaCreatorSimple implements ISchemaCreator
{
    @Override
    public List<DAOProperty> getSchema(IDBUtil utils, PropInfo info)
    {
        List<DAOProperty> props = new ArrayList<DAOProperty>();
        if (info.getType() == String.class)
            props.add(new DAOPropertyString(utils, info));
        else if ((info.getType() == Boolean.class) || (info.getType() == boolean.class))
            props.add(new DAOPropertyBoolean(utils, info));
        else if ((info.getType() == Byte.class) || (info.getType() == byte.class))
            props.add(new DAOPropertyByte(utils, info));
        else if ((info.getType() == Character.class) || (info.getType() == char.class))
            props.add(new DAOPropertyChar(utils, info));
        else if ((info.getType() == Short.class) || (info.getType() == short.class))
            props.add(new DAOPropertyShort(utils, info));
        else if ((info.getType() == Integer.class) || (info.getType() == int.class))
            props.add(new DAOPropertyInt(utils, info));
        else if ((info.getType() == Long.class) || (info.getType() == long.class))
            props.add(new DAOPropertyLong(utils, info));
        else if ((info.getType() == Float.class) || (info.getType() == float.class))
            props.add(new DAOPropertyFloat(utils, info));
        else if ((info.getType() == Double.class) || (info.getType() == double.class))
            props.add(new DAOPropertyDouble(utils, info));
        return props;
    }
}
