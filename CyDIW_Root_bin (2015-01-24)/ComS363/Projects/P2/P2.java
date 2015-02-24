import com.mysql.jdbc.*;

import javax.swing.plaf.nimbus.State;
import java.lang.System;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

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

            // A
            // Create a statement object
            Statement project2a = connect.createStatement();

            // Execute query, receive results
            ResultSet resultSet2a = project2a.executeQuery("select p.Name, i.Salary " +
                    "from Instructor i inner join Person p on i.InstructorID=p.ID;");

            // Process the result set
            System.out.println(" ");
            System.out.println("NAME           SALARY");
            System.out.println("----           ------");

            // Print report
            double totalPayroll = 0;

            String iName;
            double iSalary;

            while( resultSet2a.next() )
            {
                iName = resultSet2a.getString(1);
                iSalary = resultSet2a.getInt( "Salary" );
                System.out.println(iName + "    " + iSalary);
                totalPayroll += iSalary;
            }

            System.out.println("\nTOTAL SALARY");
            System.out.println("------------");
            System.out.println(totalPayroll + "\n");

            // B
            Statement project2b = connect.createStatement();
            String sql = "CREATE TABLE MeritList " +
                    " ( " +
                    " StudentID CHAR(9) NOT NULL, " +
                    " Classification CHAR(10), " +
                    " MentorID CHAR(9), " +
                    " GPA DOUBLE(3,2) " +
                    " ); ";
            project2b.executeUpdate( sql );

            sql = "SELECT s.StudentID, s.Classification, s.GPA, s.MentorID " +
                    "from Student s order by GPA desc;";
            ResultSet resultSet2b = project2b.executeQuery( sql );
            int count = 0;
            PreparedStatement prep = connect.prepareStatement(
                    "INSERT INTO MeritList (StudentID, Classification, MentorID, GPA) " +
                            "VALUES ( ?, ?, ?, ?);");

            String studentID;
            String classification;
            String mentorID;

            ArrayList<Double> GPAList = new ArrayList<Double>();
            while( count < 20 && resultSet2b.next() )
            {
                double GPA = resultSet2b.getDouble( "GPA" );
                if( !(GPAList.contains( GPA )) )
                {
                    studentID = resultSet2b.getString(1);
                    classification = resultSet2b.getString(2);
                    mentorID = resultSet2b.getString(4);
                    GPAList.add(GPA);
                    prep.setString(1, studentID);
                    prep.setString(2, classification);
                    prep.setString(3, mentorID);
                    prep.setDouble(4, GPA);
                    prep.execute();
                    count++;
                }
            }
            while( resultSet2b.next() )
            {
                if( GPAList.contains(resultSet2b.getDouble("GPA")) )
                {
                    studentID = resultSet2b.getString(1);
                    classification = resultSet2b.getString(2);
                    mentorID = resultSet2b.getString(4);
                    prep.setString(1, studentID);
                    prep.setString(2, classification);
                    prep.setString(3, mentorID);
                    prep.setDouble(4, resultSet2b.getDouble("GPA"));
                    prep.execute();
                }
                else
                    break;
            }

            // D
            Statement project2d = connect.createStatement();
            sql = "select m.MentorID, m.Classification, i.Salary from MeritList m " +
                    "inner join Instructor i on m.MentorID=i.InstructorID";
            ResultSet resultSet2d = project2d.executeQuery( sql );
            ArrayList<String> profs = new ArrayList<String>();
            prep = connect.prepareStatement("update Instructor set Salary=? where InstructorID=?");
            String studentClass;
            double salaryModifier = 0.0;
            while( resultSet2d.next() )
            {
                String MentorID = resultSet2d.getString("MentorID");
                if( profs.contains(MentorID) ) continue;
                else
                {
                    profs.add(MentorID);
                    studentClass = resultSet2d.getString("Classification");
                    if( studentClass.equals("Freshman") )
                    {
                        salaryModifier = .04;
                    }
                    else if( studentClass.equals("Sophomore") )
                    {
                        salaryModifier = .06;
                    }
                    else if( studentClass.equals("Junior") )
                    {
                        salaryModifier = .08;
                    }
                    else if( studentClass.equals("Senior") )
                    {
                        salaryModifier = .10;
                    }
                    int salary = resultSet2d.getInt("Salary");
                    double newSalary = salary + (salary * salaryModifier);
                    prep.setDouble(1, newSalary);
                    prep.setString(2, MentorID);
                    prep.execute();
                }
            }

            // E
            // Create a statement object
            Statement project2e = connect.createStatement();

            // Execute query, receive results
            ResultSet resultSet2e = project2e.executeQuery("select p.Name, i.Salary " +
                    "from Instructor i inner join Person p on i.InstructorID=p.ID;");

            // Process the result set
            System.out.println(" ");
            System.out.println("NAME           SALARY");
            System.out.println("----           ------");

            // Print report
            totalPayroll = 0;

            while( resultSet2e.next() )
            {
                iName = resultSet2e.getString(1);
                iSalary = resultSet2e.getInt( "Salary" );
                System.out.println(iName + "    " + iSalary);
                totalPayroll += iSalary;
            }

            System.out.println("\nTOTAL SALARY");
            System.out.println("------------");
            System.out.println(totalPayroll + "\n");

            project2a.close();
            project2b.close();
            project2d.close();
            project2e.close();
            Statement dropMerit = connect.createStatement();
            sql = "DROP TABLE MeritList;";
            dropMerit.executeUpdate(sql);
            connect.close();
        }
        catch( SQLException e )
        {
            System.out.println(e);
        }

    }
}
