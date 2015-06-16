package jo.d2k.admin.rcp.sys.ui.schema;

import jo.d2k.data.data.StarSchemaBean;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.IntegerUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class IntegerSchemaController extends TextSchemaController implements VerifyListener
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
                Integer.parseInt(ev.text);
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
        return "Integer";
    }

    @Override
    public void addDefUI(Composite parent)
    {
        parent.setLayout(new GridLayout(2, false));
        GridUtils.makeLabel(parent, "Min Value:", "");
        Text integerMinValue = GridUtils.makeText(parent, SWT.NULL, "fill=h");
        parent.setData("integerMinValue", integerMinValue);
        GridUtils.makeLabel(parent, "Max Value:", "");
        Text integerMaxValue = GridUtils.makeText(parent, SWT.NULL, "fill=h");
        parent.setData("integerMaxValue", integerMaxValue);
    }

    @Override
    public void loadFromDefPanel(StarSchemaBean schema, Composite parent)
    {
        Text integerMinValue = (Text)parent.getData("integerMinValue");
        Text integerMaxValue = (Text)parent.getData("integerMaxValue");
        schema.setSubType(new String[] { 
                String.valueOf(IntegerUtils.parseInt(integerMinValue.getText()))
                ,
                String.valueOf(IntegerUtils.parseInt(integerMaxValue.getText()))
                });
    }

    @Override
    public void storeToDefPanel(StarSchemaBean schema, Composite parent)
    {
        Text integerMinValue = (Text)parent.getData("integerMinValue");
        Text integerMaxValue = (Text)parent.getData("integerMaxValue");
        integerMinValue.setText(String.valueOf(IntegerUtils.parseInt(schema.getSubType()[0])));
        integerMaxValue.setText(String.valueOf(IntegerUtils.parseInt(schema.getSubType()[1])));
    }

}
