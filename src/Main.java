import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws Exception {
        File tempDir = Files.createTempDirectory("custom_classes").toFile();
        tempDir.deleteOnExit();
        String folderPath = "src";
        String className = "TestModule";
        File javaFile = new File(folderPath, className + ".java");
        long lastModified = 0;
        System.out.println("Програма запущена. Змініть рядок у TestModule.java та збережіть файл");

        while (true) {
            try {
                if (javaFile.exists() && javaFile.lastModified() > lastModified) {
                    lastModified = javaFile.lastModified();
                    System.out.println("\n[!] Виявлено зміни у файлі " + className + ".java");
                    // Компілюємо .java файл прямо в нашу тимчасову папку
                    boolean compiled = recompile(javaFile.getAbsolutePath(), tempDir.getAbsolutePath());
                    if (compiled) {
                        // створюємо новий завантажувач, який шукає класи тілки в тимчасовій папці
                        DynamicClassLoader loader = new DynamicClassLoader(tempDir.getAbsolutePath());
                        Class<?> testClass = loader.loadClass(className);
                        Object t = testClass.getDeclaredConstructor().newInstance();
                        System.out.println("Результат виконання t.toString(): " + t);
                    } else {
                        System.out.println("[ERROR] Помилка компіляції!");
                    }
                }
                Thread.sleep(1500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean recompile(String javaFilePath, String outputDirPath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("Помилка: Не знайдено JavaCompiler.");
            return false;
        }
                int result = compiler.run(null, null, null, "-d", outputDirPath, javaFilePath);
        return result == 0;
    }
}