package jo.d2k.admin.rcp.sys.ui;

import java.util.List;

import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.util.ui.viewers.GenericTreeContentProvider;

import org.eclipse.jface.viewers.Viewer;

public class SystemContentProvider extends GenericTreeContentProvider
{
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        super.inputChanged(viewer, oldInput, newInput);
        mHierarchy.clear();
        if (newInput == null)
            return;
        @SuppressWarnings("unchecked")
        List<SunBean> suns = (List<SunBean>)newInput;
        mHierarchy.put(newInput, suns.toArray());
        for (SunBean sun : suns)
        {
            BodyBean[] planets = sun.getChildren().toArray(new BodyBean[0]);
            mHierarchy.put(sun, planets);
            for (BodyBean planet : planets)
                if (planet.getChildren() != null)
                    mHierarchy.put(planet, planet.getChildren().toArray());
        }
    }
}
