package com.reefe.mqths.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 持久化枚举
 * @Auther: REEFE
 * @Date: 2018/6/4/004
 */
public enum RepositorySupportEnum {

    /**
     * Db compensate cache type enum.
     */
    DB("db");


    private String repository;

    RepositorySupportEnum(String repository) {
        this.repository = repository;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    /**
     * 获取补偿型缓存枚举。
     *
     * @param repository 持久化类型
     * @return 持久化枚举
     */
    public static RepositorySupportEnum acquire(String repository) {
        Optional<RepositorySupportEnum> repositorySupportEnum = Arrays.stream(RepositorySupportEnum.values())
                .filter(v -> Objects.equals(v.getRepository(), repository))
                .findFirst();
        return repositorySupportEnum.orElse(RepositorySupportEnum.DB);
    }

}
