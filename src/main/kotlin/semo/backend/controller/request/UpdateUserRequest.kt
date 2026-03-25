package semo.backend.controller.request

data class UpdateUserRequest(
    val username: PatchValue<String?> = PatchValue.undefined(),
    val password: PatchValue<String?> = PatchValue.undefined(),
    val name: PatchValue<String?> = PatchValue.undefined(),
    val email: PatchValue<String?> = PatchValue.undefined(),
    val phone: PatchValue<String?> = PatchValue.undefined(),
    val introduction: PatchValue<String?> = PatchValue.undefined(),
    val nationalityId: PatchValue<Long?> = PatchValue.undefined(),
    val keywordIds: PatchValue<List<Long>?> = PatchValue.undefined(),
)
