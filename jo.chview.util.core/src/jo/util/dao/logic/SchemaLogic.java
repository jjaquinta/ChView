package jo.util.dao.logic;

import java.util.ArrayList;
import java.util.List;

import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.dao.logic.schema.SchemaCreatorArrays;
import jo.util.dao.logic.schema.SchemaCreatorComplex;
import jo.util.dao.logic.schema.SchemaCreatorSimple;
import jo.util.intro.PropInfo;
import jo.util.intro.PropInfoLogic;
import jo.util.utils.DebugUtils;

public class SchemaLogic
{
    private static final List<ISchemaCreator> mSchemaCreators = new ArrayList<ISchemaCreator>();
    static
    {
        mSchemaCreators.add(new SchemaCreatorSimple());
        mSchemaCreators.add(new SchemaCreatorArrays());
        mSchemaCreators.add(new SchemaCreatorComplex());
    }
    
    public static void addSchemaCreator(ISchemaCreator schemaCreator)
    {
        mSchemaCreators.add(schemaCreator);
    }
    
    public static List<DAOProperty> getSchema(IDBUtil utils, Class<?> clazz)
    {
        List<DAOProperty> props = new ArrayList<DAOProperty>();
        List<PropInfo> infos = PropInfoLogic.getProps(clazz);
        for (PropInfo info : infos)
        {
            boolean found = false;
            for (ISchemaCreator schemaCreator : mSchemaCreators)
            {
                List<DAOProperty> prop = schemaCreator.getSchema(utils, info);
                if ((prop != null) && (prop.size() > 0))
                {
                    props.addAll(prop);
                    found = true;
                    break;
                }
            }
            if (!found)
                DebugUtils.error("Cannot map property "+info.getName()+" of type "+info.getType().getName()+" of class "+clazz.getName());
        }
        return props;
    }
}
