package jo.d2k.admin.rcp.sys.ui;

import java.util.List;

import jo.d2k.data.logic.ConvLogic;
import jo.d2k.data.logic.UnitUtils;
import jo.d2k.data.logic.stargen.PlanetExtraLogic;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SolidBodyPanel extends BodyPanel
{
    private Text    mType;
    private Text    mOrbitRadius;
    private Text    mGravity;
    private Text    mPressure;
    private Text    mTemperature;
    private Text    mDensity;
    private Text    mEccentricity;
    
    public SolidBodyPanel(Composite parent, int style)
    {
        super(parent, style);
    }
    
    
    @Override
    protected void addSpecifics()
    {
        GridUtils.makeLabel(this, "Type:", "align=nw");
        mType = GridUtils.makeText(this, SWT.READ_ONLY|SWT.MULTI|SWT.WRAP, "2x1 fill=h");
        GridUtils.makeLabel(this, "Orbit:", "align=nw");
        mOrbitRadius = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        mOrbitRadius.setToolTipText("distance from primary star");
        GridUtils.makeLabel(this, "Gravity:", "align=nw");
        mGravity = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        mGravity.setToolTipText("surface gravity");
        GridUtils.makeLabel(this, "Pressure:", "align=nw");
        mPressure = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        mPressure.setToolTipText("surface pressure");
        GridUtils.makeLabel(this, "Temperature:", "align=nw");
        mTemperature = GridUtils.makeText(this, SWT.READ_ONLY|SWT.MULTI|SWT.WRAP, "2x1 fill=h");
        mTemperature.setToolTipText("surface temperature");
        GridUtils.makeLabel(this, "Density:", "align=nw");
        mDensity = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        GridUtils.makeLabel(this, "Eccentricity:", "align=nw");
        mEccentricity = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        mEccentricity.setToolTipText("eccentricity of orbit");
    }
    
    @Override
    public void setBody(BodyBean body)
    {
        super.setBody(body);
        SolidBodyBean planet = (SolidBodyBean)mBody;
        List<String> types = PlanetExtraLogic.getTags(planet);
        String temp = UnitUtils.formatTemperature(planet.getSurfTemp());
        temp += " (min: "+UnitUtils.formatTemperature(planet.getSurfTemp());
        temp += ", max: "+UnitUtils.formatTemperature(planet.getSurfTemp());
        if (Math.abs(planet.getHighTemp() - planet.getMaxTemp()) > 10
                || Math.abs(planet.getLowTemp() - planet.getMinTemp()) > 10)
        {
            temp += ", night: "+UnitUtils.formatTemperature(planet.getLowTemp());
            temp += ", day: "+UnitUtils.formatTemperature(planet.getHighTemp());
        }
        temp += ")";
        
        mType.setText(StringUtils.listize(types, ", "));
        mOrbitRadius.setText(UnitUtils.formatRadius(planet.getA()*ConvLogic.KM_PER_AU));
        mGravity.setText(UnitUtils.formatGravity(planet.getSurfGrav()));
        mPressure.setText(UnitUtils.formatPressure(planet.getSurfPressure()));
        mTemperature.setText(temp);
        mDensity.setText(UnitUtils.formatDensity(planet.getDensity()));
        mEccentricity.setText(UnitUtils.formatDouble(planet.getE(), 2));
    }
}
