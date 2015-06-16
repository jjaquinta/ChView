package jo.util.ui.utils;

public class IDLogic
{
    public static String encode(String str)
    {
        char[] c = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < c.length; i++)
            if (isProtected(c[i]))
            {
                sb.append('%');
                String hex = Integer.toHexString(c[i]);
                while (hex.length() < 4)
                    hex = "0" + hex;
                sb.append(hex);
            }
            else
                sb.append(c[i]);
        return sb.toString();
    }
    
    public static String decode(String str)
    {
        if (str == null)
            return null;
        char[] c = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < c.length; i++)
            if (c[i] == '%')
            {
                String hex = new String(c, i + 1, 4);
                char ch = (char)Integer.parseInt(hex, 16);
                sb.append(ch);
                i += 4;
            }
            else
                sb.append(c[i]);
        return sb.toString();
    }
    
    private static boolean isProtected(char c)
    {
        if ((c < ' ') || (c > '~'))
            return true;
        if ((c == '%') || (c == ':'))
            return true;
        return false;
    }
}
