package info.tehnut.gourmet.core;

public class GourmetConfig {

    private ConfigLogging logging = new ConfigLogging();
    private ConfigRemote remote = new ConfigRemote();

    public ConfigLogging getLogging() {
        return logging;
    }

    public ConfigRemote getRemote() {
        return remote;
    }

    public static class ConfigLogging {
        private boolean enableDebugLogging = false;
        private boolean enableFoodLoaderLogging = false;

        public boolean enableDebugLogging() {
            return enableDebugLogging;
        }

        public boolean enableFoodLoaderLogging() {
            return enableFoodLoaderLogging;
        }
    }

    public static class ConfigRemote {
        private String remoteJson = "";

        public String getRemoteJson() {
            return remoteJson;
        }
    }
}
