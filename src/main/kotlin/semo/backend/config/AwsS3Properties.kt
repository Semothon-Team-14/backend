package semo.backend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.aws")
data class AwsS3Properties(
    var accessKeyId: String = "",
    var secretAccessKey: String = "",
    var region: String = "",
    var s3Bucket: String = "",
) {
    fun buildPublicUrl(key: String): String {
        val normalizedKey = key.trimStart('/')
        return "https://$s3Bucket.s3.$region.amazonaws.com/$normalizedKey"
    }
}
