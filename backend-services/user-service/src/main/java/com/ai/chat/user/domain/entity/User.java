package com.ai.chat.user.domain.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 * 映射数据库中的用户表，存储系统用户的基本信息
 * 使用JPA注解进行对象关系映射(ORM)
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户唯一标识ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
