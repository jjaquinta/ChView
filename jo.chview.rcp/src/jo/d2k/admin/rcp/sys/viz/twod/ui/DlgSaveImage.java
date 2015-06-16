package jo.d2k.admin.rcp.sys.viz.twod.ui;

import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.IntegerUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DlgSaveImage extends GenericDialog
{
    private int     mWidth = 1024;
    private int     mHeight = 768;
    private int     mFormat = 0;
    
    private Text    mCtrlWidth;
    private Text    mCtrlHeight;
    private Combo   mCtrlFormat;
    
    public DlgSaveImage(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Save Image");
        Composite client = new Composite(parent, SWT.NULL);
        GridUtils.setLayoutData(client, "fill=hv");
        client.setLayout(new GridLayout(2, false));
        
        GridUtils.makeLabel(client, "Width", "");
        mCtrlWidth = GridUtils.makeText(client, String.valueOf(mWidth), "fill=h");
        GridUtils.makeLabel(client, "Height", "");
        mCtrlHeight = GridUtils.makeText(client, String.valueOf(mHeight), "fill=h");
        GridUtils.makeLabel(client, "Format", "");
        mCtrlFormat = GridUtils.makeCombo(client, new String[]{ "PNG", "SVG", "HTML" }, "fill=h");
        mCtrlFormat.select(mFormat);
        
        return mCtrlWidth;
    }
    
    @Override
    protected void okPressed()
    {
        mWidth = IntegerUtils.parseInt(mCtrlWidth.getText());
        mHeight = IntegerUtils.parseInt(mCtrlHeight.getText());
        mFormat = mCtrlFormat.getSelectionIndex();
        super.okPressed();
    }

    public int getWidth()
    {
        return mWidth;
    }

    public void setWidth(int width)
    {
        mWidth = width;
    }

    public int getHeight()
    {
        return mHeight;
    }

    public void setHeight(int height)
    {
        mHeight = height;
    }

    public int getFormat()
    {
        return mFormat;
    }

    public void setFormat(int format)
    {
        mFormat = format;
    }
}
