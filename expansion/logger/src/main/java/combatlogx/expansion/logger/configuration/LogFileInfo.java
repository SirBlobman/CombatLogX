package combatlogx.expansion.logger.configuration;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class LogFileInfo implements IConfigurable {
    private String fileName;
    private String fileExtraFormat;
    private String fileExtension;

    private transient DateFormat dateFormat;
    private final Pattern fileNameFixerPattern;

    public LogFileInfo() {
        this.fileName = "logger";
        this.fileExtraFormat = "yyyy.MM.dd";
        this.fileExtension = "log";

        this.dateFormat = null;
        this.fileNameFixerPattern = Pattern.compile("[^\\w.\\-]");
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setFileName(section.getString("file-name", "logger"));
        setFileExtraFormat(section.getString("file-extra-format", "yyyy.MM.dd"));
        setFileExtension(section.getString("file-extension", "log"));
    }

    public @NotNull String getFileName() {
        return this.fileName;
    }

    public void setFileName(@NotNull String fileName) {
        this.fileName = fileName;
    }

    public @NotNull String getFileExtraFormat() {
        return this.fileExtraFormat;
    }

    public void setFileExtraFormat(@NotNull String fileExtraFormat) {
        this.fileExtraFormat = fileExtraFormat;
        this.dateFormat = null;
    }

    public @NotNull String getFileExtension() {
        return this.fileExtension;
    }

    public void setFileExtension(@NotNull String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public DateFormat getDateFormat() {
        if (this.dateFormat != null) {
            return this.dateFormat;
        }

        String fileExtraFormat = getFileExtraFormat();
        this.dateFormat = new SimpleDateFormat(fileExtraFormat);
        return this.dateFormat;
    }

    public @NotNull File getCurrentLogFile(@NotNull File baseFolder) {
        String fileName = getFileName();
        DateFormat dateFormat = getDateFormat();
        String extension = getFileExtension();

        Instant now = Instant.now();
        Date date = Date.from(now);
        String extra = dateFormat.format(date);

        String preFile = (fileName + "-" + extra + "." + extension);
        Matcher matcher = this.fileNameFixerPattern.matcher(preFile);

        String finalFileName = matcher.replaceAll("_");
        return new File(baseFolder, finalFileName);
    }
}
