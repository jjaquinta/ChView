package jo.d2k.admin.rcp.viz.sky.actions;

import jo.d2k.admin.rcp.viz.sky.ChViewSkyPanel;

import org.eclipse.jface.action.Action;

public class ActionShowConstellations extends Action
{
    private ChViewSkyPanel  mSky;
    
    public ActionShowConstellations(ChViewSkyPanel  sky)
    {
        mSky = sky;
        //setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_links"));
        setToolTipText("Show Constellations on hemispheres");
        setText("Constellations");
        setChecked(mSky.isDrawConstellations());
    }
    
    @Override
    public void run()
    {
        mSky.setDrawConstellations(!mSky.isDrawConstellations());
        setChecked(mSky.isDrawConstellations());
    }
}
