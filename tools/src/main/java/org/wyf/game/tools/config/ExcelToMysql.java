package org.wyf.game.tools.config;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.wyf.game.config")
@EnableConfigurationProperties({SpringConfig.class, DatasourceConfig.class})
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

    @Autowired
    private SpringConfig springConfig;

    @Autowired
    private DatasourceConfig datasourceConfig;

    public static void common(String path, Charset charset, RandomAccessFile config) throws Exception {
        File file = new File(path);
        String[] tables = file.list((_, name) -> (name.endsWith(".xlsx") || name.endsWith(".xls")));
        if (tables == null || tables.length == 0) {
            log.info("没有找到Excel文件");
            return;
        }
        for (String table : tables) {
            readExcel(path, new FileInputStream(path + File.separatorChar + table), charset, config);
        }
        log.info("解析所有Excel完毕");
    }

    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    public static void readExcel(String path, FileInputStream file, Charset charset, RandomAccessFile config) throws Exception {
        Workbook book = WorkbookFactory.create(file);
        Iterator<Sheet> it = book.sheetIterator();
        int k = -1;
        while (it.hasNext()) {
            Sheet sheet = it.next();
            k++;
            if (book.isSheetHidden(k)) {
                continue;
            }
            if (!sheet.getSheetName().startsWith("cfg_")) {
                continue;
            }
            Row row0 = sheet.getRow(0);// 字段名
            Row row1 = sheet.getRow(1);// 字段名
            Row row2 = sheet.getRow(2);// 字段类型
            Row row3 = sheet.getRow(3);// 字段是否是服务器用的
            int lastRowIndex = 4;// 最大行数
            for (int i = 4; i <= sheet.getLastRowNum(); i++) {
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
                String t = row3.getCell(j).getStringCellValue();
                if (TYPE_COMMON.equals(t) || TYPE_SERVER.equals(t)) {
                    String s;
                    if (cellinfo.contains("!")) {
                        s = cellinfo.replace("!", "");
                        idCellNum = j;
                    } else {
                        s = cellinfo;
                    }
                    map.put(j, s);
                }
            }
            if (map.isEmpty()) {
                continue;
            }
            if (sheet.getSheetName().equals("cfg_sensitive_words")) {
                continue;
            }
            log.info("开始解析配置表：{}", sheet.getSheetName());
            if (!new File(path + "/sql/").exists()) {
                new File(path + "/sql/").mkdirs();
            }
            RandomAccessFile sqlFile = new RandomAccessFile(path + "/sql/" + sheet.getSheetName() + ".sql", "rw");
            clearInfoForFile(path + "/sql/" + sheet.getSheetName() + ".sql");
            int firstRowIndex = sheet.getFirstRowNum() + 4;
            StringBuilder sql = new StringBuilder();
            sql.append("DROP TABLE IF EXISTS `").append(sheet.getSheetName()).append("`;\r");
            sql.append("CREATE TABLE `").append(sheet.getSheetName()).append("`  (\r");
            int finalIdCellNum = idCellNum;
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
                    case TYPE_DATETIME -> sql.append("DATETIME NOT NULL");
                    case TYPE_DATE -> sql.append("DATE NOT NULL");
                    default -> log.info("未知类型：{}", s);
                }
                sql.append(" COMMENT '");
                sql.append(row0.getCell(cellNum).getStringCellValue());
                sql.append("',\r");
            });
            if (idCellNum >= 0) {
                Cell idCell = row1.getCell(idCellNum);
                sql.append("PRIMARY KEY (`").append(idCell.getStringCellValue()).append("`) USING BTREE\r");
            } else {
                sql.delete(sql.length() - 2, sql.length() - 1);
            }
            sql.append(")");
            if (lastRowIndex != 3 || sheet.getRow(4) != null || sheet.getRow(4).getCell(0) != null) {
                String start;
                StringBuilder startBuilder = new StringBuilder("INSERT INTO `" + sheet.getSheetName() + "` (");
                int lastCellIndex;
                {
                    Row row = sheet.getRow(1);
                    lastCellIndex = row.getLastCellNum();
                    int firstCellIndex = row.getFirstCellNum();
                    for (int j = firstCellIndex; j < lastCellIndex; j++) {
                        String type = map.get(j);
                        if (type == null) {
                            continue;
                        }
                        Cell cell = row.getCell(j);
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
                            break;
                        }
                        String c = cell.toString();
                        if (c.isBlank()) {
                            sql.append("null, ");
                        } else {
                            CellType cellType = cell.getCellType();
                            switch (type) {
                                case TYPE_INT, TYPE_LONG, TYPE_BOOL -> {
                                    if (cellType == CellType.FORMULA) {
                                        sql.append(((XSSFCell) cell).getCTCell().getV());
                                    } else {
                                        sql.append(Math.round(Double.parseDouble(c)));
                                    }
                                }
                                case TYPE_STRING, TYPE_JSON, TYPE_DATETIME, TYPE_DATE -> {
                                    sql.append('\'');
                                    sql.append(cell.getStringCellValue());
                                    sql.append('\'');
                                }
                                case TYPE_FLOAT, TYPE_DOUBLE -> sql.append(cell.getNumericCellValue());
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
     */
    public static void clearInfoForFile(String fileName) {
        File file = new File(fileName);
        try {
            log.info("fileName：{}", fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExcelToMysql.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Charset charset = Charset.forName("UTF-8");
        String configPath = springConfig.getXlsxPath() + "/config.sql";
        clearInfoForFile(configPath);
        RandomAccessFile config = new RandomAccessFile(configPath, "rw");
        ExcelToMysql.common(springConfig.getXlsxPath(), charset, config);
        Connection conn = null;
        try {
            log.info("开始执行SQL脚本...");
            long start = System.currentTimeMillis();
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(datasourceConfig.jdbcUrl(), datasourceConfig.username(), datasourceConfig.password());
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
        sqlScript.append("SET FOREIGN_KEY_CHECKS = 0;");

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
        sqlScript.append("SET FOREIGN_KEY_CHECKS = 1;");

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