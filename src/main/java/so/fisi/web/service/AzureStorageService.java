package so.fisi.web.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;

@Service
public class AzureStorageService {

    @Autowired
    private BlobServiceClient blobServiceClient; // Esto quita el error de la imagen

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String subirArchivo(MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) return null;

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) containerClient.create();

        String nombreUnico = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(nombreUnico);

        blobClient.upload(archivo.getInputStream(), archivo.getSize(), true);
        return blobClient.getBlobUrl(); // Esta URL es la que guardamos en la BD
    }
}