package org.game.config.tools;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@ComponentScan(basePackages = "org.game.config",  // 扫描该包及其子包
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = MysqlToExcel.class))
@Tool
public class ExcelToMysql implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ExcelToMysql.class);
    private static final String TYPE_INT = "int";

    private static final String TYPE_STRING = "string";

    private static final String TYPE_FLOAT = "float";

    private static final String TYPE_DOUBLE = "double";

    private static final String TYPE_LONG = "long";

    private static final String TYPE_BOOL = "bool";

    private static final String TYPE_JSON = "json";

    private static final String TYPE_DATETIME = "datetime";

    private static final String TYPE_DATE = "date";

    private static final String TYPE_COMMON = "common";

    private static final String TYPE_SERVER = "server";

    @Value("${config.xlsx-path}")
    private String path;

    //数据库的url
    @Value("${spring.datasource.config.jdbc-url}")
    private String url;
    //数据库的用户名
    @Value("${spring.datasource.config.username}")
    private String username;

    //数据库的密码
    @Value("${spring.datasource.config.password}")
    private String password;

    public static void common(String path, Charset charset, RandomAccessFile config) throws Exception {
        File file = new File(path);
        String[] tables = file.list((_, name) -> (name.endsWith(".xlsx") || name.endsWith(".xls")));
        if (tables == null || tables.length == 0) {
            log.info("没有找到Excel文件");
            return;
        }
        for (String table : tables) {
            readExcel(path, new File(path + File.separatorChar + table), charset, config);
        }
        log.info("解析所有Excel完毕");
    }

    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    public static void readExcel(String path, File file, Charset charset, RandomAccessFile config) throws Exception {
        Workbook book = WorkbookFactory.create(file);
        Iterator<Sheet> it = book.sheetIterator();
        int k = -1;
        while (it.hasNext()) {
            Sheet sheet = it.next();
            k++;
            if (book.isSheetHidden(k)) {
                continue;
            }
            if (!sheet.getSheetName().startsWith("sys")) {
                continue;
            }
            Row row1 = sheet.getRow(0);// 字段名称
            Row row2 = sheet.getRow(1);// 字段类型
            Row row0 = sheet.getRow(2);// 字段注释
            int lastRowIndex = 3;// 最大行数
            for (int i = lastRowIndex; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null || row.getCell(0).toString().isEmpty()) {
                    break;
                }
                lastRowIndex = i;
            }

            Map<Integer, String> map = new HashMap<>();
            int firstCellIndex1 = row2.getFirstCellNum();
            int lastCellIndex1 = row2.getLastCellNum();
            int idCellNum = -1;
            for (int j = firstCellIndex1; j < lastCellIndex1; j++) {
                Cell cell = row2.getCell(j);
                String cellinfo = cell.getStringCellValue();
                String s;
                if (cellinfo.contains("!")) {
                    s = cellinfo.replace("!", "");
                    idCellNum = j;
                } else {
                    s = cellinfo;
                }
                map.put(j, s);
            }
            if (map.isEmpty()) {
                continue;
            }
            log.info("开始解析配置表：{}", sheet.getSheetName());
            RandomAccessFile sqlFile = new RandomAccessFile(path + "/sql/" + sheet.getSheetName() + ".sql", "rw");
            clearInfoForFile(path + "/sql/" + sheet.getSheetName() + ".sql");
            int firstRowIndex = sheet.getFirstRowNum() + 3;
            StringBuilder sql = new StringBuilder();
            sql.append("DROP TABLE IF EXISTS `").append(sheet.getSheetName()).append("`;\r");
            sql.append("CREATE TABLE `").append(sheet.getSheetName()).append("`  (\r");
            int finalIdCellNum = idCellNum;
            AtomicInteger attrNum = new AtomicInteger();
            map.forEach((cellNum, s) -> {
                Cell cell = row1.getCell(cellNum);
                sql.append('`');
                sql.append(cell.getStringCellValue());
                sql.append("` ");
                switch (s) {
                    case TYPE_INT -> sql.append("INT(0) NOT NULL");
                    case TYPE_LONG -> sql.append("BIGINT(0) NOT NULL");
                    case TYPE_STRING -> {
                        if (finalIdCellNum == cellNum) {
                            sql.append("VARCHAR(255) NOT NULL");
                        } else {
                            sql.append("VARCHAR(1000) NULL");
                        }
                    }
                    case TYPE_JSON -> sql.append("JSON NULL");
                    case TYPE_BOOL -> sql.append("BIT(1) NOT NULL");
                    case TYPE_DOUBLE, TYPE_FLOAT -> sql.append("DOUBLE NOT NULL");
                    case TYPE_DATETIME -> sql.append("DATETIME NULL");
                    case TYPE_DATE -> sql.append("DATE NOT NULL");
                    default -> log.info("未知类型：{}", s);
                }
                sql.append(" COMMENT '");
                if (row0 != null && row0.getCell(cellNum) != null) {
                    sql.append(row0.getCell(cellNum).getStringCellValue());
                    attrNum.getAndIncrement();
                }
                sql.append("',\r");
            });
            if (idCellNum >= 0) {
                Cell idCell = row1.getCell(idCellNum);
                sql.append("PRIMARY KEY (`").append(idCell.getStringCellValue()).append("`) USING BTREE\r");
            } else {
                sql.delete(sql.length() - 2, sql.length() - 1);
            }
            sql.append(")");
            if (lastRowIndex != 2 || sheet.getRow(3) != null || sheet.getRow(3).getCell(0) != null) {
                String start;
                StringBuilder startBuilder = new StringBuilder("INSERT INTO `" + sheet.getSheetName() + "` (");
                int lastCellIndex;
                {
                    lastCellIndex = row1.getLastCellNum();
                    int firstCellIndex = row1.getFirstCellNum();
                    for (int j = firstCellIndex; j < lastCellIndex; j++) {
                        String type = map.get(j);
                        if (type == null) {
                            continue;
                        }
                        Cell cell = row1.getCell(j);
                        if (cell == null || cell.toString().isBlank()) {
                            lastCellIndex = j;
                            startBuilder.delete(startBuilder.length() - 2, startBuilder.length());
                            startBuilder.append(") VALUES (");
                            break;
                        }
                        String cellinfo = cell.getStringCellValue();
                        startBuilder.append("`");
                        startBuilder.append(cellinfo);
                        startBuilder.append("`, ");
                    }
                    startBuilder.replace(startBuilder.length() - 3, startBuilder.length(), "");
                    startBuilder.append("`) VALUES ");
                    start = startBuilder.toString();
                }
                boolean b = true;
                for (int i = firstRowIndex; i <= lastRowIndex; i++) {
                    Row row = sheet.getRow(i);
                    // sheet.getColumns()返回该页的总列数
                    if (row == null) {
                        break;
                    }
                    int firstCellIndex = row.getFirstCellNum();
                    if (b) {
                        sql.append(";\r");
                        sql.append(start);
                    } else {
                        sql.append(",");
                    }
                    sql.append("(");
                    b = false;
                    for (int j = firstCellIndex; j < lastCellIndex; j++) {
                        String type = map.get(j);
                        if (type == null) {
                            continue;
                        }
                        Cell cell = row.getCell(j);
                        if (cell == null) {
                            if (type.equals(TYPE_STRING) || type.equals(TYPE_JSON) || TYPE_DATETIME.equals(type) || TYPE_DATE.equals(type)) {
                                sql.append("NULL, ");
                            } else {
                                sql.append("0, ");
                            }
                        } else {
                            CellType cellType = cell.getCellType();
                            if (TYPE_INT.equals(type) || TYPE_LONG.equals(type) || TYPE_BOOL.equals(type)) {
                                if (cellType == CellType.FORMULA) {
                                    sql.append(((XSSFCell) cell).getCTCell().getV());
                                } else if (Strings.isBlank(cell.toString())) {
                                    sql.append(0);
                                } else {
                                    sql.append(Math.round(Double.parseDouble(cell.toString())));
                                }
                            } else if (TYPE_STRING.equals(type)) {
                                sql.append('\'');
                                sql.append(cell.getStringCellValue());
                                sql.append('\'');
                            } else if (TYPE_FLOAT.equals(type) || TYPE_DOUBLE.equals(type)) {
                                sql.append(cell.getNumericCellValue());
                            } else if (TYPE_JSON.equals(type) || TYPE_DATETIME.equals(type) || TYPE_DATE.equals(type)) {
                                sql.append('\'');
                                sql.append(cell.getStringCellValue());
                                sql.append('\'');
                            }
                            sql.append(", ");
                        }
                    }
                    sql.replace(sql.length() - 2, sql.length(), "");
                    sql.append(")");
                }
            }
            byte[] bs = sql.toString().getBytes(charset);
            sqlFile.write(bs);
            config.write(bs);
            config.write(";\r".getBytes(charset));
            sqlFile.close();
            log.info("解析配置表完毕：{}", sheet.getSheetName());
        }
        book.close();
    }

    /**
     * 清空文件内容
     *
     * @param fileName
     */
    public static void clearInfoForFile(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    throw new RuntimeException("创建文件失败：" + fileName);
                }
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ExcelToMysql.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Charset charset = Charset.forName("UTF-8");
        String configPath = path + "/config.sql";
        clearInfoForFile(configPath);
        RandomAccessFile config = new RandomAccessFile(configPath, "rw");
        ExcelToMysql.common(path, charset, config);
        Connection conn = null;
        try {
            log.info("开始执行SQL脚本...");
            long start = System.currentTimeMillis();
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            executeSQLFile(conn, configPath);
            log.info("执行SQL脚本完毕 耗时：{}ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void executeSQLFile(Connection conn, String sqlFilePath) throws IOException {
        StringBuilder sqlScript = new StringBuilder();

        // 读取 SQL 文件内容
        try (BufferedReader br = new BufferedReader(new FileReader(sqlFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 忽略注释和空行
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sqlScript.append(line).append(" ");
            }
        }

        // 将 SQL 文件按语句分割
        String[] statements = sqlScript.toString().split(";");

        // 执行 SQL 语句
        for (String statement : statements) {
            String s = statement.trim();
            if (!s.isEmpty()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(s);
                    log.debug("执行成功: {}", s);
                } catch (SQLException e) {
                    log.error("执行失败: {}", s, e);
                }
            }
        }
    }
}