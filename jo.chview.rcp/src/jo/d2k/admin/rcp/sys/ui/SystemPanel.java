package jo.d2k.admin.rcp.sys.ui;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.stargen.SystemLogic;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.util.logic.ThreadLogic;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class SystemPanel extends Composite
{
    private StarBean                mStar;
    private List<SunBean>           mSystem;

    private TreeViewer  mClient;
    
    public SystemPanel(Composite parent, int style)
    {
        super(parent, style);
        mSystem = new ArrayList<SunBean>();
        setLayout(new FillLayout());
        mClient = new TreeViewer(this, SWT.FULL_SELECTION);
        mClient.setContentProvider(new SystemContentProvider());
        mClient.setLabelProvider(new SystemLabelProvider());
        mClient.setInput(mSystem);
    }
    
    public Viewer getViewer()
    {
        return mClient;
    }

    public StarBean getStar()
    {
        return mStar;
    }

    public void setStar(StarBean star)
    {
        if ((mSystem.size() > 0) && (mSystem.get(0).getStar() == star))
            return;
        mStar = star;
        if (mStar != null)
            ThreadLogic.runMethodOnBackgroundThread(this, "doGetSecondaryData");
    }

    public void doGetSecondaryData()
    {
        mSystem.clear();
        addSystems(mStar.getPrimary());
        ThreadLogic.runMethodOnUIThread(this, "doDisplaySecondaryData");
    }

    private void addSystems(StarBean star)
    {
        mSystem.add(SystemLogic.generateSystem(star));
        for (StarBean child : star.getChildren())
            addSystems(child);
    }
    
    public void doDisplaySecondaryData()
    {
        mClient.setInput(mSystem);
        mClient.expandAll();
    }
}
