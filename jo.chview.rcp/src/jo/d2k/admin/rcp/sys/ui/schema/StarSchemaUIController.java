package jo.d2k.admin.rcp.sys.ui.schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.chview.rcp.logic.StarSchemaControllerLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarSchemaBean;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.data.logic.StarSchemaLogic;
import jo.util.beans.PropertyChangeInvoker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class StarSchemaUIController
{
    private Composite               mParent;
    private StarBean                mStar;
    private List<StarSchemaBean>    mSchemas;
    private boolean                 mReadOnly;
    private Map<StarSchemaBean,Control> mControls;
    private Set<Control>            mAllControls;

    public StarSchemaUIController(Composite parent, int style)
    {   // parent is assumed to have a grid layout with 7 columns in it
        mParent = parent;
        mReadOnly = ((style&SWT.READ_ONLY) != 0);
        mAllControls = new HashSet<>();
        addUI();
        StarSchemaLogic.addUIPropertyChangeListener("schemas", new PropertyChangeInvoker(this, "doDataSourceChange", mParent));
    }

    public void addUI()
    {
        mSchemas = StarSchemaLogic.getSchemas();
        mControls = new HashMap<>();
        Set<Control> existingControls = new HashSet<>();
        for (Control c : mParent.getChildren())
            existingControls.add(c);
        for (StarSchemaBean schema : mSchemas)
        {
            Control ctrl = StarSchemaControllerLogic.getController(schema).addUI(mParent, schema, mReadOnly);
            mControls.put(schema, ctrl);
        }
        for (Control c : mParent.getChildren())
            if (!existingControls.contains(c))
                mAllControls.add(c);
    }
    
    public void removeUI()
    {
        for (Control c : mAllControls)
            c.dispose();
    }

    public StarBean getStar()
    {
        getMetadata(mStar.getMetadata());            
        return mStar;
    }
    public StarBean getMetadata(Map<String,String> metadata)
    {
        for (StarSchemaBean schema : mSchemas)
        {
            Control ctrl = mControls.get(schema);
            StarSchemaControllerLogic.getController(schema).storeToMetadata(ctrl, schema, metadata);            
        }
        return mStar;
    }

    public void setStar(StarBean star)
    {
        mStar = star;
        StarLogic.getMetadata(mStar); // load metadata
        setMetadata(star.getMetadata());
    }
    public void setMetadata(Map<String,String> metadata)
    {
        for (StarSchemaBean schema : mSchemas)
        {
            Control ctrl = mControls.get(schema);
            StarSchemaControllerLogic.getController(schema).loadFromMetadata(ctrl, schema, metadata);            
        }
    }

    public void doDataSourceChange()
    {
        removeUI();
        addUI();
        mParent.layout();
    }
    
    public static String schemaToID(StarSchemaBean schema)
    {
        return schema.getMetadataID();
    }
}
