package jo.util.ui.viewers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class GenericTreeContentProvider implements ITreeContentProvider
{
    protected Viewer                mViewer;
    protected Object                mInput;
    protected Object                mRoot;
    protected Map<Object, Object[]> mHierarchy;
    protected Map<Object, Object>   mParents;
    
    public GenericTreeContentProvider()
    {
        mHierarchy = new HashMap<Object, Object[]>();
        mParents = new HashMap<Object, Object>();
    }

    @Override
    public void dispose()
    {
        mHierarchy = null;
        mParents = null;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        mViewer = viewer;
        mInput = newInput;
    }
    
    protected Object[] doGetChildren(Object parent)
    {
        return new Object[0];
    }

    @Override
    public Object[] getChildren(Object parent)
    {
        Object[] children;
        if (!mHierarchy.containsKey(parent))
        {
            children = doGetChildren(parent);
            mHierarchy.put(parent, children);
        }
        else
            children = mHierarchy.get(parent);
        for (Object c : children)
            mParents.put(c, parent);
        return children;
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
