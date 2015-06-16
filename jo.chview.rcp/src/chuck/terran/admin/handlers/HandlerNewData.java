package chuck.terran.admin.handlers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jo.d2k.data.logic.DataLogic;
import jo.d2k.data.logic.IDataSource;
import jo.util.ui.act.GenericAction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;

public class HandlerNewData extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        doNew();
        return null;
    }

    public static void doNew()
    {
        InputDialog dlg = new InputDialog(Display.getDefault().getActiveShell(), 
                "Create New Local Database", 
                "Enter in the name for the new local database", 
                "", new DBInputValidator());
        if (dlg.open() != InputDialog.OK)
            return;
        String name = dlg.getValue();
        try
        {
            DataLogic.newDataSource(name);
        }
        catch (IOException e)
        {
            GenericAction.openError("Create New Local Database", "Error while creating database", e);
            e.printStackTrace();
        }
    }

}

class DBInputValidator implements IInputValidator
{
    private Set<String> mTaken;
    
    public DBInputValidator()
    {
        mTaken = new HashSet<>();
        for (IDataSource src : DataLogic.getDataSources())
            mTaken.add(src.getName());
    }

    @Override
    public String isValid(String name)
    {
        if (name.length() == 0)
            return "You must supply a name";
        if (mTaken.contains(name))
            return "This name is already taken";
        return null;
    }
    
}
