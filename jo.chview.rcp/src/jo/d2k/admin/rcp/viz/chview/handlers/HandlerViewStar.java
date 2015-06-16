package jo.d2k.admin.rcp.viz.chview.handlers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import jo.d2k.admin.rcp.sys.ui.InfoView;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerViewStar extends AbstractHandler
{
    public HandlerViewStar()
    {
        super();
        ChViewVisualizationLogic.mPreferences.addPropertyChangeListener("focus", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateEnablement();
            }
        });
        updateEnablement();
    }

    public void updateEnablement()
    {
        boolean infocus = ChViewVisualizationLogic.mPreferences.getFocus() != null;
        setBaseEnabled(infocus);
    }

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        StarBean star = ChViewVisualizationLogic.mPreferences.getFocus();
        if (star == null)
            return null;
        IWorkbenchPage page = window.getActivePage();
        try
        {
            InfoView info = (InfoView)page.showView(InfoView.ID, "id"+System.currentTimeMillis(), IWorkbenchPage.VIEW_ACTIVATE);
            info.setSelected(star);
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
