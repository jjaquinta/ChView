package jo.d2k.admin.rcp.viz.chview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.geom3d.Point3D;
import jo.util.utils.FormatUtils;

import org.eclipse.jface.action.StatusLineContributionItem;

public class ChViewStatus extends StatusLineContributionItem implements PropertyChangeListener
{
    public ChViewStatus()
    {
        super(ChViewStatus.class.getName(), 24);
        ChViewVisualizationLogic.mPreferences.addPropertyChangeListener(this);
        updateText();
    }
    
    private void updateText()
    {
        StringBuffer sb = new StringBuffer();
        Point3D c = ChViewVisualizationLogic.mPreferences.getCenter();
        sb.append(FormatUtils.formatDouble(c.x, 1)+","+FormatUtils.formatDouble(c.y, 1)+","+FormatUtils.formatDouble(c.z, 1)
                +" x "+FormatUtils.formatDouble(ChViewVisualizationLogic.mPreferences.getRadius(), 1));
        setText(sb.toString());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if ("center".equals(evt.getPropertyName()) || "radius".equals(evt.getPropertyName()))
            updateText();
    }
}
