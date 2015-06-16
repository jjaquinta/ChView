package chuck.terran.admin.ui;

import jo.d2k.data.data.DeletionBean;
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

public class DeletionsViewer extends GenericTableViewer2<DeletionBean>
{
    public DeletionsViewer(Composite parent, int style)
    {
        setContent(new ArrayContentProvider());
        setLabels(new DeletionLabelProvider());
        init(parent, style);
    }
    
    protected boolean addColumns()
    {
        addSortedColumn("Name", 125);
        addSortedColumn("Location", 125);
        return true;
    }
    
    class DeletionLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public Image getColumnImage(Object element, int columnIndex)
        {
            return null;
        }

        public String getColumnText(Object element, int columnIndex)
        {
            DeletionBean bean = (DeletionBean)element;
            switch (columnIndex)
            {
                case 0:
                    return bean.getName();
                case 1:
                    return FormatUtils.formatDouble(bean.getX(), 1)+","+
                            FormatUtils.formatDouble(bean.getY(), 1)+","+
                            FormatUtils.formatDouble(bean.getZ(), 1);
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
