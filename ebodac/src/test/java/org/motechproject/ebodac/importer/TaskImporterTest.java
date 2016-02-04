package org.motechproject.ebodac.importer;


import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.service.TaskService;
import org.osgi.framework.Bundle;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TaskImporter.class)
public class TaskImporterTest {

    @InjectMocks
    private TaskImporter taskImporter = new TaskImporter();

    @Mock
    private TaskService taskService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldImportTasks() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/tasks/subject-status-task.json");
        String json = IOUtils.toString(inputStream);
        inputStream.close();
        inputStream = getClass().getResourceAsStream("/tasks/subject-status-task.json");

        URL url = PowerMockito.mock(URL.class);
        Mockito.when(url.openStream()).thenReturn(inputStream);
        Mockito.when(url.getPath()).thenReturn("");

        Enumeration<URL> urls = Collections.enumeration(Collections.singleton(url));
        Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.findEntries("tasks/", "*.json", false)).thenReturn(urls);

        Mockito.when(taskService.findTasksByName("TEST EBODAC Subject status")).thenReturn(Collections.EMPTY_LIST);

        taskImporter.importTasks(bundle);

        Mockito.verify(taskService).importTask(json);
        inputStream.close();
    }

    @Test
    public void shouldNotImportTaskIfItAlreadyExist() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/tasks/subject-status-task.json");

        URL url = PowerMockito.mock(URL.class);
        Mockito.when(url.openStream()).thenReturn(inputStream);
        Mockito.when(url.getPath()).thenReturn("");

        Enumeration<URL> urls = Collections.enumeration(Collections.singleton(url));
        Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.findEntries("tasks/", "*.json", false)).thenReturn(urls);

        Mockito.when(taskService.findTasksByName("TEST EBODAC Subject status")).thenReturn(Collections.singletonList(new Task()));

        taskImporter.importTasks(bundle);

        Mockito.verify(taskService, Mockito.never()).importTask(Mockito.anyString());
        inputStream.close();
    }
}
