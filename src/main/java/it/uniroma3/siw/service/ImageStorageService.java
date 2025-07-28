
package it.uniroma3.siw.service;

import it.uniroma3.siw.configuration.UploadProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path baseDir;

    public ImageStorageService(UploadProperties props) {
        this.baseDir = Paths.get(props.getBaseDir());
    }

    /**
     * Salva un file immagine all’interno di una sottocartella della directory base,
     * generando un nome univoco con UUID per evitare collisioni o problemi derivanti
     * dal nome originale (che può contenere caratteri non sicuri o addirittura essere null).
     * 
     * @param file   il {@link MultipartFile} da salvare (può essere null o vuoto)
     * @param subdir il nome della sottocartella sotto baseDir (es. "users/42" o "books/7")
     * @return il path relativo al file salvato, pronto per essere usato come src (es. "users/42/a0f5c3d2-8b79-4e1d-9a87-2cc3fae5b6f4.jpg"),
     *         oppure null se il file era null o vuoto
     * @throws IOException se si verifica un errore di I/O durante la scrittura
     */
    public String store(MultipartFile file, String subdir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Estrazione sicura del nome originale (può essere null)
        String orig = file.getOriginalFilename();
        // Usa stringa vuota se orig è null
        String safeOrig = (orig != null ? orig : "");

        // Ricava l’estensione, se presente
        String ext = "";
        int dotIndex = safeOrig.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < safeOrig.length() - 1) {
            ext = safeOrig.substring(dotIndex); // include il “.”
        }

        // Genera un nome univoco
        String filename = UUID.randomUUID().toString() + ext;

        // Crea la directory target se manca
        Path targetDir = baseDir.resolve(subdir);
        if (Files.notExists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // Salva il file
        try (InputStream in = file.getInputStream()) {
            Path targetFile = targetDir.resolve(filename);
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // Restituisce il percorso relativo
        return subdir + "/" + filename;
    }
}
