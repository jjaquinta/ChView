package jo.d2k.data.logic;

import jo.d2k.data.data.RuntimeBean;

public class RuntimeLogic
{
    private static RuntimeBean mApplication;
    
    public static RuntimeBean getInstance()
    {
        if (mApplication == null)
        {
            mApplication = new RuntimeBean();
            mApplication.setDataSource(DataLogic.getDefaultDataSource());
        }
        return mApplication;
    }
    
    public static void incrementBusy()
    {
        synchronized (mApplication)
        {
            mApplication.setBusyCount(mApplication.getBusyCount() + 1);
        }
    }
    
    public static void decrementBusy()
    {
        synchronized (mApplication)
        {
            mApplication.setBusyCount(mApplication.getBusyCount() - 1);
        }
    }
}
