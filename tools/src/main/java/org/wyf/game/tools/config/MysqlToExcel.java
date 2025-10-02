package org.wyf.game.tools.config;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.*;

/**
 * MySQL 数据导出到 Excel 工具类
 *
 * @author
 *     WangYefeng
 */
@SpringBootApplication
@ComponentScan(basePackages = "org.wyf.game.config")
@EnableConfigurationProperties({GlobalConfig.class, DatasourceConfig.class})
public class MysqlToExcel {

    private static final Logger log = LoggerFactory.getLogger(MysqlToExcel.class);

    @Autowired
    private GlobalConfig globalConfig;

    @Autowired
    private DatasourceConfig datasourceConfig;

    private static final String DEFAULT_COLUMN_TYPE = "common";

    @EventListener(ApplicationStartedEvent.class)
    public void start() {
        // 创建文件夹
        File fileMdr = new File(globalConfig.getXlsxPath());
        if (!fileMdr.exists()) {
            fileMdr.mkdirs();
        }

        // 获取输入表名
        List<String> tables = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        log.info("请输入要加载的表名，多个请用逗号隔开，全部加载输入0：");

        if (scan.hasNext()) {
            String scanStr = scan.next();
            if (!"0".equals(scanStr)) {
                tables = Arrays.asList(scanStr.split(","));
            }
        }

        try (Connection conn = DriverManager.getConnection(
                datasourceConfig.jdbcUrl(),
                datasourceConfig.username(),
                datasourceConfig.password());
             Statement st = conn.createStatement()) {

            DatabaseMetaData dmd = conn.getMetaData();
            String dbName = conn.getCatalog();

            // 获取所有表名
            ResultSet rs = dmd.getTables(dbName, dbName, null, new String[]{"TABLE"});
            List<String> tableList = new ArrayList<>();
            while (rs.next()) {
                tableList.add(rs.getString("TABLE_NAME"));
            }

            // 如果有指定表名，只保留指定的
            List<String> finalTables = tables;
            tableList.removeIf(t -> !finalTables.isEmpty() && !finalTables.contains(t));

            for (String tableName : tableList) {
                exportTableToExcel(st, dmd, conn, dbName, tableName);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            scan.close();
        }
    }

    /**
     * 导出单个表到 Excel
     */
    private void exportTableToExcel(Statement st, DatabaseMetaData dmd, Connection conn,
                                    String dbName, String tableName) throws Exception {
        try (Workbook book = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(globalConfig.getXlsxPath() + "/" + tableName + ".xlsx")) {

            log.info("正在生成表: {}.xlsx", tableName);

            // 样式：普通文本
            CellStyle textStyle = book.createCellStyle();
            DataFormat dataFormat = book.createDataFormat();
            textStyle.setDataFormat(dataFormat.getFormat("@"));
            textStyle.setAlignment(HorizontalAlignment.CENTER);

            // Sheet
            Sheet sheet = book.createSheet(tableName);

            // SQL 查询字段
            String sql = String.format(
                    "SELECT COLUMN_NAME, column_comment, data_type " +
                            "FROM INFORMATION_SCHEMA.Columns " +
                            "WHERE table_name='%s' AND table_schema='%s'",
                    tableName, dbName
            );
            ResultSet rs = st.executeQuery(sql);

            // 获取主键
            ResultSet pkResultSet = dmd.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName);
            String pkColumnName;
            if (pkResultSet.next()) {
                pkColumnName = pkResultSet.getString("COLUMN_NAME");
            } else {
                throw new RuntimeException("表 " + tableName + " 没有主键！");
            }

            // 打印可能存在的多主键
            while (pkResultSet.next()) {
                log.info("Primary Key Column: {}", pkResultSet.getString("COLUMN_NAME"));
            }

            // 表头行
            Row row0 = sheet.createRow(0);
            Row row1 = sheet.createRow(1);
            Row row2 = sheet.createRow(2);
            Row row3 = sheet.createRow(3);

            List<String> columnNames = new ArrayList<>();
            int index = 0;

            // 遍历字段
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnComment = rs.getString("column_comment");
                String type = rs.getString("DATA_TYPE");

                if (columnName.equals(pkColumnName)) {
                    // 主键列放第一列
                    createCell(sheet, createStyles(book), row0, row1, row2, row3,
                            0, columnName, columnComment, type, true);
                    columnNames.addFirst(columnName);
                } else {
                    index++;
                    createCell(sheet, createStyles(book), row0, row1, row2, row3,
                            index, columnName, columnComment, type, false);
                    columnNames.add(columnName);
                }
            }
            // 设置自动筛选
            sheet.setAutoFilter(new CellRangeAddress(3,3,0,columnNames.size() - 1));

            // 查询表数据
            StringBuilder dataSql = new StringBuilder("SELECT ");
            for (int i = 0; i < columnNames.size(); i++) {
                dataSql.append("`").append(columnNames.get(i)).append("`");
                if (i < columnNames.size() - 1) {
                    dataSql.append(", ");
                }
            }
            dataSql.append(" FROM ").append(dbName).append(".").append(tableName);

            rs = st.executeQuery(dataSql.toString());
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();

            index = 3;
            while (rs.next()) {
                index++;
                Row row = sheet.createRow(index);

                for (int m = 0; m < cols; m++) {
                    String val = rs.getString(m + 1);
                    Cell cel = row.createCell(m);
                    cel.setCellValue(val);
                    cel.setCellStyle(textStyle);
                }
            }

            book.write(fos);
            log.info("生成表: {}.xlsx 成功！路径：{}", tableName, globalConfig.getXlsxPath() + "/" + tableName + ".xlsx");
        }
    }

    /**
     * 类型映射
     */
    private String mapType(String dbType, boolean isPk) {
        String type = switch (dbType) {
            case "varchar" -> "string";
            case "bit" -> "bool";
            case "bigint" -> "long";
            default -> dbType;
        };
        return isPk ? type + "!" : type;
    }

    /**
     * 创建各种样式
     */
    private Map<String, CellStyle> createStyles(Workbook book) {
        Map<String, CellStyle> styles = new HashMap<>();

        // 基础：居中 + 边框
        CellStyle base = book.createCellStyle();
        base.setAlignment(HorizontalAlignment.CENTER);
        base.setVerticalAlignment(VerticalAlignment.CENTER);
        base.setWrapText(true);
        base.setBorderBottom(BorderStyle.THIN);
        base.setBorderTop(BorderStyle.THIN);
        base.setBorderLeft(BorderStyle.THIN);
        base.setBorderRight(BorderStyle.THIN);

        // 表头（注释）- 蓝色背景
        CellStyle header = book.createCellStyle();
        header.cloneStyleFrom(base);
        header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = book.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        header.setFont(headerFont);
        styles.put("header", header);

        // 数据行
        CellStyle data = book.createCellStyle();
        data.cloneStyleFrom(base);
        styles.put("data", data);

        return styles;
    }


    /**
     * 创建单元格（表头/注释/类型/默认值）
     */
    private void createCell(Sheet sheet, Map<String, CellStyle> styles,
                            Row row0, Row row1, Row row2, Row row3,
                            int colIndex, String columnName, String columnComment,
                            String type, boolean isPk) {

        // 注释
        Cell cell0 = row0.createCell(colIndex);
        cell0.setCellValue(columnComment);
        cell0.setCellStyle(styles.get("header"));

        // 使用者类型
        Cell cell1 = row1.createCell(colIndex);
        cell1.setCellValue(DEFAULT_COLUMN_TYPE);
        cell1.setCellStyle(styles.get("header"));

        // 字段类型
        Cell cell2 = row2.createCell(colIndex);
        cell2.setCellValue(mapType(type, isPk));
        cell2.setCellStyle(styles.get("header"));

        // 字段名
        Cell cell3 = row3.createCell(colIndex);
        cell3.setCellValue(columnName);
        cell3.setCellStyle(styles.get("header"));

        sheet.setColumnWidth(colIndex,  isPk ? 10 * 256 : 20 * 256);
        sheet.createFreezePane(1, 4);
    }


    static void main(String[] args) {
        SpringApplication app = new SpringApplication(MysqlToExcel.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}
