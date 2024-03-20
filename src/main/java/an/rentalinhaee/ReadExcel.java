package an.rentalinhaee;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadExcel {
    public static List<String> readExcel(String stuId) {
        try {
            // 프로젝트 내에 엑셀파일 있어야 함 => 상대경로
            Resource resource = new ClassPathResource("static/studentId.xlsx");
            InputStream file = resource.getInputStream();
//            FileInputStream file = new FileInputStream("static/studentId.xlsx");
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
}
