package jo.util.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jo.util.beans.BeanLogic;
import jo.util.utils.ArrayUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

public class ThreadLogic
{
    private static IUIThreadHandler mUIHandler = new DefaultUIThreadHandler();
    
    public static final int EXEC_BACKGROUND = 0;
    public static final int EXEC_UI_ASYNCHRONOUS = 1;
    public static final int EXEC_UI_SYNCHRONOUS = 2;
    public static final int EXEC_UI = EXEC_UI_ASYNCHRONOUS;

    private static Logger logger = Logger.getLogger(ThreadLogic.class.getPackage().getName());
    
    private static Map<Thread, ThreadGroup> GROUPS = new HashMap<Thread, ThreadGroup>();
    
    public static void setUIThreadHandler(IUIThreadHandler handler)
    {
        mUIHandler = handler;
    }
    
    public static boolean isOnUIThread()
    {
        return mUIHandler.isOnUIThread();
    }

    public static void sleep(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
        }
    }
    
    public static Thread runMethodOnUIThread(final Object obj, final String method, final Object... args)
    {
        Thread t = new Thread(obj.getClass().getSimpleName()+":"+method) { public void run() {
            BeanLogic.invoke(obj, method, args);
        }};
        runOnUIThread(t);
        return t;
    }    

    public static void runOnUIThread(Runnable r)
    {
        if (r instanceof Thread)
            runOnUIThread(r, ((Thread)r).getName(), null);
        else
            runOnUIThread(r, null, null);
    }
    public static void runOnUIThread(Runnable r, Object wrt)
    {
        if (r instanceof Thread)
            runOnUIThread(r, ((Thread)r).getName(), wrt);
        else
            runOnUIThread(r, null, wrt);
    }
    public static void runOnUIThread(Runnable r, String name)
    {
        runOnUIThread(r, name, null);
    }
    public static void runOnUIThread(Runnable r, String name, Object wrt)
    {
        if (isOnUIThread())
            r.run();
        else
            mUIHandler.runOnUIThread(r, name, wrt);
    }

    public static Thread runMethodOnBackgroundThread(final Object obj, final String method, final Object... args)
    {
        Thread t = new Thread(obj.getClass().getSimpleName()+":"+method) { public void run() {
            BeanLogic.invoke(obj, method, args);
        }};
        t.start();
        return t;
    }
    
    public static void runOnBackgroundThread(Runnable r)
    {
        if (r instanceof Thread)
            runOnBackgroundThread(r, ((Thread)r).getName());
        else
            runOnBackgroundThread(r, null);
    }
    public static void runOnBackgroundThread(Runnable r, String name)
    {
        if (!isOnUIThread())
            r.run();
        else
        {
            Thread t = new Thread(r, name);
            t.start();
        }
    }
    
    public static Thread execOnThread(int mode, final Object base, final String method, final Object... args)
    {
        if (base == null)
            throw new IllegalArgumentException("base is null!");
        if (StringUtils.isTrivial(method))
            throw new IllegalArgumentException("no method specified!");
        String name = "::"+method;
        if (base instanceof Class)
            name = ((Class<?>)base).getName() + name;
        else
            name = base.getClass().getName() + name;
        ThreadGroup g = getGroup();
        Thread t;
        if (g == null)
        {
            if (BooleanUtils.parseBoolean(System.getProperty("thread.verbose")))
                System.err.println("*** Invoking "+name+" from "+Thread.currentThread().getName()+" (no group)");
            t = new Thread("Indirect Method Invocation::"+name) { public void run() {
                BeanLogic.invoke(base, method, args);
            }};
        }
        else
        {
            if (BooleanUtils.parseBoolean(System.getProperty("thread.verbose")))
                System.err.println("*** Invoking "+name+" from "+Thread.currentThread().getName()+" on group "+g.getName());
            t = new Thread(g, "Indirect Method Invocation::"+name) { public void run() {
                BeanLogic.invoke(base, method, args);
            }};
        }
        if (mode == EXEC_UI_ASYNCHRONOUS)
            mUIHandler.runOnUIThread(t);
        else if (mode == EXEC_BACKGROUND)
            t.start();
        else if (mode == EXEC_UI_SYNCHRONOUS)
            runOnUIThread(t);
        else
            throw new IllegalArgumentException("Unexpected value of mode="+mode);
        return t;
    }
    
    public static Thread execOnUIThread(Object base, String method, Object... args)
    {
        if (BooleanUtils.parseBoolean(System.getProperty("thread.verbose")))
        {
            System.out.println("THREAD.UI "+((base instanceof Class) ? ((Class<?>)base).getName() : base.getClass().getName())+"."+method);
            logger.finer("THREAD.UI "+((base instanceof Class) ? ((Class<?>)base).getName() : base.getClass().getName())+"."+method);
            Throwable t = new Throwable();
            StackTraceElement[] stack = t.getStackTrace();
            StackTraceElement last = stack[1];
            System.out.println("  Called by "+last.getFileName()+":"+last.getLineNumber()+" "+last.getClassName()+"."+last.getMethodName());
            logger.finer("  Called by "+last.getFileName()+":"+last.getLineNumber()+" "+last.getClassName()+"."+last.getMethodName());
        }
        if (method.endsWith("BG"))
            logger.warning("Invoking method "+method+" on UI thread. Did you mean background thread?");
        return execOnThread(EXEC_UI, base, method, args);
    }
    
    public static Thread execOnBackgroundThread(Object base, String method, Object... args)
    {
        if (BooleanUtils.parseBoolean(System.getProperty("thread.verbose")))
        {
            System.out.println("THREAD.BG "+((base instanceof Class) ? ((Class<?>)base).getName() : base.getClass().getName())+"."+method);
            Throwable t = new Throwable();
            StackTraceElement[] stack = t.getStackTrace();
            StackTraceElement last = stack[1];
            System.out.println("  Called by "+last.getFileName()+":"+last.getLineNumber()+" "+last.getClassName()+"."+last.getMethodName());
        }
        if (method.endsWith("UI"))
            logger.warning("Invoking method "+method+" on background thread. Did you mean UI thread?");
        return execOnThread(EXEC_BACKGROUND, base, method, args);
    }
    
    private static ThreadGroup getGroup()
    {
        ThreadGroup g = GROUPS.get(Thread.currentThread());
        if (g != null)
            return g;
        Thread[] threads = new Thread[256];
        for (ThreadGroup group : GROUPS.values())
        {
            group.enumerate(threads);
            if (ArrayUtils.contains(threads, Thread.currentThread()))
                return group;
        }
        return null;
    }
    
    public static void join(ThreadGroup group)
    {
        Thread[] threads = new Thread[1];
        while (group.activeCount() > 0)
        {
            int len = group.enumerate(threads);
            if (len == 0)
                break;
            try
            {
                threads[0].join();
            }
            catch (InterruptedException e)
            {
            }
        }
    }
    
    public static void registerThreadGroup(ThreadGroup g)
    {
        GROUPS.put(Thread.currentThread(), g);        
    }
    
    public static void unregisterThreadGroup()
    {
        GROUPS.remove(Thread.currentThread());        
    }
}
