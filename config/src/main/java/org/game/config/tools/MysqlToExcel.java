package org.game.config.tools;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * mysql数据导出到excel工具类
 *
 * @author WangYefeng
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class})
@ComponentScan(basePackages = {"org.game.config.tools"}, excludeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, value = XlsxToSql.class)})
public class MysqlToExcel implements InitializingBean {

    private static Logger log = Logger.getLogger(MysqlToExcel.class.getName());

    //数据库的url
    @Value("${spring.datasource.url}")
    private String url;
    //数据库的用户名
    @Value("${spring.datasource.username}")
    private String username;

    //数据库的密码
    @Value("${spring.datasource.password}")
    private String password;

    @Value("${config.xlsx-path}")
    private String path;

    public void getConnectionCentenForm() {
        //创建文件夹
        File fileMdr = new File(path);
        if (!fileMdr.exists()) {
            fileMdr.mkdirs();
        }
        //验证数据的正确性
        List<String> tables = new ArrayList<>();
        // 判断是否还有输入
        Scanner scan = new Scanner(System.in);
        log.info("请输入要加载的表名，多个请要逗号隔开，全部加载输入0：");
        String scanStr = null;
        if (scan.hasNext()) {
            scanStr = scan.next();
            if (!scanStr.equals("0")) {
                String[] strs = scanStr.split(",");
                tables = Arrays.asList(strs);
            }
        }

        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            Statement st = conn.createStatement();
            DatabaseMetaData dmd = conn.getMetaData();
            String name = conn.getCatalog();
            ResultSet rs = dmd.getTables(name, name, null, new String[]{"TABLE"});
            //获取所有表名　－　就是一个sheet

            List<String> table = new ArrayList<>();
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                table.add(tableName);
            }

            log.info("获取所有表名:" + table);
            List<String> finalTables = tables;
            table.removeIf(tableName -> !finalTables.isEmpty() && !finalTables.contains(tableName));
            Workbook book = new XSSFWorkbook();
            CellStyle textStyle = book.createCellStyle();
            // 获取一个数据格式对象
            DataFormat dataFormat = book.createDataFormat();
            // 设置单元格的格式为文本
            textStyle.setDataFormat(dataFormat.getFormat("@"));
            textStyle.setAlignment(HorizontalAlignment.CENTER);


            for (String tableName : table) {
                Sheet sheet = book.createSheet(tableName);
                //声明sql
                String sql = "SELECT COLUMN_NAME,column_comment ,data_type FROM INFORMATION_SCHEMA.Columns WHERE table_name= " + "'" + tableName + "'" + "  AND table_schema= " + "'" + name + "'";
                rs = st.executeQuery(sql);
                int index = 0;
                Row row0 = sheet.createRow(0);
                Row row1 = sheet.createRow(1);
                Row row2 = sheet.createRow(2);
                Row row3 = sheet.createRow(3);

                List<String> columnNames = new ArrayList<>();
                List<String> dateTypes = new ArrayList<>();
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    if (columnName.equals("id")) {
                        sheet.setColumnWidth(0, 1024 * 4);
                        Cell cell = row0.createCell(0);
                        cell.setCellValue(rs.getString("column_comment"));
                        cell.setCellStyle(textStyle);
                        Cell cell1 = row1.createCell(0);
                        columnNames.add(0, columnName);
                        cell1.setCellValue(columnName);
                        cell1.setCellStyle(textStyle);
                        Cell cell2 = row2.createCell(0);
                        String type = rs.getString("DATA_TYPE");
                        dateTypes.add(0, type);
                        if (type.equals("varchar")) {
                            type = "string";
                        } else if (type.equals("bit")) {
                            type = "bool";
                        } else if (type.equals("bigint")) {
                            type = "long";
                        }
                        cell2.setCellValue(type);
                        cell2.setCellStyle(textStyle);
                        Cell cell3 = row3.createCell(0);
                        cell3.setCellValue("server");
                        cell3.setCellStyle(textStyle);
                    } else {
                        index++;
                        sheet.setColumnWidth(index, 1024 * 6);
                        Cell cell = row0.createCell(index);
                        cell.setCellValue(rs.getString("column_comment"));
                        cell.setCellStyle(textStyle);
                        Cell cell1 = row1.createCell(index);
                        cell1.setCellStyle(textStyle);
                        columnNames.add(columnName);
                        cell1.setCellValue(columnName);
                        Cell cell2 = row2.createCell(index);
                        String type = rs.getString("DATA_TYPE");
                        dateTypes.add(type);
                        if (type.equals("varchar")) {
                            type = "string";
                        } else if (type.equals("bit")) {
                            type = "bool";
                        } else if (type.equals("bigint")) {
                            type = "long";
                        }
                        cell2.setCellValue(type);
                        cell2.setCellStyle(textStyle);
                        Cell cell3 = row3.createCell(index);
                        cell3.setCellValue("server");
                        cell3.setCellStyle(textStyle);
                    }
                }
                sql = "select ";
                for (int i = 0; i < columnNames.size(); i++) {
                    if (i == columnNames.size() - 1) {
                        sql = sql + "`" + columnNames.get(i) + "`" + " from " + name + "." + tableName;
                    } else {
                        sql = sql + "`" + columnNames.get(i) + "`" + " , ";
                    }

                }

                log.info("sql:" + sql);
                rs = st.executeQuery(sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                int cols = rsmd.getColumnCount();

                index = 3;
                //遍历数据
                while (rs.next()) {
                    index++;
                    Row row = sheet.createRow(index);

                    //设置列的宽度
                    sheet.setColumnWidth(index, 1024 * 4);

                    //声明列
                    for (int m = 0; m < cols; m++) {
                        String val = rs.getString(m + 1);
                        //声明列
                        Cell cel = row.createCell(m);
                        //放数据
                        if (dateTypes.get(m).equals("json")) {
                            if ("null".equals(val) || val == null || "".equals(val)) {
                                val = "[]";
                            }
                        }
                        cel.setCellValue(val);
                        cel.setCellStyle(textStyle);
                    }
                }
                book.write(new FileOutputStream(path + "/" + tableName + ".xlsx"));
            }
            conn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            scan.close();
        }
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MysqlToExcel.class);
        application.run(args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getConnectionCentenForm();
    }
}
