package cz.forgottenempire.servermanager.journey;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleManagementJourneyTest extends AbstractIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roleManagement_createAndManageRole_happyPath() throws Exception {
        // Create custom role with specific permissions
        String createRoleBody = """
                {
                    "name": "EditorRole",
                    "description": "Can edit scenarios",
                    "permissions": ["SCENARIO_MODIFY"]
                }
                """;
        String roleResponse = api().postJson("/api/roles", createRoleBody)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long roleId = mapper.readTree(roleResponse).get("id").asLong();

        // Update role permissions
        String updateRoleBody = """
                {
                    "description": "Can view and edit scenarios",
                    "permissions": ["SCENARIO_VIEW", "SCENARIO_MODIFY"]
                }
                """;
        api().put("/api/roles/" + roleId, updateRoleBody)
                .andExpect(status().isOk());

        // Delete the role
        api().delete("/api/roles/" + roleId)
                .andExpect(status().isNoContent());

        // Verify role is deleted
        api().get("/api/roles/" + roleId)
                .andExpect(status().isNotFound());
    }

    @Test
    void roleManagement_deleteBuiltInRole_fails() throws Exception {
        // Get list of roles to find Admin (built-in)
        String rolesResponse = api().get("/api/roles")
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long adminRoleId = null;
        for (var role : mapper.readTree(rolesResponse)) {
            if (role.get("name").asText().equals("Admin")) {
                adminRoleId = role.get("id").asLong();
                break;
            }
        }
        if (adminRoleId == null) {
            throw new IllegalStateException("Admin role not found");
        }

        // Try to delete built-in role
        api().delete("/api/roles/" + adminRoleId)
                .andExpect(status().isBadRequest());
    }

    @Test
    void roleManagement_deleteRoleWithUsers_fails() throws Exception {
        // Create a role
        String createRoleBody = """
                {
                    "name": "TempRole",
                    "description": "Temporary role",
                    "permissions": ["SYSTEM_VIEW"]
                }
                """;
        String roleResponse = api().postJson("/api/roles", createRoleBody)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long roleId = mapper.readTree(roleResponse).get("id").asLong();

        // Create and assign user to role
        String createUserBody = """
                {
                    "username": "temproleuser",
                    "password": "testpass123"
                }
                """;
        String userResponse = api().postJson("/api/users", createUserBody)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long userId = mapper.readTree(userResponse).get("id").asLong();

        String assignRoleBody = String.format("{\"roleIds\": [%d]}", roleId);
        api().put("/api/users/" + userId + "/roles", assignRoleBody)
                .andExpect(status().isOk());

        // Try to delete role while it's assigned to a user
        api().delete("/api/roles/" + roleId)
                .andExpect(status().isConflict());
    }

    @Test
    void roleManagement_createDuplicateRole_fails() throws Exception {
        String createRoleBody = """
                {
                    "name": "DuplicateRole",
                    "permissions": ["SYSTEM_VIEW"]
                }
                """;
        api().postJson("/api/roles", createRoleBody)
                .andExpect(status().isCreated());

        // Try to create another role with same name
        api().postJson("/api/roles", createRoleBody)
                .andExpect(status().isConflict());
    }

    @Test
    void roleManagement_createRoleWithUnknownPermission_fails() throws Exception {
        String createRoleBody = """
                {
                    "name": "InvalidRole",
                    "permissions": ["UNKNOWN_PERMISSION_CODE"]
                }
                """;
        api().postJson("/api/roles", createRoleBody)
                .andExpect(status().isBadRequest());
    }
}
