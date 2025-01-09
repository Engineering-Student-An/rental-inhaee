package an.rentalinhaee;

import an.rentalinhaee.domain.FeeStudent;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadExcel {

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
