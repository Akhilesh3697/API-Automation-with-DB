package com.api;

import com.details.APIConnectionDetails;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.testng.Assert.assertEquals;

public class BusinessUnitAPI {
    public static void main(String[] args) throws Exception {
        Response response = RestAssured.given().auth().basic(APIConnectionDetails.serviceNowBasicAuthUsername, APIConnectionDetails.serviceNowBasicAuthPassword).
                when().get(APIConnectionDetails.serviceNowBaseUrl + "business_unit");
        System.out.println("Status Code:"+ response.getStatusCode());
        assertEquals(200, response.getStatusCode());
        org.json.JSONObject obj = new org.json.JSONObject(response.getBody().asString());
        org.json.JSONArray array = obj.getJSONArray("result");

        int dbcount=0;

        //get connection
        Connection connection = APIConnectionDetails.getConnection();

        //create statement
        Statement statement = connection.createStatement();

        for (int i=0; i<array.length(); i++){
            org.json.JSONObject buintObj = array.getJSONObject(i);
            String apisysid = buintObj.get("sys_id")+"";
            String apisysmodcount = buintObj.get("sys_mod_count")+"";
            String apisysupdatedon=buintObj.get("sys_updated_on")+"";
            apisysupdatedon=apisysupdatedon.subSequence(0,19)+".000";
            String apisysupdatedby=buintObj.get("sys_updated_by")+"";
            String apisyscreatedon=buintObj.get("sys_created_on")+"";
            apisyscreatedon=apisyscreatedon.subSequence(0,19)+".000";
            String name=buintObj.get("name")+"";
            String apisyscreatedby=buintObj.get("sys_created_by")+"";

            String dbsysid = "";
            String dbsysmodcount="";
            String dbsysupdatedon="";
            String dbsysupdatedby="";
            String dbsyscreatedon="";
            String dbname="";
            String dbsyscreatedby="";

            //connect DB
            ResultSet resultSet =statement.executeQuery("query where sysid='"+apisysid+"';");

            while (resultSet.next()){
                dbsysid=resultSet.getString(6);
                dbsysmodcount=resultSet.getString(1);
                dbsysupdatedon=resultSet.getString(3);
                dbsysupdatedby=resultSet.getString(7);
                dbsyscreatedon=resultSet.getString(8);
                dbname=resultSet.getString(9);
                dbsyscreatedby=resultSet.getString(12);
                dbcount++;
            }
            System.out.println("API sysid="+apisysid+" || DB sysid="+dbsysid);
            assertEquals(apisysid, dbsysid);
            System.out.println("API sysmodcount="+apisysmodcount+" || DB sysmodcount="+dbsysmodcount);
            assertEquals(apisysmodcount,dbsysmodcount);
            System.out.println("API sysupdatedon="+apisysupdatedon+" || DB sysupdatedon="+dbsysupdatedon);
            assertEquals(apisysupdatedon,dbsysupdatedon);
            System.out.println("API sysupdatedby="+apisysupdatedby+" || DB sysupdatedby="+dbsysupdatedby);
            assertEquals(apisysupdatedby,dbsysupdatedby);
            System.out.println("API syscreatedon="+apisyscreatedon+" || DB syscreatedon="+dbsyscreatedon);
            assertEquals(apisyscreatedon,dbsyscreatedon);
            System.out.println("API BUname="+name+" || DB BUname="+dbname);
            assertEquals(name,dbname);
            System.out.println("API syscreatedby="+apisyscreatedby+" || DB syscreatedby="+dbsyscreatedby);
            assertEquals(apisyscreatedby,dbsyscreatedby);
            System.out.println("*******************************************");
        }
        System.out.println("API count="+array.length()+" == DB count="+dbcount);
        assertEquals(array.length(), dbcount);
    }
}
