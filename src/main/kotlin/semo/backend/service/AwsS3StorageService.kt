package semo.backend.service

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import semo.backend.config.AwsS3Properties
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream

@Service
@ConditionalOnBean(S3Client::class)
class AwsS3StorageService(
    private val s3Client: S3Client,
    private val awsS3Properties: AwsS3Properties,
) {
    fun uploadPublicObject(
        key: String,
        contentType: String,
        contentLength: Long,
        inputStream: InputStream,
    ): String {
        val normalizedKey = key.trimStart('/')
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(awsS3Properties.s3Bucket)
                .key(normalizedKey)
                .contentType(contentType)
                .build(),
            RequestBody.fromInputStream(inputStream, contentLength),
        )
        return awsS3Properties.buildPublicUrl(normalizedKey)
    }

    fun buildPublicUrl(key: String): String {
        return awsS3Properties.buildPublicUrl(key)
    }
}
