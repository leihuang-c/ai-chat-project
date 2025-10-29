package com.ai.chat.user.infrastructure.persistent.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ai.chat.user.domain.entity.User;
import com.ai.chat.user.infrastructure.persistent.entities.UserPO;

/**
 * JPA数据访问接口
 * 继承JpaRepository获得基础的CRUD操作能力
 * 使用@Query注解定义自定义的JPQL查询语句
 *
 * 注意：此接口位于基础设施层，负责与数据库的直接交互
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserPO, Long> {

    /**
     * 根据用户ID查询用户信息
     * 使用自定义查询语句，可以更灵活地控制查询
     *
     * @param id 用户ID
     * @return Optional<User> 用户信息的Optional包装
     */
    @Query("SELECT u FROM UserPO u WHERE u.id = :id")
    Optional<UserPO> getUserById(@Param("id") Long id);

    /**
     * 根据用户名查询用户信息
     * 使用Spring Data JPA的查询方法命名约定自动生成查询
     *
     * @param username 用户名
     * @return Optional<User> 用户信息的Optional包装，避免空指针异常
     * @see User
     * @see Optional
     */
    Optional<UserPO> findByUsername(String username);

    /**
     * 检查用户名是否存在
     * 验证指定用户名是否已被注册使用
     *
     * @param username 需要检查的用户名
     * @return Boolean true表示用户名已存在，false表示用户名可用
     */
    Boolean existsByUsername(String username);

    /**
     * 根据用户名和密码查询用户
     * 用于登录验证，同时匹配用户名和加密后的密码
     *
     * @param username 用户名
     * @param password 加密后的密码
     * @return Optional<User> 用户信息的Optional包装
     */
    @Query("SELECT u FROM UserPO u WHERE u.username = :username AND u.password = :password")
    Optional<UserPO> findByUsernameAndPassword(
            @Param("username") String username,
            @Param("password") String password
    );

    /**
     * 根据昵称模糊查询用户
     * 使用LIKE操作符进行模糊匹配
     *
     * @param nickname 昵称关键词
     * @return Optional<User> 用户信息的Optional包装
     */
    @Query("SELECT u FROM UserPO u WHERE u.nickname LIKE %:nickname%")
    Optional<UserPO> findByNicknameContaining(@Param("nickname") String nickname);

    /**
     * 统计指定昵称的用户数量
     * 用于验证昵称的唯一性或统计使用情况
     *
     * @param nickname 昵称
     * @return Long 用户数量
     */
    Long countByNickname(String nickname);

    /**
     * 根据用户ID和用户名查询用户
     * 用于验证用户身份或权限检查
     *
     * @param id 用户ID
     * @param username 用户名
     * @return Optional<User> 用户信息的Optional包装
     */
    Optional<UserPO> findByIdAndUsername(Long id, String username);
}