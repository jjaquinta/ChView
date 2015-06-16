/*
 * Created on Nov 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.logic;

import java.util.HashSet;
import java.util.Set;

import jo.util.beans.Bean;
import jo.util.dao.IDBUtil;
import jo.util.dao.derby.DerbyUtil;
import jo.util.utils.DebugUtils;

/**
 * @author jgrant
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SQLUtil
{

    public static String quote(String str)
    {
        StringBuffer ret = new StringBuffer();
        ret.append("'");
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++)
            switch (c[i])
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
//                case '_':
//                    ret.append("\\_");
//                    break;
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
            int ch = str.charAt(o + 1);
            str = str.substring(o + 2);
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
            return "TEXT";
        }
        else if (type.equals("char"))
        {
            return "INTEGER";
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
        else if (type.equals("[Z"))
        {
            return "BLOB";
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
        mKeywords.add("YEAR_MONTH");
        mKeywords.add("ZEROFILL");
    };

    public static String makeSQLName(String name)
    {
        if (mKeywords.contains(name.toUpperCase()))
            return "fld_" + name;
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
            beanName = beanName.substring(o + 1);
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

    public static String byteToSQL(byte[] todo)
    {
        StringBuffer vals = new StringBuffer();
        vals.append("x'");
        for (int j = 0; j < todo.length; j++)
        {
            vals.append(Integer.toHexString((todo[j]>>8)&15));
            vals.append(Integer.toHexString(todo[j]&15));
        }
        vals.append("'");
        return vals.toString();
    }
    
    public static IDBUtil getInstance()
    {
        return new IDBUtil() {
            @Override
            public String unquote(String str)
            {
                return SQLUtil.unquote(str);
            }
            @Override
            public String unmakeSQLName(String name)
            {
                return SQLUtil.unmakeSQLName(name);
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
                return SQLUtil.quote(str);
            }
            @Override
            public String makeSQLName(String name)
            {
                return SQLUtil.makeSQLName(name);
            }
            @Override
            public String makeBeanName(Class<?> beanClass)
            {
                return SQLUtil.makeBeanName(beanClass);
            }
            @Override
            public String makeBeanName(String beanName)
            {
                return SQLUtil.makeBeanName(beanName);
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
                return SQLUtil.calcSQLType(type);
            }
            @Override
            public int[] bytes2ints(byte[] bytes)
            {
                return DerbyUtil.bytes2ints(bytes);
            }
            @Override
            public String makeBeanName(Bean bean)
            {
                return SQLUtil.makeBeanName(bean);
            }
        };
    }
}
