package jo.d2k.admin.rcp.viz.chview.handlers;

import java.util.HashMap;

import jo.d2k.admin.rcp.sys.ui.DlgStarEdit;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.IDataSource;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.geom3d.Point3D;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import chuck.terran.admin.handlers.HandlerBaseReadOnly;

public class HandlerNewStar extends HandlerBaseReadOnly
{
    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        final IDataSource src = RuntimeLogic.getInstance().getDataSource();
        DlgStarEdit dlg = new DlgStarEdit(window.getShell(), false){
            @Override
            protected void okPressed()
            {
                super.okPressed();
                if (src != RuntimeLogic.getInstance().getDataSource())
                {
                    MessageDialog.openError(getShell(), "Save Error", "Data source has changed. Cannot save!");
                    return;
                }
                StarBean star = getStar();
                ChViewVisualizationLogic.updateStar(star);
            }
        };
        StarBean star = new StarBean();
        Point3D p = new Point3D();
        int count = 0;
        StarBean f = ChViewVisualizationLogic.mPreferences.getFocus();
        if (f != null)
        {
            p.x += f.getX();
            p.y += f.getY();
            p.z += f.getZ();
            count++;
        }
        for (StarBean s : ChViewVisualizationLogic.mPreferences.getSelected())
        {
            if (s == f)
                continue;
            p.x += s.getX();
            p.y += s.getY();
            p.z += s.getZ();
            count++;
        }
        if (count > 0)
            p.scale(1f/count);
        star.setX(p.x);
        star.setY(p.y);
        star.setZ(p.z);
        star.setMetadata(new HashMap<String, String>());
        dlg.setStar(star);
        dlg.setBlockOnOpen(false);
        if (dlg.open() != Dialog.OK)
            return null;        
        return null;
    }

}
