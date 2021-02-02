/**
 * Copyright (C) 2010-16 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rvesse.airline.restrictions.common;

import java.io.File;
import com.github.rvesse.airline.annotations.restrictions.PathKind;
import com.github.rvesse.airline.help.sections.HelpFormat;
import com.github.rvesse.airline.help.sections.HelpHint;
import com.github.rvesse.airline.model.ArgumentsMetadata;
import com.github.rvesse.airline.model.OptionMetadata;
import com.github.rvesse.airline.model.PositionalArgumentMetadata;
import com.github.rvesse.airline.parser.ParseState;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction;

/**
 * Implements restriction on options and arguments that expect to receive a path
 * to a file and/or directory
 * 
 * @author rvesse
 *
 */
public class PathRestriction extends AbstractCommonRestriction implements HelpHint {

    private final boolean mustExist, readable, writable, executable;
    private final PathKind kind;

    /**
     * Creates a new path restriction
     * 
     * @param mustExist
     *            Whether the path must exist
     * @param readable
     *            Whether the path must be readable, if the specific path does
     *            not exist then this check validates that the first parent in
     *            the path that exists is readable
     * @param writable
     *            Whether the path must be writable, if the specific path does
     *            not exist then this check validates that the first parent in
     *            the path that exists is writable
     * @param executable
     *            Whether the path must be executable, if the specific path does
     *            not exist then this check validates that the first parent in
     *            the path that exists is executable
     * @param kind
     */
    public PathRestriction(boolean mustExist, boolean readable, boolean writable, boolean executable, PathKind kind) {
        this.mustExist = mustExist;
        this.readable = readable;
        this.writable = writable;
        this.executable = executable;
        this.kind = kind;
    }

    protected void validate(String title, String path) {
        if (path == null)
            throw new ParseRestrictionViolatedException("%s must be given a non-null path", title, path);

        File f = new File(path);

        if (this.mustExist && !f.exists())
            throw new ParseRestrictionViolatedException(
                    "Option value '%s' was given value '%s' which is not a path to an existing file/directory", title, path);

        if (this.mustExist && f.exists()) {
            // Some things require the file to exist in order for direct
            // validation to be applied
            switch (kind) {
            case FILE:
                if (!f.isFile())
                    throw new ParseRestrictionViolatedException("%s was given value '%s' which is not a path to a file",
                            title, path);
                break;
            case DIRECTORY:
                if (!f.isDirectory())
                    throw new ParseRestrictionViolatedException(
                            "%s was given value '%s' which is not a path to a directory", title, path);
                break;
            default:
                if (!f.isFile() && !f.isDirectory())
                    throw new ParseRestrictionViolatedException(
                            "%s was given value '%s' which is not a path to a file/directory", title, path);
                break;
            }

            // Check permissions
            if (this.readable && !f.canRead())
                notReadable(title, path);

            if (this.writable && !f.canWrite())
                notWritable(title, path);

            if (this.executable && !f.canExecute())
                notExecutable(title, path);
        } else if (this.readable || this.writable || this.executable) {
            if (!f.isAbsolute())
                f = f.getAbsoluteFile();

            // May be able to check the conditions directly if the file exists
            if (f.exists()) {
                if (this.readable && !f.canRead())
                    notReadable(title, path);

                if (this.writable && !f.canWrite())
                    notWritable(title, path);

                if (this.executable && !f.canExecute())
                    notExecutable(title, path);
            } else {
                // Otherwise verify that the first portion of the path that
                // exists has the desired properties
                while (f.getParentFile() != null) {
                    f = f.getParentFile();
                    if (f.exists()) {
                        // All conditions must be met by first part of the path
                        // that exists
                        if (this.readable && !f.canRead())
                            notReadable(title, path);

                        if (this.writable && !f.canWrite())
                            notWritable(title, path);

                        if (this.executable && !f.canExecute())
                            notExecutable(title, path);

                        // All conditions satisfied so no need to continue going
                        // up the path
                        break;
                    }
                }
            }
        }
    }

    private void notExecutable(String title, String path) {
        throw new ParseRestrictionViolatedException("%s was given value '%s' which is not a executable path", title,
                path);
    }

    private void notWritable(String title, String path) {
        throw new ParseRestrictionViolatedException("%s was given value '%s' which is not a writeable path", title,
                path);
    }

    private void notReadable(String title, String path) {
        throw new ParseRestrictionViolatedException("%s was given value '%s' which it not a readable path", title,
                path);
    }

    @Override
    public <T> void preValidate(ParseState<T> state, OptionMetadata option, String value) {
        this.validate(String.format("Option '%s'", AbstractCommonRestriction.getOptionTitle(state, option)), value);
    }

    @Override
    public <T> void preValidate(ParseState<T> state, ArgumentsMetadata arguments, String value) {
        this.validate(String.format("Argument '%s'", AbstractCommonRestriction.getArgumentTitle(state, arguments)),
                value);
    }
    
    @Override
    public <T> void preValidate(ParseState<T> state, PositionalArgumentMetadata arguments, String value) {
        this.validate(String.format("Positional Argument %d ('%s')", arguments.getZeroBasedPosition(), arguments.getTitle()),
                value);
    }

    @Override
    public String getPreamble() {
        return null;
    }

    @Override
    public HelpFormat getFormat() {
        return HelpFormat.PROSE;
    }

    @Override
    public int numContentBlocks() {
        return 1;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public String[] getContentBlock(int blockNumber) {
        if (blockNumber != 0)
            throw new IndexOutOfBoundsException();

        StringBuilder builder = new StringBuilder();
        switch (this.kind) {
        case FILE:
            builder.append("This options value must be a path to a file.");
            break;
        case DIRECTORY:
            builder.append("This options value must be a path to a directory.");
            break;
        }
        if (this.mustExist) {
            if (builder.length() == 0) {
                builder.append("This options value must be a path that must exist on the file system.");
            } else {
                builder.append(" The provided path must exist on the file system.");
            }
        }
        if (this.readable) {
            if (builder.length() == 0) {
                builder.append("This options value must be a path on the file system that must be readable");
            } else {
                builder.append(" The provided path must be readable");
            }
        }
        if (this.writable) {
            if (builder.length() == 0) {
                builder.append("This options value must be a path on the file system that must be writable");
            } else if (this.readable) {
                if (this.executable) {
                    builder.append(", writable");
                } else {
                    builder.append(" and writable");
                }
            } else {
                builder.append(" The provided path must be writable");
            }
        }
        if (this.executable) {
            if (builder.length() == 0) {
                builder.append("This options value must be a path on the file system that must be executable");
            } else if (this.readable || this.writable) {
                builder.append(" and executable");
            } else {
                builder.append(" The provided path must be executable");
            }
        }
        if (this.readable || this.writable || this.executable)
            builder.append('.');

        if (builder.length() == 0)
            builder.append("This options value must be a path.");

        return new String[] { builder.toString() };
    }

}
