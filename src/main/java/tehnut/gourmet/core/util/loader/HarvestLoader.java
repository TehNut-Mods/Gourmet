package tehnut.gourmet.core.util.loader;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import tehnut.gourmet.core.util.GourmetLog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Consumer;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface HarvestLoader {

    String value() default "";

    class Gather {
        public static List<IHarvestLoader> gather(ASMDataTable dataTable) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            List<IHarvestLoader> loaders = Lists.newArrayList();
            List<ASMDataTable.ASMData> discoveredLoaders = Lists.newArrayList(dataTable.getAll(HarvestLoader.class.getName()));
            discoveredLoaders.sort((o1, o2) -> o1.getObjectName().compareToIgnoreCase(o2.getClassName()));

            for (ASMDataTable.ASMData data : discoveredLoaders) {
                String required = (String) data.getAnnotationInfo().getOrDefault("value", "");
                if (!required.isEmpty() && !Loader.isModLoaded(required))
                    continue;

                try {
                    Class<?> asmClass = Class.forName(data.getClassName());
                    if (data.getObjectName().equals(data.getClassName()))
                        handleClassAnnotation(loaders::add, asmClass);
                    else
                        handleFieldAnnotation(loaders::add, asmClass, data.getObjectName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            GourmetLog.FOOD_LOADER.info("Discovered {} harvest loader(s) in {}", loaders.size(), stopwatch.stop());
            return loaders;
        }

        private static void handleClassAnnotation(Consumer<IHarvestLoader> loaders, Class<?> asmClass) throws Exception {
            if (!IHarvestLoader.class.isAssignableFrom(asmClass)) {
                GourmetLog.FOOD_LOADER.error("Class at {} was annotated with @HarvestLoader but does not inherit from IHarvestLoader.", asmClass.getName());
                return;
            }

            Class<? extends IHarvestLoader> loaderClass = asmClass.asSubclass(IHarvestLoader.class);
            Constructor<? extends IHarvestLoader> loaderConstructor;

            try {
                loaderConstructor = loaderClass.getConstructor();
            } catch (NoSuchMethodException e) {
                GourmetLog.FOOD_LOADER.error("Class at {} was annotated with @HarvestLoader but does not provide a default constructor.", asmClass.getName());
                return;
            }

            GourmetLog.FOOD_LOADER.info("Discovered a Harvest loader at {}", asmClass.getName());
            loaders.accept(loaderConstructor.newInstance());
        }

        private static void handleFieldAnnotation(Consumer<IHarvestLoader> loaders, Class<?> asmClass, String fieldName) throws Exception {
            Field potentialLoader = asmClass.getDeclaredField(fieldName);
            if (!Modifier.isStatic(potentialLoader.getModifiers())) {
                GourmetLog.FOOD_LOADER.error("Field at {}.{} was annotated with @HarvestLoader but is not static.", asmClass.getName(), fieldName);
                return;
            }

            if (potentialLoader.getType() != IHarvestLoader.class) {
                GourmetLog.FOOD_LOADER.error("Field at {}.{} was annotated with @HarvestLoader but is not an IHarvestLoader.", asmClass.getName(), fieldName);
                return;
            }

            GourmetLog.FOOD_LOADER.info("Discovered a Harvest loader at {}.{}", asmClass.getName(), fieldName);
            loaders.accept((IHarvestLoader) potentialLoader.get(null));
        }
    }
}
