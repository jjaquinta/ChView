package jo.chview.rcp.logic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jo.d2k.admin.rcp.sys.ui.schema.ChoiceSchemaController;
import jo.d2k.admin.rcp.sys.ui.schema.DoubleSchemaController;
import jo.d2k.admin.rcp.sys.ui.schema.ISchemaController;
import jo.d2k.admin.rcp.sys.ui.schema.IntegerSchemaController;
import jo.d2k.admin.rcp.sys.ui.schema.LinkSchemaController;
import jo.d2k.admin.rcp.sys.ui.schema.TagsSchemaController;
import jo.d2k.admin.rcp.sys.ui.schema.TextSchemaController;
import jo.d2k.data.data.StarSchemaBean;

public class StarSchemaControllerLogic
{
    private static final Map<Integer,ISchemaController> mSchemaControllers = new HashMap<Integer, ISchemaController>();
    static
    {
        mSchemaControllers.put(StarSchemaBean.TEXT, new TextSchemaController());
        mSchemaControllers.put(StarSchemaBean.INTEGER, new IntegerSchemaController());
        mSchemaControllers.put(StarSchemaBean.DOUBLE, new DoubleSchemaController());
        mSchemaControllers.put(StarSchemaBean.CHOICE, new ChoiceSchemaController());
        mSchemaControllers.put(StarSchemaBean.TAGS, new TagsSchemaController());
        mSchemaControllers.put(StarSchemaBean.LINK, new LinkSchemaController());
    }

    public static ISchemaController getController(StarSchemaBean schema)
    {
        return getController(schema.getType());
    }

    public static ISchemaController getController(int type)
    {
        return mSchemaControllers.get(type);
    }
    
    public static Integer[] getSchemaTypes()
    {
        Integer[] types = mSchemaControllers.keySet().toArray(new Integer[0]);
        Arrays.sort(types);
        return types;
    }
    
    public static String[] getSchemaTypeLabels()
    {
        Integer[] types = getSchemaTypes();
        String[] labels = new String[types.length];
        for (int i = 0; i < types.length; i++)
            labels[i] = getController(types[i]).getName();
        return labels;
    }
}
