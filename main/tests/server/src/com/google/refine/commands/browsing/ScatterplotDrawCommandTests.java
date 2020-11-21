package com.google.refine.commands.browsing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.refine.browsing.facets.ScatterplotFacet;
import com.google.refine.commands.Command;
import com.google.refine.commands.browsing.GetScatterplotCommand;
import com.google.refine.util.ParsingUtilities;

public class ScatterplotDrawCommandTests {
    protected HttpServletRequest request = null;
    protected HttpServletResponse response = null;
	protected StringWriter writer = null;
	protected Command command = null;
	
    @BeforeMethod
    public void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        command = new GetScatterplotCommand();
        writer = new StringWriter();
        try {
            when(response.getWriter()).thenReturn(new PrintWriter(writer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String configJson = "{"
    		+ "\"name\":\"a (x) vs. b (y)\","
    		+ "\"cx\":\"a\","
    		+ "\"cy\":\"b\","
    		+ "\"l\":150,"
    		+ "\"ex\":\"value\","
    		+ "\"ey\":\"value\","
    		+ "\"dot\":0.8,"
    		+ "\"dimX\":\"log\","
    		+ "\"dimY\":\"lin\","
    		+ "\"type\":\"scatterplot\","
    		+ "\"fromX\":1,"
    		+ "\"toX\":2,"
    		+ "\"fromY\":3,"
    		+ "\"toY\":4,"
    		+ "\"color\":\"ff6a00\""
    		+ "}";


    public static String configJsonWithNone = "{"
    		+ "\"name\":\"b (x) vs. y (y)\","
    		+ "\"cx\":\"b\","
    		+ "\"cy\":\"y\","
    		+ "\"l\":150,"
    		+ "\"ex\":\"value\","
    		+ "\"ey\":\"value\","
    		+ "\"dot\":1.4,"
    		+ "\"dimX\":\"lin\","
    		+ "\"dimY\":\"lin\","
    		+ "\"r\":\"none\","
    		+ "\"type\":\"scatterplot\","
    		+ "\"fromX\":0,"
    		+ "\"toX\":0,"
    		+ "\"fromY\":0,"
    		+ "\"toY\":0,"
    		+ "\"color\":\"ff6a00\"}";

    @Test
    public void testParseConfig() throws JsonParseException, JsonMappingException, IOException {
    	GetScatterplotCommand.PlotterConfig config = ParsingUtilities.mapper.readValue(configJson, GetScatterplotCommand.PlotterConfig.class);
    	Assert.assertEquals("a", config.columnNameX);
    	Assert.assertEquals("b", config.columnNameY);
    	Assert.assertEquals(ScatterplotFacet.LOG, config.dimX);
    	Assert.assertEquals(ScatterplotFacet.LIN, config.dimY);
    }
    
    @Test
    public void testParseConfigWithNone() throws JsonParseException, JsonMappingException, IOException {
    	GetScatterplotCommand.PlotterConfig config = ParsingUtilities.mapper.readValue(configJsonWithNone, GetScatterplotCommand.PlotterConfig.class);
    	Assert.assertEquals(0, config.rotation);
    }
	
}
