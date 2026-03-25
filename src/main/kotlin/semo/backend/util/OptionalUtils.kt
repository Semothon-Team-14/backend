package semo.backend.util

import java.util.Optional

inline fun <T> Optional<T>?.applyIfProvided(
    block: (T?) -> Unit,
) {
    if (this != null) {
        block(orElse(null))
    }
}
