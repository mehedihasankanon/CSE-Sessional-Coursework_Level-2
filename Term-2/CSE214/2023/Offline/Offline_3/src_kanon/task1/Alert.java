package task1;

import java.time.LocalDateTime;

public class Alert {
    private final String title;
    private final Category cat;
    private final String location;
    private final int severityLevel;
    private final String instructions;
    private final String msg;

    private Alert(Builder builder) {
        this.title = builder.title;
        this.cat = builder.cat;
        this.location = builder.location;
        this.severityLevel = builder.severityLevel;
        this.instructions = builder.instructions;
        this.msg = builder.msg;
    }

    public Category getCat() {
        return cat;
    }

    public String getMsg() {
        return String.format("%s | %s | Loc: %s | Severity: %d | Instructions: %s",
                msg, title, location, severityLevel, instructions);
    }

    public static class Builder {
        private final Category cat;
        private final String msg;
        private String title = "General Alert";
        private String location = "Unknown";
        private int severityLevel = 1;
        private String instructions = "No specific instructions.";

        public Builder(Category cat, String msg) {
            this.cat = cat;
            this.msg = msg;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder severityLevel(int severityLevel) {
            this.severityLevel = severityLevel;
            return this;
        }

        public Builder instructions(String instructions) {
            this.instructions = instructions;
            return this;
        }

        public Alert build() {
            return new Alert(this);
        }
    }

    private static String getCurrentTime() {
        return LocalDateTime.now().toString();
    }

    private static String alertMsg(Category alert) {
        return "[ " + getCurrentTime() + " ]: " + alert.toString() + ' ' + "ALERT";
    }

    public static Alert generateAlert(Category cat) {
        return new Alert.Builder(cat, alertMsg(cat))
                .title(cat.toString() + " Warning")
                .location("Dhaka Division")
                .severityLevel(8)
                .instructions("Evacuate immediately to higher ground.")
                .build();
    }
}