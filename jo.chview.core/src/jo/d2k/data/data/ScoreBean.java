package jo.d2k.data.data;

import jo.util.beans.Bean;

public class ScoreBean extends Bean
{
    private String  mListID;
    private String  mUsername;
    private long    mScore;
    private long    mDate;
    
    public String getListID()
    {
        return mListID;
    }
    public void setListID(String listID)
    {
        mListID = listID;
    }
    public String getUsername()
    {
        return mUsername;
    }
    public void setUsername(String username)
    {
        mUsername = username;
    }
    public long getScore()
    {
        return mScore;
    }
    public void setScore(long score)
    {
        mScore = score;
    }
    public long getDate()
    {
        return mDate;
    }
    public void setDate(long date)
    {
        mDate = date;
    }
}
