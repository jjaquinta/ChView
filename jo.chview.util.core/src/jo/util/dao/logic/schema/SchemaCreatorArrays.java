package jo.util.dao.logic.schema;

import java.util.ArrayList;
import java.util.List;

import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.dao.data.props.DAOPropertyBooleans;
import jo.util.dao.data.props.DAOPropertyBytes;
import jo.util.dao.data.props.DAOPropertyChars;
import jo.util.dao.data.props.DAOPropertyDoubles;
import jo.util.dao.data.props.DAOPropertyFloats;
import jo.util.dao.data.props.DAOPropertyInts;
import jo.util.dao.data.props.DAOPropertyLongs;
import jo.util.dao.data.props.DAOPropertyShorts;
import jo.util.dao.data.props.DAOPropertyStrings;
import jo.util.dao.logic.ISchemaCreator;
import jo.util.intro.PropInfo;

public class SchemaCreatorArrays implements ISchemaCreator
{
    @Override
    public List<DAOProperty> getSchema(IDBUtil utils, PropInfo info)
    {
        if (!info.getType().isArray())
            return null;
        List<DAOProperty> props = new ArrayList<DAOProperty>();
        if (info.getType() == String[].class)
            props.add(new DAOPropertyStrings(utils, info));
        else if ((info.getType() == Boolean[].class) || (info.getType() == boolean[].class))
            props.add(new DAOPropertyBooleans(utils, info));
        else if ((info.getType() == Byte[].class) || (info.getType() == byte[].class))
            props.add(new DAOPropertyBytes(utils, info));
        else if ((info.getType() == Character[].class) || (info.getType() == char[].class))
            props.add(new DAOPropertyChars(utils, info));
        else if ((info.getType() == Short[].class) || (info.getType() == short[].class))
            props.add(new DAOPropertyShorts(utils, info));
        else if ((info.getType() == Integer[].class) || (info.getType() == int[].class))
            props.add(new DAOPropertyInts(utils, info));
        else if ((info.getType() == Long[].class) || (info.getType() == long[].class))
            props.add(new DAOPropertyLongs(utils, info));
        else if ((info.getType() == Float[].class) || (info.getType() == float[].class))
            props.add(new DAOPropertyFloats(utils, info));
        else if ((info.getType() == Double[].class) || (info.getType() == double[].class))
            props.add(new DAOPropertyDoubles(utils, info));
        return props;
    }
}
