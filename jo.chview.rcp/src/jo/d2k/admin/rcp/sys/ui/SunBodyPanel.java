package jo.d2k.admin.rcp.sys.ui;

import jo.d2k.data.logic.ConvLogic;
import jo.d2k.data.logic.UnitUtils;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SunBodyPanel extends BodyPanel
{
    private Text    mLuminosity;
    private Text    mLife;
    private Text    mAge;
    private Text    mRecosphere;

    public SunBodyPanel(Composite parent, int style)
    {
        super(parent, style);
    }
    
    @Override
    protected void addSpecifics()
    {
        GridUtils.makeLabel(this, "Luminosity:", "");
        mLuminosity = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        GridUtils.makeLabel(this, "Age:", "");
        mAge = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        GridUtils.makeLabel(this, "", "");
        mLife = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        GridUtils.makeLabel(this, "Ecosphere:", "").setToolTipText("Habitable Ecosphere Radius");
        mRecosphere = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        mRecosphere.setToolTipText("Habitable Ecosphere Radius");
    }
    
    @Override
    public void setBody(BodyBean body)
    {
        super.setBody(body);
        SunBean sun = (SunBean)mBody;
        mLuminosity.setText(UnitUtils.formatLuminosity(sun.getLuminosity()));
        mAge.setText(UnitUtils.formatTimeSpan(sun.getAge()));
        mLife.setText(UnitUtils.formatTimeSpan(sun.getLife() - sun.getAge())+" left on main sequence");
        mRecosphere.setText(UnitUtils.formatRadius(sun.getREcosphere()*ConvLogic.KM_PER_AU)
                + " ("+UnitUtils.formatRadius(sun.getMinREcosphere()*ConvLogic.KM_PER_AU)
                +" - "+UnitUtils.formatRadius(sun.getMaxREcosphere()*ConvLogic.KM_PER_AU)+")");
    }
}
