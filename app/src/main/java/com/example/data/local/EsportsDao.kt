package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminConfig
import com.example.data.model.EsportsMatch
import com.example.data.model.MatchTeamResult
import com.example.data.model.Player
import com.example.data.model.Team
import com.example.data.model.Tournament
import kotlinx.coroutines.flow.Flow

@Dao
interface EsportsDao {

    // --- Tournaments ---
    @Query("SELECT * FROM tournaments ORDER BY createdAt DESC")
    fun getAllTournaments(): Flow<List<Tournament>>

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    fun getTournamentById(id: Long): Flow<Tournament?>

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    suspend fun getTournamentByIdDirect(id: Long): Tournament?

    @Query("SELECT * FROM tournaments ORDER BY createdAt ASC LIMIT 1")
    suspend fun getFirstTournamentDirect(): Tournament?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament): Long

    @Update
    suspend fun updateTournament(tournament: Tournament)

    @Query("DELETE FROM tournaments WHERE id = :id")
    suspend fun deleteTournament(id: Long)


    // --- Teams ---
    @Query("SELECT * FROM teams WHERE tournamentId = :tournamentId ORDER BY slotNumber ASC, name ASC")
    fun getTeamsByTournament(tournamentId: Long): Flow<List<Team>>

    @Query("SELECT * FROM teams WHERE tournamentId = :tournamentId ORDER BY slotNumber ASC, name ASC")
    suspend fun getTeamsByTournamentDirect(tournamentId: Long): List<Team>

    @Query("SELECT COUNT(*) FROM teams WHERE tournamentId = :tournamentId")
    suspend fun getTeamsCount(tournamentId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: Team): Long

    @Update
    suspend fun updateTeam(team: Team)

    @Query("DELETE FROM teams WHERE id = :id")
    suspend fun deleteTeam(id: Long)

    @Query("DELETE FROM teams WHERE tournamentId = :tournamentId")
    suspend fun deleteTeamsByTournament(tournamentId: Long)


    // --- Players ---
    @Query("SELECT * FROM players WHERE teamId = :teamId ORDER BY isCaptain DESC, ign ASC")
    fun getPlayersByTeam(teamId: Long): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE teamId = :teamId ORDER BY isCaptain DESC, ign ASC")
    suspend fun getPlayersByTeamDirect(teamId: Long): List<Player>

    @Query("SELECT p.* FROM players p INNER JOIN teams t ON p.teamId = t.id WHERE t.tournamentId = :tournamentId")
    fun getPlayersForTournament(tournamentId: Long): Flow<List<Player>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player): Long

    @Query("DELETE FROM players WHERE id = :id")
    suspend fun deletePlayer(id: Long)

    @Query("DELETE FROM players WHERE teamId = :teamId")
    suspend fun deletePlayersByTeam(teamId: Long)


    // --- Matches ---
    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId ORDER BY matchNumber ASC")
    fun getMatchesByTournament(tournamentId: Long): Flow<List<EsportsMatch>>

    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId ORDER BY matchNumber ASC")
    suspend fun getMatchesByTournamentDirect(tournamentId: Long): List<EsportsMatch>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    fun getMatchById(id: Long): Flow<EsportsMatch?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: EsportsMatch): Long

    @Update
    suspend fun updateMatch(match: EsportsMatch)

    @Query("DELETE FROM matches WHERE id = :id")
    suspend fun deleteMatch(id: Long)

    @Query("DELETE FROM matches WHERE tournamentId = :tournamentId")
    suspend fun deleteMatchesByTournament(tournamentId: Long)


    // --- Match Results ---
    @Query("SELECT * FROM match_team_results WHERE matchId = :matchId")
    fun getResultsByMatch(matchId: Long): Flow<List<MatchTeamResult>>

    @Query("SELECT * FROM match_team_results WHERE matchId = :matchId")
    suspend fun getResultsByMatchDirect(matchId: Long): List<MatchTeamResult>

    @Query("SELECT r.* FROM match_team_results r INNER JOIN matches m ON r.matchId = m.id WHERE m.tournamentId = :tournamentId")
    fun getResultsByTournament(tournamentId: Long): Flow<List<MatchTeamResult>>

    @Query("SELECT r.* FROM match_team_results r INNER JOIN matches m ON r.matchId = m.id WHERE m.tournamentId = :tournamentId")
    suspend fun getResultsByTournamentDirect(tournamentId: Long): List<MatchTeamResult>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateResult(result: MatchTeamResult): Long

    @Query("DELETE FROM match_team_results WHERE matchId = :matchId")
    suspend fun deleteResultsForMatch(matchId: Long)

    @Query("DELETE FROM match_team_results WHERE teamId = :teamId")
    suspend fun deleteResultsForTeam(teamId: Long)


    // --- Admin Config ---
    @Query("SELECT * FROM admin_config WHERE id = 1 LIMIT 1")
    fun getAdminConfig(): Flow<AdminConfig?>

    @Query("SELECT * FROM admin_config WHERE id = 1 LIMIT 1")
    suspend fun getAdminConfigDirect(): AdminConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAdminConfig(config: AdminConfig)


    // --- Bulk Wipe for Reset ---
    @Query("DELETE FROM tournaments")
    suspend fun deleteAllTournaments()

    @Query("DELETE FROM teams")
    suspend fun deleteAllTeams()

    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()

    @Query("DELETE FROM match_team_results")
    suspend fun deleteAllResults()
}
