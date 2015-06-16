package jo.d2k.admin.rcp.sys.viz.twod.handlers;

import java.io.File;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.logic.TwoDRenderLogic;
import jo.d2k.admin.rcp.sys.viz.twod.ui.DlgSaveImage;
import jo.d2k.admin.rcp.sys.viz.twod.ui.TwoDView;
import jo.util.ui.act.GenericAction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class HandlerSaveImage extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        DlgSaveImage dlg = new DlgSaveImage(window.getShell());
        if (dlg.open() != Dialog.OK)
            return null;
        String saveFile = null;
        if (dlg.getFormat() == 0)
            saveFile = GenericAction.getSaveFile(HandlerSaveImage.class, null, "PNG File", "*.png");
        else if (dlg.getFormat() == 1)
            saveFile = GenericAction.getSaveFile(HandlerSaveImage.class, null, "SVG File", "*.svg");
        else if (dlg.getFormat() == 2)
            saveFile = GenericAction.getSaveFile(HandlerSaveImage.class, null, "HTML File", "*.htm");
        if (saveFile == null)
            return null;
        IWorkbenchPage page = window.getActivePage();
        IWorkbenchPart part = page.getActivePart();
        TwoDView view = (TwoDView)part;
        TwoDDisplay disp = view.getTwoDDisplay();
        TwoDRenderLogic.saveFile(disp, dlg.getWidth(), dlg.getHeight(), dlg.getFormat(), new File(saveFile));
        return null;
    }

}
