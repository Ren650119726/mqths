/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.reefe.mqths.common.constant;

/**
 * 全局系统常量
 * @author REEFE
 */
public interface CommonConstant {


    String DB_MYSQL = "mysql";

    String DB_SQLSERVER = "sqlserver";

    String DB_ORACLE = "oracle";

    String PATH_SUFFIX = "/mqth";

    /**
     * 数据库前缀
     */
    String DB_PREFIX = "mqth_";

    /**
     * redis key 前缀
     */
    String RECOVER_REDIS_KEY_PRE = "mqth:transaction:%s";

    /**
     * 事务上下文
     */
    String MQTH_TRANSACTION_CONTEXT = "MQTH_TRANSACTION_CONTEXT";

    /**
     * RocketMQ Topic主题 tags 分隔符
     */
    String TOPIC_TAG_SEPARATOR = ",";

    int SUCCESS = 1;

    int ERROR = 0;

}
