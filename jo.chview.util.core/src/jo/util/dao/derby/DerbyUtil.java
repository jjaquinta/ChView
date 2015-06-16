/*
 * Created on Nov 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.derby;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.util.beans.Bean;
import jo.util.dao.IDBUtil;
import jo.util.utils.DebugUtils;

/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DerbyUtil
{

    public static String quote(String str)
    {
    	StringBuffer ret = new StringBuffer();
    	ret.append("'");
    	char[] c = str.toCharArray();
    	for (int i = 0; i < c.length; i++)
    		switch(c[i])
    		{
    			case 0:
    				ret.append("\\0");
    				break;
    			case '\'':
    				ret.append("\\\'");
    				break;
    			case '\"':
    				ret.append("\\\"");
    				break;
    			case '\b':
    				ret.append("\\b");
    				break;
    			case '\n':
    				ret.append("\\n");
    				break;
    			case '\r':
    				ret.append("\\r");
    				break;
    			case '\t':
    				ret.append("\\t");
    				break;
    			case 26:
    				ret.append("\\z");
    				break;
    			case '\\':
    				ret.append("\\\\");
    				break;
    			case '%':
    				ret.append("\\%");
    				break;
    			case '_':
    				ret.append("\\_");
    				break;
    			default:
    				ret.append(c[i]);
    				break;
    		}
    	ret.append("'");
    	return ret.toString();
    }

    public static String unquote(String str)
    {
        if (str == null)
            return null;
    	StringBuffer ret = new StringBuffer();
    	for (;;)
    	{
    		int o = str.indexOf("\\");
    		if (o < 0)
    		{
    			ret.append(str);
    			break;
    		}
    		ret.append(str.substring(0, o));
    		int ch = str.charAt(o+1);
    		str = str.substring(o+2);
    		switch (ch)
    		{
    			case '0':
    				ret.append((char)0);
    				break;
    			case 'b':
    				ret.append("\b");
    				break;
    			case 't':
    				ret.append("\t");
    				break;
    			case 'z':
    				ret.append((char)26);
    				break;
    			default:
    				ret.append((char)ch);
    				break;
    		}
    	}
    	return ret.toString();
    }

    public static String calcSQLType(String type)
    {
    	if (type.equals("java.lang.String"))
    	{
    		return "VARCHAR(128)";
    	}
    	else if (type.equals("int"))
    	{
    		return "INTEGER";
    	}
    	else if (type.equals("long"))
    	{
    		return "BIGINT";
    	}
    	else if (type.equals("double"))
    	{
    		return "DOUBLE";
    	}
    	else if (type.equals("boolean"))
    	{
    		return "VARCHAR(1)";
    	}
        else if (type.equals("[B"))
        {
            return "BLOB";
        }
        else if (type.equals("[I"))
        {
            return "BLOB";
        }
        else if (type.equals("[java.lang.String"))
        {
            return "VARCHAR(256)";
        }
    	else
    	{
    		DebugUtils.error("Can't handle type " + type);
    		return null;
    	}
    }
    
    private static Set<String> mKeywords;
    static
    {
        mKeywords = new HashSet<String>();
        mKeywords.add("ADD");
        mKeywords.add("ALL");
        mKeywords.add("ALTER");
        mKeywords.add("ANALYZE");
        mKeywords.add("AND");
        mKeywords.add("AS");
        mKeywords.add("ASC");
        mKeywords.add("ASENSITIVE");
        mKeywords.add("BEFORE");
        mKeywords.add("BETWEEN");
        mKeywords.add("BIGINT");
        mKeywords.add("BINARY");
        mKeywords.add("BLOB");
        mKeywords.add("BOTH");
        mKeywords.add("BY");
        mKeywords.add("CALL");
        mKeywords.add("CASCADE");
        mKeywords.add("CASE");
        mKeywords.add("CHANGE");
        mKeywords.add("CHAR");
        mKeywords.add("CHARACTER");
        mKeywords.add("CHECK");
        mKeywords.add("COLLATE");
        mKeywords.add("COLUMN");
        mKeywords.add("CONDITION");
        mKeywords.add("CONNECTION");
        mKeywords.add("CONSTRAINT");
        mKeywords.add("CONTINUE");
        mKeywords.add("CONVERT");
        mKeywords.add("CREATE");
        mKeywords.add("CROSS");
        mKeywords.add("CURRENT_DATE");
        mKeywords.add("CURRENT_TIME");
        mKeywords.add("CURRENT_TIMESTAMP");
        mKeywords.add("CURRENT_USER");
        mKeywords.add("CURSOR");
        mKeywords.add("DATABASE");
        mKeywords.add("DATABASES");
        mKeywords.add("DAY_HOUR");
        mKeywords.add("DAY_MICROSECOND");
        mKeywords.add("DAY_MINUTE");
        mKeywords.add("DAY_SECOND");
        mKeywords.add("DEC");
        mKeywords.add("DECIMAL");
        mKeywords.add("DECLARE");
        mKeywords.add("DEFAULT");
        mKeywords.add("DELAYED");
        mKeywords.add("DELETE");
        mKeywords.add("DESC");
        mKeywords.add("DESCRIBE");
        mKeywords.add("DETERMINISTIC");
        mKeywords.add("DISTINCT");
        mKeywords.add("DISTINCTROW");
        mKeywords.add("DIV");
        mKeywords.add("DOUBLE");
        mKeywords.add("DROP");
        mKeywords.add("DUAL");
        mKeywords.add("EACH");
        mKeywords.add("ELSE");
        mKeywords.add("ELSEIF");
        mKeywords.add("ENCLOSED");
        mKeywords.add("ESCAPED");
        mKeywords.add("EXISTS");
        mKeywords.add("EXIT");
        mKeywords.add("EXPLAIN");
        mKeywords.add("FALSE");
        mKeywords.add("FETCH");
        mKeywords.add("FLOAT");
        mKeywords.add("FOR");
        mKeywords.add("FORCE");
        mKeywords.add("FOREIGN");
        mKeywords.add("FROM");
        mKeywords.add("FULLTEXT");
        mKeywords.add("GOTO");
        mKeywords.add("GRANT");
        mKeywords.add("GROUP");
        mKeywords.add("HAVING");
        mKeywords.add("HIGH_PRIORITY");
        mKeywords.add("HOUR_MICROSECOND");
        mKeywords.add("HOUR_MINUTE");
        mKeywords.add("HOUR_SECOND");
        mKeywords.add("IF");
        mKeywords.add("IGNORE");
        mKeywords.add("IN");
        mKeywords.add("INDEX");
        mKeywords.add("INFILE");
        mKeywords.add("INNER");
        mKeywords.add("INOUT");
        mKeywords.add("INSENSITIVE");
        mKeywords.add("INSERT");
        mKeywords.add("INT");
        mKeywords.add("INTEGER");
        mKeywords.add("INTERVAL");
        mKeywords.add("INTO");
        mKeywords.add("IS");
        mKeywords.add("ITERATE");
        mKeywords.add("JOIN");
        mKeywords.add("KEY");
        mKeywords.add("KEYS");
        mKeywords.add("KILL");
        mKeywords.add("LEADING");
        mKeywords.add("LEAVE");
        mKeywords.add("LEFT");
        mKeywords.add("LIKE");
        mKeywords.add("LIMIT");
        mKeywords.add("LINES");
        mKeywords.add("LOAD");
        mKeywords.add("LOCALTIME");
        mKeywords.add("LOCALTIMESTAMP");
        mKeywords.add("LOCK");
        mKeywords.add("LONG");
        mKeywords.add("LONGBLOB");
        mKeywords.add("LONGTEXT");
        mKeywords.add("LOOP");
        mKeywords.add("LOW_PRIORITY");
        mKeywords.add("MATCH");
        mKeywords.add("MEDIUMBLOB");
        mKeywords.add("MEDIUMINT");
        mKeywords.add("MEDIUMTEXT");
        mKeywords.add("MIDDLEINT");
        mKeywords.add("MINUTE_MICROSECOND");
        mKeywords.add("MINUTE_SECOND");
        mKeywords.add("MOD");
        mKeywords.add("MODIFIES");
        mKeywords.add("NATURAL");
        mKeywords.add("NOT");
        mKeywords.add("NO_WRITE_TO_BINLOG");
        mKeywords.add("NULL");
        mKeywords.add("NUMERIC");
        mKeywords.add("ON");
        mKeywords.add("OPTIMIZE");
        mKeywords.add("OPTION");
        mKeywords.add("OPTIONALLY");
        mKeywords.add("OR");
        mKeywords.add("ORDER");
        mKeywords.add("OUT");
        mKeywords.add("OUTER");
        mKeywords.add("OUTFILE");
        mKeywords.add("PRECISION");
        mKeywords.add("PRIMARY");
        mKeywords.add("PROCEDURE");
        mKeywords.add("PURGE");
        mKeywords.add("READ");
        mKeywords.add("READS");
        mKeywords.add("REAL");
        mKeywords.add("REFERENCES");
        mKeywords.add("REGEXP");
        mKeywords.add("RENAME");
        mKeywords.add("REPEAT");
        mKeywords.add("REPLACE");
        mKeywords.add("REQUIRE");
        mKeywords.add("RESTRICT");
        mKeywords.add("RETURN");
        mKeywords.add("REVOKE");
        mKeywords.add("RIGHT");
        mKeywords.add("RLIKE");
        mKeywords.add("SCHEMA");
        mKeywords.add("SCHEMAS");
        mKeywords.add("SECOND_MICROSECOND");
        mKeywords.add("SELECT");
        mKeywords.add("SENSITIVE");
        mKeywords.add("SEPARATOR");
        mKeywords.add("SET");
        mKeywords.add("SHOW");
        mKeywords.add("SMALLINT");
        mKeywords.add("SONAME");
        mKeywords.add("SPATIAL");
        mKeywords.add("SPECIFIC");
        mKeywords.add("SQL");
        mKeywords.add("SQLEXCEPTION");
        mKeywords.add("SQLSTATE");
        mKeywords.add("SQLWARNING");
        mKeywords.add("SQL_BIG_RESULT");
        mKeywords.add("SQL_CALC_FOUND_ROWS");
        mKeywords.add("SQL_SMALL_RESULT");
        mKeywords.add("SSL");
        mKeywords.add("STARTING");
        mKeywords.add("STRAIGHT_JOIN");
        mKeywords.add("TABLE");
        mKeywords.add("TERMINATED");
        mKeywords.add("THEN");
        mKeywords.add("TINYBLOB");
        mKeywords.add("TINYINT");
        mKeywords.add("TINYTEXT");
        mKeywords.add("TO");
        mKeywords.add("TRAILING");
        mKeywords.add("TRIGGER");
        mKeywords.add("TRUE");
        mKeywords.add("UNDO");
        mKeywords.add("UNION");
        mKeywords.add("UNIQUE");
        mKeywords.add("UNLOCK");
        mKeywords.add("UNSIGNED");
        mKeywords.add("UPDATE");
        mKeywords.add("USAGE");
        mKeywords.add("USE");
        mKeywords.add("USING");
        mKeywords.add("UTC_DATE");
        mKeywords.add("UTC_TIME");
        mKeywords.add("UTC_TIMESTAMP");
        mKeywords.add("VALUES");
        mKeywords.add("VARBINARY");
        mKeywords.add("VARCHAR");
        mKeywords.add("VARCHARACTER");
        mKeywords.add("VARYING");
        mKeywords.add("WHEN");
        mKeywords.add("WHERE");
        mKeywords.add("WHILE");
        mKeywords.add("WITH");
        mKeywords.add("WRITE");
        mKeywords.add("XOR");
        mKeywords.add("YEAR");
        mKeywords.add("YEAR_MONTH");
        mKeywords.add("ZEROFILL");
    };

    public static String makeSQLName(String name)
    {
        name = name.toUpperCase();
        if (mKeywords.contains(name))
            return "fld_"+name;
        else
            return name;
    }

    public static String unmakeSQLName(String name)
    {
        if (name.startsWith("fld_"))
            return name.substring(4);
        else
            return name;
    }
    
    public static String makeBeanName(String beanName)
    {
        int o = beanName.lastIndexOf(".");
        if (o > 0)
            beanName = beanName.substring(o+1);
        return beanName;
    }
    
    public static String makeBeanName(Class<?> beanClass)
    {
        return makeBeanName(beanClass.getName());
    }
    
    public static String makeBeanName(Bean bean)
    {
        return makeBeanName(bean.getClass());
    }
    
    private static Set<String> mNumericClasses;
    static
    {
        mNumericClasses = new HashSet<String>();
        mNumericClasses.add(Number.class.getName());
        mNumericClasses.add("byte");
        mNumericClasses.add("short");
        mNumericClasses.add("int");
        mNumericClasses.add("long");
        mNumericClasses.add("float");
        mNumericClasses.add("double");
    };
    
    public static boolean isNumeric(Class<?> type)
    {
        return mNumericClasses.contains(type.getName());
    }
    
    private static Set<String> mNumericTypes;
    static
    {
        mNumericTypes = new HashSet<String>();
        mNumericTypes.add("INTEGER");
        mNumericTypes.add("SMALLINT");
        mNumericTypes.add("BIGINT");
        mNumericTypes.add("DECIMAL");
        mNumericTypes.add("NUMERIC");
        mNumericTypes.add("FLOAT");
        mNumericTypes.add("REAL");
        mNumericTypes.add("DOUBLE PRECISION");
    };
    
    public static boolean isNumeric(String type)
    {
        return mNumericTypes.contains(type.toUpperCase());
    }
    
    public static int[] bytes2ints(byte[] bytes)
    {
        int[] ints = new int[bytes.length / 4];
        for (int i = 0; i < ints.length; i += 4)
            ints[i] = bytes2int(bytes, i*4);
        return ints;
    }
    
    private static int bytes2int(byte[] bytes, int o)
    {
        int i = 0;
        for (int j = 3; j >= 0; j--)
            i = i*256 + bytes[o+j]&0xff;
        return i;
    }

    public static byte[] ints2bytes(int[] ints)
    {
        byte[] bytes = new byte[ints.length * 4];
        for (int i = 0; i < ints.length; i++)
            int2bytes(ints[i], bytes, i*4);
        return bytes;
    }

    private static void int2bytes(int i, byte[] bytes, int o)
    {
        for (int j = 0; j < 4; j++)
        {
            byte b = (byte)(i&0xff);
            bytes[o+j] = b;
            i >>= 8;
        }        
    }
    
    public static String strings2string(String[] strings)
    {
        StringBuffer sb = new StringBuffer();
        for (String string : strings)
        {
            if (sb.length() > 0)
                sb.append(",");
            for (char c : string.toCharArray())
            {
                if ((c == ',') || (c == '\\'))
                    sb.append('\\');
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    public static String[] string2strings(String string)
    {
        List<String> strings = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        char[] cs = string.toCharArray();
        for (int i = 0; i < cs.length; i++)
            if (cs[i] == ',')
            {
                strings.add(sb.toString());
                sb.setLength(0);
            }
            else if (cs[i] == '\\')
            {
                sb.append(cs[++i]);
            }
            else
                sb.append(cs[i]);
        strings.add(sb.toString());
        return strings.toArray(new String[0]);
    }
    
    public static IDBUtil getInstance()
    {
        return new IDBUtil() {
            @Override
            public String unquote(String str)
            {
                return DerbyUtil.unquote(str);
            }
            @Override
            public String unmakeSQLName(String name)
            {
                return DerbyUtil.unmakeSQLName(name);
            }
            @Override
            public String strings2string(String[] strings)
            {
                return DerbyUtil.strings2string(strings);
            }
            @Override
            public String[] string2strings(String string)
            {
                return DerbyUtil.string2strings(string);
            }
            @Override
            public String quote(String str)
            {
                return DerbyUtil.quote(str);
            }
            @Override
            public String makeSQLName(String name)
            {
                return DerbyUtil.makeSQLName(name);
            }
            @Override
            public String makeBeanName(Class<?> beanClass)
            {
                return DerbyUtil.makeBeanName(beanClass);
            }
            @Override
            public String makeBeanName(String beanName)
            {
                return DerbyUtil.makeBeanName(beanName);
            }
            @Override
            public boolean isNumeric(Class<?> type)
            {
                return DerbyUtil.isNumeric(type);
            }
            @Override
            public byte[] ints2bytes(int[] ints)
            {
                return DerbyUtil.ints2bytes(ints);
            }
            @Override
            public String calcSQLType(String type)
            {
                return DerbyUtil.calcSQLType(type);
            }
            @Override
            public int[] bytes2ints(byte[] bytes)
            {
                return DerbyUtil.bytes2ints(bytes);
            }
            @Override
            public String makeBeanName(Bean bean)
            {
                return DerbyUtil.makeBeanName(bean);
            }
        };
    }
}
