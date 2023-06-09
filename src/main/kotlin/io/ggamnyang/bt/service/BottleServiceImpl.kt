package io.ggamnyang.bt.service

import io.ggamnyang.bt.domain.entity.Bottle
import io.ggamnyang.bt.domain.entity.User
import io.ggamnyang.bt.domain.enum.BottleSource
import io.ggamnyang.bt.dto.request.PostBottleRequest
import io.ggamnyang.bt.repository.BottleRepository
import io.ggamnyang.bt.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class BottleServiceImpl(
    private val bottleRepository: BottleRepository,
    private val userRepository: UserRepository
) : BottleService {

    override fun findAll(user: User, bottleSource: BottleSource): List<Bottle> =
        when (bottleSource) {
            BottleSource.CREATED -> bottleRepository.findAllByCreator(user)
            BottleSource.RECEIVED -> bottleRepository.findAllByReceiver(user)
        }

    override fun save(bottle: Bottle) = bottleRepository.save(bottle)
    override fun createBottle(creator: User, dto: PostBottleRequest): Bottle {
        val receiver = findReceiver(creator)

        return Bottle(creator, receiver, dto.letter)
    }

    private fun findReceiver(creator: User): User {
        val users = userRepository.findRandom()
        if (users.first().id == creator.id) return users.last() // FIXME: user가 1명이라면?

        return users.first()
    }
}
