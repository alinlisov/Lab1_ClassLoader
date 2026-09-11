import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DynamicClassLoader extends ClassLoader {
    private final String classPath;
    public DynamicClassLoader(String classPath) {
        super(ClassLoader.getSystemClassLoader());
        this.classPath = classPath;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
               if (name.equals("TestModule")) {
            return findClass(name);
        }
           return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        File classFile = new File(classPath, name.replace('.', '/') + ".class");
        if (!classFile.exists()) {
            throw new ClassNotFoundException("Файл класу не знайдено: " + classFile.getAbsolutePath());
        }
        try {
            byte[] bytes = Files.readAllBytes(classFile.toPath());
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Не вдалося прочитати файл класу", e);
        }
    }
}