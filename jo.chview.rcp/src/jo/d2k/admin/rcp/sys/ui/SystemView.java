package jo.d2k.admin.rcp.sys.ui;

import jo.d2k.data.data.StarBean;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class SystemView extends ViewPart
{
    public static final String     ID = SystemView.class.getName();

    private Object      mSelected;
    
    private SystemPanel mSystemPanel;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent)
    {
        mSystemPanel = new SystemPanel(parent, SWT.NULL);
        getViewSite().setSelectionProvider(mSystemPanel.getViewer());
        getViewSite().getPage().addSelectionListener(new ISelectionListener() {            
            @Override
            public void selectionChanged(IWorkbenchPart part, ISelection sel)
            {
                if (sel.isEmpty())
                    setSelected(null);
                else
                    setSelected(((IStructuredSelection)sel).getFirstElement());
            }
        });
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        mSystemPanel.setFocus();
    }

    public Object getSelected()
    {
        return mSelected;
    }

    public void setSelected(Object selected)
    {
        mSelected = selected;
        if (mSelected == null)
            mSystemPanel.setStar(null);
        else if (mSelected instanceof StarBean)
            mSystemPanel.setStar((StarBean)mSelected);
    }
}