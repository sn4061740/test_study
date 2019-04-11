package com.xcore.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xcore.cache.beans.CacheCountBean;
import com.xcore.down.TaskModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableOperate {
    private DBManager manager;
    private SQLiteDatabase db;

    public TableOperate() {
        //创建数据库
        manager = DBManager.newInstances();
        db = manager.getDataBase();
    }

    /**
     * 查询数据库的名，数据库的添加
     *
     * @param <T>        泛型代表AttendInformation，Customer，Order，User，WorkDaily类
     * @param tableName  查询的数据库的名字
     * @param entityType 查询的数据库所对应的module
     * @param fieldNames  查询的字段名
     * @param values      查询的字段值
     * @return 返回查询结果，结果为AttendInformation，Customer，Order，User，WorkDaily对象
     */
    public <T> ArrayList<T> query(String tableName, Class<T> entityType, String[] fieldNames, String[] values) {
        ArrayList<T> list = new ArrayList();
        if(db==null){
            return list;
        }
        try {
            String fName="";
            for(int i=0;i<fieldNames.length;i++){
                if(i==0){
                    fName+=fieldNames[i]+"=?";
                }else{
                    fName+=" and "+fieldNames[i]+"=?";
                }
            }
            if(values==null){
                values= new String[]{};
            }
            Cursor cursor = db.query(tableName, null, fName, values, null, null, " id desc", null);
            if(cursor==null){
                return list;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                T t = entityType.newInstance();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
                    String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
                    Field field = entityType.getField(columnName);//获取该字段名的Field对象。
                    field.setAccessible(true);//取消对age属性的修饰符的检查访问，以便为属性赋值
                    field.set(t, content);
                    field.setAccessible(false);//恢复对age属性的修饰符的检查访问
                }
                list.add(t);
                cursor.moveToNext();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     *  分页查询
     * @param pageIndex
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> query(String tableName, Class<T> entityType,int pageIndex,String cType,int pageSize) {
        ArrayList<T> list = new ArrayList();
        if(db==null){
            return list;
        }
        try {
            String sql = "SELECT * FROM " + tableName
                    + " where tType=? and tDelete=? order by updateTime desc LIMIT ?,? " ;// where "+TableConfig.Cache.CACHE_TYPE+"=? order by ?
            String selectionArgs[] = new String[] {
                    cType,
                    "1",
                    String.valueOf((pageIndex - 1) * pageSize),
                    String.valueOf(pageSize) };
            Cursor cursor =db.rawQuery(sql,selectionArgs);//  db.query(tableName, null, null,
                    //null, null, null, "'"+descColum+"' desc", "5,9");
            if(cursor==null){
                return list;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                    T t = entityType.newInstance();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
                        String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
                        Field field = entityType.getDeclaredField(columnName);//获取该字段名的Field对象。
                        field.setAccessible(true);//取消对age属性的修饰符的检查访问，以便为属性赋值
                        field.set(t, content);
                        field.setAccessible(false);//恢复对age属性的修饰符的检查访问
                    }
                    list.add(t);
                    cursor.moveToNext();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *  分页查询
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> query(String cType,Class<T> entityType,String date1,String date2,int pageIndex,int pageSize) {
        ArrayList<T> list = new ArrayList();
        if(db==null){
            return list;
        }
        try {
            String sql = "SELECT * FROM " + TableConfig.Cache.TABLE_CACHE
                    + " where tType=? and tDelete=? and updateTime>=? and updateTime<=? " +
                    "order by updateTime desc LIMIT ?,? " ;
            String selectionArgs[] = new String[] {
                    cType,
                    "1",
                    date1,
                    date2,
                    String.valueOf((pageIndex - 1) * pageSize),
                    String.valueOf(pageSize) };
            if(date2.length()<=0){
                sql = "SELECT * FROM " + TableConfig.Cache.TABLE_CACHE
                        + " where tType=? and tDelete=? and updateTime<=? " +
                        "order by updateTime desc LIMIT ?,? " ;
                selectionArgs=new String[]{
                        cType,
                        "1",
                        date1,
                        String.valueOf((pageIndex - 1) * pageSize),
                        String.valueOf(pageSize) };
            }

            Cursor cursor =db.rawQuery(sql,selectionArgs);
            if(cursor==null){
                return list;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                    T t = entityType.newInstance();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
                        String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
                        Field field = entityType.getDeclaredField(columnName);//获取该字段名的Field对象。
                        field.setAccessible(true);//取消对age属性的修饰符的检查访问，以便为属性赋值
                        field.set(t, content);
                        field.setAccessible(false);//恢复对age属性的修饰符的检查访问
                    }
                    list.add(t);
                    cursor.moveToNext();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *  分页查询
     * @param pageIndex
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> query(String tableName, Class<T> entityType,int pageIndex) {
        ArrayList<T> list = new ArrayList();
        if(db==null){
            return list;
        }
        try {
            String sql = "SELECT * FROM " + tableName
                    + " where dicDelete=? order by dicUpdateTime desc LIMIT ?,? " ;// where "+TableConfig.Cache.CACHE_TYPE+"=? order by ?
            String selectionArgs[] = new String[] {
                    "1",
                    String.valueOf((pageIndex - 1) * 10),
                    String.valueOf(10) };
            Cursor cursor =db.rawQuery(sql,selectionArgs);//  db.query(tableName, null, null,
            //null, null, null, "'"+descColum+"' desc", "5,9");
            if(cursor==null){
                return list;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                    T t = entityType.newInstance();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
                        String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
                        Field field = entityType.getDeclaredField(columnName);//获取该字段名的Field对象。
                        field.setAccessible(true);//取消对age属性的修饰符的检查访问，以便为属性赋值
                        field.set(t, content);
                        field.setAccessible(false);//恢复对age属性的修饰符的检查访问
                    }
                    list.add(t);
                    cursor.moveToNext();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return list;
    }
    List<String> fieldLists=Arrays.asList(
            "selected",
            "showed",
            "isShowCheck",
            "checked",
            "isPlay",
            "isStop",
            "timeCount",
            "totalCount1",
            "streamInfo",
            "streamInfo1",
            "serialVersionUID",
            "$change",
            "bgSelected"
    );
    /**
     * 向数据库插入数据
     *
     * @param tableName 数据库插入数据的数据表
     * @param object    数据库插入的对象
     */
    public void insert(String tableName, Object object) {
        if(db==null){
            return;
        }
        try {
            Class clazz = object.getClass();
            Field[] fields =clazz.getFields();// clazz.getDeclaredFields();//获取该类所有的属性

            ContentValues value = new ContentValues();
            for (Field field : fields) {
                    field.setAccessible(true); //取消对age属性的修饰符的检查访问，以便为属性赋值
                if(fieldLists.contains(field.getName())){
                    continue;
                }
                    String content = (String) field.get(object);//获取该属性的内容
                    value.put(field.getName(), content);
                    field.setAccessible(false);//恢复对age属性的修饰符的检查访问
            }
            db.beginTransaction();
            long insertId=db.insert(tableName, null, value);
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除数据
     *
     * @param tableName 删除数据库的表名
     * @param fieldName 删除的字段名
     * @param value     删除的字段的值
     */
    public void delete(String tableName, String fieldName, String value) {
        if(db==null){
            return;
        }
        try {
            db.beginTransaction();
            db.delete(tableName, fieldName + "=?", new String[]{value});
            db.setTransactionSuccessful();
            db.endTransaction();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 更改数据库内容
     *
//     * @param tableName   更改数据的数据表
//     * @param columnName  更改的数据的字段名
//     * @param columnValue 更改的数据的字段值
//     * @param object      更改的数据
     */
    public int uptate(String sql,String ...objs) {//String columnName, String columnValue, Object object
        if(db==null){
            return -1;
        }
        int n=-1;
        try {
            db.beginTransaction();
            db.execSQL(sql,objs);
            db.setTransactionSuccessful();
            db.endTransaction();
            n=1;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return n;
    }

    //分类查询总条数 缓存
    public <T> ArrayList<T> queryCacheCount(Class<T> entityType){
        ArrayList<T> list=new ArrayList<>();
        if(db==null){
            return list;
        }
        try {
            String sql = "SELECT count(*) as count," + TableConfig.Cache.CACHE_TYPE + " as vt FROM " + TableConfig.Cache.TABLE_CACHE
                    + " WHERE " + TableConfig.Cache.CACHE_DELETE + "='1' GROUP BY " + TableConfig.Cache.CACHE_TYPE;

            Cursor cursor = db.rawQuery(sql, null);
            if(cursor==null){
                return list;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                T t = entityType.newInstance();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
                    String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
                    //                    Log.e("TAG",columnName);
                    Field field = entityType.getDeclaredField(columnName);//获取该字段名的Field对象。
                    field.setAccessible(true);//取消对age属性的修饰符的检查访问，以便为属性赋值
                    field.set(t, content);
                    field.setAccessible(false);//恢复对age属性的修饰符的检查访问
                }
                list.add(t);
                cursor.moveToNext();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return list;
    }

    //分类查询总条数 缓存
    public <T> ArrayList<T> queryDownCount(Class<T> entityType){
        ArrayList<T> list=new ArrayList<>();
        if(db==null){
            return list;
        }
        try {
            String sql="SELECT count(*) as count FROM "+TableConfig.DOWN.TABLE_DOWN
                    +" WHERE "+TableConfig.DOWN.DOWN_DELETE+"='1'";

            Cursor cursor= db.rawQuery(sql,null);
            if(cursor==null){
                return list;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                    T t = entityType.newInstance();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
                        String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
                        Field field = entityType.getDeclaredField(columnName);//获取该字段名的Field对象。
                        field.setAccessible(true);//取消对age属性的修饰符的检查访问，以便为属性赋值
                        field.set(t, content);
                        field.setAccessible(false);//恢复对age属性的修饰符的检查访问
                    }
                    list.add(t);
                    cursor.moveToNext();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return list;
    }


    ///////////////////////////////////
//    public void insertTask(TaskModel taskModel){
//        String sql="INSERT INTO "+TableConfig.NEW_CACHE.TABLE_NEW_CACHE+" ("
//                +TableConfig.NEW_CACHE.NEW_CACHE_ID+","
//                +TableConfig.NEW_CACHE.NEW_CACHE_TITLE+","
//                +TableConfig.NEW_CACHE.NEW_CACHE_URL+","
//                +TableConfig.NEW_CACHE.NEW_CACHE_CONVER+","
//                +TableConfig.NEW_CACHE.NEW_CACHE_KEY+","
//                +TableConfig.NEW_CACHE.NEW_CACHE_FILESIZE+","
//                +TableConfig.NEW_CACHE.NEW_CACHE_PERCENT+","+TableConfig.NEW_CACHE.ID+") " +
//                "VALUES (1, 'Paul', 32, 'California', 20000.00 );";
//        db.execSQL(sql);
//    }
//    public <T>List<T> getTaskList(Class<T> entityType){
//        List<T> list=new ArrayList<>();
//        try {
//            Cursor cursor = db.rawQuery("SELECT * FROM " + TableConfig.NEW_CACHE.TABLE_NEW_CACHE, null);
//            if (cursor == null) {
//                return list;
//            }
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                T t = entityType.newInstance();
//                for (int i = 0; i < cursor.getColumnCount(); i++) {
//                    String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
//                    String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
//                    Field field = entityType.getField(columnName);//获取该字段名的Field对象。
//                    field.setAccessible(true);//取消对age属性的修饰符的检查访问，以便为属性赋值
//                    field.set(t, content);
//                    field.setAccessible(false);//恢复对age属性的修饰符的检查访问
//                }
//                list.add(t);
//                cursor.moveToNext();
//            }
//        }catch (Exception ex){}
//        return list;
//    }

    public void selectTable(){
        try {
            Cursor cursor = db.rawQuery("select * from sqlite_master", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
                    String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
                    Log.e("TAG", columnName + ":" + content);
                }
                cursor.moveToNext();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
