package jo.d2k.admin.rcp.sys.ui.schema;

import java.util.Map;

import jo.d2k.data.data.StarSchemaBean;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface ISchemaController
{
    public String getName();
    public Control addUI(Composite parent, StarSchemaBean schema, boolean readOnly);
    public void loadFromMetadata(Control ctrl, StarSchemaBean schema, Map<String, String> metadata);
    public void storeToMetadata(Control ctrl, StarSchemaBean schema, Map<String, String> metadata);
    public void addDefUI(Composite parent);
    public void loadFromDefPanel(StarSchemaBean schema, Composite parent);
    public void storeToDefPanel(StarSchemaBean schema, Composite parent);
}
