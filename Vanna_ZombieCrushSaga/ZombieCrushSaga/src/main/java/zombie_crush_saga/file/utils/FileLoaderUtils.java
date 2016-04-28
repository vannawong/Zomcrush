package zombie_crush_saga.file.utils;

import zombie_crush_saga.ZombieCrushSaga;

import java.io.File;

public class FileLoaderUtils {
    // This should fail if it cannot open file so we throw exception instead of catch it
    public static File loadFile(String name) throws Exception {
        try {
            return new File(ZombieCrushSaga.class.getResource(name).toURI());
        } catch (Exception e) {
            throw new Exception("Could not load file with name " + name, e);
        }
    }
}
