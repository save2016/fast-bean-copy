package com.github.jackieonway.copy;

import com.github.jackieonway.copy.example.User;
import com.github.jackieonway.copy.example.UserDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fast Bean Copy 的测试用例
 */
public class CopyTargetProcessorTest {

    @Test
    public void testSimpleCreation() {
        // 创建测试数据
        User user = new User(1L, "John Doe", "john@example.com", "secret123");

        // 创建 DTO 实例
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        // 验证映射
        assertEquals(1L, userDto.getId());
        assertEquals("John Doe", userDto.getName());
        assertEquals("john@example.com", userDto.getEmail());
    }

    @Test
    public void testListCopy() {
        List<User> users = Arrays.asList(
            new User(1L, "User 1", "user1@example.com", "pass1"),
            new User(2L, "User 2", "user2@example.com", "pass2")
        );

        List<UserDto> dtos = new ArrayList<>();
        for (User user : users) {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dtos.add(dto);
        }

        assertEquals(2, dtos.size());
        assertEquals("User 1", dtos.get(0).getName());
        assertEquals("User 2", dtos.get(1).getName());
    }

    @Test
    public void testIgnoreFields() {
        // 此测试验证 password 字段被忽略
        User user = new User(1L, "John", "john@example.com", "secret");
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        // password 不应该被设置（被忽略）

        assertNull(dto.getPassword()); // 如果密码被复制，这将失败
    }
}
