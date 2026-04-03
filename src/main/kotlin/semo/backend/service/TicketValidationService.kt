package semo.backend.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import semo.backend.controller.request.RecognizeBoardingPassRequest
import semo.backend.dto.TicketTripDraftDto
import semo.backend.entity.City
import semo.backend.exception.city.CityNotFoundException
import semo.backend.exception.ticket.TicketAirportUnsupportedException
import semo.backend.exception.ticket.TicketCityMismatchException
import semo.backend.exception.ticket.TicketImageRequiredException
import semo.backend.exception.ticket.TicketRecognitionFailedException
import semo.backend.repository.jpa.CityRepository
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Base64
import javax.imageio.ImageIO
import kotlin.math.absoluteValue

@Service
class TicketValidationService(
    private val cityRepository: CityRepository,
) {
    fun recognizeBoardingPass(request: RecognizeBoardingPassRequest): TicketTripDraftDto {
        if (request.imageBase64.isBlank()) {
            throw TicketImageRequiredException()
        }

        val image = decodeImage(request.imageBase64)
        val decoded = decodeBarcode(image)
        val parsed = parseBoardingPass(decoded.text)
        val arrivalCityId = airportCodeToCityId[parsed.toAirportCode]
            ?: throw TicketAirportUnsupportedException(parsed.toAirportCode)
        val arrivalCity = findCityById(arrivalCityId)

        if (request.cityId != null && request.cityId != arrivalCity.id) {
            throw TicketCityMismatchException(request.cityId, arrivalCity.id)
        }

        val titleBase = arrivalCity.cityNameKorean.ifBlank { arrivalCity.cityNameEnglish }
        val departureDateTime = parsed.departureTime?.let { LocalDateTime.of(parsed.flightDate, it) }
        val departureLandingDateTime = parsed.landingTime?.let { landingTime ->
            if (departureDateTime == null) {
                LocalDateTime.of(parsed.flightDate, landingTime)
            } else {
                val landingDate = if (landingTime.isBefore(departureDateTime.toLocalTime())) {
                    parsed.flightDate.plusDays(1)
                } else {
                    parsed.flightDate
                }
                LocalDateTime.of(landingDate, landingTime)
            }
        }
        val draft = TicketTripDraftDto(
            title = "$titleBase 여행",
            cityId = arrivalCity.id,
            startDate = parsed.flightDate,
            endDate = parsed.flightDate,
            departureDateTime = departureDateTime,
            departureLandingDateTime = departureLandingDateTime,
            passengerName = parsed.passengerName,
            fromAirportCode = parsed.fromAirportCode,
            toAirportCode = parsed.toAirportCode,
            operatingCarrierDesignator = parsed.operatingCarrierDesignator,
            flightNumber = parsed.flightNumber,
            barcodeFormat = decoded.barcodeFormat.name,
            rawData = parsed.rawData,
        )

        logger.info(
            "BOARDING_PASS_PARSED cityId={} cityName={} passengerName={} route={}->{} carrier={} flightNumber={} date={} dep={} land={} format={}",
            draft.cityId,
            titleBase,
            draft.passengerName,
            draft.fromAirportCode,
            draft.toAirportCode,
            draft.operatingCarrierDesignator,
            draft.flightNumber,
            draft.startDate,
            draft.departureDateTime,
            draft.departureLandingDateTime,
            draft.barcodeFormat,
        )

        return draft
    }

    private fun findCityById(cityId: Long): City {
        return cityRepository.findById(cityId)
            .orElseThrow { CityNotFoundException(cityId) }
    }

    private fun decodeImage(imageBase64: String): BufferedImage {
        val payload = imageBase64.substringAfter("base64,", imageBase64).trim()
        val bytes = try {
            Base64.getDecoder().decode(payload)
        } catch (_: IllegalArgumentException) {
            throw TicketRecognitionFailedException("이미지 인코딩이 올바르지 않습니다.")
        }

        val decoded = ImageIO.read(ByteArrayInputStream(bytes))
            ?: throw TicketRecognitionFailedException("이미지를 읽을 수 없습니다.")

        return normalizeImage(decoded)
    }

    private fun decodeBarcode(image: BufferedImage): DecodedBarcode {
        val hints = mapOf(
            DecodeHintType.TRY_HARDER to true,
            DecodeHintType.POSSIBLE_FORMATS to listOf(
                BarcodeFormat.PDF_417,
                BarcodeFormat.QR_CODE,
                BarcodeFormat.AZTEC,
                BarcodeFormat.DATA_MATRIX,
            ),
        )

        val candidates = listOf(
            image,
            rotateImage(image, 90.0),
            rotateImage(image, 180.0),
            rotateImage(image, 270.0),
        )

        for (candidate in candidates) {
            try {
                val source = BufferedImageLuminanceSource(candidate)
                val bitmap = BinaryBitmap(HybridBinarizer(source))
                val result = MultiFormatReader().decode(bitmap, hints)
                return DecodedBarcode(
                    text = result.text,
                    barcodeFormat = result.barcodeFormat,
                )
            } catch (_: NotFoundException) {
                continue
            }
        }

        throw TicketRecognitionFailedException()
    }

    private fun rotateImage(image: BufferedImage, degrees: Double): BufferedImage {
        if (degrees % 360.0 == 0.0) {
            return image
        }

        val radians = Math.toRadians(degrees)
        val sin = kotlin.math.abs(kotlin.math.sin(radians))
        val cos = kotlin.math.abs(kotlin.math.cos(radians))
        val newWidth = kotlin.math.floor(image.width * cos + image.height * sin).toInt()
        val newHeight = kotlin.math.floor(image.height * cos + image.width * sin).toInt()
        val rotated = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        val transform = AffineTransform().apply {
            translate(newWidth / 2.0, newHeight / 2.0)
            rotate(radians)
            translate(-image.width / 2.0, -image.height / 2.0)
        }

        val graphics = rotated.createGraphics()
        configureGraphics(graphics)
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, newWidth, newHeight)
        graphics.transform = transform
        graphics.drawImage(image, 0, 0, null)
        graphics.dispose()
        return rotated
    }

    private fun normalizeImage(image: BufferedImage): BufferedImage {
        if (image.type == BufferedImage.TYPE_INT_RGB) {
            return image
        }

        val normalized = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        val graphics = normalized.createGraphics()
        configureGraphics(graphics)
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, image.width, image.height)
        graphics.drawImage(image, 0, 0, null)
        graphics.dispose()
        return normalized
    }

    private fun configureGraphics(graphics: Graphics2D) {
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }

    private fun parseBoardingPass(rawData: String): ParsedBoardingPass {
        val normalized = rawData
            .replace("\u0000", "")
            .replace("\r", "")
            .replace("\n", "")
            .trim()

        if (normalized.length < 58 || (normalized[0] != 'M' && normalized[0] != 'N')) {
            throw TicketRecognitionFailedException("IATA BCBP 형식이 아닙니다.")
        }

        val passengerName = normalized.substring(2, 22).trim()
        val fromAirportCode = normalized.substring(30, 33).trim()
        val toAirportCode = normalized.substring(33, 36).trim()
        val operatingCarrierDesignator = normalized.substring(36, 39).trim()
        val flightNumber = normalized.substring(39, 44).trim().trimStart('0').ifBlank {
            normalized.substring(39, 44).trim()
        }
        val julianDate = normalized.substring(44, 47).trim()
        val timeCandidates = extractTimeCandidates(normalized, flightNumber)

        return ParsedBoardingPass(
            rawData = normalized,
            passengerName = passengerName,
            fromAirportCode = fromAirportCode,
            toAirportCode = toAirportCode,
            operatingCarrierDesignator = operatingCarrierDesignator,
            flightNumber = flightNumber,
            flightDate = inferFlightDate(julianDate),
            departureTime = timeCandidates.firstOrNull(),
            landingTime = timeCandidates.drop(1).firstOrNull(),
        )
    }

    private fun extractTimeCandidates(rawData: String, flightNumber: String): List<LocalTime> {
        val paddedFlightNumber = flightNumber.padStart(4, '0')
        val uniqueTimes = linkedMapOf<String, LocalTime>()
        val regex = Regex("""(?<!\d)([01]\d|2[0-3])[:]?([0-5]\d)(?!\d)""")
        regex.findAll(rawData).forEach { match ->
            val token = match.value.replace(":", "")
            if (token == paddedFlightNumber) {
                return@forEach
            }
            val hour = token.substring(0, 2).toInt()
            val minute = token.substring(2, 4).toInt()
            val time = LocalTime.of(hour, minute)
            uniqueTimes.putIfAbsent(time.toString(), time)
        }
        return uniqueTimes.values.toList()
    }

    private fun inferFlightDate(julianDate: String): LocalDate {
        val dayOfYear = julianDate.toIntOrNull()
            ?: throw TicketRecognitionFailedException("탑승권 날짜를 해석할 수 없습니다.")
        val today = LocalDate.now()
        val candidates = listOf(today.year - 1, today.year, today.year + 1)
            .mapNotNull { year ->
                runCatching { LocalDate.ofYearDay(year, dayOfYear) }.getOrNull()
            }

        if (candidates.isEmpty()) {
            throw TicketRecognitionFailedException("탑승권 날짜를 해석할 수 없습니다.")
        }

        return candidates
            .sortedWith(
                compareBy<LocalDate> { if (it.isBefore(today.minusDays(14))) 1 else 0 }
                    .thenBy { ChronoUnit.DAYS.between(today, it).absoluteValue },
            )
            .first()
    }

    private data class DecodedBarcode(
        val text: String,
        val barcodeFormat: BarcodeFormat,
    )

    private data class ParsedBoardingPass(
        val rawData: String,
        val passengerName: String,
        val fromAirportCode: String,
        val toAirportCode: String,
        val operatingCarrierDesignator: String,
        val flightNumber: String,
        val flightDate: LocalDate,
        val departureTime: LocalTime?,
        val landingTime: LocalTime?,
    )

    companion object {
        private val logger = LoggerFactory.getLogger(TicketValidationService::class.java)

        private val airportCodeToCityId = mapOf(
            "ICN" to 1L,
            "GMP" to 1L,
            "PUS" to 2L,
            "TAE" to 3L,
            "NRT" to 4L,
            "HND" to 4L,
            "KIX" to 5L,
            "ITM" to 5L,
            "UKB" to 5L,
            "FUK" to 6L,
            "PEK" to 7L,
            "PKX" to 7L,
            "PVG" to 8L,
            "SHA" to 8L,
            "SZX" to 9L,
            "TPE" to 10L,
            "TSA" to 10L,
            "KHH" to 11L,
            "RMQ" to 12L,
            "HKG" to 13L,
            "SIN" to 16L,
            "BKK" to 19L,
            "DMK" to 19L,
            "CNX" to 20L,
            "HKT" to 21L,
            "HAN" to 22L,
            "SGN" to 23L,
            "DAD" to 24L,
            "KUL" to 25L,
            "SZB" to 25L,
            "PEN" to 26L,
            "BKI" to 27L,
            "CGK" to 28L,
            "DPS" to 29L,
            "SUB" to 30L,
            "MNL" to 31L,
            "CEB" to 32L,
            "DVO" to 33L,
            "DEL" to 34L,
            "BOM" to 35L,
            "BLR" to 36L,
            "SYD" to 37L,
            "MEL" to 38L,
            "BNE" to 39L,
            "AKL" to 40L,
            "WLG" to 41L,
            "CHC" to 42L,
            "JFK" to 43L,
            "LGA" to 43L,
            "EWR" to 43L,
            "LAX" to 44L,
            "SFO" to 45L,
            "YYZ" to 46L,
            "YVR" to 47L,
            "YUL" to 48L,
            "MEX" to 49L,
            "CUN" to 50L,
            "GDL" to 51L,
            "GRU" to 52L,
            "CGH" to 52L,
            "GIG" to 53L,
            "SDU" to 53L,
            "SSA" to 54L,
            "EZE" to 55L,
            "AEP" to 55L,
            "COR" to 56L,
            "MDZ" to 57L,
            "LHR" to 58L,
            "LGW" to 58L,
            "STN" to 58L,
            "LTN" to 58L,
            "LCY" to 58L,
            "MAN" to 59L,
            "EDI" to 60L,
            "CDG" to 61L,
            "ORY" to 61L,
            "LYS" to 62L,
            "NCE" to 63L,
            "BER" to 64L,
            "MUC" to 65L,
            "FRA" to 66L,
            "FCO" to 67L,
            "CIA" to 67L,
            "MXP" to 68L,
            "LIN" to 68L,
            "BGY" to 68L,
            "VCE" to 69L,
            "MAD" to 70L,
            "BCN" to 71L,
            "SVQ" to 72L,
            "AMS" to 73L,
            "RTM" to 74L,
            "ZRH" to 76L,
            "GVA" to 77L,
            "IST" to 79L,
            "SAW" to 79L,
            "DXB" to 80L,
            "AUH" to 81L,
            "JED" to 82L,
        )
    }
}
