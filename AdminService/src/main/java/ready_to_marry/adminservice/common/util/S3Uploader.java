package ready_to_marry.adminservice.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;
    private final String bucketName = "rtm-app-bucket"; // 또는 @Value 로 주입

    /**
     * 1. 폴더 기준 업로드 (파일명 자동 생성)
     * ex) uploadToFolder(file, "admin/events")
     */
    public String uploadToFolder(MultipartFile file, String dirName) {
        String fileName = dirName + "/" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        return putAndReturnUrl(file, fileName);
    }

    /**
     * 2. Key 직접 지정 업로드
     * ex) uploadWithKey(file, "admin/events/event-123.jpg")
     */
    public String uploadWithKey(MultipartFile file, String key) {
        return putAndReturnUrl(file, key);
    }

    /**
     * 3. 파일 삭제
     */
    public void delete(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    /**
     * 내부 공통 업로드 로직
     */
    private String putAndReturnUrl(MultipartFile file, String key) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);

            return amazonS3.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }
}
