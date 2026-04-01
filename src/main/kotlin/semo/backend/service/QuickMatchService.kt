package semo.backend.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import semo.backend.controller.request.CreateQuickMatchRequest
import semo.backend.dto.QuickMatchAcceptResultDto
import semo.backend.dto.QuickMatchDto
import semo.backend.entity.City
import semo.backend.entity.QuickMatch
import semo.backend.entity.QuickMatchResponse
import semo.backend.entity.User
import semo.backend.enums.QuickMatchResponseStatus
import semo.backend.enums.QuickMatchStatus
import semo.backend.enums.QuickMatchTargetType
import semo.backend.exception.city.CityNotFoundException
import semo.backend.exception.quickmatch.QuickMatchAlreadyResolvedException
import semo.backend.exception.quickmatch.QuickMatchNotFoundException
import semo.backend.exception.quickmatch.QuickMatchResponderNotEligibleException
import semo.backend.exception.quickmatch.QuickMatchSelfResponseNotAllowedException
import semo.backend.exception.user.UserNotFoundException
import semo.backend.mapstruct.MingleMapStruct
import semo.backend.mapstruct.QuickMatchMapStruct
import semo.backend.repository.jpa.CityRepository
import semo.backend.repository.jpa.QuickMatchRepository
import semo.backend.repository.jpa.QuickMatchResponseRepository
import semo.backend.repository.jpa.TripRepository
import semo.backend.repository.jpa.UserRepository
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class QuickMatchService(
    private val quickMatchRepository: QuickMatchRepository,
    private val quickMatchResponseRepository: QuickMatchResponseRepository,
    private val userRepository: UserRepository,
    private val cityRepository: CityRepository,
    private val tripRepository: TripRepository,
    private val localService: LocalService,
    private val mingleService: MingleService,
    private val minglerService: MinglerService,
    private val chatRoomService: ChatRoomService,
    private val quickMatchMapStruct: QuickMatchMapStruct,
    private val mingleMapStruct: MingleMapStruct,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    fun getQuickMatches(cityId: Long?, targetType: QuickMatchTargetType?): List<QuickMatchDto> {
        val quickMatches = when {
            cityId == null && targetType == null -> quickMatchRepository.findAllByStatusOrderByCreatedDateTimeDesc(QuickMatchStatus.PENDING)
            cityId == null && targetType != null ->
                quickMatchRepository.findAllByStatusAndTargetTypeOrderByCreatedDateTimeDesc(QuickMatchStatus.PENDING, targetType)
            cityId != null && targetType == null -> {
                findCityById(cityId)
                quickMatchRepository.findAllByCityIdAndStatusOrderByCreatedDateTimeDesc(cityId, QuickMatchStatus.PENDING)
            }
            else -> {
                findCityById(cityId!!)
                quickMatchRepository.findAllByCityIdAndStatusAndTargetTypeOrderByCreatedDateTimeDesc(
                    cityId,
                    QuickMatchStatus.PENDING,
                    targetType!!,
                )
            }
        }
        return quickMatchMapStruct.toDtos(quickMatches)
    }

    fun getQuickMatch(quickMatchId: Long): QuickMatchDto {
        return quickMatchMapStruct.toDto(findQuickMatchById(quickMatchId))
    }

    @Transactional
    fun createQuickMatch(userId: Long, request: CreateQuickMatchRequest): QuickMatchDto {
        val requester = findUserById(userId)
        val city = findCityById(request.cityId)
        val now = LocalDateTime.now()
        val quickMatch = quickMatchRepository.save(
            QuickMatch(
                requesterUser = requester,
                city = city,
                message = request.message?.trim()?.takeIf { it.isNotEmpty() },
                targetType = request.targetType,
                status = QuickMatchStatus.PENDING,
                createdDateTime = now,
                updatedDateTime = now,
            ),
        )

        val targetUserIds = findTargetUserIdsByTargetType(
            cityId = city.id,
            excludeUserId = requester.id,
            targetType = request.targetType,
        )
        publishCityAlert(
            cityId = city.id,
            eventType = "QUICK_MATCH_CREATED",
            quickMatch = quickMatch,
            targetType = request.targetType,
            targetUserIds = targetUserIds,
        )

        return quickMatchMapStruct.toDto(quickMatch)
    }

    @Transactional
    fun acceptQuickMatch(userId: Long, quickMatchId: Long): QuickMatchAcceptResultDto {
        val responder = findUserById(userId)
        val quickMatch = findQuickMatchById(quickMatchId)
        validateQuickMatchResponseEligibility(responder.id, quickMatch)
        if (quickMatch.status != QuickMatchStatus.PENDING) {
            throw QuickMatchAlreadyResolvedException(quickMatch.id, quickMatch.status)
        }

        val now = LocalDateTime.now()
        upsertResponse(quickMatch, responder, QuickMatchResponseStatus.ACCEPTED, now)

        val createdMingle = mingleService.createMingleForQuickMatch(
            cityId = quickMatch.city.id,
            title = "Quick Match in ${quickMatch.city.cityNameEnglish}",
            description = quickMatch.message,
        )
        minglerService.ensureJoinedMingle(quickMatch.requesterUser.id, createdMingle.id)
        minglerService.ensureJoinedMingle(responder.id, createdMingle.id)
        val createdChatRoom = chatRoomService.createMingleChatRoom(
            mingleId = createdMingle.id,
            participantUserIds = setOf(quickMatch.requesterUser.id, responder.id),
        )

        quickMatch.status = QuickMatchStatus.ACCEPTED
        quickMatch.acceptedByUser = responder
        quickMatch.mingle = createdMingle
        quickMatch.updatedDateTime = now
        val savedQuickMatch = quickMatchRepository.save(quickMatch)

        publishCityAlert(
            cityId = quickMatch.city.id,
            eventType = "QUICK_MATCH_ACCEPTED",
            quickMatch = savedQuickMatch,
            targetType = quickMatch.targetType,
            targetUserIds = emptyList(),
        )
        publishUserAlert(quickMatch.requesterUser.id, "QUICK_MATCH_ACCEPTED", savedQuickMatch)
        publishUserAlert(responder.id, "QUICK_MATCH_ACCEPTED", savedQuickMatch)

        return QuickMatchAcceptResultDto(
            quickMatch = quickMatchMapStruct.toDto(savedQuickMatch),
            mingle = mingleMapStruct.toDto(createdMingle),
            chatRoom = createdChatRoom,
        )
    }

    @Transactional
    fun declineQuickMatch(userId: Long, quickMatchId: Long): QuickMatchDto {
        val responder = findUserById(userId)
        val quickMatch = findQuickMatchById(quickMatchId)
        validateQuickMatchResponseEligibility(responder.id, quickMatch)
        if (quickMatch.status != QuickMatchStatus.PENDING) {
            throw QuickMatchAlreadyResolvedException(quickMatch.id, quickMatch.status)
        }

        upsertResponse(quickMatch, responder, QuickMatchResponseStatus.DECLINED, LocalDateTime.now())
        publishUserAlert(quickMatch.requesterUser.id, "QUICK_MATCH_DECLINED", quickMatch)
        return quickMatchMapStruct.toDto(quickMatch)
    }

    private fun upsertResponse(
        quickMatch: QuickMatch,
        responder: User,
        status: QuickMatchResponseStatus,
        now: LocalDateTime,
    ) {
        val existingResponse = quickMatchResponseRepository.findByQuickMatchIdAndResponderUserId(quickMatch.id, responder.id)
        if (existingResponse != null) {
            existingResponse.status = status
            existingResponse.updatedDateTime = now
            quickMatchResponseRepository.save(existingResponse)
            return
        }
        quickMatchResponseRepository.save(
            QuickMatchResponse(
                quickMatch = quickMatch,
                responderUser = responder,
                status = status,
                createdDateTime = now,
                updatedDateTime = now,
            ),
        )
    }

    private fun validateQuickMatchResponseEligibility(userId: Long, quickMatch: QuickMatch) {
        if (quickMatch.requesterUser.id == userId) {
            throw QuickMatchSelfResponseNotAllowedException(quickMatch.id, userId)
        }
        val activeTravelerUserIds = findActiveTravelerUserIds(
            cityId = quickMatch.city.id,
            excludeUserId = quickMatch.requesterUser.id,
        )
        val localUserIds = findLocalUserIds(
            cityId = quickMatch.city.id,
            excludeUserId = quickMatch.requesterUser.id,
        )
        val isEligible = when (quickMatch.targetType) {
            QuickMatchTargetType.MINGLERS -> activeTravelerUserIds.contains(userId)
            QuickMatchTargetType.LOCALS -> localUserIds.contains(userId)
            QuickMatchTargetType.ANY -> activeTravelerUserIds.contains(userId) || localUserIds.contains(userId)
        }
        if (!isEligible) {
            throw QuickMatchResponderNotEligibleException(userId, quickMatch.city.id)
        }
    }

    private fun findActiveTravelerUserIds(cityId: Long, excludeUserId: Long): List<Long> {
        return tripRepository.findActiveTravelerUserIdsByCityId(
            cityId = cityId,
            targetDate = LocalDate.now(),
            excludeUserId = excludeUserId,
        )
    }

    private fun findLocalUserIds(cityId: Long, excludeUserId: Long): List<Long> {
        return localService.findLocalUserIdsByCityIdExcludingUserId(cityId, excludeUserId)
    }

    private fun findTargetUserIdsByTargetType(
        cityId: Long,
        excludeUserId: Long,
        targetType: QuickMatchTargetType,
    ): List<Long> {
        val travelerUserIds = findActiveTravelerUserIds(cityId, excludeUserId)
        val localUserIds = findLocalUserIds(cityId, excludeUserId)
        return when (targetType) {
            QuickMatchTargetType.MINGLERS -> travelerUserIds
            QuickMatchTargetType.LOCALS -> localUserIds
            QuickMatchTargetType.ANY -> (travelerUserIds + localUserIds).distinct()
        }.sorted()
    }

    private fun publishCityAlert(
        cityId: Long,
        eventType: String,
        quickMatch: QuickMatch,
        targetType: QuickMatchTargetType,
        targetUserIds: List<Long>,
    ) {
        val payload = CityQuickMatchSocketEvent(
            eventType = eventType,
            targetType = targetType,
            quickMatch = quickMatchMapStruct.toDto(quickMatch),
            targetUserIds = targetUserIds,
        )
        val typedTopic = "/topic/cities/$cityId/quick-matches/${targetType.name.lowercase()}"
        listOf(
            "/topic/cities/$cityId/quick-matches",
            typedTopic,
        ).forEach { destination ->
            simpMessagingTemplate.convertAndSend(destination, payload)
        }
    }

    private fun publishUserAlert(userId: Long, eventType: String, quickMatch: QuickMatch) {
        simpMessagingTemplate.convertAndSend(
            "/topic/users/$userId/quick-matches",
            UserQuickMatchSocketEvent(
                eventType = eventType,
                quickMatch = quickMatchMapStruct.toDto(quickMatch),
            ),
        )
    }

    private fun findQuickMatchById(quickMatchId: Long): QuickMatch {
        return quickMatchRepository.findById(quickMatchId)
            .orElseThrow { QuickMatchNotFoundException(quickMatchId) }
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
    }

    private fun findCityById(cityId: Long): City {
        return cityRepository.findById(cityId)
            .orElseThrow { CityNotFoundException(cityId) }
    }

    data class CityQuickMatchSocketEvent(
        val eventType: String,
        val targetType: QuickMatchTargetType,
        val quickMatch: QuickMatchDto,
        val targetUserIds: List<Long>,
    )

    data class UserQuickMatchSocketEvent(
        val eventType: String,
        val quickMatch: QuickMatchDto,
    )
}
