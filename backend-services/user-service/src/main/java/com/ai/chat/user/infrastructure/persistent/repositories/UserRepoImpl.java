package com.ai.chat.user.infrastructure.persistent.repositories;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ai.chat.user.domain.entity.User;
import com.ai.chat.user.domain.repository.UserRepo;
import com.ai.chat.user.infrastructure.persistent.entities.UserPO;
import com.ai.chat.user.infrastructure.persistent.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

/**
 * Chat历史记录仓库实现类。
 * 实现领域层的ChatHistoryRepo接口，提供具体的持久化操作。
 * 使用JpaChatHistoryRepository进行数据库操作，使用Mapper进行对象转换。
 */
@Component
@RequiredArgsConstructor
public class UserRepoImpl implements UserRepo {

    private final JpaUserRepository jpaUserRepository;
    private final UserMapper mapper;

    /**
     * 根据用户ID查询用户信息。 
     * 使用自定义查询语句，可以更灵活地控制查询。
     *
     * @param id 用户ID
     * @return Optional<User> 用户信息的Optional包装
     */
    @Override
    public Optional<User> getUserById(Long id) {
        Optional<UserPO> userPO = jpaUserRepository.getUserById(id);
        return userPO.map(mapper::toDomain);
    }

    /**
     * 根据用户名查询用户信息。 
     * 使用Spring Data JPA的查询方法命名约定自动生成查询。
     *
     * @param username 用户名
     * @return Optional<User> 用户信息的Optional包装，避免空指针异常
     * @see User
     * @see Optional
     */
    @Override
    public Optional<User> findByUsername(String username) {
        Optional<UserPO> userPO = jpaUserRepository.findByUsername(username);
        return userPO.map(mapper::toDomain);
    }

    /**
     * 检查用户名是否存在。
     * 验证指定用户名是否已被注册使用。
     *
     * @param username 需要检查的用户名
     * @return Boolean true表示用户名已存在，false表示用户名可用
     */
    @Override
    public Boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    /**
     * 根据用户名和密码查询用户。 
     * 用于登录验证，同时匹配用户名和加密后的密码。
     *
     * @param username 用户名
     * @param password 加密后的密码
     * @return Optional<User> 用户信息的Optional包装
     */
    @Override
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        Optional<UserPO> userPO = jpaUserRepository.findByUsernameAndPassword(username, password);
        return userPO.map(mapper::toDomain);
    }

    /**
     * 根据昵称模糊查询用户。 
     * 使用LIKE操作符进行模糊匹配。
     *
     * @param nickname 昵称关键词
     * @return Optional<User> 用户信息的Optional包装
     */
    @Override
    public Optional<User> findByNicknameContaining(String nickname) {
        Optional<UserPO> userPO = jpaUserRepository.findByNicknameContaining(nickname);
        return userPO.map(mapper::toDomain);
    }

    /**
     * 统计指定昵称的用户数量。 
     * 用于验证昵称的唯一性或统计使用情况。
     *
     * @param nickname 昵称
     * @return Long 用户数量
     */
    @Override
    public Long countByNickname(String nickname) {
        return jpaUserRepository.countByNickname(nickname);
    }

    /**
     * 根据用户ID和用户名查询用户。 
     * 用于验证用户身份或权限检查。
     *
     * @param id 用户ID
     * @param username 用户名
     * @return Optional<User> 用户信息的Optional包装
     */
    @Override
    public Optional<User> findByIdAndUsername(Long id, String username) {
        Optional<UserPO> userPO = jpaUserRepository.findByIdAndUsername(id, username);
        return userPO.map(mapper::toDomain);
    }

    /**
     * 保存用户信息
     *
     * @param user 领域对象
     * @return 保存后的领域对象（包含生成的ID）
     */
    @Override
    public User save(User user) {
        UserPO userPO = mapper.toPO(user);
        UserPO savedPO = jpaUserRepository.save(userPO);
        return mapper.toDomain(savedPO);
    }

}
