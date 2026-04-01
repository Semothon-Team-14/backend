package semo.backend.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
@EnableConfigurationProperties(AwsS3Properties::class)
class AwsS3Config {
    @Bean
    @ConditionalOnExpression(
        "'\${app.aws.access-key-id:}' != '' && '\${app.aws.secret-access-key:}' != '' && '\${app.aws.region:}' != '' && '\${app.aws.s3-bucket:}' != ''",
    )
    fun s3Client(awsS3Properties: AwsS3Properties): S3Client {
        return S3Client.builder()
            .region(Region.of(awsS3Properties.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        awsS3Properties.accessKeyId,
                        awsS3Properties.secretAccessKey,
                    ),
                ),
            )
            .build()
    }
}
