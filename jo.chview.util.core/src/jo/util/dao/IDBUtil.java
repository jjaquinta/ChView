package jo.util.dao;

import jo.util.beans.Bean;

public interface IDBUtil
{
    public String quote(String str);
    public String unquote(String str);
    public String calcSQLType(String type);
    public String makeSQLName(String name);
    public String unmakeSQLName(String name);
    public String makeBeanName(String beanName);
    public String makeBeanName(Class<?> beanClass);
    public String makeBeanName(Bean bean);
    public boolean isNumeric(Class<?> type);
    public int[] bytes2ints(byte[] bytes);
    public byte[] ints2bytes(int[] ints);
    public String strings2string(String[] strings);
    public String[] string2strings(String string);
}
