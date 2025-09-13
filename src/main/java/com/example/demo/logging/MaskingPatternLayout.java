package com.example.demo.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;

/**
 * A custom Logback layout that masks sensitive data in log messages.
 * It extends the default PatternLayout to intercept and modify the log message
 * before it's written to the output.
 */
public class MaskingPatternLayout extends PatternLayout {

    @Override
    public String doLayout(ILoggingEvent event) {
        // Get the original log message from the default layout
        String originalMessage = super.doLayout(event);
        // Apply our custom masking logic
        String maskedMessage = MaskingUtils.maskSensitiveData(originalMessage);

        // Ensure a newline is always at the end to prevent log lines from concatenating.
        if (!maskedMessage.endsWith(CoreConstants.LINE_SEPARATOR)) {
            return maskedMessage + CoreConstants.LINE_SEPARATOR;
        }

        return maskedMessage;
    }
}
