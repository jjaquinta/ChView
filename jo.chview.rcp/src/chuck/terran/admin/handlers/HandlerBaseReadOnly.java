package chuck.terran.admin.handlers;

import jo.d2k.data.logic.IDataSource;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.beans.PropertyChangeInvoker;

import org.eclipse.core.commands.AbstractHandler;

abstract public class HandlerBaseReadOnly extends AbstractHandler
{
    protected boolean mEnabled;
    
    public HandlerBaseReadOnly()
    {
        RuntimeLogic.getInstance().addPropertyChangeListener("dataSource", new PropertyChangeInvoker(this, "updateEnablement"));
        updateEnablement();
    }
    
    @Override
    public boolean isEnabled()
    {
        return mEnabled;
    }

    public void updateEnablement()
    {
        IDataSource ds = RuntimeLogic.getInstance().getDataSource();
        if (ds == null)
            return;
        mEnabled = !ds.isReadOnly();
    }
}
