package com.tu.thesis.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tu.thesis.business.OtherImpl;
import com.tu.thesis.entity.UniTimeSlots;
import com.tu.thesis.generator.BusinessObject;
import com.tu.thesis.generator.DAYS;

public class CSVHelper {

	public static void exportDataToExcel(String fileName, Map<DAYS, BusinessObject[][]> schedule) throws IOException {

		Map<DAYS, BusinessObject[][]> theMap = new TreeMap<>(schedule);

		List<UniTimeSlots> timeSlots = OtherImpl.retrieveAllTimeSlots();
		timeSlots.add(0, new UniTimeSlots(20, ""));

		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Program");

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		int counter = 0;

		for (DAYS d : theMap.keySet()) {
			String days = "";
			switch (d) {
			case MON:
				days = "Понеделник";
				break;
			case TUE:
				days = "Вторник";
				break;
			case WED:
				days = "Сряда";
				break;
			case THU:
				days = "Четвъртък";
				break;
			case FR:
				days = "Петък";
				break;
			}

			Row rowD = sheet.createRow((short) counter);

			Cell cellDay = rowD.createCell((short) 0);
			Font fontDays = workbook.createFont();
			fontDays.setBold(true);
			fontDays.setFontHeightInPoints((short) 12);
			fontDays.setColor(IndexedColors.SEA_GREEN.getIndex());

			CellStyle daysCellStyle = workbook.createCellStyle();
			daysCellStyle.setFont(fontDays);

			daysCellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellDay.setCellValue(days);
			cellDay.setCellStyle(daysCellStyle);

			sheet.addMergedRegion(new CellRangeAddress(counter, counter + 1, 0, 14));
			// cellDay.setCellValue(new XSSFRichTextString("This is a test of merging"));

			counter += 2;

			// Create a Row
			Row headerRow = sheet.createRow(counter);

			// Create cells
			for (int i = 0; i < timeSlots.size(); i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(timeSlots.get(i).getName());
				cell.setCellStyle(headerCellStyle);
			}

			sheet.setColumnWidth(0, 1000);

			// Resize all columns to fit the content size
			for (int i = 1; i < timeSlots.size(); i++) {
				// sheet.autoSizeColumn(i);

				sheet.setColumnWidth(i, 3600);
			}

			counter++;
			int rowNum = counter;
			Row row = sheet.createRow((short) rowNum);
			row.setHeightInPoints((2 * sheet.getDefaultRowHeightInPoints()));
			for (int i = 0; i < theMap.get(d).length; i++) {

				row = sheet.createRow(rowNum);

				for (int j = 0; j < theMap.get(d)[i].length; j++) {

					String value = "";
					if (theMap.get(d)[i][j] != null && theMap.get(d)[i][j].getSub() != null) {
						value = theMap.get(d)[i][j].getSub().getName();
						if (theMap.get(d)[i][j].isLecture()) {
							value += " (л) - ";
						} else {
							value += " (у) - ";
						}
						value += theMap.get(d)[i][j].getRoom().getId();

					}

					row.createCell(0).setCellValue(i);
					Cell cell = row.createCell(j + 1);
					CellStyle cs = workbook.createCellStyle();
					cs.setWrapText(true);
					cell.setCellStyle(cs);
					cell.setCellValue(value);

					// row.createCell(j + 1).setCellValue(value);

				}

				rowNum++;
			}
			counter = rowNum;
		}

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();

	}
}
