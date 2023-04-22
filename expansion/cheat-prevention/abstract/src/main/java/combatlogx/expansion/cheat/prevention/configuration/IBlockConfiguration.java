package combatlogx.expansion.cheat.prevention.configuration;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

import org.jetbrains.annotations.NotNull;

public interface IBlockConfiguration extends IConfigurable {
    boolean isPreventInteraction();
    boolean isPreventBreaking();
    boolean isPreventPlacing();
    boolean isPreventPortalCreation();

    boolean isPreventInteraction(@NotNull XMaterial blockType);
    boolean isPreventBreaking(@NotNull XMaterial blockType);
    boolean isPreventPlacing(@NotNull XMaterial blockType);
}
