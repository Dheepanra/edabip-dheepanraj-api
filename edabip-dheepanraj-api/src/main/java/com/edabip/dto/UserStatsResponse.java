package com.edabip.dto;

public class UserStatsResponse {

    private final long total;
    private final long active;
    private final long inactive;
    private final long newThisMonth;

    public UserStatsResponse(long total, long active, long inactive, long newThisMonth) {
        this.total = total;
        this.active = active;
        this.inactive = inactive;
        this.newThisMonth = newThisMonth;
    }

    public long getTotal() { return total; }
    public long getActive() { return active; }
    public long getInactive() { return inactive; }
    public long getNewThisMonth() { return newThisMonth; }
}
