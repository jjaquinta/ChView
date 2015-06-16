package jo.util.logic;

import java.util.LinkedList;
import java.util.List;

public class DefaultUIThreadHandler implements IUIThreadHandler
{
    private static final String UI_THREAD_NAME = "UI Thread";
    private final List<Runnable> mUIQueue = new LinkedList<Runnable>();
    private Thread mUIThread = null;

    @Override
    public boolean isOnUIThread()
    {
        return UI_THREAD_NAME.equals(Thread.currentThread().getName());
    }

    @Override
    public synchronized void runOnUIThread(Runnable r)
    {
        boolean wakeup = false;
        synchronized (mUIQueue)
        {
            wakeup = mUIQueue.size() == 0;
            mUIQueue.add(r);
        }
        if (mUIThread == null)
        {
            mUIThread = new Thread(UI_THREAD_NAME) { public void run() { runUIThreads(); } };
            mUIThread.start();
        }
        else
            if (wakeup)
                mUIThread.interrupt();
    }

    @Override
    public void runOnUIThread(Runnable r, String name, Object wrt)
    {
        if (r instanceof Thread)
            ((Thread)r).setName(name);
        runOnUIThread(r);
    }

    private void runUIThreads()
    {
        for (;;)
        {
            try
            {
                Runnable nextThread = null;
                synchronized (mUIQueue)
                {
                    if (mUIQueue.size() > 0)
                    {
                        nextThread = mUIQueue.get(0);
                        mUIQueue.remove(0);
                    }
                }
                if (nextThread != null)
                    nextThread.run();
                else
                    try
                    {
                        Thread.sleep(60*1000);
                    }
                    catch (InterruptedException e)
                    {
                    }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
    }
}
