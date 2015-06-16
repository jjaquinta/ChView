package jo.d2k.admin.rcp.sys.ui;

import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.data.data.StarBean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class StarSystemPanel extends Composite implements IToText
{
    private List<StarBean>    mStars;
    private boolean     mReadOnly;
    
    private TabFolder   mClient;
    
    public StarSystemPanel(Composite parent, int style)
    {
        super(parent, style);
        int editStyle = (style&SWT.READ_ONLY);
        mReadOnly = editStyle != 0;
        setLayout(new FillLayout());
        mClient = new TabFolder(this, SWT.TOP);
    }

    public List<StarBean> getStars()
    {
        return mStars;
    }

    public void setStars(List<StarBean> stars)
    {
        mStars = stars;
        // clear existing tabs
        while (mClient.getItemCount() > 0)
            mClient.getItem(0).dispose();
        // add one tab per star
        for (StarBean star : stars)
        {
            TabItem tab = new TabItem(mClient, SWT.NONE);
            tab.setText(ChViewRenderLogic.getStarName(star));
            tab.setImage(ChViewRenderLogic.getStarIcon(star));
            StarPanel tabClient = new StarPanel(mClient, mReadOnly ? SWT.READ_ONLY : SWT.NULL);
            tab.setControl(tabClient);
            tabClient.setStar(star);
        }
    }

    @Override
    public String toText()
    {
        TabItem tab = mClient.getItem(mClient.getSelectionIndex());
        StarPanel panel = (StarPanel)tab.getControl();
        return panel.toText();
    }

    @Override
    public String toHTML()
    {
        TabItem tab = mClient.getItem(mClient.getSelectionIndex());
        StarPanel panel = (StarPanel)tab.getControl();
        return panel.toHTML();
    }

    @Override
    public void doEdit()
    {
        TabItem tab = mClient.getItem(mClient.getSelectionIndex());
        StarPanel panel = (StarPanel)tab.getControl();
        panel.doEdit();
    }

    @Override
    public void doDel()
    {
        TabItem tab = mClient.getItem(mClient.getSelectionIndex());
        StarPanel panel = (StarPanel)tab.getControl();
        panel.doDel();
    }
}
