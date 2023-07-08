package combatlogx.expansion.cheat.prevention.configuration;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

public interface IBucketConfiguration extends IConfigurable {
    boolean isPreventBucketEmpty();

    boolean isPreventBucketFill();

    boolean isPreventEmpty(@NotNull XMaterial material);

    boolean isPreventFill(@NotNull XMaterial material);
}
