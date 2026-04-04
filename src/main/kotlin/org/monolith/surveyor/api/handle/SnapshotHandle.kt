package org.monolith.surveyor.api.handle

/**
 * Opaque handle to a snapshot.
 */
interface SnapshotHandle {
    val id: String
    val label: String?
}