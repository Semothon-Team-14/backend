package semo.backend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.aws")
data class AwsS3Properties(
    var accessKeyId: String = "",
    var secretAccessKey: String = "",
    var region: String = "",
    var s3Bucket: String = "",
    var profilePicturesBucket: String = "semothon-14-profile-pictures",
) {
    fun buildPublicUrl(key: String): String {
        return buildPublicUrlForBucket(s3Bucket, key)
    }

    fun buildPublicUrlForBucket(bucket: String, key: String): String {
        val normalizedKey = key.trimStart('/')
        return "https://$bucket.s3.$region.amazonaws.com/$normalizedKey"
    }
}
