package chuck.terran.admin.ui.prefs;

import java.util.HashMap;
import java.util.Map;

import jo.d2k.data.logic.MetadataLogic;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DatabaseInfoPrefs extends PreferencePage implements IWorkbenchPreferencePage
{
    private static final Map<String,String> mDefaults = new HashMap<String, String>();
    
    private Text    mPassword;
    private Text    mContact;
    private Text    mCopyright;
    private Text    mNotes;
    
    @Override
    protected Control createContents(Composite parent)
    {
        boolean ro = RuntimeLogic.getInstance().getDataSource().isReadOnly();
        Composite client = new Composite(parent, SWT.NULL);
        client.setLayout(new GridLayout(2, false));
        if (ro)
            GridUtils.makeLabel(client, "Settings may not be changed in read-only database.", "2x1 fill=h");
        GridUtils.makeLabel(client, "Password:", "");
        mPassword = GridUtils.makeText(client, SWT.PASSWORD, "fill=h");
        GridUtils.makeLabel(client, "Contact:", "");
        mContact = GridUtils.makeText(client, SWT.NULL, "fill=h");
        GridUtils.makeLabel(client, "Copyright:", "");
        mCopyright = GridUtils.makeText(client, SWT.NULL, "fill=h");
        GridUtils.makeLabel(client, "Notes:", "2x1 fill=h");
        mNotes = GridUtils.makeText(client, SWT.MULTI|SWT.WRAP|SWT.H_SCROLL|SWT.V_SCROLL, "2x1 fill=hv");

        ControlUtils.setEnabled(client, !ro);

        load(MetadataLogic.getAsMap("db.info", -1));
        return client;
    }

    @Override
    public boolean performOk()
    {
        store();
        return true;
    }
    
    @Override
    protected void performApply()
    {
        store();
        load(MetadataLogic.getAsMap("db.info", -1));
    }
    
    @Override
    protected void performDefaults()
    {
        load(mDefaults);
    }

    @Override
    public void init(IWorkbench wb)
    {
    }

    private void load(Map<String,String> data)
    {
        ControlUtils.setText(mPassword, data.get("password"));
        ControlUtils.setText(mContact, data.get("contact"));
        ControlUtils.setText(mCopyright, data.get("copyright"));
        ControlUtils.setText(mNotes, data.get("notes"));
    }

    private void store()
    {
        Map<String,String> data = MetadataLogic.getAsMap("db.info", -1);
        data.put("password", mPassword.getText());
        data.put("contact", mContact.getText());
        data.put("copyright", mCopyright.getText());
        data.put("notes", mNotes.getText());
        MetadataLogic.setAsMap("db.info", -1, data);
    }
}
