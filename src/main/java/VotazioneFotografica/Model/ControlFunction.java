package VotazioneFotografica.Model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ControlFunction {

    // 1. Iniettiamo i nomi/percorsi dei file dal file application.properties
    // Se non esistono nel properties, di default useranno "status.txt" e "lock.txt"
    @Value("${app.file.status:status.txt}")
    private String statusFilePath;

    @Value("${app.file.lock:lock.txt}")
    private String lockFilePath;

    private int control;
    private boolean control2;

    // 2. Spostiamo la logica di I/O dal costruttore a un metodo @PostConstruct
    @PostConstruct
    public void init() throws IOException {
        this.control = initializeAndReadFileInt(statusFilePath);
        this.control2 = initializeAndReadFile(lockFilePath);
    }

    private boolean initializeAndReadFile(String pathStr) throws IOException {
        Path path = Paths.get(pathStr);

        if (!Files.exists(path)) {
            Files.writeString(path, "false");
            return false;
        }

        String test = Files.readString(path).trim();
        return "true".equalsIgnoreCase(test);
    }

    private int initializeAndReadFileInt(String pathStr) throws IOException {
        Path path = Paths.get(pathStr);

        if (!Files.exists(path)) {
            Files.writeString(path, "0");
            return 0;
        }

        try {
            return Integer.parseInt(Files.readString(path).trim());
        } catch (NumberFormatException e) {
            Files.writeString(path, "0");
            return 0;
        }
    }

    // 3. Aggiunto 'synchronized' per evitare problemi se due utenti votano contemporaneamente
    public synchronized int getControl() {
        return control;
    }

    public synchronized void setControl(int control) throws IOException {
        this.control = control;
        Files.writeString(Paths.get(statusFilePath), String.valueOf(this.control));
    }

    public synchronized boolean isControl2() {
        return control2;
    }

    public synchronized void setControl2() throws IOException {
        this.control2 = !this.control2;
        Files.writeString(Paths.get(lockFilePath), String.valueOf(this.control2));
    }
}