import java.lang.System;
import java.sql.*;
import java.sql.ResultSet;
import java.sql.Statement;

public class P2
{

    public static void main( String args[] )
    {
        // Load JDBC driver
        try
        {
            Class.forName( "com.mysql.jdbc.Driver" );
        }
        catch( Exception e )
        {
            System.err.println( "Unable to load the JDBC driver." );
            e.printStackTrace();
        }

        try
        {
            // Connect to database
            Connection connect;
            String databaseURL = "jdbc:mysql://csdb.cs.iastate.edu:3306/mattroseDB";
            String user = "mattrose";
            String password = "mattrose-50";
            connect = DriverManager.getConnection( databaseURL, user, password );
            System.out.println( "*** Connected to the database ***" );

            // Drop tables

            // Create and populate tables
            Statement createTables = connect.createStatement();
            String sql = "CREATE TABLE Person" +
                    "(ID CHAR(9) NOT NULL," +
                    "Name CHAR(20)," +
                    "Address CHAR(30)," +
                    "DOB DATE," +
                    "primary key(ID))";
            createTables.executeUpdate( sql );
            sql = "CREATE TABLE Instructor" +
                    "(" +
                    "InstructorID CHAR(9) NOT NULL references Person," +
                    "Rank CHAR(12)," +
                    "Salary INTEGER," +
                    "primary key(InstructorID)" +
                    ")";
            createTables.executeUpdate( sql );
            sql = "CREATE TABLE Student" +
                    "(" +
                    "StudentID CHAR(9) NOT NULL," +
                    "Classification CHAR(10)," +
                    "GPA DOUBLE(3,2)," +
                    "MentorID CHAR(9) references Instructor," +
                    "CreditHours INTEGER," +
                    "primary key(StudentID)" +
                    ")";
            createTables.executeUpdate( sql );
            sql = "CREATE TABLE Course" +
                    "(" +
                    "CourseCode CHAR(6) NOT NULL," +
                    "CourseName CHAR(50)," +
                    "PreReq CHAR(6)," +
                    "primary key(CourseCode)" +
                    ")";
            createTables.executeUpdate( sql );
            sql = "CREATE TABLE Offering" +
                    "(" +
                    "CourseCode CHAR(6) NOT NULL," +
                    "SectionNo INTEGER NOT NULL," +
                    "InstructorID CHAR(9) NOT NULL references Instructor," +
                    "primary key(CourseCode, SectionNo)" +
                    ")";
            createTables.executeUpdate( sql );


            // Create a statement object
            Statement project2a = connect.createStatement();

            // Execute query, receive results
            ResultSet resultSet2a = project2a.executeQuery("select i.Name, i.Salary" + "  " +
                "from Instructor i");

            // Process the result set
            System.out.println(" ");
            System.out.println("NAME        SALARY");
            System.out.println("----        ------");

            // Print report

            int count = 0;
            double totalPayroll = 0;

            String iName;
            double iSalary;

            while( resultSet2a.next() )
            {
                iName = resultSet2a.getString( 1 );
                iSalary = resultSet2a.getInt( "Salary" );
                System.out.println(iName + "    " + iSalary);
                count++;
                totalPayroll += iSalary;
            }
        }
    }

}