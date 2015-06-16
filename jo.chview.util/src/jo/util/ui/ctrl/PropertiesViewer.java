package jo.util.ui.ctrl;

import java.util.Map;

import jo.util.ui.viewers.GenericTableViewer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class PropertiesViewer extends GenericTableViewer
{
    public PropertiesViewer(Composite parent, int style)
    {
        setContent(new PropertiesContentProvider());
        setLabels(new PropertiesLabelProvider());
        init(parent, style);
    }
    
    protected boolean addColumns()
    {
        addSortedColumn("Key", 125);
        addSortedColumn("Value", 150);
        return true;
    }
    
    class PropertiesContentProvider implements IStructuredContentProvider
    {
        public void dispose()
        {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }

        public Object[] getElements(Object inputElement)
        {
            if (inputElement instanceof Map)
            {
                Map<?,?> map = (Map<?,?>)inputElement;
                return map.entrySet().toArray();
            }
            return null;
        }        
    }
    
    class PropertiesLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public Image getColumnImage(Object element, int columnIndex)
        {
            return null;
        }

        public String getColumnText(Object element, int columnIndex)
        {
            if (element instanceof Map.Entry)
            {
                Map.Entry<?,?> entry = (Map.Entry<?,?>)element;
                if (columnIndex == 0)
                    return entry.getKey().toString();
                else if (columnIndex == 1)
                    return entry.getValue().toString();
            }
            return null;
        }
        
    }
}
