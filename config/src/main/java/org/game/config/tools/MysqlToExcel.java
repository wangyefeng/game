package org.game.config.tools;

import org.apache.poi.ss.usermodel.Cell;
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
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

    @Value("${spring.datasource.dbname}")
    private String name;

    @Value("${config.xlsx-path}")
    private String path;

    public void getConnectionCentenForm() {
        //创建文件夹
        File fileMdr = new File(path);
        if (!fileMdr.exists()) {
            System.out.println("开始创建文件夹" + path + "  是否成功：" + fileMdr.mkdirs());
        }
        //验证数据的正确性
        List<String> tables = new ArrayList<>();
        // 判断是否还有输入
        Scanner scan = new Scanner(System.in);
        log.info("请输入要加载的表名，多个请要逗号隔开，全部加载输入0：");
        String scanStr = null;
        if (scan.hasNext()) {
            scanStr = scan.next();
            System.out.println("输入的数据为：" + scanStr);
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
            ResultSet rs = dmd.getTables(name, name, null, new String[]{"TABLE"});
            //获取所有表名　－　就是一个sheet

            List<String> table = new ArrayList<String>();
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                table.add(tableName);
            }

            log.info("获取所有表名:" + table);

            if (scanStr.equals("0")) {
                tables = table;
            } else {
                List<String> tab = new ArrayList<String>();
                for (String tableName : tables) {
                    if (table.contains(tableName)) {
                        tab.add(tableName);
                    } else {
                        System.out.println("输入的表明：" + tableName + "不存在！！！");
                    }
                }
                if (CollectionUtils.isEmpty(tab)) {
                    System.out.println("输入的表明全部不存在，直接加载全部配置");
                    tables = table;
                } else {
                    tables = tab;
                }
            }

            for (String tableName : tables) {
                Workbook book = new XSSFWorkbook();
                Sheet sheet = book.createSheet(tableName);
                //声明sql
                String sql = "SELECT COLUMN_NAME,column_comment ,data_type FROM INFORMATION_SCHEMA.Columns WHERE table_name= " + "'" + tableName + "'" + "  AND table_schema= " + "'" + name + "'";
                rs = st.executeQuery(sql);
                int index = -1;
                Row row0 = sheet.createRow(0);
                Row row1 = sheet.createRow(1);
                Row row2 = sheet.createRow(2);
                Row row3 = sheet.createRow(3);

                List<String> columnNames = new ArrayList<>();
                List<String> dateTypes = new ArrayList<>();
                while (rs.next()) {
                    index++;
                    sheet.setColumnWidth(index, 1024 * 4);
                    Cell cell = row0.createCell(index);
                    cell.setCellValue(rs.getString("column_comment"));
                    Cell cell1 = row1.createCell(index);
                    String name = rs.getString("COLUMN_NAME");
                    columnNames.add(name);
                    cell1.setCellValue(name);
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
                    Cell cell3 = row3.createCell(index);
                    cell3.setCellValue("server");
                }
                sql = "select ";
                for (int i = 0; i < columnNames.size(); i++) {
                    if (i == columnNames.size() - 1) {
                        sql = sql + "`" + columnNames.get(i) + "`" + " from " + name + "." + tableName;
                    } else {
                        sql = sql + "`" + columnNames.get(i) + "`" + " , ";
                    }

                }

                System.out.println("sql:" + sql);
                //                sql1 = " from " + dbName + "." + tableName;
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
                    }
                }
                book.write(new FileOutputStream(path + "/" + tableName + ".xlsx"));
            }
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
