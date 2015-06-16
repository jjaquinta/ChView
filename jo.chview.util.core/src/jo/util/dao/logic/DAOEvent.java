/*
 * Created on Apr 11, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.logic;

import jo.util.beans.Bean;

public class DAOEvent
{
    public static final int DELETION = 1;
    public static final int ADDITION = 2;
    public static final int UPDATE = 3;
    
    private int     mId;
    private String  mBeanName;
    private Bean    mBean;
    
    /**
     * @return the bean
     */
    public Bean getBean()
    {
        return mBean;
    }
    /**
     * @param bean the bean to set
     */
    public void setBean(Bean bean)
    {
        mBean = bean;
    }
    /**
     * @return the beanName
     */
    public String getBeanName()
    {
        return mBeanName;
    }
    /**
     * @param beanName the beanName to set
     */
    public void setBeanName(String beanName)
    {
        mBeanName = beanName;
    }
    /**
     * @return the id
     */
    public int getId()
    {
        return mId;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id)
    {
        mId = id;
    }
}
