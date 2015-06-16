package chuck.terran.admin.handlers;

import jo.d2k.data.io.derby.DerbyDataSource;
import jo.d2k.data.logic.DataLogic;
import jo.d2k.data.logic.IDataSource;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.ui.act.GenericAction;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class HandlerDeleteData extends HandlerBaseReadOnly
{
    public void updateEnablement()
    {
        IDataSource ds = RuntimeLogic.getInstance().getDataSource();
        if (ds == null)
            return;
        if (!(ds instanceof DerbyDataSource) || ds.isReadOnly())
            mEnabled = false;
        else
            mEnabled = true;
    }

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IDataSource src = RuntimeLogic.getInstance().getDataSource();
        if (!(src instanceof DerbyDataSource))
        {
            GenericAction.openError("Delete Database", src.getName()+" is not a local database.", null);
            return null;
        }
        if (!GenericAction.openQuestion("Delete Database", "Do you really want to delete local database '"+src.getName()+"' forever?"))
            return null;
        DerbyDataSource dsrc = (DerbyDataSource)src;
        DataLogic.deleteDataSource(dsrc.getDBName());
        return null;
    }
}
