/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.tools.proto;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import io.spine.tools.AbstractSourceFile;
import io.spine.type.RejectionMessage;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Protobuf file which also gives access to its {@link FileDescriptorProto descriptor}.
 *
 * @author Alexander Yevsyukov
 */
public final class SourceFile extends AbstractSourceFile {

    private final FileDescriptorProto file;

    private SourceFile(FileDescriptorProto file) {
        super(toPath(file));
        this.file = file;
    }

    public static SourceFile from(FileDescriptorProto file) {
        final SourceFile result = new SourceFile(file);
        return result;
    }

    private static Path toPath(FileDescriptorProto file) {
        checkNotNull(file);
        final Path result = Paths.get(file.getName());
        return result;
    }

    public static boolean isRejectionsFile(FileDescriptorProto descriptor) {
        // By convention rejections are generated into one file.
        if (descriptor.getOptions()
                      .getJavaMultipleFiles()) {
            return false;
        }
        final String javaOuterClassName = descriptor.getOptions()
                                                    .getJavaOuterClassname();
        if (javaOuterClassName.isEmpty()) {
            // There's no outer class name given in options.
            // Assuming the file name ends with `rejections.proto`, it's a good rejections file.
            return true;
        }

        final boolean result =
                javaOuterClassName.endsWith(RejectionMessage.OUTER_CLASS_NAME_SUFFIX);
        return result;
    }

    public FileDescriptorProto getDescriptor() {
        return file;
    }
}