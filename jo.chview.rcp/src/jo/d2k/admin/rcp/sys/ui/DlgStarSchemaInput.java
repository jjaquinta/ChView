package jo.d2k.admin.rcp.sys.ui;

import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarSchemaBean;
import jo.d2k.data.logic.StarColumnLogic;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DlgStarSchemaInput extends GenericDialog
{
    private String          mPreExistingTitle;
    private StarSchemaBean  mSchema;
    
    private StarSchemaPanel mClient;
    
    public DlgStarSchemaInput(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Star Schema");
        mClient = new StarSchemaPanel(parent, SWT.NULL);
        GridUtils.setLayoutData(mClient, "fill=hv");
        mClient.setSchema(mSchema);
        mClient.getTitle().addModifyListener(new ModifyListener() {            
            @Override
            public void modifyText(ModifyEvent e)
            {
                updateEnablement();
            }
        });
        return mClient;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        super.createButtonsForButtonBar(parent);
        updateEnablement();
    }
    
    private void updateEnablement()
    {
        Button ok = getButton(OK);
        String txt = mClient.getTitle().getText();
        if (StringUtils.isTrivial(txt))
        {
            ok.setEnabled(false);
            ok.setToolTipText("You must specify a name for this field");
            return;
        }
        if (!txt.equals(mPreExistingTitle))
        {
            for (StarColumn col : StarColumnLogic.getPotentialColumns())
                if (txt.equals(col.getTitle()))
                {
                    ok.setEnabled(false);
                    ok.setToolTipText("A field with this title already exists");
                    return;
                }
        }
        ok.setEnabled(true);
        ok.setToolTipText("");
    }
    
    @Override
    protected void okPressed()
    {
        mSchema = mClient.getSchema();        
        super.okPressed();
    }

    public StarSchemaBean getSchema()
    {
        return mSchema;
    }

    public void setSchema(StarSchemaBean schema)
    {
        mSchema = schema;
        mPreExistingTitle = mSchema.getTitle();
    }
}
