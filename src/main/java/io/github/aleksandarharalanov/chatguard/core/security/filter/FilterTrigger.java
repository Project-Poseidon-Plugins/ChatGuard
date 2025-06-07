package io.github.aleksandarharalanov.chatguard.core.security.filter;

import org.bukkit.Location;

import io.github.aleksandarharalanov.chatguard.core.config.FilterTerm;

public class FilterTrigger {
    private final String section;
    private final FilterTerm filterTerm;
    private Location violationLocation;

    public FilterTrigger(String section, FilterTerm filterTerm) {
        this.section = section;
        this.filterTerm = filterTerm;
    }

    public String getSection() {
        return section;
    }

    public FilterTerm getFilterTerm() {
        return filterTerm;
    }

    public void setViolationLocation(Location violationLocation) {
        this.violationLocation = violationLocation;
    }

    public Location getViolationLocation() {
        return violationLocation;
    }
}
