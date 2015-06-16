package jo.util.logic;

public interface IUIThreadHandler
{
    public boolean isOnUIThread();
    public void runOnUIThread(Runnable r);
    public void runOnUIThread(Runnable r, String name, Object wrt);
}
