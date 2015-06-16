package jo.d2k.admin.rcp.viz.chview.handlers;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class HandlerSelectAll extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        ChViewVisualizationLogic.addToSelection(ChViewVisualizationLogic.mPreferences.getStars());
        return null;
    }

}
