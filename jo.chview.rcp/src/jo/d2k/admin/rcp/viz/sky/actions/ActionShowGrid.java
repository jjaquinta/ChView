package jo.d2k.admin.rcp.viz.sky.actions;

import jo.d2k.admin.rcp.viz.sky.ChViewSkyPanel;

import org.eclipse.jface.action.Action;

public class ActionShowGrid extends Action
{
    private ChViewSkyPanel  mSky;
    
    public ActionShowGrid(ChViewSkyPanel  sky)
    {
        mSky = sky;
        //setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_links"));
        setToolTipText("Show grid on hemispheres");
        setText("Grid");
        setChecked(mSky.isDrawGrid());
    }
    
    @Override
    public void run()
    {
        mSky.setDrawGrid(!mSky.isDrawGrid());
        setChecked(mSky.isDrawGrid());
    }
}
