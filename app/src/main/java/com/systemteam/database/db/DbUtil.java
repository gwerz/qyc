/*
******************************* Copyright (c)*********************************\
**
**                 (c) Copyright 2015, 蒋朋, china, qd. sd
**                          All Rights Reserved
**
**                           By()
**                         
**-----------------------------------版本信息------------------------------------
** 版    本: V0.1
**
**------------------------------------------------------------------------------
********************************End of Head************************************\
*/

package com.systemteam.database.db;


import com.systemteam.dao.BikeInfoDao;

/**
 * @Description 获取表 Helper 的工具类
 * @author scofield.hhl@gmail.com
 * @time 2016/12/2
 */
public class DbUtil {
    private static DBHelper sTaskModelHelper;


    private static BikeInfoDao getTaskModelDao() {
        return DbCore.getDaoSession().getBikeInfoDao();
    }

    public static DBHelper getTaskModelHelperHelper() {
        if (sTaskModelHelper == null) {
            sTaskModelHelper = new DBHelper(getTaskModelDao());
        }
        return sTaskModelHelper;
    }


}
