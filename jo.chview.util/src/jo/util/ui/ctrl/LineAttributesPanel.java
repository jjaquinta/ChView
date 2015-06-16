package jo.util.ui.ctrl;

import jo.util.ui.utils.GridUtils;
import jo.util.ui.utils.LineAttributesLogic;
import jo.util.utils.obj.FloatUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class LineAttributesPanel extends Composite
{
    private LineAttributes  mAttributes;
    
    private Combo           mCap;
    private Combo           mJoin;
    private Text            mMiterLimit;
    private Combo           mStyle;
    private Text            mWidth;
    private LineAttributesSwath    mSwath;

    public LineAttributesPanel(Composite parent, int style) 
    {
        super(parent, style);
        setLayout(new GridLayout(2, false));
        GridUtils.makeLabel(this, "Width:", "");
        mWidth = GridUtils.makeText(this, "", "fill=h");
        GridUtils.makeLabel(this, "Style:", "");
        mStyle = GridUtils.makeCombo(this, LineAttributesLogic.STYLE_LABELS, "fill=h");
        GridUtils.makeLabel(this, "Cap:", "");
        mCap = GridUtils.makeCombo(this, LineAttributesLogic.CAP_LABELS, "fill=h");
        GridUtils.makeLabel(this, "Join:", "");
        mJoin = GridUtils.makeCombo(this, LineAttributesLogic.JOIN_LABELS, "fill=h");
        GridUtils.makeLabel(this, "Miter Limit:", "");
        mMiterLimit = GridUtils.makeText(this, "", "fill=h");
        GridUtils.makeLabel(this, "", "");
        mSwath = new LineAttributesSwath(this, SWT.BORDER);
        GridUtils.setLayoutData(mSwath, "size=1x24 fill=h");
        
        SelectionListener sl = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateSwatch();
            }
        };
        mStyle.addSelectionListener(sl);
        mCap.addSelectionListener(sl);
        mJoin.addSelectionListener(sl);
        FocusListener fl = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                updateSwatch();
            }
        };
        mWidth.addFocusListener(fl);
        mMiterLimit.addFocusListener(fl);
    }

    public void updateSwatch()
    {
        mSwath.setAttributes(getAttributes());
    }
    
    public LineAttributes getAttributes()
    {
        if (mAttributes == null)
            mAttributes = new LineAttributes(1);
        mAttributes.cap = mCap.getSelectionIndex() + 1;
        mAttributes.join = mJoin.getSelectionIndex() + 1;
        mAttributes.miterLimit = FloatUtils.parseFloat(mMiterLimit.getText());
        mAttributes.style = mStyle.getSelectionIndex() + 1;
        mAttributes.width = FloatUtils.parseFloat(mWidth.getText());
        return mAttributes;
    }

    public void setAttributes(LineAttributes attributes)
    {
        mAttributes = attributes;
        mCap.select(mAttributes.cap - 1);
        mJoin.select(mAttributes.join - 1);
        mMiterLimit.setText(String.valueOf(mAttributes.miterLimit));
        mStyle.select(mAttributes.style - 1);
        mWidth.setText(String.valueOf(mAttributes.width));
    }
}