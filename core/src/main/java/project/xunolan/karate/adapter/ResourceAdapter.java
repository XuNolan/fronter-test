package project.xunolan.karate.adapter;

import com.intuit.karate.resource.Resource;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

public class ResourceAdapter implements Resource {
    private final InputStream inputStream;

    public ResourceAdapter(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isClassPath() {
        return false;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public String getRelativePath() {
        return "";
    }

    @Override
    public Resource resolve(String path) {
        return null;
    }

    @Override
    public InputStream getStream() {
        return inputStream;
    }
}
