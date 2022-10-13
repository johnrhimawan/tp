package seedu.address.model.internship;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an Internship's task in InTrack.
 * Guarantees: immutable; is valid as declared in {@link #isValidPosition(String)}
 */
public class Task {

    public static final String MESSAGE_CONSTRAINTS =
            "Positions should only contain alphanumeric characters and spaces, and it should not be blank";

    /*
     * The first character of the position must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String taskName;

    /**
     * Constructs a {@code Task}.
     *
     * @param task Name of task to be completed in the internship application.
     */
    public Task(String task) {
        requireNonNull(task);
        checkArgument(isValidPosition(task), MESSAGE_CONSTRAINTS);
        taskName = task;
    }
}
