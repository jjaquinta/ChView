package jo.d2k.admin.rcp.sys.ui.schema;

import java.util.Map;

import jo.d2k.data.data.StarSchemaBean;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class OrSchemaController implements ISchemaController
{
    @Override
    public Control addUI(Composite parent, StarSchemaBean schema,
            boolean readOnly)
    {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public void loadFromMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public void storeToMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public String getName()
    {
        return "And";
    }

    @Override
    public void addDefUI(Composite parent)
    {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public void loadFromDefPanel(StarSchemaBean schema, Composite parent)
    {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public void storeToDefPanel(StarSchemaBean schema, Composite parent)
    {
        throw new IllegalStateException("Not supported");
    }

}
