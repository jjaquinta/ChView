package jo.d2k.data.logic.schema;

import java.util.HashMap;
import java.util.Map;

import jo.d2k.data.data.StarSchemaBean;

public class StarSchemaComparatorLogic
{
    private static final Map<Integer,ISchemaComparator> mSchemaComparators = new HashMap<Integer, ISchemaComparator>();
    static
    {
        mSchemaComparators.put(StarSchemaBean.TEXT, new TextSchemaComparator());
        mSchemaComparators.put(StarSchemaBean.INTEGER, new IntegerSchemaComparator());
        mSchemaComparators.put(StarSchemaBean.DOUBLE, new DoubleSchemaComparator());
        mSchemaComparators.put(StarSchemaBean.CHOICE, new ChoiceSchemaComparator());
        mSchemaComparators.put(StarSchemaBean.TAGS, new TagsSchemaComparator());
        mSchemaComparators.put(StarSchemaBean.LINK, new LinkSchemaComparator());
    }

    public static ISchemaComparator getComparator(StarSchemaBean schema)
    {
        return getComparator(schema.getType());
    }

    public static ISchemaComparator getComparator(int type)
    {
        return mSchemaComparators.get(type);
    }
}
