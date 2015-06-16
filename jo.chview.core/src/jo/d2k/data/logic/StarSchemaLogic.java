package jo.d2k.data.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.data.data.MetadataBean;
import jo.d2k.data.data.StarSchemaBean;
import jo.util.beans.PropChangeSupport;
import jo.util.logic.CSVLogic;
import jo.util.utils.obj.IntegerUtils;

public class StarSchemaLogic
{
    private static PropChangeSupport mPCS = new PropChangeSupport(StarSchemaLogic.class);

    private static List<StarSchemaBean> mSchemas = null;
    private static Map<String,StarSchemaBean> mSchemaIndex = null;
    static
    {
        RuntimeLogic.getInstance().addPropertyChangeListener("dataSource", new PropertyChangeListener() {           
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                mSchemas = null;
                mPCS.fireMonotonicPropertyChange("schemas", mSchemas);
            }
        });
    }
    
    public static void makeID(StarSchemaBean schema)
    {
        String id = "";
        for (char c : schema.getTitle().toCharArray())
            if (Character.isAlphabetic(c))
                id += Character.toUpperCase(c);
        if (id.length() == 0)
            id = "ID";
        id += String.valueOf(System.currentTimeMillis()%10);
        schema.setMetadataID(id);
    }
    
    public static List<StarSchemaBean> getSchemas()
    {
        if (mSchemas == null)
        {
            mSchemas = new ArrayList<StarSchemaBean>();
            mSchemaIndex = new HashMap<String, StarSchemaBean>();
            List<MetadataBean> mds = MetadataLogic.findByDomainIndex("starSchemaDef", -1);
            for (MetadataBean md : mds)
            {
                StarSchemaBean schema = new StarSchemaBean();
                schema.setOID(md.getOID());
                schema.setMetadataID(md.getKey());
                String val = md.getStringValue();
                String[] vals = CSVLogic.splitCSVLine(val);
                if (vals.length < 5)
                {
                    schema.setType(StarSchemaBean.TEXT);
                    schema.setIndex(mSchemas.size());
                    schema.setTitle(schema.getMetadataID());
                    schema.setSubType(new String[0]);
                }
                else
                {
                    schema.setType(IntegerUtils.parseInt(vals[0]));
                    schema.setIndex(IntegerUtils.parseInt(vals[1]));
                    schema.setTitle(vals[2]);
                    schema.setWidth(IntegerUtils.parseInt(vals[3]));
                    schema.setSortBy(IntegerUtils.parseInt(vals[4]));
                    String[] subType = new String[vals.length - 5];
                    System.arraycopy(vals, 3, subType, 0, vals.length - 5);
                    schema.setSubType(subType);
                }
                mSchemas.add(schema);
                mSchemaIndex.put(schema.getMetadataID(), schema);
            }
            Collections.sort(mSchemas, new Comparator<StarSchemaBean>() {
                @Override
                public int compare(StarSchemaBean o1, StarSchemaBean o2)
                {
                    return o1.getIndex() - o2.getIndex();
                }
            });
        }
        return mSchemas;
    }
    public static StarSchemaBean getSchema(String metadataID)
    {
        getSchemas();
        return mSchemaIndex.get(metadataID);
    }
    
    public static void setSchemas(List<StarSchemaBean> newSchemas)
    {
        for (int i = 0; i < newSchemas.size(); i++)
            newSchemas.get(i).setIndex(i);
        List<MetadataBean> schemas = new ArrayList<MetadataBean>();
        for (StarSchemaBean schema : newSchemas)
        {
            List<String> vals = new ArrayList<String>();
            vals.add(String.valueOf(schema.getType()));
            vals.add(String.valueOf(schema.getIndex()));
            vals.add(schema.getTitle());
            vals.add(String.valueOf(schema.getWidth()));
            vals.add(String.valueOf(schema.getSortBy()));
            if (schema.getSubType() != null)
                for (String val : schema.getSubType())
                    vals.add(val);
            MetadataBean md = new MetadataBean();
            md.setDomain("starSchemaDef");
            md.setIndex(-1);
            md.setKey(schema.getMetadataID());
            md.setStringValue(CSVLogic.toCSVLine(vals));
            schemas.add(md);
        }
        MetadataLogic.delete("starSchemaDef", -1);
        MetadataLogic.update(schemas);
        mSchemas = null;
        mPCS.fireMonotonicPropertyChange("schemas", mSchemas);
    }
    
    // listeners
    public static void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(prop, pcl);
    }
    public static void addPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(pcl);
    }
    public static void addUIPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(prop, pcl);
    }
    public static void addUIPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(pcl);
    }
    public static void removePropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.removePropertyChangeListener(pcl);
    }
}
