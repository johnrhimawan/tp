package seedu.intrack.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.intrack.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.intrack.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.intrack.logic.parser.CliSyntax.PREFIX_POSITION;
import static seedu.intrack.logic.parser.CliSyntax.PREFIX_SALARY;
import static seedu.intrack.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.intrack.logic.parser.CliSyntax.PREFIX_WEBSITE;
import static seedu.intrack.model.Model.PREDICATE_SHOW_ALL_INTERNSHIPS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.intrack.commons.core.Messages;
import seedu.intrack.commons.core.index.Index;
import seedu.intrack.commons.util.CollectionUtil;
import seedu.intrack.logic.commands.exceptions.CommandException;
import seedu.intrack.model.Model;
import seedu.intrack.model.internship.Email;
import seedu.intrack.model.internship.Internship;
import seedu.intrack.model.internship.Name;
import seedu.intrack.model.internship.Position;
import seedu.intrack.model.internship.Remark;
import seedu.intrack.model.internship.Salary;
import seedu.intrack.model.internship.Status;
import seedu.intrack.model.internship.Task;
import seedu.intrack.model.internship.Website;
import seedu.intrack.model.tag.Tag;

/**
 * Edits the details of an existing internship in the tracker.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the internship identified by "
            + "the index number used in the displayed list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_POSITION + "POSITION] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_WEBSITE + "WEBSITE] "
            + "[" + PREFIX_SALARY + "SALARY] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_SALARY + "200000 "
            + PREFIX_EMAIL + "newemail@example.com";

    public static final String MESSAGE_EDIT_INTERNSHIP_SUCCESS = "Edited internship: %1$s";

    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    public static final String MESSAGE_DUPLICATE_INTERNSHIP = "This internship already exists in the tracker.";

    private final Index index;
    private final EditInternshipDescriptor editInternshipDescriptor;

    /**
     * @param index of the internship in the filtered internship list to edit
     * @param editInternshipDescriptor details to edit the internship with
     */
    public EditCommand(Index index, EditInternshipDescriptor editInternshipDescriptor) {
        requireNonNull(index);
        requireNonNull(editInternshipDescriptor);

        this.index = index;
        this.editInternshipDescriptor = new EditInternshipDescriptor(editInternshipDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Internship> lastShownList = model.getFilteredInternshipList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_INTERNSHIP_DISPLAYED_INDEX);
        }

        Internship internshipToEdit = lastShownList.get(index.getZeroBased());
        Internship editedInternship = createEditedInternship(internshipToEdit, editInternshipDescriptor);

        if (!internshipToEdit.isSameInternship(editedInternship) && model.hasInternship(editedInternship)) {
            throw new CommandException(MESSAGE_DUPLICATE_INTERNSHIP);
        }

        model.setInternship(internshipToEdit, editedInternship);
        model.updateFilteredInternshipList(PREDICATE_SHOW_ALL_INTERNSHIPS);
        return new CommandResult(String.format(MESSAGE_EDIT_INTERNSHIP_SUCCESS, editedInternship));
    }

    /**
     * Creates and returns a {@code Internship} with the details of {@code internshipToEdit}
     * edited with {@code editInternshipDescriptor}.
     */
    private static Internship createEditedInternship(Internship internshipToEdit,
                                                     EditInternshipDescriptor editInternshipDescriptor) {
        assert internshipToEdit != null;

        Name updatedName = editInternshipDescriptor.getName().orElse(internshipToEdit.getName());
        Position updatedPosition = editInternshipDescriptor.getPosition().orElse(internshipToEdit.getPosition());
        Status updatedStatus = internshipToEdit.getStatus(); // edit command does not allow editing status
        Email updatedEmail = editInternshipDescriptor.getEmail().orElse(internshipToEdit.getEmail());
        Salary updatedSalary = editInternshipDescriptor.getSalary().orElse(internshipToEdit.getSalary());
        Website updatedWebsite = editInternshipDescriptor.getWebsite().orElse(internshipToEdit.getWebsite());
        List<Task> updatedTasks = internshipToEdit.getTasks();
        Set<Tag> updatedTags = editInternshipDescriptor.getTags().orElse(internshipToEdit.getTags());
        Remark updatedRemark = internshipToEdit.getRemark(); // edit command does not allow editing remarks

        return new Internship(updatedName, updatedPosition, updatedStatus, updatedEmail, updatedWebsite,
                updatedTasks, updatedSalary, updatedTags, updatedRemark);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editInternshipDescriptor.equals(e.editInternshipDescriptor);
    }

    /**
     * Stores the details to edit the internship with. Each non-empty field value will replace the
     * corresponding field value of the internship.
     */
    public static class EditInternshipDescriptor {
        private Name name;
        private Position position;
        private Email email;
        private Salary salary;
        private Website website;
        private Set<Tag> tags;

        public EditInternshipDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditInternshipDescriptor(EditInternshipDescriptor toCopy) {
            setName(toCopy.name);
            setPosition(toCopy.position);
            setEmail(toCopy.email);
            setWebsite(toCopy.website);
            setSalary(toCopy.salary);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, position, email, website, salary, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        public Optional<Position> getPosition() {
            return Optional.ofNullable(position);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setSalary(Salary salary) {
            this.salary = salary;
        }

        public Optional<Salary> getSalary() {
            return Optional.ofNullable(salary);
        }

        public void setWebsite(Website website) {
            this.website = website;
        }

        public Optional<Website> getWebsite() {
            return Optional.ofNullable(website);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditInternshipDescriptor)) {
                return false;
            }

            // state check
            EditInternshipDescriptor e = (EditInternshipDescriptor) other;

            return getName().equals(e.getName())
                    && getPosition().equals(e.getPosition())
                    && getEmail().equals(e.getEmail())
                    && getWebsite().equals(e.getWebsite())
                    && getSalary().equals(e.getSalary())
                    && getTags().equals(e.getTags());
        }
    }

}
