package jo.d2k.admin.rcp.viz.chview;

import jo.util.geom3d.Point3D;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.FormatUtils;
import jo.util.utils.obj.DoubleUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DlgGoto extends GenericDialog
{
    private Point3D mCenter;
    private double  mRadius;
    
    private Text    mX;
    private Text    mY;
    private Text    mZ;
    private Text    mCtrlRadius;
    
    public DlgGoto(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Change Viewpoint");
        Composite client = new Composite(parent, SWT.NULL);
        GridUtils.setLayoutData(client, "fill=hv");
        client.setLayout(new GridLayout(2, false));
        
        GridUtils.makeLabel(client, "Enter the new location to center the view on:", "2x1");
        GridUtils.makeLabel(client, "X", "");
        mX = GridUtils.makeText(client, FormatUtils.formatDouble(mCenter.x, 2), "fill=h");
        GridUtils.makeLabel(client, "Y", "");
        mY = GridUtils.makeText(client, FormatUtils.formatDouble(mCenter.y, 2), "fill=h");
        GridUtils.makeLabel(client, "Z", "");
        mZ = GridUtils.makeText(client, FormatUtils.formatDouble(mCenter.z, 2), "fill=h");
        GridUtils.makeLabel(client, "Radius", "");
        mCtrlRadius = GridUtils.makeText(client, FormatUtils.formatDouble(mRadius, 2), "fill=h");
        
        return mX;
    }
    
    @Override
    protected void okPressed()
    {
        mCenter.x = DoubleUtils.parseDouble(mX.getText());
        mCenter.y = DoubleUtils.parseDouble(mY.getText());
        mCenter.z = DoubleUtils.parseDouble(mZ.getText());
        mRadius = DoubleUtils.parseDouble(mCtrlRadius.getText());
        super.okPressed();
    }

    public Point3D getCenter()
    {
        return mCenter;
    }

    public void setCenter(Point3D center)
    {
        mCenter = center;
    }

    public double getRadius()
    {
        return mRadius;
    }

    public void setRadius(double radius)
    {
        mRadius = radius;
    }
}
