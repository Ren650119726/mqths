package com.reefe.mqths.core.spi.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.reefe.mqths.common.bean.entity.MqthParticipant;
import com.reefe.mqths.common.bean.entity.MqthTransaction;
import com.reefe.mqths.common.config.MqthConfig;
import com.reefe.mqths.common.config.MqthDbConfig;
import com.reefe.mqths.common.enums.MqthStatusEnum;
import com.reefe.mqths.common.enums.RepositorySupportEnum;
import com.reefe.mqths.common.exception.MqthException;
import com.reefe.mqths.common.exception.MqthRuntimeException;
import com.reefe.mqths.common.serializer.ObjectSerializer;
import com.reefe.mqths.common.utils.RepositoryPathUtils;
import com.reefe.mqths.core.spi.CoordinatorDao;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 本地事务消息 仅支持Mysql数据库 dao
 *
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public class JdbcCoordinatorDao implements CoordinatorDao {
    private Logger logger = LoggerFactory.getLogger(JdbcCoordinatorDao.class);

    private DruidDataSource dataSource;

    private String tableName;

    private ObjectSerializer serializer;

    /**
     * 初始化操作
     *
     * @param modelName  项目模块名称
     * @param mqthConfig 配置信息
     * @throws MqthRuntimeException 自定义异常
     */
    @Override
    public void init(String modelName, MqthConfig mqthConfig) throws MqthRuntimeException {
        //初始化 druid连接池 dataSource
        dataSource = new DruidDataSource();
        final MqthDbConfig dbConfig = mqthConfig.getMqthDbConfig();
        dataSource.setUrl(dbConfig.getUrl());
        dataSource.setDriverClassName(dbConfig.getDriverClassName());
        dataSource.setUsername(dbConfig.getUsername());
        dataSource.setPassword(dbConfig.getPassword());
        dataSource.setInitialSize(dbConfig.getInitialSize());
        dataSource.setMaxActive(dbConfig.getMaxActive());
        dataSource.setMinIdle(dbConfig.getMinIdle());
        dataSource.setMaxWait(dbConfig.getMaxWait());
        dataSource.setValidationQuery(dbConfig.getValidationQuery());
        dataSource.setTestOnBorrow(dbConfig.getTestOnBorrow());
        dataSource.setTestOnReturn(dbConfig.getTestOnReturn());
        dataSource.setTestWhileIdle(dbConfig.getTestWhileIdle());
        dataSource.setPoolPreparedStatements(dbConfig.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(dbConfig.getMaxPoolPreparedStatementPerConnectionSize());
        this.tableName = RepositoryPathUtils.buildDbTableName(modelName);
        StringBuilder createTableSql = new StringBuilder();
        createTableSql.append("CREATE TABLE `")
                .append(tableName).append("` (\n")
                .append("  `trans_id` varchar(64) NOT NULL,\n")
                .append("  `target_class` varchar(256) ,\n")
                .append("  `target_method` varchar(128) ,\n")
                .append("  `retried_count` int(3) NOT NULL,\n")
                .append("  `create_time` datetime NOT NULL,\n")
                .append("  `last_time` datetime NOT NULL,\n")
                .append("  `version` int(6) NOT NULL,\n")
                .append("  `status` int(2) NOT NULL,\n")
                .append("  `invocation` longblob,\n")
                .append("  `role` int(2) NOT NULL,\n")
                .append("  `error_msg` text ,\n")
                .append("   PRIMARY KEY (`trans_id`),\n")
                .append("   KEY  `status_last_time` (`last_time`,`status`) USING BTREE \n")
                .append(")");
        //创建数据库表
        executeUpdate(createTableSql.toString());
    }

    private int executeUpdate(String sql, Object... params) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject((i + 1), params[i]);
                }
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("executeUpdate-> " + e.getMessage());
        }
        return 0;
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return RepositorySupportEnum.DB.getRepository();
    }

    @Override
    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 创建本地事务对象
     *
     * @param mqthTransaction 事务对象
     * @return rows 1 成功   0 失败
     */
    @Override
    public int create(MqthTransaction mqthTransaction) {
        StringBuilder sql = new StringBuilder()
                .append("insert into ")
                .append(tableName)
                .append("(trans_id,target_class,target_method,retried_count,create_time,last_time,version,status,invocation,role,error_msg)")
                .append(" values(?,?,?,?,?,?,?,?,?,?,?)");
        try {

            final byte[] serialize = serializer.serialize(mqthTransaction.getMqthParticipants());
            return executeUpdate(sql.toString(),
                    mqthTransaction.getTransId(),
                    mqthTransaction.getTargetClass(),
                    mqthTransaction.getTargetMethod(),
                    mqthTransaction.getRetriedCount(),
                    mqthTransaction.getCreateTime(),
                    mqthTransaction.getLastTime(),
                    mqthTransaction.getVersion(),
                    mqthTransaction.getStatus(),
                    serialize,
                    mqthTransaction.getRole(),
                    mqthTransaction.getErrorMsg());

        } catch (MqthException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 更新补偿数据状态
     *
     * @param transId 事务id
     * @param status  状态
     * @return rows 1 成功
     * @throws MqthRuntimeException 异常
     */
    @Override
    public int updateStatus(String transId, int status) throws MqthRuntimeException {
        String sql = "update " + tableName +
                " set status=?  where trans_id = ?  ";
        return executeUpdate(sql, status, transId);
    }

    /**
     * 更新事务失败日志
     *
     * @param mqthTransaction 实体对象
     * @return rows 1 成功
     * @throws MqthRuntimeException 异常信息
     */
    @Override
    public int updateParticipant(MqthTransaction mqthTransaction) throws MqthRuntimeException {
        String sql = "update " + tableName +
                " set invocation=?  where trans_id = ?  ";

        try {
            final byte[] serialize = serializer.serialize(mqthTransaction.getMqthParticipants());

            return executeUpdate(sql, serialize,
                    mqthTransaction.getTransId());

        } catch (MqthException e) {
            e.printStackTrace();
            throw new MqthRuntimeException(e.getMessage());
        }

    }

    /**
     * 更新事务失败日志
     *
     * @param mqthTransaction 实体对象
     * @return rows 1 成功
     * @throws MqthRuntimeException 异常信息
     */
    @Override
    public int updateFailTransaction(MqthTransaction mqthTransaction) throws MqthRuntimeException {
        String sql = "update " + tableName +
                " set  status=? ,error_msg=? ,retried_count =?,last_time = ?   where trans_id = ?  ";
        mqthTransaction.setLastTime(new Date());
        return executeUpdate(sql, mqthTransaction.getStatus(), mqthTransaction.getErrorMsg(),
                mqthTransaction.getRetriedCount(),
                mqthTransaction.getLastTime(),
                mqthTransaction.getTransId());

    }

    /**
     * 获取延迟多长时间后的事务信息,只要为了防止并发的时候，刚新增的数据被执行
     *
     * @param date 延迟后的时间
     * @return List<MythTransaction>
     */
    @Override
    public List<MqthTransaction> listAllByDelay(Date date) {
        String sb = "select * from " +
                tableName +
                " where last_time <?  and status = " + MqthStatusEnum.BEGIN.getCode();

        List<Map<String, Object>> list = executeQuery(sb, date);

        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().filter(Objects::nonNull)
                    .map(this::buildByResultMap).collect(Collectors.toList());
        }

        return null;
    }

    private MqthTransaction buildByResultMap(Map<String, Object> map) {
        MqthTransaction mqthTransaction = new MqthTransaction();
        mqthTransaction.setTransId((String) map.get("trans_id"));
        mqthTransaction.setRetriedCount((Integer) map.get("retried_count"));
        mqthTransaction.setCreateTime((Date) map.get("create_time"));
        mqthTransaction.setLastTime((Date) map.get("last_time"));
        mqthTransaction.setVersion((Integer) map.get("version"));
        mqthTransaction.setStatus((Integer) map.get("status"));
        mqthTransaction.setRole((Integer) map.get("role"));
        byte[] bytes = (byte[]) map.get("invocation");
        try {
            final List<MqthParticipant> participants = serializer.deSerialize(bytes, CopyOnWriteArrayList.class);
            mqthTransaction.setMqthParticipants(participants);
        } catch (MqthException e) {
            e.printStackTrace();
        }
        return mqthTransaction;
    }

    private List<Map<String, Object>> executeQuery(String sql, Object... params) {

        List<Map<String, Object>> list = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject((i + 1), params[i]);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> rowData = new HashMap<>(16);
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.put(md.getColumnName(i), rs.getObject(i));
                    }
                    list.add(rowData);
                }
            }

        } catch (SQLException e) {
            logger.error("executeQuery-> " + e.getMessage());
        }
        return list;
    }

}
