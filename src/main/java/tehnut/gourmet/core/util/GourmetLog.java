package tehnut.gourmet.core.util;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tehnut.gourmet.Gourmet;
import tehnut.gourmet.core.GourmetConfig;

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
            return GourmetConfig.logging.enableDebugLogging || Gourmet.DEV_MODE;
        }
    },
    FOOD_LOADER() {
        @Override
        boolean enabled() {
            return GourmetConfig.logging.enableFoodLoaderLogging || Gourmet.DEV_MODE;
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
