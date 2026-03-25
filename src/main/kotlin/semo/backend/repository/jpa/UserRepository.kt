package semo.backend.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import semo.backend.entity.User

@Repository
interface UserRepository : JpaRepository<User, Long>
