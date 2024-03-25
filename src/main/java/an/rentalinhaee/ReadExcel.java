package an.rentalinhaee;

import an.rentalinhaee.domain.FeeStudent;
import an.rentalinhaee.domain.Student;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadExcel {
    public static List<String> readOneExcel(String stuId) {
        try {
            // 프로젝트 내에 엑셀파일 있어야 함
            Resource resource = new ClassPathResource("static/studentId.xlsx");
            InputStream file = resource.getInputStream();
            IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);

            XSSFWorkbook workbook = new XSSFWorkbook(file);

            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();

            Integer stuIdInt = Integer.parseInt(stuId);
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    // 엑셀 파일에 학번 존재 O => <학번, 이름> 담은 리스트 리턴
                    if (cell.getCellType() == CellType.NUMERIC){
                        if(cell.getNumericCellValue() == stuIdInt) {
                            List<String> returnTrue = new ArrayList<>();
                            returnTrue.add(stuId);
                            returnTrue.add(cellIterator.next().getStringCellValue());
                            return returnTrue;
                        }
                    }
                }
            }
            file.close();
        } catch (Exception e){
            e.printStackTrace();

        }
        // 엑셀 파일에 학번 존재 X => 2개짜리 빈 리스트 리턴
        List<String> returnFalse = new ArrayList<>();
        returnFalse.add("");
        returnFalse.add("");
        return returnFalse;
    }

    public static List<Student> readAllExcel() {
        List<Student> allList = new ArrayList<>();
        try {
            // 프로젝트 내에 엑셀파일 있어야 함
            Resource resource = new ClassPathResource("static/studentId.xlsx");
            InputStream file = resource.getInputStream();
            IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);

            XSSFWorkbook workbook = new XSSFWorkbook(file);

            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (cell.getCellType() == CellType.NUMERIC) {
                        Integer integer = (int) cell.getNumericCellValue();
                        Student student = new Student(integer.toString(), cellIterator.next().getStringCellValue());
                        allList.add(student);
                    }
                }
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allList;
    }

    public static List<FeeStudent> uploadExcel(MultipartFile file) throws IOException {
        List<FeeStudent> result = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheet.getRow(i);
            DataFormatter formatter = new DataFormatter();

            String stuId = formatter.formatCellValue(row.getCell(0));
            String name = formatter.formatCellValue(row.getCell(1));

            if(!stuId.isEmpty() && !name.isEmpty()){
                FeeStudent feeStudent = new FeeStudent(stuId, name);
                result.add(feeStudent);
            }

        }
        return result;
    }
}
