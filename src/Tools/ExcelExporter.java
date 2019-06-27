/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.*;

import jxl.*;
import jxl.write.*;

/**
 *
 * @author Developer
 */
public class ExcelExporter {

    public static boolean ExcelExport(JTable table, String fileName) {
        System.setProperty("file.encoding", "UTF-8");
       try {
            File file = new File(fileName + ".xls");
            WritableWorkbook workbook1 = Workbook.createWorkbook(file);
            WritableSheet sheet1 = workbook1.createSheet("Sheet", 0);
            TableModel model = table.getModel();

            for (int i = 0; i < model.getColumnCount(); i++) {
                Label column = new Label(i, 0, model.getColumnName(i));
                sheet1.addCell(column);
            }
            int j = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                for (j = 0; j < model.getColumnCount(); j++) {
                    Label row = new Label(j, i + 1,
                            model.getValueAt(i, j).toString());
                    sheet1.addCell(row);
                }
            }
            workbook1.write();
            workbook1.close();
            return true;
        } catch (IOException | WriteException ex) {
            return false;
        }
    }
}
