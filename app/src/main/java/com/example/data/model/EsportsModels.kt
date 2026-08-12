package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MatchStatus {
    UPCOMING,
    LIVE,
    FINISHED
}

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val gameTitle: String = "Free Fire",
    val description: String = "",
    val bannerColorHex: String = "#00E5FF",
    val isArchived: Boolean = false,
    val killPointMultiplier: Int = 1,
    // Default placement points: 1st=12, 2nd=9, 3rd=8, 4th=7, 5th=6, 6th=5, 7th=4, 8th=3, 9th=2, 10th=1, 11th-16th=0
    val placementPointsCsv: String = "12,9,8,7,6,5,4,3,2,1,0,0,0,0,0,0",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getPointsForPlacement(placement: Int): Int {
        if (placement < 1) return 0
        val points = placementPointsCsv.split(",").mapNotNull { it.trim().toIntOrNull() }
        val index = placement - 1
        return if (index < points.size) points[index] else 0
    }
}

@Entity(tableName = "teams")
data class Team(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: Long,
    val name: String,
    val tag: String,
    val logoColorHex: String = "#FFB800",
    val slotNumber: Int = 1
)

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teamId: Long,
    val ign: String,
    val role: String = "Rusher",
    val isCaptain: Boolean = false
)

@Entity(tableName = "matches")
data class EsportsMatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: Long,
    val matchNumber: Int,
    val matchName: String,
    val mapName: String = "Bermuda",
    val status: MatchStatus = MatchStatus.UPCOMING,
    val startTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "match_team_results")
data class MatchTeamResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchId: Long,
    val teamId: Long,
    val placement: Int = 0, // 1 to 16, 0 if unranked
    val kills: Int = 0,
    val booyah: Boolean = false
)

@Entity(tableName = "admin_config")
data class AdminConfig(
    @PrimaryKey val id: Int = 1,
    val pin: String = "739284",
    val activeTournamentId: Long = 1
)

// UI & Leaderboard Helper Models
data class TeamLeaderboardEntry(
    val team: Team,
    val rank: Int = 0,
    val matchesPlayed: Int = 0,
    val booyahCount: Int = 0,
    val totalKills: Int = 0,
    val totalKillPoints: Int = 0,
    val totalPlacementPoints: Int = 0,
    val totalPoints: Int = 0,
    val topPlayer: String? = null
)

data class TeamWithPlayers(
    val team: Team,
    val players: List<Player> = emptyList(),
    val leaderboardStats: TeamLeaderboardEntry? = null
)

data class MatchWithResults(
    val match: EsportsMatch,
    val results: List<MatchTeamScore> = emptyList()
)

data class MatchTeamScore(
    val resultId: Long = 0,
    val matchId: Long,
    val team: Team,
    val placement: Int = 0,
    val kills: Int = 0,
    val placementPoints: Int = 0,
    val totalPoints: Int = 0,
    val booyah: Boolean = false
)
