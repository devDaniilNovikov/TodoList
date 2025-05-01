package dn.tasktracker.service.impl;


import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public  class ExportServiceImpl implements ExportService {

    private final TaskRepository taskRepository;


    @Override
    @SneakyThrows
    public void exportToExcelFile(String filePath) {

        List<TaskEntity> tasks = taskRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Задачи");
            CellStyle headerStyle = getCellStyle(workbook);
            Row headerRow = sheet.createRow(0);
            createHeaderCell(headerRow, 0, "ID", headerStyle);
            createHeaderCell(headerRow, 1, "Название", headerStyle);
            createHeaderCell(headerRow, 2, "Описание", headerStyle);
            createHeaderCell(headerRow, 3, "Статус", headerStyle);
            createHeaderCell(headerRow, 4, "Дата создания", headerStyle);
            createHeaderCell(headerRow, 5, "Пользователь", headerStyle);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int rowNum = 1;
            for (TaskEntity task : tasks){
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(task.getId());
                row.createCell(1).setCellValue(task.getTitle());
                row.createCell(2).setCellValue(task.getDescription());
                row.createCell(3).setCellValue(task.getStatus());
                row.createCell(4).setCellValue(task.getCreatedAt()
                        .format(formatter)
                );
                row.createCell(5).setCellValue(task.getUser().getUsername());
            }

        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }
        log.info("Задачи выгружены в файл: {}", filePath);
        } catch (IOException e) {
            log.error("Ошибка при выгрузке задач в файл: {}", e.getMessage());
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
