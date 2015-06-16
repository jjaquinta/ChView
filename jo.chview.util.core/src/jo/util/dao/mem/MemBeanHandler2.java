package jo.util.dao.mem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.util.beans.Bean;
import jo.util.dao.IOBeanHandler2;
import jo.util.dao.logic.SQLUtil;
import jo.util.utils.BeanUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class MemBeanHandler2<TheBean extends Bean> implements IOBeanHandler2<TheBean>
{
    private long                mNextOID = System.currentTimeMillis();
    private Map<Long, TheBean>  mCache;
    private Class<?>            mBeanClass;
    private boolean             mReadOnly;
    
    public MemBeanHandler2(Class<?> beanClass)
    {
        mBeanClass = beanClass;
        mCache = new HashMap<Long, TheBean>();
    }
    
    /* (non-Javadoc)
     * @see house.wish.io.IOBeanHandler#newInstance()
     */
    public TheBean newInstance()
    {
        try
        {
            @SuppressWarnings("unchecked")
            TheBean ret = (TheBean)mBeanClass.newInstance();
            ret.setOID(mNextOID++);
            return ret;
        }
        catch (Exception e)
        {
            DebugUtils.error("Can't make new instance of bean.", e);
            return null;
        }
    }

    @Override
    public void update(TheBean bean)
    {
        if (mReadOnly)
            return;
        TheBean stored = findByOID(bean.getOID());
        if (stored == null)
            mCache.put(bean.getOID(), bean);
        else if (stored != bean)
            stored.set(bean);
    }

    @Override
    public void update(Collection<TheBean> beans)
    {
        if (mReadOnly)
            return;
        for (TheBean bean : beans)
            update(bean);
    }

    @Override
    public void update(List<TheBean> beans)
    {
        if (mReadOnly)
            return;
        for (TheBean bean : beans)
            update(bean);
    }

    @Override
    public void delete(TheBean bean)
    {
        if (mReadOnly)
            return;
        mCache.remove(bean.getOID());
    }

    @Override
    public void delete(Collection<TheBean> beans)
    {
        if (mReadOnly)
            return;
        for (TheBean bean : beans)
            delete(bean);
    }

    @Override
    public void delete(String field, String value)
    {
        if (mReadOnly)
            return;
        delete(find(field, value));        
    }

    @Override
    public void deleteAll()
    {
        if (mReadOnly)
            return;
        mCache.clear();
    }

    @Override
    public TheBean findByOID(long oid)
    {
        return mCache.get(oid);
    }

    @Override
    public List<TheBean> findAll()
    {
        List<TheBean> beans = new ArrayList<TheBean>();
        beans.addAll(mCache.values());
        return beans;
    }
    
    public int countAll()
    {
        return mCache.size();
    }

    @Override
    public Class<?> getBeanType()
    {
        return mBeanClass;
    }

    @Override
    public boolean isValidOID(long oid)
    {
        return mCache.containsKey(oid);
    }

    @Override
    public Collection<String> findColumn(String colName)
    {
        Set<String> cols = new HashSet<String>();
        for (TheBean bean : mCache.values())
        {
            Object val = BeanUtils.get(bean, colName);
            if (val != null)
                cols.add(val.toString());
        }
        return cols;
    }

    @Override
    public List<TheBean> find(String[] cols, String[] vals, boolean single,
            boolean isOr, String sortBy) throws Exception
    {
        return find(cols, vals, single, isOr, sortBy, false);
    }

    @Override
    public List<TheBean> find(String[] cols, String[] vals, boolean single,
            boolean isOr, final String sortBy, boolean fuzzy) throws Exception
    {
        List<TheBean> beans = new ArrayList<TheBean>();
        for (TheBean bean : mCache.values())
            if (isMatch(bean, cols, vals, isOr, fuzzy))
            {
                beans.add(bean);
                if (single)
                    return beans;
            }
        if (!StringUtils.isTrivial(sortBy))
            Collections.sort(beans, new Comparator<TheBean>() {
                public int compare(TheBean bean1, TheBean bean2) {
                    Object o1 = BeanUtils.get(bean1, sortBy);
                    Object o2 = BeanUtils.get(bean2, sortBy);
                    return MemBeanHandler2.this.compare(o1, o2, false);
                }
            });
        return beans;
    }

    private boolean isMatch(TheBean bean, String[] cols, String[] vals,
            boolean isOr, boolean fuzzy)
    {
        for (int i = 0; i < cols.length; i++)
            if (isMatch(bean, cols[i], vals[i], fuzzy))
            {
                if (isOr)
                    return true;
            }
            else
            {
                if (!isOr)
                    return false;
            }
        return !isOr;
    }

    private boolean isMatch(TheBean bean, String col, String val, boolean fuzzy)
    {
        Object beanVal = BeanUtils.get(bean, col);
        return compare(beanVal, val, fuzzy) == 0;
    }

    private int compare(Object val1, Object val2, boolean fuzzy)
    {
        if (val1 == null)
            if (val2 == null)
                return 0;
            else
                return -1;
        else
            if (val2 == null)
                return 1;
            else
            {
                if (fuzzy)
                    return val1.toString().compareToIgnoreCase(val2.toString());
                else
                    return val1.toString().compareTo(val2.toString());
            }
    }

    @Override
    public List<TheBean> find(String[] cols, String[] vals, boolean single,
            String sortBy) throws Exception
    {
        return find(cols, vals, single, false, sortBy);
    }

    @Override
    public TheBean find(String colName, long colVal)
    {
        String[] colNames = new String[1];
        colNames[0] = colName;
        String[] colVals = new String[1];
        colVals[0] = String.valueOf(colVal);
        List<TheBean> ret;
        try
        {
            ret = find(colNames, colVals, true, null);
        }
        catch (Exception e)
        {
            DebugUtils.error("Can't perform find", e);
            return null;
        }
        if (ret.size() == 0)
            return null;
        return (TheBean)ret.get(0);
    }

    @Override
    public TheBean find(String colName, String colVal)
    {
        String[] colNames = new String[1];
        colNames[0] = colName;
        String[] colVals = new String[1];
        colVals[0] = SQLUtil.quote(colVal);
        List<TheBean> ret;
        try
        {
            ret = find(colNames, colVals, true, null);
        }
        catch (Exception e)
        {
            DebugUtils.error("Can't do find", e);
            return null;
        }
        if (ret.size() == 0)
            return null;
        return (TheBean)ret.get(0);
    }

    @Override
    public TheBean find(String colName1, String colVal1, String colName2,
            String colVal2)
    {
        String[] colNames = new String[2];
        colNames[0] = colName1;
        colNames[1] = colName2;
        String[] colVals = new String[2];
        colVals[0] = colVal1;
        colVals[1] = colVal2;
        List<TheBean> ret;
        try
        {
            ret = find(colNames, colVals, true, null);
        }
        catch (Exception e)
        {
            DebugUtils.error("Can't find "+colName1+"="+colVal1+" && "+colName2+"="+colVal2, e);
            return null;
        }
        if (ret.size() == 0)
            return null;
        return (TheBean)ret.get(0);
    }

    @Override
    public List<TheBean> findMultiple(String colName, long colVal, String sortBy)
    {
        String[] colNames = new String[1];
        colNames[0] = colName;
        String[] colVals = new String[1];
        colVals[0] = String.valueOf(colVal);
        try
        {
            return find(colNames, colVals, false, sortBy);
        }
        catch (Exception e)
        {
            DebugUtils.error("Can't find multiple for "+colName+"="+colVal+" in "+mBeanClass.getName(), e);
            return new ArrayList<TheBean>();
        }
    }

    @Override
    public List<TheBean> findMultiple(String colName, String colVal,
            String sortBy)
    {
        String[] colNames = new String[1];
        colNames[0] = colName;
        String[] colVals = new String[1];
        colVals[0] = colVal;
        try
        {
            return find(colNames, colVals, false, sortBy);
        }
        catch (Exception e)
        {
            DebugUtils.error("Can't find multiple for "+colName+"="+colVal, e);
            return new ArrayList<TheBean>();
        }
    }

    @Override
    public List<TheBean> findMultiple(String colName1, String colVal1,
            String colName2, String colVal2, String sortBy)
    {
        String[] colNames = new String[2];
        colNames[0] = colName1;
        colNames[1] = colName2;
        String[] colVals = new String[2];
        colVals[0] = colVal1;
        colVals[1] = colVal2;
        try
        {
            return find(colNames, colVals, false, sortBy);
        }
        catch (Exception e)
        {
            DebugUtils.error("Can't find multiple for "+colName1+"="+colVal1+" && "+colName2+"="+colVal2, e);
            return new ArrayList<TheBean>();
        }
    }

    @Override
    public List<TheBean> find(String[] cols, String[] vals, boolean isOr,
            String sortBy, boolean fuzzy, int offset, int limit)
            throws Exception
    {
        List<TheBean> ret = find(cols, vals, false, isOr, sortBy, fuzzy);
        while (offset-- > 0)
            ret.remove(0);
        while (ret.size() > limit)
            ret.remove(ret.size() - 1);
        return ret;
    }

    public boolean isReadOnly()
    {
        return mReadOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        mReadOnly = readOnly;
    }

}
