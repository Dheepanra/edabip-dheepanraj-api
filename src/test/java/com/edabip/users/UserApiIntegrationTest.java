package com.edabip.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // each test rolls back, so the seed data stays intact
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String NEW_USER =
            "{\"name\":\"Test User\",\"email\":\"test.user@example.com\",\"role\":\"Editor\"}";

    @Test
    void listUsers_returnsPagedSeedData() throws Exception {
        mockMvc.perform(get("/api/v1/users?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalElements", is(15)))
                .andExpect(jsonPath("$.data.totalPages", is(2)))
                .andExpect(jsonPath("$.data.content.length()", is(10)));
    }

    @Test
    void searchUsers_filtersByName() throws Exception {
        mockMvc.perform(get("/api/v1/users?search=alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements", is(1)))
                .andExpect(jsonPath("$.data.content[0].userId", is("USR001")));
    }

    @Test
    void getUser_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/users/USR999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void addUser_createsWithGeneratedIdAndDefaultStatus() throws Exception {
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(NEW_USER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId", is("USR016")))
                .andExpect(jsonPath("$.data.status", is("Active")));
    }

    @Test
    void addUser_duplicateEmail_returns409() throws Exception {
        String dup = "{\"name\":\"Another Alice\",\"email\":\"alice@example.com\",\"role\":\"User\"}";
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(dup))
                .andExpect(status().isConflict());
    }

    @Test
    void addUser_invalidBody_returns400WithFieldErrors() throws Exception {
        String bad = "{\"name\":\"\",\"email\":\"not-an-email\",\"role\":\"Boss\"}";
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(bad))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.role").exists());
    }

    @Test
    void editUser_updatesFields() throws Exception {
        String body = "{\"name\":\"Alice J\",\"email\":\"alice@example.com\",\"role\":\"Viewer\",\"status\":\"Inactive\"}";
        mockMvc.perform(put("/api/v1/users/USR001").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role", is("Viewer")))
                .andExpect(jsonPath("$.data.status", is("Inactive")));
    }

    @Test
    void deleteUser_thenGetReturns404() throws Exception {
        mockMvc.perform(delete("/api/v1/users/USR015")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/users/USR015")).andExpect(status().isNotFound());
    }

    @Test
    void stats_returnsCounts() throws Exception {
        mockMvc.perform(get("/api/v1/users/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalUsers", is(15)))
                .andExpect(jsonPath("$.data.activeUsers", is(11)))
                .andExpect(jsonPath("$.data.inactiveUsers", is(4)));
    }

    @Test
    void invalidPagination_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/users?page=0&size=500"))
                .andExpect(status().isBadRequest());
    }
}
