package jo.d2k.data.io;

import java.util.List;

import jo.d2k.data.data.ScoreBean;
import jo.util.dao.IOBeanHandler2;

public interface IOScoreHandler extends IOBeanHandler2<ScoreBean>
{
    public List<ScoreBean> findByListPrefixAndUsername(String listPrefix, String username);
}
