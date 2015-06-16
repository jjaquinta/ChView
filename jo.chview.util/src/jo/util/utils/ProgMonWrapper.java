package jo.util.utils;

import org.eclipse.core.runtime.IProgressMonitor;

public class ProgMonWrapper implements IProgMon
{
    private IProgressMonitor    mProgMon;
    
    public ProgMonWrapper(IProgressMonitor progMon)
    {
        mProgMon = progMon;
    }

    @Override
    public void beginTask(String name, int totalWork)
    {
        mProgMon.beginTask(name, totalWork);
    }

    @Override
    public void done()
    {
        mProgMon.done();
    }

    @Override
    public void internalWorked(double work)
    {
        mProgMon.internalWorked(work);
    }

    @Override
    public boolean isCanceled()
    {
        return mProgMon.isCanceled();
    }

    @Override
    public void setCanceled(boolean value)
    {
        mProgMon.setCanceled(value);
    }

    @Override
    public void setTaskName(String name)
    {
        mProgMon.setTaskName(name);
    }

    @Override
    public void subTask(String name)
    {
        mProgMon.subTask(name);
    }

    @Override
    public void worked(int work)
    {
        mProgMon.worked(work);
    }
     
}
