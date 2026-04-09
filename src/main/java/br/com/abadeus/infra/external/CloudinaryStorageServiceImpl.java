package br.com.abadeus.infra.external;

import br.com.abadeus.domain.interfaces.StorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryStorageServiceImpl implements StorageService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String pasta) throws Exception {
        String publicId = pasta + "/" + UUID.randomUUID();

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", publicId,
                        "resource_type", "auto"
                )
        );

        return (String) result.get("secure_url");
    }

    @Override
    public void deleteFile(String url) {
        try {

            String publicId = url
                    .replaceAll("https://res.cloudinary.com/[^/]+/image/upload/v\\d+/", "")
                    .replaceAll("\\.[^.]+$", "");

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Erro ao deletar arquivo no Cloudinary: " + e.getMessage());
        }
    }
}
