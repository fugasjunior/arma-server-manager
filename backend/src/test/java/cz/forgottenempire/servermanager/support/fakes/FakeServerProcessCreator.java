package cz.forgottenempire.servermanager.support.fakes;

import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Primary
public class FakeServerProcessCreator extends ServerProcessCreator {

    private final FakeProcessFactory fakeProcessFactory;

    @Autowired
    public FakeServerProcessCreator(FakeProcessFactory fakeProcessFactory) {
        this.fakeProcessFactory = fakeProcessFactory;
    }

    @Override
    public Process startProcessWithRedirectedOutput(File executable, List<String> parameters, File outputFile)
            throws IOException {
        if (outputFile != null && outputFile.getParentFile() != null) {
            outputFile.getParentFile().mkdirs();
        }
        return fakeProcessFactory.resolveForServerProcess();
    }
}
