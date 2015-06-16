package jo.d2k.admin.rcp.sys.ui.schema;

import java.util.Map;

import jo.d2k.data.data.StarSchemaBean;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class TextSchemaController implements ISchemaController
{

    @Override
    public Control addUI(Composite parent, StarSchemaBean schema,
            boolean readOnly)
    {
        GridUtils.makeLabel(parent, schema.getTitle()+":", "align=nw");
        Text ctrl;
        final int width = IntegerUtils.parseInt(schema.getSubType()[0]);
        int style = readOnly ? SWT.READ_ONLY : SWT.NULL;
        if (width > 40)
            ctrl = GridUtils.makeText(parent, style|SWT.MULTI|SWT.WRAP|SWT.H_SCROLL|SWT.V_SCROLL, "6x1 fill=hv");
        else
            ctrl = GridUtils.makeText(parent, style, "6x1 fill=h");
        if (!readOnly && (width > 0))
            ctrl.addVerifyListener(new VerifyListener() {                
                @Override
                public void verifyText(VerifyEvent ev)
                {
                    if ((ev.character == '\b') || (ev.character == 127))
                        return;
                    Text txt = (Text)ev.widget;
                    if (txt.getText().length() + ev.text.length() > width)
                    {
                        ev.doit = false;
                    }
                }
            });
        return ctrl;
    }

    @Override
    public void loadFromMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        String val = metadata.get(StarSchemaUIController.schemaToID(schema));
        ControlUtils.setText(ctrl, val);
    }

    @Override
    public void storeToMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        String val = ControlUtils.getText(ctrl);
        if (StringUtils.isTrivial(val))
            metadata.remove(StarSchemaUIController.schemaToID(schema));
        else
            metadata.put(StarSchemaUIController.schemaToID(schema), val);
    }

    @Override
    public String getName()
    {
        return "Text";
    }

    @Override
    public void addDefUI(Composite parent)
    {
        parent.setLayout(new GridLayout(2, false));
        GridUtils.makeLabel(parent, "Max Size:", "");
        Text textMaxSize = GridUtils.makeText(parent, SWT.NULL, "fill=h");
        parent.setData("textMaxSize", textMaxSize);
    }

    @Override
    public void loadFromDefPanel(StarSchemaBean schema, Composite parent)
    {
        Text textMaxSize = (Text)parent.getData("textMaxSize");
        schema.setSubType(new String[] { String.valueOf(IntegerUtils.parseInt(textMaxSize.getText())) });
    }

    @Override
    public void storeToDefPanel(StarSchemaBean schema, Composite parent)
    {
        Text textMaxSize = (Text)parent.getData("textMaxSize");
        if (schema.getSubType().length > 0)
            textMaxSize.setText(String.valueOf(IntegerUtils.parseInt(schema.getSubType()[0])));        
    }

}
