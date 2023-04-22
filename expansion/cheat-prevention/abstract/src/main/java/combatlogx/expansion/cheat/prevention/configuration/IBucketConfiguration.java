package combatlogx.expansion.cheat.prevention.configuration;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

import org.jetbrains.annotations.NotNull;

public interface IBucketConfiguration extends IConfigurable {
    boolean isPreventBucketEmpty();
    boolean isPreventBucketFill();

    boolean isPreventEmpty(@NotNull XMaterial material);
    boolean isPreventFill(@NotNull XMaterial material);
}
