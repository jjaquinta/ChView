package jo.util.ui.ctrl;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;

public class MenuUtils
{
    private static SelectionListener mActionMenuListener = new SelectionAdapter(){        
        @Override
        public void widgetSelected(SelectionEvent e)
        {
            Action a = (Action)e.widget.getData("action");
            Event ev = new Event();
            ev.widget = e.widget;
            a.runWithEvent(ev);
        }
    }; 
    
    private static SelectionListener mCommandMenuListener = new SelectionAdapter(){        
        @Override
        public void widgetSelected(SelectionEvent e)
        {
            Command cmd = (Command)e.widget.getData("cmd");
            ExecutionEvent ev = new ExecutionEvent();
            try
            {
                cmd.executeWithChecks(ev);
            }
            catch (ExecutionException | NotDefinedException
                    | NotEnabledException | NotHandledException e1)
            {
                throw new IllegalStateException(e1);
            }
        }
    }; 
    
    public static MenuItem addAction(Menu m, Action a)
    {
        MenuItem mi = new MenuItem(m, SWT.PUSH);
        mi.setText(a.getText());
        mi.setData("action", a);
        mi.addSelectionListener(mActionMenuListener);
        return mi;
    }

    public static void addCommandCategory(IWorkbenchWindow win, Menu menu, String cmdCategoryID)
    {
        ICommandService cs = (ICommandService)win.getService(ICommandService.class);
        for (Command cmd : cs.getDefinedCommands())
            try
            {
                if (cmdCategoryID.equals(cmd.getCategory().getId()))
                    doAddCommand(menu, cmd);
            }
            catch (NotDefinedException e)
            {
                throw new IllegalStateException(e); // shouldn't happen
            }
    }
    
    public static void addCommand(IWorkbenchWindow win, Menu menu, String cmdID)
    {
        ICommandService cs = (ICommandService)win.getService(ICommandService.class);
        Command cmd = cs.getCommand(cmdID);
        if (!cmd.isDefined())
            return;
        doAddCommand(menu, cmd);
    }

    public static void doAddCommand(Menu menu, Command cmd)
    {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        try
        {
            mi.setText(cmd.getName());
        }
        catch (NotDefinedException e)
        {
            throw new IllegalStateException(e); // shouldn't happen
        }
        mi.setEnabled(cmd.isDefined() && cmd.isHandled() && cmd.isEnabled());
        mi.setData("cmd", cmd);
        mi.addSelectionListener(mCommandMenuListener);
    }
}
