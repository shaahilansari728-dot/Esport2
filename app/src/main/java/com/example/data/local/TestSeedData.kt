package com.example.data.local

import com.example.data.model.EsportsMatch
import com.example.data.model.MatchStatus
import com.example.data.model.MatchTeamResult
import com.example.data.model.Player
import com.example.data.model.Team
import com.example.data.model.Tournament

object TestSeedData {

    fun getDefaultTournament(): Tournament {
        return Tournament(
            id = 1,
            name = "Free Fire Championship 2026",
            gameTitle = "Free Fire Battle Royale",
            description = "Official Season 1 Esports Series featuring 16 Top Professional Teams",
            bannerColorHex = "#00E5FF",
            killPointMultiplier = 1,
            placementPointsCsv = "12,9,8,7,6,5,4,3,2,1,0,0,0,0,0,0"
        )
    }

    val sampleTeamDefs = listOf(
        TeamDef("EVOS Divine", "EVOS", "#00E5FF", 1, listOf("EVOS_Sam13", "EVOS_MR05", "EVOS_Street", "EVOS_Abu")),
        TeamDef("Total Gaming", "TG", "#FFB800", 2, listOf("TG_Fozyia", "TG_Mafia", "TG_Fizzy", "TG_Prince")),
        TeamDef("LOUD Esports", "LOUD", "#22C55E", 3, listOf("LOUD_Cauan7", "LOUD_Kroonos", "LOUD_Lost", "LOUD_Draco")),
        TeamDef("Gods Reign", "GR", "#EF4444", 4, listOf("GR_Iconic", "GR_Ninja", "GR_Viper", "GR_Pahadi")),
        TeamDef("Nigma Galaxy", "NG", "#8B5CF6", 5, listOf("NG_Golden", "NG_Vasi", "NG_Dark", "NG_Shadow")),
        TeamDef("Alpha7 Esports", "A7", "#3B82F6", 6, listOf("A7_Carrilho", "A7_Pedro", "A7_Reus", "A7_Max")),
        TeamDef("Team Elite", "TE", "#F59E0B", 7, listOf("TE_Killer", "TE_R3D", "TE_Xavier", "TE_Joker")),
        TeamDef("Orangutan", "OG", "#EC4899", 8, listOf("OG_Ash", "OG_JAY", "OG_Wizard", "OG_Blaze")),
        TeamDef("Skylightz Gaming", "SG", "#10B981", 9, listOf("SG_Radhe", "SG_Gamer", "SG_ProPlayer", "SG_Slayer")),
        TeamDef("Stalwart Esports", "STE", "#6366F1", 10, listOf("STE_Legend", "STE_Falcon", "STE_Hunter", "STE_Storm")),
        TeamDef("Velocity Gaming", "VLT", "#D97706", 11, listOf("VLT_Alpha", "VLT_Beta", "VLT_Gamma", "VLT_Delta")),
        TeamDef("S8UL Esports", "S8UL", "#14B8A6", 12, listOf("S8UL_Mortal", "S8UL_Viper", "S8UL_Regaltos", "S8UL_Aman")),
        TeamDef("Blind Esports", "BLD", "#8257E5", 13, listOf("BLD_Psycho", "BLD_Venom", "BLD_Spidey", "BLD_Ghost")),
        TeamDef("Team Secret", "TS", "#06B6D4", 14, listOf("TS_Kev", "TS_Spark", "TS_Raptor", "TS_Ace")),
        TeamDef("Falcon Esports", "FLC", "#E11D48", 15, listOf("FLC_Eagle", "FLC_Hawks", "FLC_Sky", "FLC_Breeze")),
        TeamDef("Apex Gaming", "APX", "#A855F7", 16, listOf("APX_Titan", "APX_Nova", "APX_Rogue", "APX_Apex"))
    )

    data class TeamDef(
        val name: String,
        val tag: String,
        val colorHex: String,
        val slot: Int,
        val players: List<String>
    )

    val sampleMatches = listOf(
        EsportsMatch(
            id = 1,
            tournamentId = 1,
            matchNumber = 1,
            matchName = "Match 1 - Bermuda",
            mapName = "Bermuda",
            status = MatchStatus.FINISHED
        ),
        EsportsMatch(
            id = 2,
            tournamentId = 1,
            matchNumber = 2,
            matchName = "Match 2 - Purgatory",
            mapName = "Purgatory",
            status = MatchStatus.FINISHED
        ),
        EsportsMatch(
            id = 3,
            tournamentId = 1,
            matchNumber = 3,
            matchName = "Match 3 - Alpine",
            mapName = "Alpine",
            status = MatchStatus.FINISHED
        ),
        EsportsMatch(
            id = 4,
            tournamentId = 1,
            matchNumber = 4,
            matchName = "Match 4 - Kalahari",
            mapName = "Kalahari",
            status = MatchStatus.LIVE
        ),
        EsportsMatch(
            id = 5,
            tournamentId = 1,
            matchNumber = 5,
            matchName = "Match 5 - Nexterra",
            mapName = "Nexterra",
            status = MatchStatus.UPCOMING
        )
    )

    // Pre-calculated realistic placements & kills for finished matches 1, 2, 3
    // Match 1 Placements: EVOS 1st (12 kills), TG 2nd (8 kills), LOUD 3rd (6 kills), etc.
    val match1Placements = listOf(
        PlacementInfo(teamSlot = 1, placement = 1, kills = 12, booyah = true),   // EVOS
        PlacementInfo(teamSlot = 2, placement = 2, kills = 8, booyah = false),   // TG
        PlacementInfo(teamSlot = 3, placement = 3, kills = 6, booyah = false),   // LOUD
        PlacementInfo(teamSlot = 4, placement = 4, kills = 7, booyah = false),   // GR
        PlacementInfo(teamSlot = 5, placement = 5, kills = 4, booyah = false),   // NG
        PlacementInfo(teamSlot = 6, placement = 6, kills = 5, booyah = false),   // A7
        PlacementInfo(teamSlot = 7, placement = 7, kills = 3, booyah = false),   // TE
        PlacementInfo(teamSlot = 8, placement = 8, kills = 2, booyah = false),   // OG
        PlacementInfo(teamSlot = 9, placement = 9, kills = 4, booyah = false),   // SG
        PlacementInfo(teamSlot = 10, placement = 10, kills = 1, booyah = false), // STE
        PlacementInfo(teamSlot = 11, placement = 11, kills = 2, booyah = false), // VLT
        PlacementInfo(teamSlot = 12, placement = 12, kills = 0, booyah = false), // S8UL
        PlacementInfo(teamSlot = 13, placement = 13, kills = 1, booyah = false), // BLD
        PlacementInfo(teamSlot = 14, placement = 14, kills = 3, booyah = false), // TS
        PlacementInfo(teamSlot = 15, placement = 15, kills = 0, booyah = false), // FLC
        PlacementInfo(teamSlot = 16, placement = 16, kills = 1, booyah = false)  // APX
    )

    // Match 2 Placements: TG 1st (14 kills), EVOS 2nd (7 kills), GR 3rd (9 kills)...
    val match2Placements = listOf(
        PlacementInfo(teamSlot = 2, placement = 1, kills = 14, booyah = true),   // TG
        PlacementInfo(teamSlot = 1, placement = 2, kills = 7, booyah = false),   // EVOS
        PlacementInfo(teamSlot = 4, placement = 3, kills = 9, booyah = false),   // GR
        PlacementInfo(teamSlot = 3, placement = 4, kills = 5, booyah = false),   // LOUD
        PlacementInfo(teamSlot = 7, placement = 5, kills = 6, booyah = false),   // TE
        PlacementInfo(teamSlot = 5, placement = 6, kills = 3, booyah = false),   // NG
        PlacementInfo(teamSlot = 8, placement = 7, kills = 4, booyah = false),   // OG
        PlacementInfo(teamSlot = 6, placement = 8, kills = 2, booyah = false),   // A7
        PlacementInfo(teamSlot = 12, placement = 9, kills = 5, booyah = false),  // S8UL
        PlacementInfo(teamSlot = 10, placement = 10, kills = 2, booyah = false), // STE
        PlacementInfo(teamSlot = 9, placement = 11, kills = 1, booyah = false),  // SG
        PlacementInfo(teamSlot = 14, placement = 12, kills = 0, booyah = false), // TS
        PlacementInfo(teamSlot = 11, placement = 13, kills = 2, booyah = false), // VLT
        PlacementInfo(teamSlot = 13, placement = 14, kills = 1, booyah = false), // BLD
        PlacementInfo(teamSlot = 16, placement = 15, kills = 0, booyah = false), // APX
        PlacementInfo(teamSlot = 15, placement = 16, kills = 1, booyah = false)  // FLC
    )

    // Match 3 Placements: LOUD 1st (11 kills), A7 2nd (10 kills), EVOS 3rd (8 kills)...
    val match3Placements = listOf(
        PlacementInfo(teamSlot = 3, placement = 1, kills = 11, booyah = true),   // LOUD
        PlacementInfo(teamSlot = 6, placement = 2, kills = 10, booyah = false),  // A7
        PlacementInfo(teamSlot = 1, placement = 3, kills = 8, booyah = false),   // EVOS
        PlacementInfo(teamSlot = 2, placement = 4, kills = 6, booyah = false),   // TG
        PlacementInfo(teamSlot = 5, placement = 5, kills = 7, booyah = false),   // NG
        PlacementInfo(teamSlot = 4, placement = 6, kills = 4, booyah = false),   // GR
        PlacementInfo(teamSlot = 9, placement = 7, kills = 5, booyah = false),   // SG
        PlacementInfo(teamSlot = 7, placement = 8, kills = 3, booyah = false),   // TE
        PlacementInfo(teamSlot = 13, placement = 9, kills = 4, booyah = false),  // BLD
        PlacementInfo(teamSlot = 8, placement = 10, kills = 2, booyah = false),  // OG
        PlacementInfo(teamSlot = 10, placement = 11, kills = 1, booyah = false), // STE
        PlacementInfo(teamSlot = 15, placement = 12, kills = 3, booyah = false), // FLC
        PlacementInfo(teamSlot = 12, placement = 13, kills = 1, booyah = false), // S8UL
        PlacementInfo(teamSlot = 11, placement = 14, kills = 0, booyah = false), // VLT
        PlacementInfo(teamSlot = 14, placement = 15, kills = 2, booyah = false), // TS
        PlacementInfo(teamSlot = 16, placement = 16, kills = 0, booyah = false)  // APX
    )

    data class PlacementInfo(
        val teamSlot: Int,
        val placement: Int,
        val kills: Int,
        val booyah: Boolean
    )
}
