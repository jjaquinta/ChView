package jo.d2k.admin.rcp.viz.chview.handlers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import jo.d2k.admin.rcp.sys.ui.DlgStarEdit;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.RuntimeLogic;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerEditStar extends AbstractHandler
{
    public HandlerEditStar()
    {
        super();
        ChViewVisualizationLogic.mPreferences.addPropertyChangeListener("focus", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateEnablement();
            }
        });
        RuntimeLogic.getInstance().addPropertyChangeListener("dataSource", new PropertyChangeListener() {
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
        boolean readonly = RuntimeLogic.getInstance().getDataSource().isReadOnly();
        setBaseEnabled(infocus && !readonly);
    }

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        StarBean star = ChViewVisualizationLogic.mPreferences.getFocus();
        if (star == null)
            return null;
        DlgStarEdit dlg = new DlgStarEdit(window.getShell());
        dlg.setStar(star);
        if (dlg.open() != Dialog.OK)
            return null;
        star = dlg.getStar();
        ChViewVisualizationLogic.updateStar(star);
        return null;
    }

}
