package com.xcore.cache;

import android.content.Context;
import android.util.Log;

import com.lzy.okgo.cache.CacheMode;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheCountBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.cache.beans.DictionaryBean;
import com.xcore.cache.beans.XCommentBean;
import com.xcore.down.TaskModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库缓存
 */
public class DBHandler {
    private Context context= MainApplicationContext.context;
    TableOperate operate;

    public DBHandler(){
        operate= new TableOperate();//创建数据库操作类
    }

    /**
     * 添加缓存记录
     * @param cache1
     */
    public void insertCache(CacheBean cache1){
        if(operate==null){
            return;
        }
        try {
            final CacheBean cache = cache1;
            ArrayList<CacheBean> list = operate.query(TableConfig.Cache.TABLE_CACHE, CacheBean.class,
                    new String[]{TableConfig.UPDATE_COLUM, TableConfig.Cache.CACHE_TYPE},
                    new String[]{cache.getShortId(), cache.gettType()});
            if (list != null && list.size() > 0) {//更新
                update(cache);
            } else {
                operate.insert(TableConfig.Cache.TABLE_CACHE, cache);
            }
        }catch (Exception ex){}
    }

    /**
     * 更新
     * @param cacheBean1
     */
    public void update(CacheBean cacheBean1){
        if(operate==null){
            return;
        }
        try {
            final CacheBean cacheBean = cacheBean1;
            String sql = "update " + TableConfig.Cache.TABLE_CACHE + " set updateTime=?,playPosition=?,cover=?,title=?,"
                    + TableConfig.Cache.CACHE_DELETE + "=? where shortId=? and " + TableConfig.Cache.CACHE_TYPE + "=?";
            operate.uptate(sql, System.currentTimeMillis() + "", cacheBean.getPlayPosition() + "",
                    cacheBean.getCover(),cacheBean.getTitle(), cacheBean.gettDelete(),
                    cacheBean.getShortId(), cacheBean.gettType());
        }catch (Exception ex){}
    }

    /**
     * 删除数据
     * @param o1 要删除的数据
     */
    public void delete(CacheBean o1){
        if(operate==null){
            return;
        }
        try {
            final CacheBean o = o1;
            String sql = "update " + TableConfig.Cache.TABLE_CACHE + " set updateTime=?,"
                    + TableConfig.Cache.CACHE_DELETE + "=0 where " + TableConfig.Cache.CACHE_SHORT_ID + "=? and " + TableConfig.Cache.CACHE_TYPE + "=?";
            operate.uptate(sql, o.getShortId(), o.gettType());
        }catch (Exception ex){}
    }

    /**
     * 得到缓存记录
     * @return List<Cache>
     */
    public List<CacheBean> query(int pageIndex,String type){
        if(operate==null){
            return new ArrayList<>();
        }
        try {
            return operate.query(TableConfig.Cache.TABLE_CACHE, CacheBean.class, pageIndex, type, 10);
        }catch (Exception ex){
            return new ArrayList<>();
        }
    }

    /**
     * 得到一条数据
     * @param o 查询数据
     * @return
     */
    public CacheBean query(CacheBean o){
        if(o==null||operate==null){
            return null;
        }
        try {
            List<CacheBean> collects = operate.query(TableConfig.Cache.TABLE_CACHE, CacheBean.class,
                    new String[]{TableConfig.UPDATE_COLUM, TableConfig.Cache.CACHE_TYPE},
                    new String[]{o.getShortId(), o.gettType()});
            if (collects != null && collects.size() > 0) {
                return collects.get(0);
            }
        }catch (Exception ex){}
        return null;
    }
    //时间查询
    public List<CacheBean> getQuery(String type,String date1,String date2,int pageIndex,int pageSize){
        if(operate==null){
            return new ArrayList<>();
        }
        try {
            List<CacheBean> list = operate.query(type, CacheBean.class, date1, date2, pageIndex, pageSize);
            return (list == null || list.size() <= 0) ? new ArrayList<CacheBean>() : list;
        }catch (Exception ex){}
        return new ArrayList<>();
    }

    /**
     * 得到缓存的总数
     * @return
     */
    public List<CacheCountBean> getCacheCount(){
        if(operate==null){
            return new ArrayList<>();
        }
        try {
            List<CacheCountBean> list = operate.queryCacheCount(CacheCountBean.class);
            return (list == null || list.size() <= 0) ? new ArrayList<CacheCountBean>() : list;
        }catch (Exception ex){}
        return new ArrayList<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// 下载缓存 /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    //添加到缓存
    public void insertDown(CacheModel cacheModel1){
        if(operate==null){
            return;
        }
        final CacheModel cacheModel=cacheModel1;
        cacheModel.setUpdateTime(System.currentTimeMillis()+"");
        List<CacheModel> cacheModels = operate.query(TableConfig.DOWN.TABLE_DOWN,
                CacheModel.class, new String[]{TableConfig.DOWN.DOWN_STREAM_ID}, new String[]{cacheModel.getStreamId()});
        if (cacheModels.size() > 0) {
            update(cacheModel);
        } else {
            operate.insert(TableConfig.DOWN.TABLE_DOWN, cacheModel);
        }
    }
    //更新
    public void update(CacheModel cacheModel){
        final CacheModel cacheModel1=cacheModel;
        if(operate==null){
            return;
        }
        String sql="update "+TableConfig.DOWN.TABLE_DOWN+" set "
                +TableConfig.DOWN.DOWN_UPDATE_TIME+"=?,"
                +TableConfig.DOWN.DOWN_DELETE+"=?,"+
                TableConfig.DOWN.DOWN_CONVER+"=?,"
                +TableConfig.DOWN.DOWN_TOTAL_SIZE+"=?,"
                +TableConfig.DOWN.DOWN_DOWNSIZE+"=?,"
                +TableConfig.DOWN.DOWN_PERCENT+"=?"+
                " where streamId=?";
        try {
            operate.uptate(sql, System.currentTimeMillis() + "",
                    "1",
                    cacheModel1.getConver(),
                    cacheModel1.getTotalSize(),
                    cacheModel1.getDownSize(),
                    cacheModel1.getPercent(),
                    cacheModel1.getStreamId());
        }catch (Exception e){}
    }

    //得到所有的下载信息
    public List<CacheModel> getDowns(){
        if(operate==null){
            return new ArrayList<>();
        }
        try {
            List<CacheModel> list = operate.query(TableConfig.DOWN.TABLE_DOWN, CacheModel.class, new String[]{}, new String[]{});
            return (list == null || list.size() <= 0) ? new ArrayList<CacheModel>() : list;
        }catch (Exception ex){}
        return new ArrayList<>();
    }

    //删除
    public void delteDown(CacheModel cacheModel1){
        if(operate==null){
            return;
        }
        final CacheModel cacheModel=cacheModel1;
        try {
            operate.delete(TableConfig.DOWN.TABLE_DOWN, TableConfig.DOWN.DOWN_STREAM_ID, cacheModel.getStreamId());
        }catch (Exception e){}
    }
    //删除
    public void delteCache(CacheModel cacheModel1){
        if(operate==null){
            return;
        }
        final CacheModel cacheModel=cacheModel1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    operate.delete(TableConfig.DOWN.TABLE_DOWN, TableConfig.DOWN.DOWN_SHORT_ID, cacheModel.getShortId());
                }catch (Exception e){}
            }
        }).start();
    }

    /**
     * 得到下载的总数
     * @return
     */
    public List<CacheCountBean> getDownCount(){
        if(operate==null){
            return new ArrayList<>();
        }
        try {
            List<CacheCountBean> cacheCountBeans = operate.queryDownCount(CacheCountBean.class);
            if (cacheCountBeans != null && cacheCountBeans.size() > 0) {
                cacheCountBeans.get(0).vt = CacheType.CACHE_DOWN;
            }
            return cacheCountBeans;
        }catch (Exception ex){}
        return new ArrayList<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// 搜索历史 /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    //插入关健字
    public void insertDic(DictionaryBean dictionaryBean1){
        if(operate==null){
            return;
        }
        final DictionaryBean dictionaryBean=dictionaryBean1;
        try {
            ArrayList<DictionaryBean> list = operate.query(TableConfig.Dictionary.TABLE_DIC, DictionaryBean.class,
                    new String[]{TableConfig.Dictionary.DIC_NAME},
                    new String[]{dictionaryBean.getDicName()});
            if (list != null && list.size() > 0) {//更新
                update(dictionaryBean);
            } else {
                operate.insert(TableConfig.Dictionary.TABLE_DIC, dictionaryBean);
            }
        }catch (Exception e){}
    }
    //得到关健字信息
    public DictionaryBean getDic(DictionaryBean o){
        if(o==null||operate==null){
            return null;
        }
        try {
            List<DictionaryBean> dics = operate.query(TableConfig.Dictionary.TABLE_DIC, DictionaryBean.class,
                    new String[]{TableConfig.Dictionary.DIC_NAME},
                    new String[]{o.getDicName()});
            if (dics != null && dics.size() > 0) {
                return dics.get(0);
            }
        }catch (Exception ex){}
        return null;
    }
    public void update(DictionaryBean dictionaryBean1){
        if(operate==null){
            return;
        }
        final DictionaryBean dictionaryBean=dictionaryBean1;
        try{
            String sql="update "+TableConfig.Dictionary.TABLE_DIC+" set dicUpdateTime=?,"
                    +TableConfig.Dictionary.DIC_DELETE+"=? where dicName=?";
            operate.uptate(sql,System.currentTimeMillis()+"",dictionaryBean.getDicDelete(),
                    dictionaryBean.getDicName());
        }catch (Exception ex){}
    }
    /**
     * 得到历史搜索记录
     * @return List<Cache>
     */
    public List<DictionaryBean> query(int pageIndex){
        if(operate==null){
            return new ArrayList<>();
        }
        try {
            List<DictionaryBean> list = operate.query(TableConfig.Dictionary.TABLE_DIC, DictionaryBean.class, pageIndex);
            return (list == null || list.size() <= 0) ? new ArrayList<DictionaryBean>() : list;
        }catch (Exception ex){}
        return new ArrayList<>();
    }


    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// 评论点赞 /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    //添加、更新
    public void updateComment(XCommentBean xCommentBean1){
        if(operate==null){
            return;
        }
        try {
            final XCommentBean xCommentBean = xCommentBean1;
            xCommentBean.cTime = System.currentTimeMillis() + "";
            List<XCommentBean> list = operate.query(TableConfig.COMMENT.TABLE_COMMENT, XCommentBean.class, new String[]{
                    TableConfig.COMMENT.COMMENT_SHORT_ID
            }, new String[]{
                    xCommentBean.shortId
            });
            if (list == null || list.size() <= 0) {//添加
                operate.insert(TableConfig.COMMENT.TABLE_COMMENT, xCommentBean);
            } else {//更新
                String sql = "update " + TableConfig.COMMENT.TABLE_COMMENT + " set cTime=?,"
                        + TableConfig.COMMENT.COMMENT_DELETE + "=? where shortId=?";
                operate.uptate(sql, xCommentBean.cTime, xCommentBean.cDelete,
                        xCommentBean.shortId);
            }
        }catch (Exception ex){}
    }

    /**
     * 根据评论ID 得到是否点赞
     */
    public XCommentBean getCommentByShortId(String shortId){
        if(operate==null){
            return new XCommentBean();
        }
        try {
            List<XCommentBean> list = operate.query(TableConfig.COMMENT.TABLE_COMMENT, XCommentBean.class, new String[]{
                    TableConfig.COMMENT.COMMENT_SHORT_ID
            }, new String[]{
                    shortId
            });
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        }catch (Exception ex){}
        return new XCommentBean();
    }


    /////////////////////// M3U8 缓存 //////////////////////////////////////

    //添加,更新
    public void update(TaskModel taskModel){
//        if(operate==null){
//            return;
//        }
//        try {
//            final TaskModel model = taskModel;
//            List<TaskModel> list = operate.query(TableConfig.NEW_CACHE.TABLE_NEW_CACHE, TaskModel.class, new String[]{
//                 TableConfig.NEW_CACHE.NEW_CACHE_ID
//            }, new String[]{
//                 model.getTaskId()
//            });
//            if (list == null || list.size() <= 0) {//添加
//                operate.insert(TableConfig.NEW_CACHE.TABLE_NEW_CACHE, model);
//            } else {//更新
//                String sql = "update " + TableConfig.NEW_CACHE.TABLE_NEW_CACHE + " set " + TableConfig.NEW_CACHE.NEW_CACHE_PERCENT + "=? where "
//                        + TableConfig.NEW_CACHE.NEW_CACHE_ID + "=?";
//                operate.uptate(sql, model.getPercent() + "", model.getTaskId());
//            }
//        }catch (Exception ex){}
    }
    //删除
    public void delete(TaskModel taskModel){
        if(operate==null){
            return;
        }
        final TaskModel model=taskModel;
        try {
            operate.delete(TableConfig.NEW_CACHE.TABLE_NEW_CACHE, TableConfig.NEW_CACHE.NEW_CACHE_ID, model.getTaskId());
        }catch (Exception e){}
    }
    //获取所有缓存
    public List<TaskModel> getTasks(){
        if(operate==null){
            return new ArrayList<>();
        }
        List<TaskModel> list =operate.query(TableConfig.NEW_CACHE.TABLE_NEW_CACHE,TaskModel.class,new String[]{},new String[]{});
        return (list==null||list.size()<=0)?new ArrayList<TaskModel>():list;
    }

}
