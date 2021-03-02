# Dummy Modules
Dummy modules are modules that are copies of plugins that don't have maven repositories.
This allows CodeMC to build CombatLogX without including premium or non-downloaded jars in the GitHub repository.
Only API methods that are used by CombatLogX are copied into dummy modules, and their contents are replaced with a
dummy UnsupportedOperationException.

## Current Modules
| Plugin | Reason |
| ------ | ------ |
| CrackShot | No maven repository available. |
| GriefDefender | I prefer not to use the JitPack repository. |
| Lands | I prefer not to use the JitPack repository. |
| MassiveCore | No maven repository available. |
| Factions | No maven repository available. |