package jo.d2k.admin.rcp.viz.sky.actions;

import jo.d2k.admin.rcp.viz.sky.ChViewSkyPanel;

import org.eclipse.jface.action.Action;

public class ActionShowBorder extends Action
{
    private ChViewSkyPanel  mSky;
    
    public ActionShowBorder(ChViewSkyPanel  sky)
    {
        mSky = sky;
        //setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_links"));
        setToolTipText("Show Border around hemisphere");
        setText("Border");
        setChecked(mSky.isDrawBorder());
    }
    
    @Override
    public void run()
    {
        mSky.setDrawBorder(!mSky.isDrawBorder());
        setChecked(mSky.isDrawBorder());
    }
}
