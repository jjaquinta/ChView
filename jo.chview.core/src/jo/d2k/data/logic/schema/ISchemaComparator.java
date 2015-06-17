package jo.d2k.data.logic.schema;

import jo.d2k.data.data.StarBean;

public interface ISchemaComparator
{
    public String getName();
    public String[] getOptions();
    public boolean isArgFor(int option);
    public boolean isMatch(StarBean star, String id, int option, Object arg);
}
