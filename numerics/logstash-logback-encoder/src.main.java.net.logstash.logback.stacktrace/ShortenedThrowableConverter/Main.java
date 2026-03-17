/** filtered and transformed by ARG-V */

/*
 * Copyright 2013-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.sosy_lab.sv_benchmarks.Verifier;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A {@link ThrowableHandlingConverter} (similar to logback's {@link ThrowableProxyConverter})
 * that formats stacktraces by doing the following:
 *
 * <ul>
 * <li>Limits the number of stackTraceElements per throwable
 *     (applies to each individual throwable.  e.g. caused-bys and suppressed).
 *     See {@link #maxDepthPerThrowable}.</li>
 * <li>Limits the total length in characters of the trace.
 *     See {@link #maxLength}.</li>
 * <li>Abbreviates class names based.
 *     See {@link #setShortenedClassNameLength(int)}.</li>
 * <li>Filters out consecutive unwanted stackTraceElements based on regular expressions.
 *     See {@link #excludes}.</li>
 * <li>Truncate individual stacktraces after any element matching one the configured
 *     regular expression.
 *     See {@link #truncateAfterPatterns}.
 * <li>Uses evaluators to determine if the stacktrace should be logged.
 *     See {@link #evaluators}.</li>
 * <li>Outputs in either 'normal' order (root-cause-last), or root-cause-first.
 *     See {@link #rootCauseFirst}.</li>
 * </ul>
 *
 * To use this with a {@link PatternLayout}, you must configure {@code conversionRule}
 * as described <a href="http://logback.qos.ch/manual/layouts.html#customConversionSpecifier">here</a>.
 * Options can be specified in the pattern in the following order:
 * <ol>
 * <li>maxDepthPerThrowable = "full" or "short" or an integer value</li>
 * <li>shortenedClassNameLength = "full" or "short" or an integer value</li>
 * <li>maxLength = "full" or "short" or an integer value</li>
 * </ol>
 *
 * The other options can be listed in any order and are interpreted as follows:
 * <ul>
 * <li>"rootFirst" - indicating that stacks should be printed root-cause first
 * <li>"inlineHash" - indicating that hexadecimal error hashes should be computed and inlined
 * <li>"inline" - indicating that the whole stack trace should be inlined, using "\\n" as separator
 * <li>"omitCommonFrames" - omit common frames
 * <li>"keepCommonFrames" - keep common frames
 * <li>evaluator name - name of evaluators that will determine if the stacktrace is ignored
 * <li>exclusion pattern - pattern for stack trace elements to exclude
 * </ul>
 *
 * <p>
 * For example,
 * <pre>
 * {@code
 *     <conversionRule conversionWord="stack"
 *                   converterClass="net.logstash.logback.stacktrace.ShortenedThrowableConverter" />
 *
 *     <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
 *         <encoder>
 *             <pattern>[%thread] - %msg%n%stack{5,1024,10,rootFirst,omitCommonFrames,regex1,regex2,evaluatorName}</pattern>
 *         </encoder>
 *     </appender>
 * }
 * </pre>
 */
public class Main {

    public static int FULL_MAX_DEPTH_PER_THROWABLE = Integer.MAX_VALUE;
    public static int SHORT_MAX_DEPTH_PER_THROWABLE = 3;
    public static int DEFAULT_MAX_DEPTH_PER_THROWABLE = FULL_MAX_DEPTH_PER_THROWABLE;

    public static int FULL_MAX_LENGTH = Integer.MAX_VALUE;
    public static int SHORT_MAX_LENGTH = 1024;
    public static int DEFAULT_MAX_LENGTH = FULL_MAX_LENGTH;

    public static int FULL_CLASS_NAME_LENGTH = -1;
    public static int SHORT_CLASS_NAME_LENGTH = 10;
    public static int DEFAULT_CLASS_NAME_LENGTH = FULL_CLASS_NAME_LENGTH;

    private static String ELLIPSIS = "...";
    private static int BUFFER_INITIAL_CAPACITY = 4096;

    private static String OPTION_VALUE_FULL = "full";
    private static String OPTION_VALUE_SHORT = "short";
    private static String OPTION_VALUE_ROOT_FIRST = "rootFirst";
    private static String OPTION_VALUE_INLINE_HASH = "inlineHash";
    private static String OPTION_VALUE_OMITCOMMONFRAMES = "omitCommonFrames";
    private static String OPTION_VALUE_KEEPCOMMONFRAMES = "keepCommonFrames";
    private static String OPTION_VALUE_INLINE_STACK = "inline";

    private static int OPTION_INDEX_MAX_DEPTH = 0;
    private static int OPTION_INDEX_SHORTENED_CLASS_NAME = 1;
    private static int OPTION_INDEX_MAX_LENGTH = 2;

    /**
     * String sequence to use to delimit lines instead of {@link CoreConstants#LINE_SEPARATOR}
     * when inline is active
     */
    public static String DEFAULT_INLINE_SEPARATOR = "\\n";

    private AtomicInteger errorCount = new AtomicInteger();

    /**
     * Maximum number of stackTraceElements printed per throwable.
     */
    private int maxDepthPerThrowable = DEFAULT_MAX_DEPTH_PER_THROWABLE;

    /**
     * Maximum number of characters in the entire stacktrace.
     */
    private int maxLength = DEFAULT_MAX_LENGTH;

    /**
     * Patterns used to determine which stacktrace elements to exclude.
     *
     * The strings being matched against are in the form "fullyQualifiedClassName.methodName"
     * (e.g. "java.lang.Object.toString").
     *
     * Note that these elements will only be excluded if and only if
     * more than one consecutive line matches an exclusion pattern.
     */
    private List<Pattern> excludes = new ArrayList<>();

    /**
     * Patterns used to determine after which element the stack trace must be truncated.
     *
     * The strings being matched against are in the form "fullyQualifiedClassName.methodName"
     * (e.g. "java.lang.Object.toString").
     */
    private List<Pattern> truncateAfterPatterns = new ArrayList<>();

    /**
     * True to print the root cause first.  False to print exceptions normally (root cause last).
     */
    private boolean rootCauseFirst = Verifier.nondetBoolean();

    /**
     * True to compute and inline stack hashes.
     */
    private boolean inlineHash = Verifier.nondetBoolean();

    /**
     * True to omit common frames
     */
    private boolean omitCommonFrames = true;

    /** line delimiter */
    private String lineSeparator = Verifier.nondetString();

    public void start() {
        parseOptions();
    }

    private void parseOptions() {
        List<String> optionList = null;

        if (optionList == null) {
            assert true; //inline assert generated by ARG-V
			return;
        }
        final int optionListSize = optionList.size();
        for (int i = 0; i < optionListSize; i++) {
            String option = optionList.get(i);
        }
    }

    /** ARG-V: suitable */
	 private int parseIntegerOptionValue(String option, int valueIfFull, int valueIfShort, int valueIfNonParsable) {
        if (OPTION_VALUE_FULL.equals(option)) {
            assert true; //inline assert generated by ARG-V
			return valueIfFull;
        } else if (OPTION_VALUE_SHORT.equals(option)) {
            assert true; //inline assert generated by ARG-V
			return valueIfShort;
        } else {
            try {
                assert true; //inline assert generated by ARG-V
				return Integer.parseInt(option);
            } catch (Exception e) {
                assert true; //inline assert generated by ARG-V
				return valueIfNonParsable;
            }
        }
    }


    public String getLineSeparator() {
        assert true; //inline assert generated by ARG-V
		return Verifier.nondetString();
    }


    /**
     * Appends a placeholder indicating that some frames were not written.
     */
    /** ARG-V: suitable */
	 private void appendPlaceHolder(StringBuilder builder, int indent, int consecutiveExcluded, String message) {
        builder.append(ELLIPSIS)
                .append(" ")
                .append(consecutiveExcluded)
                .append(" ")
                .append(message)
                .append(getLineSeparator());
    }

    /**
     * Set the length to which class names should be abbreviated.
     * Cannot be used if a custom {@link Abbreviator} has been set through {@link #setClassNameAbbreviator(Abbreviator)}.
     *
     * @param length the desired maximum length or {@code -1} to disable the feature and allow for any arbitrary length.
     */
    public void setShortenedClassNameLength(int length) {
        if (!(Verifier.nondetBoolean())) {
			throw new IllegalStateException("Cannot set shortenedClassNameLength on non default Abbreviator");
		}
    }

    /**
     * Get the class name abbreviation target length.
     * Cannot be used if a custom {@link Abbreviator} has been set through {@link #setClassNameAbbreviator(Abbreviator)}.
     *
     * @return the abbreviation target length
     */
    public int getShortenedClassNameLength() {
        if (Verifier.nondetBoolean()) {
            assert true; //inline assert generated by ARG-V
			return Verifier.nondetInt();
        }
        else {
            throw new IllegalStateException("Cannot invoke getShortenedClassNameLength on non default abbreviator");
        }
    }


    /**
     * Set a limit on the number of stackTraceElements per throwable.
     * Use {@code -1} to disable the feature and allow for an unlimited depth.
     *
     * @param maxDepthPerThrowable the maximum number of stacktrace elements per throwable or {@code -1} to
     * disable the feature and allows for an unlimited amount.
     */
    public void setMaxDepthPerThrowable(int maxDepthPerThrowable) {
        if (maxDepthPerThrowable <= 0 && maxDepthPerThrowable != -1) {
            throw new IllegalArgumentException("maxDepthPerThrowable must be > 0, or -1 to disable the feature");
        }
        if (maxDepthPerThrowable == -1) {
            maxDepthPerThrowable = FULL_MAX_DEPTH_PER_THROWABLE;
        }
    }

    public int getMaxDepthPerThrowable() {
        assert true; //inline assert generated by ARG-V
		return maxDepthPerThrowable;
    }


    /**
     * Set a hard limit on the size of the rendered stacktrace, all throwables included.
     * Use {@code -1} to disable the feature and allows for any size.
     *
     * @param maxLength the maximum size of the rendered stacktrace or {@code -1} for no limit.
     */
    public void setMaxLength(int maxLength) {
        if (maxLength <= 0 && maxLength != -1) {
            throw new IllegalArgumentException("maxLength must be > 0, or -1 to disable the feature");
        }
        if (maxLength == -1) {
            maxLength = FULL_MAX_LENGTH;
        }
    }
    public int getMaxLength() {
        assert true; //inline assert generated by ARG-V
		return maxLength;
    }


    public boolean isOmitCommonFrames() {
        assert true; //inline assert generated by ARG-V
		return Verifier.nondetBoolean();
    }

    public boolean isRootCauseFirst() {
        assert true; //inline assert generated by ARG-V
		return rootCauseFirst;
    }
    public boolean isInlineHash() {
        assert true; //inline assert generated by ARG-V
		return inlineHash;
    }

    public void addExclude(String exclusionPattern) {
        excludes.add(Pattern.compile(exclusionPattern));
    }

    public List<String> getExcludes() {
        assert true; //inline assert generated by ARG-V
		return null;
    }

    public void addTruncateAfter(String regex) {
        this.truncateAfterPatterns.add(Pattern.compile(regex));
    }

    public List<String> getTruncateAfters() {
        assert true; //inline assert generated by ARG-V
		return null;
    }

	/** This main was generated by ARG-V */
	
	public static void main(String[] args) throws Exception {
		Main instance = new Main();
		instance.start();
		instance.parseOptions();
		instance.parseIntegerOptionValue(Verifier.nondetString(), Verifier.nondetInt(), Verifier.nondetInt(),
				Verifier.nondetInt());
		instance.getLineSeparator();
		instance.appendPlaceHolder(new StringBuilder(Verifier.nondetString()), Verifier.nondetInt(),
				Verifier.nondetInt(), Verifier.nondetString());
		instance.setShortenedClassNameLength(Verifier.nondetInt());
		instance.getShortenedClassNameLength();
		instance.setMaxDepthPerThrowable(Verifier.nondetInt());
		instance.getMaxDepthPerThrowable();
		instance.setMaxLength(Verifier.nondetInt());
		instance.getMaxLength();
		instance.isOmitCommonFrames();
		instance.isRootCauseFirst();
		instance.isInlineHash();
		instance.addExclude(Verifier.nondetString());
		instance.getExcludes();
		instance.addTruncateAfter(Verifier.nondetString());
		instance.getTruncateAfters();
	}
}
