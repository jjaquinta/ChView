package jo.d2k.admin.rcp.sys.ui.schema;

import jo.d2k.data.data.StarSchemaBean;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.DoubleUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class DoubleSchemaController extends TextSchemaController implements VerifyListener
{
    @Override
    public Control addUI(Composite parent, StarSchemaBean schema,
            boolean readOnly)
    {
        Text ctrl = (Text)super.addUI(parent, schema, readOnly);
        ctrl.addVerifyListener(this);
        return ctrl;
    }

    @Override
    public void verifyText(VerifyEvent ev)
    {        
        try
        {
            if (ev.text.length() > 0)
                Double.parseDouble(ev.text);
            ev.doit = true;
        }
        catch (NumberFormatException e)
        {
            ev.doit = false;
        }
    }

    @Override
    public String getName()
    {
        return "Floating Point";
    }

    @Override
    public void addDefUI(Composite parent)
    {
        parent.setLayout(new GridLayout(2, false));
        GridUtils.makeLabel(parent, "Min Value:", "");
        Text doubleMinValue = GridUtils.makeText(parent, SWT.NULL, "fill=h");
        parent.setData("doubleMinValue", doubleMinValue);
        GridUtils.makeLabel(parent, "Max Value:", "");
        Text doubleMaxValue = GridUtils.makeText(parent, SWT.NULL, "fill=h");
        parent.setData("doubleMaxValue", doubleMaxValue);
    }

    @Override
    public void loadFromDefPanel(StarSchemaBean schema, Composite parent)
    {
        Text doubleMinValue = (Text)parent.getData("doubleMinValue");
        Text doubleMaxValue = (Text)parent.getData("doubleMaxValue");
        schema.setSubType(new String[] {
                String.valueOf(DoubleUtils.parseDouble(doubleMinValue.getText()))
                ,
                String.valueOf(DoubleUtils.parseDouble(doubleMaxValue.getText()))
        });
    }

    @Override
    public void storeToDefPanel(StarSchemaBean schema, Composite parent)
    {
        Text doubleMinValue = (Text)parent.getData("doubleMinValue");
        Text doubleMaxValue = (Text)parent.getData("doubleMaxValue");
        doubleMinValue.setText(String.valueOf(DoubleUtils.parseDouble(schema.getSubType()[0])));
        doubleMaxValue.setText(String.valueOf(DoubleUtils.parseDouble(schema.getSubType()[1])));
    }

}
