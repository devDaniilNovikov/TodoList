package dn.tasktracker.service.impl;


import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportServiceImpl implements ExportService {


    private final TaskRepository taskRepository;

    @Override
    public Resource exportToExcelFile() {
        List<TaskEntity> tasks = taskRepository.findAll();
        String[] headers = {"ID", "Название", "Описание", "Статус", "Дата создания", "Пользователь"};
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String filename = "tasks_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm")) + ".xlsx";
        String filePath = System.getProperty("user.home") + "/" + filename;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Задачи");
            CellStyle headerStyle = getCellStyle(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                createHeaderCell(headerRow, i, headers[i], headerStyle);
            }

            int rowNum = 1;
            for (TaskEntity task : tasks) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(task.getId());
                row.createCell(1).setCellValue(task.getTitle());
                row.createCell(2).setCellValue(task.getDescription());
                row.createCell(3).setCellValue(task.getStatus());
                row.createCell(4).setCellValue(task.getCreatedAt().format(formatter));
                row.createCell(5).setCellValue(task.getUser().getUsername());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                workbook.write(fileOutputStream);
                log.info("Задачи выгружены в файл: {}", filePath);
            }
            return new ByteArrayResource(Files.readAllBytes(Paths.get(filePath)));
            } catch (IOException e) {
            log.error("Ошибка при экспорте задач: {}", e.getMessage());
            throw new RuntimeException(e);
            }
        }



    private CellStyle getCellStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        return headerStyle;
    }

    private void createHeaderCell(Row row,int column,String value,CellStyle style){
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
