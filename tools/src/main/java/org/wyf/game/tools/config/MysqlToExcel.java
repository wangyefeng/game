package org.wyf.game.tools.config;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * mysql数据导出到excel工具类
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(useDefaultFilters = false)
public class MysqlToExcel implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(MysqlToExcel.class);

    //数据库的url
    @Value("${spring.datasource.config.jdbc-url}")
    private String url;
    //数据库的用户名
    @Value("${spring.datasource.config.username}")
    private String username;

    //数据库的密码
    @Value("${spring.datasource.config.password}")
    private String password;

    @Value("${config.xlsx-path}")
    private String path;

    public void getConnectionCentenForm() {
        //创建文件夹
        File fileMdr = new File(path);
        if (!fileMdr.exists()) {
            if (!fileMdr.mkdirs()) {
                throw new RuntimeException("创建文件失败：" + path);
            }
        }
        //验证数据的正确性
        List<String> tables = new ArrayList<>();
        // 判断是否还有输入
        Scanner scan = new Scanner(System.in);
        log.info("请输入要加载的表名，多个请要逗号隔开，全部加载输入0：");
        String scanStr;
        if (scan.hasNext()) {
            scanStr = scan.next();
            if (!scanStr.equals("0")) {
                String[] strs = scanStr.split(",");
                tables = Arrays.asList(strs);
            }
        }

        Connection conn;
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

            List<String> finalTables = tables;
            table.removeIf(tableName -> !finalTables.isEmpty() && !finalTables.contains(tableName));

            for (String tableName : table) {
                try (Workbook book = new XSSFWorkbook();
                     FileOutputStream fos = new FileOutputStream(path + "/" + tableName + ".xlsx")) {
                    CellStyle textStyle = book.createCellStyle();
                    // 获取一个数据格式对象
                    DataFormat dataFormat = book.createDataFormat();
                    // 设置单元格的格式为文本
                    textStyle.setDataFormat(dataFormat.getFormat("@"));
                    textStyle.setAlignment(HorizontalAlignment.CENTER);
                    log.info("正在生成表:{}.xlsx", tableName);
                    Sheet sheet = book.createSheet(tableName);
                    //声明sql
                    StringBuilder sql = new StringBuilder("SELECT COLUMN_NAME,column_comment ,data_type FROM INFORMATION_SCHEMA.Columns WHERE table_name= " + "'" + tableName + "'" + "  AND table_schema= " + "'" + name + "'");
                    rs = st.executeQuery(sql.toString());

                    ResultSet pkResultSet = dmd.getPrimaryKeys(null, null, tableName);
                    String pkColumnName;
                    if (pkResultSet.next()) {
                        pkColumnName = pkResultSet.getString("COLUMN_NAME");
                    } else {
                        throw new RuntimeException("表" + tableName + "没有主键！");
                    }

                    while (pkResultSet.next()) {
                        String columnName = pkResultSet.getString("COLUMN_NAME");
                        System.out.println("Primary Key Column: " + columnName);
                    }

                    int index = 0;
                    Row row0 = sheet.createRow(0);
                    Row row1 = sheet.createRow(1);
                    Row row2 = sheet.createRow(2);
                    Row row3 = sheet.createRow(3);

                    List<String> columnNames = new ArrayList<>();
                    while (rs.next()) {
                        String columnName = rs.getString("COLUMN_NAME");
                        if (columnName.equals(pkColumnName)) {// 主键列 放第一行
                            sheet.setColumnWidth(0, 1024 * 4);
                            Cell cell = row0.createCell(0);
                            cell.setCellValue(rs.getString("column_comment"));
                            cell.setCellStyle(textStyle);
                            Cell cell1 = row1.createCell(0);
                            columnNames.addFirst(columnName);
                            cell1.setCellValue(columnName);
                            cell1.setCellStyle(textStyle);
                            Cell cell2 = row2.createCell(0);
                            String type = rs.getString("DATA_TYPE");
                            type = switch (type) {
                                case "varchar" -> "string";
                                case "bit" -> "bool";
                                case "bigint" -> "long";
                                default -> type;
                            };
                            type += "!";// 加上!表示主键
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
                            type = switch (type) {
                                case "varchar" -> "string";
                                case "bit" -> "bool";
                                case "bigint" -> "long";
                                default -> type;
                            };
                            cell2.setCellValue(type);
                            cell2.setCellStyle(textStyle);
                            Cell cell3 = row3.createCell(index);
                            cell3.setCellValue("server");
                            cell3.setCellStyle(textStyle);
                        }
                    }
                    sql = new StringBuilder("select ");
                    for (int i = 0; i < columnNames.size(); i++) {
                        if (i == columnNames.size() - 1) {
                            sql.append("`").append(columnNames.get(i)).append("`").append(" from ").append(name).append(".").append(tableName);
                        } else {
                            sql.append("`").append(columnNames.get(i)).append("`").append(" , ");
                        }

                    }
                    rs = st.executeQuery(sql.toString());
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
                            //设置列的值
                            cel.setCellValue(val);
                            cel.setCellStyle(textStyle);
                        }
                    }
                    book.write(fos);
                }
                log.info("生成表:{}.xlsx成功！ 路径：{}", tableName, path + "\\" + tableName + ".xlsx");
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
    public void afterPropertiesSet() {
        getConnectionCentenForm();
    }
}
