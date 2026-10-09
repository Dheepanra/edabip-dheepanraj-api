package com.edabip.users.dto;

public class UserStatsResponse {

    private final long totalUsers;
    private final long activeUsers;
    private final long inactiveUsers;
    private final long newThisMonth;

    public UserStatsResponse(long totalUsers, long activeUsers, long inactiveUsers, long newThisMonth) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
        this.newThisMonth = newThisMonth;
    }

    public long getTotalUsers() { return totalUsers; }
    public long getActiveUsers() { return activeUsers; }
    public long getInactiveUsers() { return inactiveUsers; }
    public long getNewThisMonth() { return newThisMonth; }
}
