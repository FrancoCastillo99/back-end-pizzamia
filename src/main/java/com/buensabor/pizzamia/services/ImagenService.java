package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.Imagen;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImagenService {

    @Autowired
    private Cloudinary cloudinary;

    public Imagen uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "pizzamia",
                        "resource_type", "auto"
                )
        );

        Imagen imagen = new Imagen();
        imagen.setUrlImagen((String) uploadResult.get("secure_url"));

        return imagen;
    }
}
