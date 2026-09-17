package task1;

public final class Alert {
    private final String title;
    private final DisasterCategory category;
    private final String affectedLocation;
    private final String severityLevel;
    private final String safetyInstructions;

    public Alert(String title, DisasterCategory category, String affectedLocation,
                 String severityLevel, String safetyInstructions) {
        this.title = title;
        this.category = category;
        this.affectedLocation = affectedLocation;
        this.severityLevel = severityLevel;
        this.safetyInstructions = safetyInstructions;
    }

    public String getTitle() {
        return title;
    }

    public DisasterCategory getCategory() {
        return category;
    }

    public String getAffectedLocation() {
        return affectedLocation;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public String getSafetyInstructions() {
        return safetyInstructions;
    }

    @Override
    public String toString() {
        return "[" + category + "] " + title +
                " | Location: " + affectedLocation +
                " | Severity: " + severityLevel +
                " | Instructions: " + safetyInstructions;
    }
}
