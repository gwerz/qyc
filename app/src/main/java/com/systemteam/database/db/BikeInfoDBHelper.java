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


import com.systemteam.bean.BikeInfo;

import org.greenrobot.greendao.AbstractDao;

/**
 * @Description 具体表的实现类
 * @author scofield.hhl@gmail.com
 * @time 2016/12/2
 */
public class BikeInfoDBHelper extends BaseDbHelper<BikeInfo, Long> {


    public BikeInfoDBHelper(AbstractDao dao) {
        super(dao);
    }
}
