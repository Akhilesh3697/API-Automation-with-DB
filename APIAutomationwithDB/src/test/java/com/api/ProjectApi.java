package com.api;

import com.utilities.BaseTest;
import com.utilities.PropRepo;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import static org.testng.Assert.assertEquals;

public class ProjectApi extends BaseTest {
    @Test
    public void projectApi() throws Exception {
        PropRepo.loadAllProperties();

        File file = new File("JiraProjectAPI.txt");

        String schemaQuery=PropRepo.getString("projectapischema.query");
        List<String> columnnames = new ArrayList<String>();
        Map<String, String> APIvalues=null;
        int count=0;
        int flag=0;
        String column="";

        Response response = RestAssured.given().header(PropRepo.getString("jira.headerkey"), PropRepo.getString("jira.headervalue"))
                .when().get(PropRepo.getString("jira.projectApiUrl"));
        //System.out.println("Project API Status code="+response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        org.json.JSONArray array = new org.json.JSONArray(response.getBody().asString());
        //System.out.println("Project API length="+array.length());

        //Connection for Snowflake
        Connection connection = getConnection();

        getDBcolumns(schemaQuery, columnnames, connection);

        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));){
            for(int i=0; i<array.length(); i++) {
                APIvalues=new HashedMap();
                org.json.JSONObject pobj = array.getJSONObject(i);
                Iterator iterator = pobj.keySet().iterator();

                while(iterator.hasNext()){
                    String key = (String) iterator.next();
                    if(key.equals("id") || key.equals("key") || key.equals("name")) {
                        APIvalues.put("PROJECT"+key.toUpperCase(), pobj.get(key).toString());
                    } else if (key.equals("projectTypeKey")) {
                        APIvalues.put("PROJECTTYPE", pobj.get(key).toString());
                    } else if(key.equals("projectCategory")) {
                        Iterator<String> p = pobj.getJSONObject("projectCategory").keySet().iterator();
                        while(p.hasNext()) {
                            String pckey=p.next();
                            if(pckey.equals("id") || pckey.equals("name") || pckey.equals("description")) {
                                APIvalues.put("PROJECTCATEGORY"+pckey.toUpperCase(), pobj.getJSONObject("projectCategory").get(pckey).toString());
                            }
                        }
                    }
                    APIvalues.put(key, pobj.get(key).toString());
                }

                //DB Connection
                resultSet1=statement.executeQuery(PropRepo.getString("project.query")+"'"+APIvalues.get("PROJECTID")+"';");

                if(resultSet1 != null) {
                    while(resultSet1.next()) {
                        flag=1;
                        column=compareAPIandDBValues(APIvalues, columnnames, column, out);
                        count++;
                    }
                }
                if(column.isEmpty() == false) {
                    out.println(column+" is/are not matching"+APIvalues.get("id"));
                    //System.out.println(column+" is/are not matching"+APIvalues.get("id"));
                    out.println("**************************************************************");
                    column="";
                } else if(column.isEmpty() && flag==1) {
                    out.println("All the columns are matching for Project ID="+APIvalues.get("id"));
                    // System.out.println("All the columns are matching for Project ID="+APIvalues.get("id"));
                    out.println("*****************************************************************");
                }
                if(flag==1) {
                    flag=0;
                } else {
                    out.println("No data in the Database for Project ID="+APIvalues.get("id"));
                    //System.out.println("No data in the Database for Project ID="+APIvalues.get("id"));
                }
            }
            assertEquals(array.length(), count);
            out.println("Jira Project API count="+array.length());
            out.println("Jira Project DB count="+count);
        }
    }
}
