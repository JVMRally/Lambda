package com.jvmrally.lambda.utility;

import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/**
 * Util
 */
public class Util {

    private Util() {
    }

    /**
     * Method to find a role in a guild. If role is not found, returns Optional.empty
     * 
     * @param guild the guild to search
     * @param name  the role to find
     * @return the found role, or Optional.empty
     */
    public static Optional<Role> getRole(Guild guild, String name) {
        List<Role> roles = guild.getRolesByName(name, true);
        if (roles.size() == 1) {
            return Optional.of(roles.get(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Checks whether the target user has a specific role
     * 
     * @param member     the user to check
     * @param targetRole the name of the target role
     * @return
     */
    public static boolean hasRole(Member member, String targetRole) {
        return member.getRoles().stream()
                .filter(role -> role.getName().equalsIgnoreCase(targetRole)).count() == 1;
    }

    public static void addRoleToUser(Guild guild, Member member, String role) {
        getRole(guild, role).ifPresent(r -> guild.addRoleToMember(member, r).queue());
    }

    public static void removeRoleFromUser(Guild guild, Member member, String role) {
        getRole(guild, role).ifPresent(r -> guild.removeRoleFromMember(member, r).queue());
    }
}
