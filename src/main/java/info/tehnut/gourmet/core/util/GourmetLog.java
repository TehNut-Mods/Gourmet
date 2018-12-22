package info.tehnut.gourmet.core.util;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import info.tehnut.gourmet.Gourmet;
import info.tehnut.gourmet.core.GourmetConfig;

public enum GourmetLog {

    DEFAULT(Gourmet.NAME) {
        @Override
        boolean enabled() {
            return true;
        }
    },
    DEBUG() {
        @Override
        boolean enabled() {
            return Gourmet.CONFIG.getLogging().enableDebugLogging();
        }
    },
    FOOD_LOADER() {
        @Override
        boolean enabled() {
            return Gourmet.CONFIG.getLogging().enableFoodLoaderLogging();
        }
    },
    ;

    private final Logger logger;

    GourmetLog(String logName) {
        logger = LogManager.getLogger(logName);
    }

    GourmetLog() {
        logger = LogManager.getLogger(Gourmet.NAME + "|" + WordUtils.capitalizeFully(name().replace("_", " ")));
    }

    abstract boolean enabled();

    public void info(String input, Object... args) {
        if (enabled())
            logger.info(input, args);
    }

    public void error(String input, Object... args) {
        if (enabled())
            logger.error(input, args);
    }

    public void warn(String input, Object... args) {
        if (enabled())
            logger.warn(input, args);
    }
}
