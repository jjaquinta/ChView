package chuck.terran.admin.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.logic.StarColumnLogic;
import jo.util.beans.PropertyChangeInvoker;
import jo.util.ui.ctrl.CtrlUtils;
import jo.util.ui.viewers.GenericTableTreeViewer2;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class StarsViewer extends GenericTableTreeViewer2<StarBean>
{
    private List<StarColumn>    mColumns;
    
    public StarsViewer(Composite parent, int style)
    {
        mColumns = StarColumnLogic.getDefaultColumns();
        setContent(new StarsContentProvider());
        setLabels(new StarsLabelProvider(mColumns));
        init(parent, style);
        StarColumnLogic.addUIPropertyChangeListener("columns", new PropertyChangeInvoker(this, "resetColumns", getControl()));
    }
    
    public void resetColumns()
    {
        setColumns(StarColumnLogic.getDefaultColumns());
    }
    
    protected boolean addColumns()
    {
        for (StarColumn column : mColumns)
            addSortedColumn(column.getTitle(), column.getWidth(), column.getSortBy(), SWT.UP, true);
        return true;
    }
    
    class StarsLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        private List<StarColumn>    mColumns;
        
        public StarsLabelProvider(List<StarColumn> columns)
        {
            mColumns = columns;
        }
        
        public Image getColumnImage(Object element, int columnIndex)
        {
            return null;
        }

        public String getColumnText(Object element, int columnIndex)
        {
            StarBean star = (StarBean)element;
            StarColumn col = mColumns.get(columnIndex);
            return StarColumnLogic.getText(ChViewVisualizationLogic.mPreferences, star, col);
        }

        public List<StarColumn> getColumns()
        {
            return mColumns;
        }

        public void setColumns(List<StarColumn> columns)
        {
            mColumns = columns;
        }
        
    }

    // p in screen coordinates
    public int[] getSelectedCell(Point p)
    {
        int[] rc = new int[2];
        TreeItem[] sel = getViewer().getTree().getSelection();
        if (sel.length == 0)
            rc[0] = -1;
        else
            rc[0] = getViewer().getTree().indexOf(sel[0]);
        Point l = CtrlUtils.getActualLocation(getViewer().getTree());
        p.x -= l.x;
        p.y -= l.y;
        int x = p.x;
        TreeColumn[] cols = getViewer().getTree().getColumns();
        for (rc[1] = 0; rc[1] < cols.length; rc[1]++)
        {
            if (x < cols[rc[1]].getWidth())
                break;
            x -= cols[rc[1]].getWidth();
        }
        return rc;
    }
    
    class StarsContentProvider implements ITreeContentProvider
    {
        private Map<StarBean,Object[]> mChildren;
        private Map<StarBean,StarBean> mParents;

        public StarsContentProvider()
        {
            mChildren = new HashMap<StarBean, Object[]>();
            mParents = new HashMap<>();
        }
        
        @Override
        public void dispose()
        {
            mChildren = null;
            mParents = null;
        }

        @Override
        public void inputChanged(Viewer viewer, Object inputOld, Object inputNew)
        {
            mChildren.clear();
            mParents.clear();
        }

        @Override
        public Object[] getChildren(Object parent)
        {
            if (parent instanceof Collection<?>)
                return ((Collection<?>)parent).toArray();
            if (!mChildren.containsKey(parent))
            {
                StarBean star = (StarBean)parent;
                List<StarBean> children = star.getChildren();
                mChildren.put(star, children.toArray());
                for (StarBean child : children)
                    mParents.put(child, star);
            }
            return mChildren.get(parent);
        }

        @Override
        public Object[] getElements(Object parent)
        {
            return getChildren(parent);
        }

        @Override
        public Object getParent(Object child)
        {
            return mParents.get(child);
        }

        @Override
        public boolean hasChildren(Object parent)
        {
            return getChildren(parent).length > 0;
        }
        
    }

    public List<StarColumn> getColumns()
    {
        return mColumns;
    }

    public void setColumns(List<StarColumn> columns)
    {
        mColumns = columns;
        ((StarsLabelProvider)getLabels()).setColumns(mColumns);
        for (TreeColumn col : getViewer().getTree().getColumns())
            col.dispose();
        addColumns();
        getViewer().refresh(true);
    }
}
