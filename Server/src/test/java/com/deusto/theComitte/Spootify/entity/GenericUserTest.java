package com.deusto.theComitte.Spootify.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GenericUserTest {
    
    private GenericUser genericUser;
    private GenericUser sameIdUser;
    private GenericUser differentIdUser;
    
    @BeforeEach
    void setUp() {
        // Create test instances
        genericUser = new User(1L, "Test User", "test@example.com", "password123");
        sameIdUser = new User(1L, "Same ID User", "same@example.com", "samepassword");
        differentIdUser = new User(2L, "Different ID User", "diff@example.com", "diffpassword");
    }
    
    @Test
    @DisplayName("Test full constructor")
    void testFullConstructor() {
        assertEquals(1L, genericUser.getId());
        assertEquals("Test User", genericUser.getName());
        assertEquals("test@example.com", genericUser.getEmail());
        assertEquals("password123", genericUser.getPassword());
    }
    
    @Test
    @DisplayName("Test constructor with name, email, and password")
    void testNameEmailPasswordConstructor() {
        GenericUser user = new User("New User", "new@example.com", "newpassword");
        
        assertEquals(0L, user.getId()); // Default value for unspecified ID
        assertEquals("New User", user.getName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newpassword", user.getPassword());
    }
    
    @Test
    @DisplayName("Test empty constructor")
    void testEmptyConstructor() {
        GenericUser user = new User();
        
        assertEquals(0L, user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }
    
    @Test
    @DisplayName("Test ID setter and getter")
    void testIdSetterGetter() {
        GenericUser user = new User();
        
        user.setId(5L);
        assertEquals(5L, user.getId());
        
        user.setId(10L);
        assertEquals(10L, user.getId());
    }
    
    @Test
    @DisplayName("Test name setter and getter")
    void testNameSetterGetter() {
        GenericUser user = new User();
        assertNull(user.getName());
        
        user.setName("John Doe");
        assertEquals("John Doe", user.getName());
        
        user.setName("Jane Smith");
        assertEquals("Jane Smith", user.getName());
    }
    
    @Test
    @DisplayName("Test email setter and getter")
    void testEmailSetterGetter() {
        GenericUser user = new User();
        assertNull(user.getEmail());
        
        user.setEmail("john@example.com");
        assertEquals("john@example.com", user.getEmail());
        
        user.setEmail("jane@example.com");
        assertEquals("jane@example.com", user.getEmail());
    }
    
    @Test
    @DisplayName("Test password setter and getter")
    void testPasswordSetterGetter() {
        GenericUser user = new User();
        assertNull(user.getPassword());
        
        user.setPassword("secret123");
        assertEquals("secret123", user.getPassword());
        
        user.setPassword("newsecret456");
        assertEquals("newsecret456", user.getPassword());
    }
}