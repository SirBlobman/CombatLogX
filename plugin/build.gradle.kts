import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

val pluginVersion = rootProject.version.toString();
val pluginDepend = listOf("BlueSlimeCore")
val pluginSoftDepend = listOf(
    "AngelChest",
    "ASkyBlock",
    "BentoBox", // BSkyBlock is an addon of BentoBox and not its own plugin.
    "Citizens",
    "CMI",
    "CrackShot",
    "CrashClaim",
    "Essentials",
    "FabledSkyBlock",
    "FeatherBoard",
    "FlagWar", // Optional for the Towny Compatibility expansion.
    "GriefDefender",
    "GriefPrevention",
    "HuskHomes",
    "HuskSync",
    "HuskTowns",
    "iDisguise",
    "IridiumSkyblock",
    "Kingdoms",
    "Konquest",
    "Lands",
    "LibsDisguises",
    "MarriageMaster",
    "MCPets",
    "MythicMobs",
    "PlaceholderAPI",
    "PlayerParticles",
    "PreciousStones",
    "PremiumVanish",
    "ProtectionStones",
    "ProtocolLib", // Required for the Force Field expansion.
    "RedProtect",
    "Residence",
    "Sentinel", // Optional for the Citizens Compatibility expansion.
    "SuperiorSkyblock2",
    "SuperVanish",
    "Towny",
    "UltimateClaims",
    "uSkyBlock",
    "VanishNoPacket",
    "WorldGuard"
)

plugins {
    id("maven-publish")
    id("com.gradleup.shadow") version "9.2.2"
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
}

bukkit {
    name = "CombatLogX"
    description = "A modular server plugin that punishes players for logging out during combat."
    website = "https://modrinth.com/project/combatlogx/"

    main = "com.github.sirblobman.combatlogx.CombatPlugin"
    version = pluginVersion
    apiVersion = "1.13"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    foliaSupported = true
    authors = listOf("SirBlobman")

    depend = pluginDepend
    softDepend = pluginSoftDepend

    commands {
        register("combatlogx") {
            description = "Main command for CombatLogX."
            permission = "combatlogx.command.combatlogx"
            usage = "/<command> help"
            aliases = listOf("combatlog", "combattagx", "combattag", "clx", "ctx")
        }

        register("combat-timer") {
            description = "Check how much time you have left in combat."
            permission = "combatlogx.command.combat-timer"
            usage = "/<command> [player]"
            aliases = listOf("combattimer", "combattime", "ctime", "clt", "ct")
        }

        register("togglepvp") {
            description = "Enable or disable PVP for a player."
            permission = "combatlogx.command.togglepvp"
            description = """
                /<command> check
                /<command> on/off
                /<command> admin on/off <player>
            """.trimIndent()
            aliases = listOf("toggle-pvp", "pvptoggle", "pvp")
        }
    }

    permissions {
        register("combatlogx.bypass") {
            description = "Default bypass permission if not configured."
            default = BukkitPluginDescription.Permission.Default.FALSE
        }

        register("combatlogx.bypass.force.field") {
            description = "Default force-field bypass permission if not configured."
            default = BukkitPluginDescription.Permission.Default.FALSE
        }

        register("combatlogx.command.combat-timer") {
            description = "Access to the '/combat-timer' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.togglepvp") {
            description = "Access to the '/togglepvp' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.togglepvp.admin") {
            description = "Access to the '/togglepvp admin' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx") {
            description = "Access to the '/combatlogx' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.about") {
            description = "Access to the '/combatlogx about' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.help") {
            description = "Access to the '/combatlogx help' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.reload") {
            description = "Access to the '/combatlogx reload' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.tag") {
            description = "Access to the '/combatlogx tag' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.toggle") {
            description = "Access to the '/combatlogx toggle' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.untag") {
            description = "Access to the '/combatlogx untag' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.version") {
            description = "Access to the '/combatlogx version' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

fun quickRegister(definition: NamedDomainObjectContainer<PaperPluginDescription.DependencyDefinition>, name: String, required: Boolean) {
    definition.register(name) {
        this.load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        this.required = required
        this.joinClasspath = true
    }
}

paper {
    name = "CombatLogX"
    description = "A modular server plugin that punishes players for logging out during combat."
    website = "https://modrinth.com/project/combatlogx/"

    main = "com.github.sirblobman.combatlogx.CombatPlugin"
    version = pluginVersion
    apiVersion = "1.19"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    foliaSupported = true
    authors = listOf("SirBlobman")

    dependencies {
        pluginDepend.forEach { it -> quickRegister(serverDependencies, it, true) }
        pluginSoftDepend.forEach { it -> quickRegister(serverDependencies, it, false) }
    }

    permissions {
        register("combatlogx.bypass") {
            description = "Default bypass permission if not configured."
            default = BukkitPluginDescription.Permission.Default.FALSE
        }

        register("combatlogx.bypass.force.field") {
            description = "Default force-field bypass permission if not configured."
            default = BukkitPluginDescription.Permission.Default.FALSE
        }

        register("combatlogx.command.combat-timer") {
            description = "Access to the '/combat-timer' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.togglepvp") {
            description = "Access to the '/togglepvp' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.togglepvp.admin") {
            description = "Access to the '/togglepvp admin' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx") {
            description = "Access to the '/combatlogx' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.about") {
            description = "Access to the '/combatlogx about' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.help") {
            description = "Access to the '/combatlogx help' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.reload") {
            description = "Access to the '/combatlogx reload' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.tag") {
            description = "Access to the '/combatlogx tag' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.toggle") {
            description = "Access to the '/combatlogx toggle' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.untag") {
            description = "Access to the '/combatlogx untag' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("combatlogx.command.combatlogx.version") {
            description = "Access to the '/combatlogx version' command."
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

repositories {
    maven("https://repo.helpch.at/releases/")
}

dependencies {
    // Local Dependencies
    implementation(project(":api"))

    // Java Dependencies
    implementation("org.zeroturnaround:zt-zip:1.17") // ZT Zip

    // Plugin Dependencies
    compileOnly("me.clip:placeholderapi:2.11.6") // PlaceholderAPI
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set(null as String?)
        archiveFileName.set("CombatLogX.jar")

        relocate("org.zeroturnaround.zip", "com.github.sirblobman.combatlogx.zip")
        relocate("org.slf4j", "com.github.sirblobman.combatlogx.zip.slf4j")
    }

    named<ProcessResources>("processResources") {
        inputs.property("pluginVersion", pluginVersion)
        filesMatching("config.yml") {
            expand(inputs.properties)
        }
    }

    named("build") {
        dependsOn("shadowJar")
    }
}
