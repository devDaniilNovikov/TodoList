package dn.tasktracker.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface ExportService {

    Resource exportToExcelFile();
}
