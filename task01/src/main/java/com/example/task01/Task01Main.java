
package com.example.task01;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Task01Main {

    public static void main(String[] args)
            throws IOException, InterruptedException {

        System.out.println(extractSoundName(
                new File("task01/src/main/resources/3727.mp3")
        ));
    }

    public static String extractSoundName(File file)
            throws IOException, InterruptedException {

        ProcessBuilder builder = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-of", "flat",
                "-show_format",
                file.getAbsolutePath()
        );

        builder.redirectErrorStream(true);
        Process process = builder.start();

        String title = null;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        process.getInputStream(),
                        StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("format.tags.title=")) {
                    title = line.substring(
                            "format.tags.title=".length()
                    );

                    if (title.startsWith("\"")
                            && title.endsWith("\"")) {
                        title = title.substring(
                                1, title.length() - 1
                        );
                    }
                }
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException(
                    "ffprobe завершился с кодом " + exitCode
            );
        }

        if (title == null) {
            throw new IOException("Название трека не найдено");
        }

        return title;
    }
}
