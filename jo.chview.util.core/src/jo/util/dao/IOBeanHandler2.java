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

public interface IOBeanHandler2<TheBean extends Bean>
{
    public TheBean newInstance();
    public void update(TheBean bean);
    public void update(List<TheBean> beans);
    public void update(Collection<TheBean> beans);
    public void delete(TheBean bean);
    public void delete(Collection<TheBean> beans);
    public void delete(String field, String value);
    public void deleteAll();
    public TheBean findByOID(long oid);
    public List<TheBean> findAll();
    public int countAll();
    public Class<?> getBeanType();
    public boolean isValidOID(long oid);
    public Collection<String> findColumn(String colName);
    public List<TheBean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy) throws Exception;
    public List<TheBean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy, boolean fuzzy) throws Exception;
    public List<TheBean> find(String[] cols, String[] vals, boolean single, String sortBy) throws Exception;
    public List<TheBean> find(String[] cols, String[] vals, boolean isOr, String sortBy, boolean fuzzy, int offset, int limit) throws Exception;
    public TheBean find(String colName, long colVal);
    public TheBean find(String colName, String colVal);
    public TheBean find(String colName1, String colVal1, String colName2, String colVal2);
    public List<TheBean> findMultiple(String colName, long colVal, String sortBy);
    public List<TheBean> findMultiple(String colName, String colVal, String sortBy);
    public List<TheBean> findMultiple(String colName1, String colVal1, String colName2, String colVal2, String sortBy);
}