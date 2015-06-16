package chuck.terran.admin.ui;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.logic.StarRouteLogic;
import jo.util.ui.ctrl.CtrlUtils;
import jo.util.ui.viewers.GenericTableViewer2;
import jo.util.utils.FormatUtils;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

public class RoutesViewer extends GenericTableViewer2<StarRouteBean>
{
    public RoutesViewer(Composite parent, int style)
    {
        setContent(new ArrayContentProvider());
        setLabels(new RouteLabelProvider());
        init(parent, style);
    }
    
    protected boolean addColumns()
    {
        addSortedColumn("From", 125);
        addSortedColumn("To", 125);
        addSortedColumn("Type", 50);
        addSortedColumn("Distance", 75);
        return true;
    }
    
    class RouteLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public Image getColumnImage(Object element, int columnIndex)
        {
            return null;
        }

        public String getColumnText(Object element, int columnIndex)
        {
            StarRouteBean route = (StarRouteBean)element;
            StarRouteLogic.getReferences(route);
            switch (columnIndex)
            {
                case 0:
                    if (route.getStar1Ref() == null)
                        return "???";
                    else
                        return ChViewRenderLogic.getStarName(route.getStar1Ref());
                case 1:
                    if (route.getStar2Ref() == null)
                        return "???";
                    else
                        return ChViewRenderLogic.getStarName(route.getStar2Ref());
                case 2:
                    return String.valueOf(route.getType() + 1);
                case 3:
                    return FormatUtils.formatDouble(route.getDistance(), 1);
            }
            return null;
        }
        
    }

    // p in screen coordinates
    public int[] getSelectedCell(Point p)
    {
        int[] rc = new int[2];
        rc[0] = getViewer().getTable().getSelectionIndex();
        Point l = CtrlUtils.getActualLocation(getViewer().getTable());
        p.x -= l.x;
        p.y -= l.y;
        int x = p.x;
        TableColumn[] cols = getViewer().getTable().getColumns();
        for (rc[1] = 0; rc[1] < cols.length; rc[1]++)
        {
            if (x < cols[rc[1]].getWidth())
                break;
            x -= cols[rc[1]].getWidth();
        }
        return rc;
    }
}
