package jo.d2k.admin.rcp.sys.ui;

import jo.d2k.data.logic.UnitUtils;
import jo.d2k.data.logic.stargen.SystemLogic;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.util.ui.utils.FontUtils;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BodyPanel extends Composite
{
    protected BodyBean    mBody;

    private Label   mNameLabel;
    private Text    mName;
    private Text    mMass;
    private Text    mRadius;
    private Text    mURI;
    private Text    mParent;
    private Button  mGoParent;
    private Text    mSibling;
    private Button  mGoSibling;
    private Text    mChild;
    private Button  mGoChild;
    
    public BodyPanel(Composite parent, int style)
    {
        super(parent, style);
        setLayout(new GridLayout(3, false));
        mNameLabel = GridUtils.makeLabel(this, "Name:", "");
        mName = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        mName.setFont(FontUtils.getFont(mName.getFont(), +4, SWT.BOLD));
        GridUtils.makeLabel(this, "Mass:", "");
        mMass = GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        GridUtils.makeLabel(this, "Radius:", "");
        mRadius= GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        addSpecifics();
        GridUtils.makeLabel(this, "URI:", "");
        mURI= GridUtils.makeText(this, SWT.READ_ONLY, "2x1 fill=h");
        GridUtils.makeLabel(this, "Parent:", "");
        mParent = GridUtils.makeText(this, SWT.READ_ONLY, "fill=h");
        mGoParent = GridUtils.makeButton(this, "go", "");
        GridUtils.makeLabel(this, "Sibling:", "");
        mSibling = GridUtils.makeText(this, SWT.READ_ONLY, "fill=h");
        mGoSibling = GridUtils.makeButton(this, "go", "");
        GridUtils.makeLabel(this, "Child:", "");
        mChild = GridUtils.makeText(this, SWT.READ_ONLY, "fill=h");
        mGoChild = GridUtils.makeButton(this, "go", "");
        
        mGoParent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doGoParent();
            }
        });
        mGoSibling.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doGoSibling();
            }
        });
        mGoChild.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doGoChild();
            }
        });
    }

    protected void addSpecifics()
    {        
    }
    
    public BodyBean getBody()
    {
        return mBody;
    }

    public void setBody(BodyBean body)
    {
        mBody = body;
        mParent.setText("");
        mGoParent.setEnabled(false);
        mSibling.setText("");
        mGoSibling.setEnabled(false);
        mChild.setText("");
        mGoChild.setEnabled(false);
        if (mBody == null)
        {
            mNameLabel.setImage(null);
            mName.setText("");
            mMass.setText("");
            mRadius.setText("");
            mURI.setText("");
        }
        else
        {
            mNameLabel.setImage(SystemLabelProvider.getImageForObject(mBody));
            mName.setText(mBody.getName());
            mMass.setText(UnitUtils.formatMass(mBody.getMass()));
            mRadius.setText(UnitUtils.formatRadius(mBody.getRadius()));
            mURI.setText(SystemLogic.getURI(mBody));
            if (mBody.getParent() != null)
            {
                mParent.setText(mBody.getParent().getName());
                mGoParent.setEnabled(true);
            }
            if (mBody.getNextBody() != null)
            {
                mSibling.setText(mBody.getNextBody().getName());
                mGoSibling.setEnabled(true);
            }
            if (mBody.getFirstChild() != null)
            {
                mChild.setText(mBody.getFirstChild().getName());
                mGoChild.setEnabled(true);
            }
        }
        pack();
    }
    
    private void doGoParent()
    {
        setBody(mBody.getParent());
    }
    
    private void doGoSibling()
    {
        setBody(mBody.getNextBody());
    }
    
    private void doGoChild()
    {
        setBody(mBody.getFirstChild());
    }
}
