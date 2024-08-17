rootProject.name = "CombatLogX"

// CombatLogX API
include("api")

// Base Spigot Plugin
include("plugin")

// CombatLogX Expansions
include("expansion")

// Normal Expansions
include("expansion:action-bar")
include("expansion:boss-bar")
include("expansion:damage-tagger")
include("expansion:damage-effects")
include("expansion:death-effects")
include("expansion:force-field")
include("expansion:glowing")
include("expansion:logger")
include("expansion:loot-protection")
include("expansion:mob-tagger")
include("expansion:newbie-helper")
include("expansion:rewards")
include("expansion:scoreboard")

// Cheat Prevention
include("expansion:cheat-prevention:abstract")
include("expansion:cheat-prevention:legacy")
include("expansion:cheat-prevention:modern")
include("expansion:cheat-prevention:paper")
include("expansion:cheat-prevention")

// End Crystals
include("expansion:end-crystal:legacy")
include("expansion:end-crystal:modern")
include("expansion:end-crystal:moderner")
include("expansion:end-crystal")

// Compatibility expansions
include("expansion:compatibility")
include("expansion:compatibility:AngelChest")
include("expansion:compatibility:ASkyBlock")
include("expansion:compatibility:BSkyBlock")
include("expansion:compatibility:Citizens")
include("expansion:compatibility:CMI")
include("expansion:compatibility:CrackShot")
include("expansion:compatibility:CrashClaim")
include("expansion:compatibility:EssentialsX")
include("expansion:compatibility:FabledSkyBlock")
include("expansion:compatibility:Factions")
include("expansion:compatibility:FeatherBoard")
include("expansion:compatibility:GriefDefender")
include("expansion:compatibility:GriefPrevention")
include("expansion:compatibility:HuskHomes")
include("expansion:compatibility:HuskSync")
include("expansion:compatibility:HuskTowns")
include("expansion:compatibility:iDisguise")
include("expansion:compatibility:IridiumSkyblock")
include("expansion:compatibility:KingdomsX")
include("expansion:compatibility:Konquest")
include("expansion:compatibility:Lands")
include("expansion:compatibility:LibsDisguises")
include("expansion:compatibility:LuckPerms")
include("expansion:compatibility:MarriageMaster")
include("expansion:compatibility:MCPets")
include("expansion:compatibility:MythicMobs")
include("expansion:compatibility:PlaceholderAPI")
include("expansion:compatibility:PlayerParticles")
include("expansion:compatibility:PreciousStones")
include("expansion:compatibility:ProtectionStones")
include("expansion:compatibility:RedProtect")
include("expansion:compatibility:Residence")
include("expansion:compatibility:SuperiorSkyblock")
include("expansion:compatibility:SuperVanish")
include("expansion:compatibility:Towny")
include("expansion:compatibility:UltimateClaims")
include("expansion:compatibility:uSkyBlock")
include("expansion:compatibility:VanishNoPacket")
include("expansion:compatibility:WorldGuard")
// include("expansion:compatibility:ZNPCsPlus")

// Final ZIP Builder
include("builder")
