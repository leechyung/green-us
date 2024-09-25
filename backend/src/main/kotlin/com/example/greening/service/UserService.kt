package com.example.greening.service

import com.example.greening.domain.item.User
import com.example.greening.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository) {

    /**
     * 회원 가입
     */
    @Transactional
    fun save(user: User) {
        validateDuplicateUser(user) //중복 회원 검사
        userRepository.save(user)
    }

    @Transactional
    fun updateUser(userSeq: Int, newUser: User) :User? {
        val existingUser = userRepository.findOne(userSeq)
        if (existingUser != null) {
            // 필드를 직접 업데이트
            existingUser.userName = newUser.userName ?: existingUser.userName
            existingUser.userEmail = newUser.userEmail ?: existingUser.userEmail
            existingUser.userAddr = newUser.userAddr ?: existingUser.userAddr
            existingUser.userAddrDetail = newUser.userAddrDetail ?: existingUser.userAddr
            existingUser.userPhone = newUser.userPhone ?: existingUser.userPhone
            existingUser.userPhoto = newUser.userPhoto ?: existingUser.userPhoto
            existingUser.userPedometer = newUser.userPedometer ?: existingUser.userPedometer
            existingUser.userWCount = newUser.userWCount ?: existingUser.userWCount
            // 관리자, 리뷰 및 결제 정보를 업데이트하지 않고 기존 정보 유지한다는 가정 -> 추후 필요에 따라 코드 변경
            existingUser.admins = existingUser.admins
//            existingUser.reviews = newUser.reviews
//            existingUser.payments = newUser.payments

            userRepository.save(existingUser)
            return existingUser
        } else {
            throw IllegalStateException("회원이 존재하지 않습니다.")
        }
    }

    @Transactional
    fun updateUserWCount(userSeq: Int) {
        val existingUser = userRepository.findOne(userSeq)
        if (existingUser != null && existingUser.userWCount!! < 5) {
            existingUser.userWCount = existingUser.userWCount!!.plus(1)
            userRepository.save(existingUser)
        } else {
            throw IllegalStateException("신고 횟수가 5회 이상입니다.")
        }
    }

    private fun validateDuplicateUser(user: User) {
        if(user.userEmail!=null) {
            val findUsers = userRepository.findByEmail(user.userEmail!!)
            if (findUsers!=null) {
                throw IllegalStateException("이미 존재하는 회원입니다.")
            }
        }else{
            throw IllegalStateException("잘못된 접근입니다.")
        }
    }

    @Transactional
    fun deleteUserByEmail(userEmail : String): User? {
        return userRepository.deleteByEmail(userEmail)
    }

    @Transactional
    fun deleteUser(userId: Int) {
        userRepository.deleteById(userId)
    }

    // 회원 전체 조회
    fun findUsers(): List<User> {
        return userRepository.findAll()
    }

    fun findOne(userSeq: Int): User? {
        return userRepository.findOne(userSeq) as User
    }

    fun findByEmail(userEmail: String): User?{
        return userRepository.findByEmail(userEmail)
    }
    fun findByPhone(userPhone: String): User?{
        return userRepository.findByPhone(userPhone)
    }

    fun findUserSeqByEmail(userEmail: String): Int? {
        return userRepository.findUserSeqByEmail(userEmail)
    }

    fun findUserWCountByEmail(userEmail: String): Int? {
        return userRepository.findUserWCountByEmail(userEmail)
    }
}