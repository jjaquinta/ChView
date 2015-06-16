package jo.d2k.admin.rcp.viz.chview.handlers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

public class HandlerViewToggle extends AbstractHandler implements IElementUpdater
{
    private UIElement mElement;
    protected String  mViewID;
    protected String  mViewSecondaryID;
    private Set<IWorkbenchPage> mListening;
    
    public HandlerViewToggle()
    {
        mListening = new HashSet<IWorkbenchPage>();
    }

    private IViewReference findView(IWorkbenchWindow window)
    {
        IWorkbenchPage page = window.getActivePage();
        if (!mListening.contains(page))
        {
            mListening.add(page);
            page.addPartListener(new IPartListener() {                
                @Override
                public void partOpened(IWorkbenchPart part)
                {
                    updateElement();
                }
                
                @Override
                public void partDeactivated(IWorkbenchPart part)
                {
                    updateElement();
                }
                
                @Override
                public void partClosed(IWorkbenchPart part)
                {
                    updateElement();
                }
                
                @Override
                public void partBroughtToTop(IWorkbenchPart part)
                {
                }
                
                @Override
                public void partActivated(IWorkbenchPart part)
                {
                    updateElement();
                }
            });
        }
        for (IViewReference ref : page.getViewReferences())
            if (ref.getId().equals(mViewID))
            {
                if (mViewSecondaryID == null)
                        return ref;
                if ((mViewSecondaryID.length() == 0) && (ref.getSecondaryId() == null))
                    return ref;
                if (mViewSecondaryID.equals(ref.getSecondaryId()))
                    return ref;
            }
        return null;
    }
    
    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(ev);
        IWorkbenchPage page = window.getActivePage();
        IViewReference view = findView(window);
        if (view != null)
            page.hideView(view);
        else
            try
            {
                page.showView(mViewID);
            }
            catch (PartInitException e)
            {
                e.printStackTrace();
            }
        return null;
    }

    @Override
    public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters)
    {
        mElement = element;
        updateElement();
    }
    
    private void updateElement()
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IViewReference view = findView(window);
        mElement.setChecked(view != null);
    }
}
