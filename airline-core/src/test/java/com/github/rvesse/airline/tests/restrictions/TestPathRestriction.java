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
package com.github.rvesse.airline.tests.restrictions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException;

public class TestPathRestriction {

    @Test
    public void path_restriction_must_exist_01() throws IOException {
        File f = new File("target/paths.txt");
        try {
            // Populate the file to make sure it exists
            try (FileWriter writer = new FileWriter(f)) {
                writer.append("test");
                writer.close();
            }

            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);
            Paths cmd = parser.parse("--path", f.getPath());
            Assert.assertEquals(cmd.pathMustExist, f.getPath());

            cmd = parser.parse("--file", f.getPath());
            Assert.assertEquals(cmd.fileMustExit, f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void path_restriction_must_exist_02() throws IOException {
        File f = new File("target/paths.txt");
        try {
            // Populate the file to make sure it exists
            try (FileWriter writer = new FileWriter(f)) {
                writer.append("test");
                writer.close();
            }

            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // Trying to parse a file to an argument that specified to require a
            // directory should produce an exception
            parser.parse("--directory", f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void path_restriction_must_exist_03() throws IOException {
        File f = new File("target/paths.txt");
        SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

        // Trying to parse a non-existent file should throw an exception
        parser.parse("--path", f.getPath());
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void path_restriction_must_exist_04() throws IOException {
        File f = new File("target/paths.txt");
        SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

        // Trying to parse a non-existent file should throw an exception
        parser.parse("--file", f.getPath());
    }

    @Test
    public void path_restriction_readable_01() throws IOException {
        File f = new File("target/paths.txt");
        try {
            // Populate the file to make sure it exists
            try (FileWriter writer = new FileWriter(f)) {
                writer.append("test");
                writer.close();
            }
            f.setReadable(true);
            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // OK as path is readable
            Paths cmd = parser.parse("--readable", f.getPath());
            Assert.assertEquals(cmd.readable, f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test
    public void path_restriction_readable_02() throws IOException {
        File f = new File("target/paths.txt");
        try {
            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // OK as parent path is readable even though actual file does not
            // exist
            Assert.assertFalse(f.exists());
            Paths cmd = parser.parse("--readable", f.getPath());
            Assert.assertEquals(cmd.readable, f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void path_restriction_readable_03() throws IOException {
        File f = new File("target/paths.txt");
        try {
            // Populate the file to make sure it exists
            try (FileWriter writer = new FileWriter(f)) {
                writer.append("test");
                writer.close();
            }
            f.setReadable(false);
            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // Should fail as path is not readable
            parser.parse("--readable", f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test
    public void path_restriction_writable_01() throws IOException {
        File f = new File("target/paths.txt");
        try {
            // Populate the file to make sure it exists
            try (FileWriter writer = new FileWriter(f)) {
                writer.append("test");
                writer.close();
            }
            f.setWritable(true);
            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // OK as path is writeable
            Paths cmd = parser.parse("--writable", f.getPath());
            Assert.assertEquals(cmd.writable, f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test
    public void path_restriction_writable_02() throws IOException {
        File f = new File("target/paths.txt");
        try {
            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // OK as parent path is writable even though actual file does not
            // exist
            Assert.assertFalse(f.exists());
            Paths cmd = parser.parse("--writable", f.getPath());
            Assert.assertEquals(cmd.writable, f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void path_restriction_writeable_03() throws IOException {
        File f = new File("target/paths.txt");
        try {
            // Populate the file to make sure it exists
            try (FileWriter writer = new FileWriter(f)) {
                writer.append("test");
                writer.close();
            }
            f.setWritable(false);
            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // Should fail as path is not writable
            parser.parse("--writable", f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test
    public void path_restriction_executable_01() throws IOException {
        File f = new File("target/paths.txt");
        try {
            // Populate the file to make sure it exists
            try (FileWriter writer = new FileWriter(f)) {
                writer.append("test");
                writer.close();
            }
            f.setExecutable(true);
            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // OK as path is executable
            Paths cmd = parser.parse("--executable", f.getPath());
            Assert.assertEquals(cmd.executable, f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test
    public void path_restriction_executable_02() throws IOException {
        File f = new File("target/paths.txt");
        try {
            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // OK as parent path is executable even though actual file does not
            // exist
            Assert.assertFalse(f.exists());
            Paths cmd = parser.parse("--executable", f.getPath());
            Assert.assertEquals(cmd.executable, f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test(expectedExceptions = ParseRestrictionViolatedException.class)
    public void path_restriction_executable_03() throws IOException {
        File f = new File("target/paths.txt");
        try {
            // Populate the file to make sure it exists
            try (FileWriter writer = new FileWriter(f)) {
                writer.append("test");
                writer.close();
            }
            f.setExecutable(false);
            SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);

            // Should fail as path is not executable
            parser.parse("--executable", f.getPath());
        } finally {
            if (f.exists())
                f.delete();
        }
    }

    @Test
    public void path_restriction_help() throws IOException {
        SingleCommand<Paths> parser = SingleCommand.<Paths> singleCommand(Paths.class);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Help.help(parser.getCommandMetadata(), output);

        String usage = new String(output.toByteArray(), StandardCharsets.UTF_8);
        // System.out.println(usage);

        // Remove the line breaks and indentation to ensure we can reliably
        // match the output
        usage = usage.replace('\n', ' ');
        usage = usage.replaceAll("(\\s)\\s+", "$1");
        Assert.assertTrue(usage.contains("must be a path"));
        Assert.assertTrue(usage.contains("must be a path to a file"));
        Assert.assertTrue(usage.contains("must be a path to a directory"));
        Assert.assertTrue(usage.contains("must be readable"));
        Assert.assertTrue(usage.contains("must be readable and writable"));
        Assert.assertTrue(usage.contains("must be executable"));
        Assert.assertTrue(usage.contains("must exist"));
    }
}
