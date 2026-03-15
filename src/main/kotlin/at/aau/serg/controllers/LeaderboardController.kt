package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(): List<GameResult> =
        gameResultService.getGameResults().sortedWith(compareBy({ -it.score }, { -it.timeInSeconds }, { it.id }))
        //ID based tiebreaker is upheld for same score, same time edge cases

    @GetMapping
    fun getLeaderboardForRank(@RequestBody rank: Int): List<GameResult> {
        val leaderboard = getLeaderboard()

        // Validate rank
        if (rank < 1 || rank > leaderboard.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Rank does not exist")
        }

        val index = rank - 1  // convert rank to index

        val start = maxOf(0, index - 3)
        val end = minOf(leaderboard.size, index + 4) // +4 because of how subList works

        return leaderboard.subList(start, end)
    }
}