/*
 * Created on Aug 14, 2005
 *
 */
package jo.util.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class URLUtils
{
    public static InputStream readURLAsStream(String u) throws IOException
    {
        URL url = new URL(u);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        return is;
    }

    public static byte[] readURL(String u) throws IOException
    {
        InputStream is = readURLAsStream(u);
        byte[] ret = StreamUtils.readStream(is);
        is.close();
        return ret;
    }

    public static String readURLAsString(String u) throws IOException
    {
        return new String(readURL(u));
    }

    public static String readURLAsString(String u, String charset) throws IOException
    {
        return new String(readURL(u), charset);
    }
    
    public static void copy(String in, File out) throws IOException
    {
        InputStream is = readURLAsStream(in);
        OutputStream os = new FileOutputStream(out);
        StreamUtils.copy(is, os);
        is.close();
        os.close();
    }
}
