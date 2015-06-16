/*
 * Created on Sep 14, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.utils.io;

import java.io.PrintStream;

import jo.util.utils.DebugUtils;

public class CommandLineLogger implements LogEngine
{
    public static int       mDebugLevel = DebugUtils.INFO;
    static
    {
        String level = System.getProperty("debug.level");
        if (level != null)
            mDebugLevel = Integer.parseInt(level);
    }

    public void log(int severity, String msg, Throwable exception)
    {
        if (severity > mDebugLevel)
            return;
        PrintStream os = System.out;
        String prefix = "     ";
        switch (severity)
        {
            case DebugUtils.CRITICAL:
                prefix = "CRIT ";
                os = System.err;
                break;
            case DebugUtils.ERROR:
                prefix = "ERROR";
                os = System.err;
                break;
            case DebugUtils.WARN:
                prefix = "WARN ";
                os = System.out;
                break;
            case DebugUtils.INFO:
                prefix = "INFO ";
                os = System.out;
                break;
            case DebugUtils.TRACE:
                prefix = "TRACE";
                os = System.out;
                break;
        }
        if (msg != null)
            os.println(prefix+" "+msg);
        if (exception != null)
            exception.printStackTrace(os);
    }

}
