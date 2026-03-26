package semo.backend.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.aws.s3")
data class S3Properties(
    var bucket: String = "",
    var region: String = "",
    var accessKey: String? = null,
    var secretKey: String? = null,
    var endpoint: String? = null,
)
