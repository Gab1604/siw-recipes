package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Restituisce tutte le immagini.
     * @return lista di immagini
     */
    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    /**
     * Trova un'immagine tramite ID.
     * @param id identificatore
     * @return Optional contenente l'immagine se trovata
     */
    public Optional<Image> findById(Long id) {
        return imageRepository.findById(id);
    }

    /**
     * Salva o aggiorna un'immagine.
     * @param image l'immagine da salvare
     * @return l'immagine salvata
     */
    public Image save(Image image) {
        return imageRepository.save(image);
    }

    /**
     * Elimina un'immagine tramite ID.
     * @param id identificatore dell'immagine
     */
    public void deleteById(Long id) {
        imageRepository.deleteById(id);
    }
}
