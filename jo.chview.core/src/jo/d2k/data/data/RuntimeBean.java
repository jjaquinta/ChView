package jo.d2k.data.data;

import jo.d2k.data.logic.IDataSource;
import jo.util.beans.PCSBean;

public class RuntimeBean extends PCSBean
{
    private int mBusyCount;
    private IDataSource mDataSource;

    public int getBusyCount()
    {
        return mBusyCount;
    }

    public void setBusyCount(int busyCount)
    {
        queuePropertyChange("busyCount", mBusyCount, busyCount);
        mBusyCount = busyCount;
        firePropertyChange();
    }

    public IDataSource getDataSource()
    {
        return mDataSource;
    }

    public void setDataSource(IDataSource dataSource)
    {
        queuePropertyChange("dataSource", mDataSource, dataSource);
        mDataSource = dataSource;
        firePropertyChange();
    }
}
