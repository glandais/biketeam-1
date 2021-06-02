package info.tomacla.biketeam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import info.tomacla.biketeam.common.Json;
import info.tomacla.biketeam.domain.map.Map;
import info.tomacla.biketeam.domain.map.MapType;
import liquibase.util.file.FilenameUtils;
import net.lingala.zip4j.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ArchiveService {

    @Value("${archive.directory:undefined}")
    private String archiveDirectory;

    @Autowired
    private MapService mapService;

    private ExecutorService executor = Executors.newFixedThreadPool(4);

    public boolean isActivated() {
        return !archiveDirectory.equals("undefined");
    }

    public List<String> listArchives() {
        if (isActivated()) {
            return Stream.of(new File(archiveDirectory).listFiles())
                    .filter(file -> !file.isDirectory())
                    .filter(file -> file.getName().endsWith(".zip"))
                    .map(File::getName)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void importArchive(String archiveName) {
        try {
            if (isActivated() && Files.exists(Path.of(archiveDirectory, archiveName))) {
                final Path targetArchive = Path.of(archiveDirectory, archiveName);

                // create target directory
                Path unzipDestination = Path.of(archiveDirectory, FilenameUtils.removeExtension(archiveName));
                Files.createDirectories(unzipDestination);

                // extract to directory
                new ZipFile(targetArchive.toFile()).extractAll(unzipDestination.toString());

                if (Files.exists(Path.of(unzipDestination.toString(), "descriptor.json"))) {
                    final List<ImportMapElement> importMapElements = Json.parse(Path.of(unzipDestination.toString(), "descriptor.json"), new TypeReference<List<ImportMapElement>>() {
                    });

                    for (ImportMapElement importMapElement : importMapElements) {
                        executor.submit(new ImportMap(unzipDestination, importMapElement, mapService));
                    }

                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ImportMapElement {

        private String fileName;
        private String name;
        private MapType type;
        private boolean visible;
        private List<String> tags;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public MapType getType() {
            return type;
        }

        public void setType(MapType type) {
            this.type = type;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    public static class ImportMap implements Runnable {

        private Path workingDirectory;
        private ImportMapElement element;
        private MapService mapService;

        public ImportMap(Path workingDirectory, ImportMapElement element, MapService mapService) {
            this.workingDirectory = workingDirectory;
            this.element = element;
            this.mapService = mapService;
        }

        @Override
        public void run() {
            try {

                final InputStream fileInputStream = Files.newInputStream(Path.of(workingDirectory.toString(), element.getFileName()));

                final Map newMap = mapService.save(fileInputStream, element.getName());

                newMap.setType(element.getType());
                newMap.setTags(element.getTags());
                newMap.setVisible(element.isVisible());
                mapService.save(newMap);

            } catch (Exception e) {
                // TODO log
            }
        }

    }

}
