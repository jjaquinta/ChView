//class: IOBeanHandler
/**
 * The IOBeanHandler class.
 * 
 * @author uoakeju
 * @version Jun 18, 2004
 */
package jo.util.dao;

import java.util.Collection;
import java.util.List;

import jo.util.beans.Bean;

public interface IOBeanHandler
{
    public Bean newInstance();
    public void update(Bean bean);
    public void update(Collection<Bean> beans);
    public void delete(Bean bean);
    public void delete(Collection<Bean> beans);
    public void deleteAll();
    public Bean findByOID(long oid);
    public List<Bean> findAll();
    public Class<?> getBeanType();
    public boolean isValidOID(long oid);
    public Collection<String> findColumn(String colName);
    public List<Bean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy) throws Exception;
    public List<Bean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy, boolean fuzzy) throws Exception;
    public List<Bean> find(String[] cols, String[] vals, boolean single, String sortBy) throws Exception;
    public Bean find(String colName, long colVal);
    public Bean find(String colName, String colVal);
    public Bean find(String colName1, String colVal1, String colName2, String colVal2);
    public List<Bean> findMultiple(String colName, long colVal, String sortBy);
    public List<Bean> findMultiple(String colName, String colVal, String sortBy);
    public List<Bean> findMultiple(String colName1, String colVal1, String colName2, String colVal2, String sortBy);
}