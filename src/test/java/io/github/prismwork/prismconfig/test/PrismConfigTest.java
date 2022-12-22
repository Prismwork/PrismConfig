package io.github.prismwork.prismconfig.test;

import blue.endless.jankson.Comment;
import io.github.prismwork.prismconfig.api.PrismConfig;
import io.github.prismwork.prismconfig.api.config.DefaultDeserializers;
import io.github.prismwork.prismconfig.api.config.DefaultSerializers;
import org.junit.jupiter.api.Test;

import java.io.*;

public class PrismConfigTest {
    @Test
    void testConfig() {
        File configFile = new File("config.json");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile));) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            // stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();

            String content = stringBuilder.toString();
            TestConfig config = PrismConfig.getInstance().serialize(
                    TestConfig.class,
                    content,
                    DefaultSerializers.getInstance().json(TestConfig.class)
            );
            System.out.println(config.string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File configFile1 = new File("config1.json5");
        if (!configFile1.exists()) {
            try {
                configFile1.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        TestConfig config1 = new TestConfig();
        PrismConfig.getInstance().deserializeAndWrite(
                TestConfig.class,
                config1,
                DefaultDeserializers.getInstance().json5(TestConfig.class),
                configFile1
        );
    }

    public static class TestConfig {
        public boolean bool1 = false;
        public boolean bool2 = true;
        @Comment("Hello from comment")
        public String string = "Hi";
    }
}
