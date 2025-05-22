package ready_to_marry.adminservice.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3Uploader {
    public String upload(MultipartFile file, String dirName) {
        // TODO: S3 업로드 로직 실제 구현 필요
        return "https://s3.fake-bucket.com/" + dirName + "/" + file.getOriginalFilename();
    }
}
