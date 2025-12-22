package com.ai.chat.user.infrastructure.persistent.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.ai.chat.user.domain.entity.User;
import com.ai.chat.user.infrastructure.persistent.entities.UserPO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * MapStruct实例，用于非Spring环境下的手动调用
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

/**
     * 将领域对象转换为持久化对象
     *
     * @param domain 领域对象
     * @return 持久化对象
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "nickname", target = "nickname")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "createTime", target = "createTime")
    @Mapping(source = "updateTime", target = "updateTime")
    UserPO toPO(User domain);

    /**
     * 将持久化对象转换为领域对象
     *
     * @param po 持久化对象
     * @return 领域对象
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "nickname", target = "nickname")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "createTime", target = "createTime")
    @Mapping(source = "updateTime", target = "updateTime")
    User toDomain(UserPO po);
}
