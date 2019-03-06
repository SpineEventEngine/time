/*
 * Copyright 2019, TeamDev. All rights reserved.
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

package io.spine.time.string;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import io.spine.string.SerializableStringifier;

import java.text.ParseException;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static io.spine.util.Exceptions.newIllegalArgumentException;

/**
 * The stringifier for web-safe representation of timestamps.
 *
 * <p>The stringifier replaces colons in the time part of a a RFC 3339 date string
 * with dashes when converting a timestamp to a string. It also restores the colons
 * back during the backward conversion.
 */
final class WebSafeTimestampStringifier extends SerializableStringifier<Timestamp> {

    private static final long serialVersionUID = 0L;
    private static final WebSafeTimestampStringifier INSTANCE = new WebSafeTimestampStringifier();

    private static final char COLON = ':';
    private static final Pattern PATTERN_COLON = Pattern.compile(String.valueOf(COLON));
    private static final String DASH = "-";

    /**
     * The index of a character separating hours and minutes.
     */
    private static final int HOUR_SEPARATOR_INDEX = 13;
    /**
     * The index of a character separating minutes and seconds.
     */
    private static final int MINUTE_SEPARATOR_INDEX = 16;

    private WebSafeTimestampStringifier() {
        super("TimeStringifiers.forTimestampWebSafe()");
    }

    static WebSafeTimestampStringifier instance() {
        return INSTANCE;
    }

    /**
     * Converts the passed timestamp string into a web-safe string, replacing colons to dashes.
     */
    private static String toWebSafe(String str) {
        final String result = PATTERN_COLON.matcher(str)
                                           .replaceAll(DASH);
        return result;
    }

    /**
     * Converts the passed web-safe timestamp representation to the RFC 3339 date string format.
     */
    private static String fromWebSafe(String webSafe) {
        checkArgument(webSafe.length() > MINUTE_SEPARATOR_INDEX + 2,
                      "The passed string (%) is not in web-safe date/time format",
                      webSafe);
        char[] chars = webSafe.toCharArray();
        chars[HOUR_SEPARATOR_INDEX] = COLON;
        chars[MINUTE_SEPARATOR_INDEX] = COLON;
        return String.valueOf(chars);
    }

    @Override
    protected String toString(Timestamp value) {
        String result = Timestamps.toString(value);
        result = toWebSafe(result);
        return result;
    }

    @Override
    protected Timestamp fromString(String webSafe) {
        try {
            String rfcStr = fromWebSafe(webSafe);
            return Timestamps.parse(rfcStr);
        } catch (ParseException e) {
            throw newIllegalArgumentException(e.getMessage(), e);
        }
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
