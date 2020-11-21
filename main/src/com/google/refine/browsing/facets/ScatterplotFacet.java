/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.google.refine.browsing.facets;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.refine.browsing.FilteredRecords;
import com.google.refine.browsing.FilteredRows;
import com.google.refine.browsing.RecordFilter;
import com.google.refine.browsing.RowFilter;
import com.google.refine.browsing.filters.AnyRowRecordFilter;
import com.google.refine.browsing.filters.DualExpressionsNumberComparisonRowFilter;
import com.google.refine.browsing.util.ExpressionBasedRowEvaluable;
import com.google.refine.browsing.util.NumericBinIndex;
import com.google.refine.browsing.util.NumericBinRecordIndex;
import com.google.refine.browsing.util.NumericBinRowIndex;
import com.google.refine.expr.Evaluable;
import com.google.refine.expr.MetaParser;
import com.google.refine.expr.ParsingException;
import com.google.refine.model.Column;
import com.google.refine.model.Project;

public class ScatterplotFacet implements Facet {

    public static final int LIN = 0;
    public static final int LOG = 1;
    
    public static final int NO_ROTATION = 0;
    public static final int ROTATE_CW = 1;
    public static final int ROTATE_CCW = 2;
    
    /*
     * Configuration, from the client side
     */
    public static class ScatterplotFacetConfig implements FacetConfig {
        @JsonProperty("name")
        protected String name; // name of facet
    
        @JsonProperty(X_EXPRESSION)
        protected String expressionX; // expression to compute the x numeric value(s) per row
        @JsonProperty(Y_EXPRESSION)
        protected String expressionY; // expression to compute the y numeric value(s) per row
        @JsonProperty(X_COLUMN_NAME)
        protected String columnNameX; // column to base the x expression on, if any
        @JsonProperty(Y_COLUMN_NAME)
        protected String columnNameY; // column to base the y expression on, if any
        
        @JsonProperty(SIZE)
        protected int size;
        @JsonIgnore
        protected int dimX;
        @JsonIgnore
        protected int dimY;
        @JsonIgnore
        protected String rotationStr;
        @JsonIgnore
        protected int rotation;
    
        @JsonIgnore
        protected double l = 1.;
        @JsonProperty(DOT)
        protected double dot;
    
        @JsonIgnore
        protected String colorStr = "000000";
        @JsonIgnore
        protected Color getColor() {
            return new Color(Integer.parseInt(colorStr, 16));
        }
        
        @JsonProperty(FROM_X)
        protected double fromX; // the numeric selection for the x axis, from 0 to 1
        @JsonProperty(TO_X)
        protected double toX;
        @JsonProperty(FROM_Y)
        protected double fromY; // the numeric selection for the y axis, from 0 to 1
        @JsonProperty(TO_Y)
        protected double toY;
        
        // false if we're certain that all rows will match
        // and there isn't any filtering to do
        protected boolean isSelected() {
            return fromX > 0 || toX < 1 || fromY > 0 || toY < 1;
        }
        
        @JsonProperty(DIM_X)
        public String getDimX() {
            return dimX == LIN ? "lin" : "log";
        }
        
        @JsonProperty(DIM_Y)
        public String getDimY() {
            return dimY == LIN ? "lin" : "log";
        }
        
        @Override
        public ScatterplotFacet apply(Project project) {
            ScatterplotFacet facet = new ScatterplotFacet();
            facet.initializeFromConfig(this, project);
            return facet;
        }
        
        public static int getRotation(String rotation) {
            rotation = rotation.toLowerCase();
            if ("cw".equals(rotation) || "right".equals(rotation)) {
                return ScatterplotFacet.ROTATE_CW;
            } else if ("ccw".equals(rotation) || "left".equals(rotation)) {
                return ScatterplotFacet.ROTATE_CCW;
            } else {
                return NO_ROTATION;
            }
        }

        @Override
        public String getJsonType() {
            return "scatterplot";
        }
    }
    ScatterplotFacetConfig config;

    /*
     * Derived configuration data
     */
    protected int        columnIndexX;
    protected int        columnIndexY;
    protected Evaluable  evalX;
    protected Evaluable  evalY;
    protected String     errorMessageX;
    protected String     errorMessageY;

    protected double minX; 
    protected double maxX;
    protected double minY;
    protected double maxY;
    protected AffineTransform t;
    
    protected String image;
    
        
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String COLOR = "color";
    public static final String BASE_COLOR = "base_color";
    public static final String SIZE = "l";
    public static final String ROTATION = "r";
    public static final String DOT = "dot";
    public static final String DIM_X = "dimX";
    public static final String DIM_Y = "dimY";

    public static final String X_COLUMN_NAME = "cx";
    public static final String X_EXPRESSION = "ex";
    public static final String MIN_X = "minX";
    public static final String MAX_X = "maxX";
    public static final String TO_X = "toX";
    public static final String FROM_X = "fromX";
    public static final String ERROR_X = "error_x";
    
    public static final String Y_COLUMN_NAME = "cy";
    public static final String Y_EXPRESSION = "ey";
    public static final String MIN_Y = "minY";
    public static final String MAX_Y = "maxY";
    public static final String TO_Y = "toY";
    public static final String FROM_Y = "fromY";
    public static final String ERROR_Y = "error_y";
    
    private static final boolean IMAGE_URI = false;
    
    public static String emptyImage;
    
    final static Logger LOGGER = LoggerFactory.getLogger("scatterplot_facet");
    
    static {
        try {
            emptyImage = serializeImage(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR));
        } catch (IOException e) {
            emptyImage = "";
        }
    }
    
    @JsonProperty(NAME)
    public String getName() {
        return config.name;
    }
    
    @JsonProperty(X_COLUMN_NAME)
    public String getXColumnName() {
        return config.columnNameX;
    }
    
    @JsonProperty(X_EXPRESSION)
    public String getXExpression() {
        return config.expressionX;
    }
    
    @JsonProperty(Y_COLUMN_NAME)
    public String getYColumnName() {
        return config.columnNameY;
    }
    
    @JsonProperty(Y_EXPRESSION)
    public String getYExpression() {
        return config.expressionY;
    }
    
    @JsonProperty(SIZE)
    public int getSize() {
        return config.size;
    }
    
    @JsonProperty(DIM_X)
    public int getDimX() {
        return config.dimX;
    }
    
    @JsonProperty(DIM_Y)
    public int getDimY() {
        return config.dimY;
    }
    
    @JsonProperty(DOT)
    public double getDot() {
        return config.dot;
    }
    
    @JsonProperty(ROTATION)
    public double getRotation() {
        return config.rotation;
    }
    
    @JsonProperty(COLOR)
    public String getColorString() {
        return config.colorStr;
    }
    
    @JsonProperty(IMAGE)
    @JsonInclude(Include.NON_NULL)
    public String getImage() {
        if (IMAGE_URI) {
            return image;
        }
        return null;
    }
    
    @JsonProperty(ERROR_X)
    @JsonInclude(Include.NON_NULL)
    public String getErrorX() {
        return errorMessageX;
    }
    
    @JsonProperty(FROM_X)
    @JsonInclude(Include.NON_NULL)
    public Double getFromX() {
        if (errorMessageX == null && !Double.isInfinite(minX) && !Double.isInfinite(maxX)) {
            return config.fromX;
        }
        return null;
    }
    
    @JsonProperty(TO_X)
    @JsonInclude(Include.NON_NULL)
    public Double getToX() {
        if (errorMessageX == null && !Double.isInfinite(minX) && !Double.isInfinite(maxX)) {
            return config.toX;
        }
        return null;
    }
    
    @JsonProperty(ERROR_Y)
    @JsonInclude(Include.NON_NULL)
    public String getErrorY() {
        return errorMessageY;
    }
    
    @JsonProperty(FROM_Y)
    @JsonInclude(Include.NON_NULL)
    public Double getFromY() {
        if (errorMessageY == null && !Double.isInfinite(minY) && !Double.isInfinite(maxY)) {
            return config.fromY;
        }
        return null;
    }
    
    @JsonProperty(TO_Y)
    @JsonInclude(Include.NON_NULL)
    public Double getToY() {
        if (errorMessageY == null && !Double.isInfinite(minY) && !Double.isInfinite(maxY)) {
            return config.toY;
        }
        return null;
    }
     
    public void initializeFromConfig(ScatterplotFacetConfig configuration, Project project) {
        config = configuration;
        
        t = createRotationMatrix(config.rotation, config.l);
        
        if (config.columnNameX.length() > 0) {
            Column x_column = project.columnModel.getColumnByName(config.columnNameX);
            if (x_column != null) {
                columnIndexX = x_column.getCellIndex();
                
                NumericBinIndex index_x = ScatterplotFacet.getBinIndex(project, x_column, evalX, config.expressionX);
                minX = index_x.getMin();
                maxX = index_x.getMax();
            } else {
                errorMessageX = "No column named " + config.columnNameX;
            }
        } else {
            columnIndexX = -1;
        }
        
        try {
            evalX = MetaParser.parse(config.expressionX);
        } catch (ParsingException e) {
            errorMessageX = e.getMessage();
        }
        
        if (config.columnNameY.length() > 0) {
            Column y_column = project.columnModel.getColumnByName(config.columnNameY);
            if (y_column != null) {
                columnIndexY = y_column.getCellIndex();
                
                NumericBinIndex index_y = ScatterplotFacet.getBinIndex(project, y_column, evalY, config.expressionY);
                minY = index_y.getMin();
                maxY = index_y.getMax();
            } else {
                errorMessageY = "No column named " + config.columnNameY;
            }
        } else {
            columnIndexY = -1;
        }
        
        try {
            evalY = MetaParser.parse(config.expressionY);
        } catch (ParsingException e) {
            errorMessageY = e.getMessage();
        }
        
    }

    @Override
    public RowFilter getRowFilter(Project project) {
        if (config.isSelected() && 
            evalX != null && errorMessageX == null && 
            evalY != null && errorMessageY == null) 
        {
            return new DualExpressionsNumberComparisonRowFilter(
                    evalX, config.columnNameX, columnIndexX, evalY, config.columnNameY, columnIndexY) {
                
                double fromXPixels = config.fromX * config.l;
                double toXPixels = config.toX * config.l;
                double fromYPixels = config.fromY * config.l;
                double toYPixels = config.toY * config.l;
                
                @Override
                protected boolean checkValues(double x, double y) {
                    Point2D.Double p = new Point2D.Double(x, y);
                    p = translateCoordinates(p, minX, maxX, minY, maxY, config.dimX, config.dimY, config.l, t);
                    return p.x >= fromXPixels && p.x <= toXPixels && p.y >= fromYPixels && p.y <= toYPixels;
                };
            };
        } else {
            return null;
        }
    }

    @Override
    public RecordFilter getRecordFilter(Project project) {
        RowFilter rowFilter = getRowFilter(project);
        return rowFilter == null ? null : new AnyRowRecordFilter(rowFilter);
    }

    @Override
    public void computeChoices(Project project, FilteredRows filteredRows) {
        if (evalX != null && evalY != null && errorMessageX == null && errorMessageY == null) {
            Column column_x = project.columnModel.getColumnByCellIndex(columnIndexX);
            NumericBinIndex index_x = getBinIndex(project, column_x, evalX, config.expressionX, "row-based");
            
            Column column_y = project.columnModel.getColumnByCellIndex(columnIndexY);
            NumericBinIndex index_y = getBinIndex(project, column_y, evalY, config.expressionY, "row-based");

            retrieveDataFromBinIndices(index_x, index_y);
            
            if (IMAGE_URI) {
                if (index_x.isNumeric() && index_y.isNumeric()) {
                    ScatterplotDrawingRowVisitor drawer = new ScatterplotDrawingRowVisitor(
                      columnIndexX, columnIndexY, minX, maxX, minY, maxY, 
                      config.size, config.dimX, config.dimY, config.rotation, config.dot, config.getColor()
                    );
                    filteredRows.accept(project, drawer);
                 
                    try {
                        image = serializeImage(drawer.getImage());
                    } catch (IOException e) {
                        LOGGER.warn("Exception caught while generating the image", e);
                    }
                } else {
                    image = emptyImage;
                }
            }
        }
    }
    
    @Override
    public void computeChoices(Project project, FilteredRecords filteredRecords) {
        if (evalX != null && evalY != null && errorMessageX == null && errorMessageY == null) {
            Column column_x = project.columnModel.getColumnByCellIndex(columnIndexX);
            NumericBinIndex index_x = getBinIndex(project, column_x, evalX, config.expressionX, "record-based");
            
            Column column_y = project.columnModel.getColumnByCellIndex(columnIndexY);
            NumericBinIndex index_y = getBinIndex(project, column_y, evalY, config.expressionY, "record-based");
            
            retrieveDataFromBinIndices(index_x, index_y);
            
            if (IMAGE_URI) {
                if (index_x.isNumeric() && index_y.isNumeric()) {
                    ScatterplotDrawingRowVisitor drawer = new ScatterplotDrawingRowVisitor(
                      columnIndexX, columnIndexY, minX, maxX, minY, maxY, 
                      config.size, config.dimX, config.dimY, config.rotation, config.dot, config.getColor()
                    );
                    filteredRecords.accept(project, drawer);
                 
                    try {
                        image = serializeImage(drawer.getImage());
                    } catch (IOException e) {
                        LOGGER.warn("Exception caught while generating the image", e);
                    }
                } else {
                    image = emptyImage;
                }
            }
        }
    }
    
    protected void retrieveDataFromBinIndices(NumericBinIndex index_x, NumericBinIndex index_y) {
        minX = index_x.getMin();
        maxX = index_x.getMax();
                    
        minY = index_y.getMin();
        maxY = index_y.getMax();
    }
    
    public static String serializeImage(RenderedImage image) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
        ImageIO.write(image, "png", output);
        output.close();
        String encoded = Base64.encodeBase64String(output.toByteArray());
        String url =  "data:image/png;base64," + encoded;
        return url;
    }
    
    public static int getAxisDim(String type) {
        return ("log".equals(type.toLowerCase())) ? LOG : LIN;
    }
   
    
    public static NumericBinIndex getBinIndex(Project project, Column column, Evaluable eval, String expression) {
        return getBinIndex(project, column, eval, expression, "row-based");
    }
    
    public static NumericBinIndex getBinIndex(Project project, Column column, Evaluable eval, String expression, String mode) {
        String key = "numeric-bin:" + mode + ":" + expression;
        if (eval == null) {
            try {
                eval = MetaParser.parse(expression);
            } catch (ParsingException e) {
                LOGGER.warn("Error parsing expression", e);
            }
        }
        NumericBinIndex index = (NumericBinIndex) column.getPrecompute(key);
        if (index == null) {
            index = "row-based".equals(mode) ? 
                    new NumericBinRowIndex(project, new ExpressionBasedRowEvaluable(column.getName(), column.getCellIndex(), eval)) :
                        new NumericBinRecordIndex(project, new ExpressionBasedRowEvaluable(column.getName(), column.getCellIndex(), eval));

                    column.setPrecompute(key, index);
        }
        return index;
    }
    
    private static double sRotateScale = 1 / Math.sqrt(2.0);
    
    public static AffineTransform createRotationMatrix(int rotation, double l) {
        if (rotation == ScatterplotFacet.ROTATE_CW) {
            AffineTransform t = AffineTransform.getTranslateInstance(0, l / 2);
            t.scale(sRotateScale, sRotateScale);
            t.rotate(-Math.PI / 4);
            return t;
        } else if (rotation == ScatterplotFacet.ROTATE_CCW) {
            AffineTransform t = AffineTransform.getTranslateInstance(l / 2, 0);
            t.scale(sRotateScale, sRotateScale);
            t.rotate(Math.PI / 4);
            return t;
        } else {
            return null;
        }
    }
    
    public static Point2D.Double translateCoordinates(
            Point2D.Double p, 
            double minX, double maxX, double minY, double maxY,
            int dimX, int dimY, double l, AffineTransform t) {
        
        double x = p.x;
        double y = p.y;
        
        double relative_x = x - minX;
        double range_x = maxX - minX;
        if (dimX == ScatterplotFacet.LOG) {
            x = Math.log10(relative_x + 1) * l / Math.log10(range_x + 1);
        } else {
            x = relative_x * l / range_x;
        }

        double relative_y = y - minY;
        double range_y = maxY - minY;
        if (dimY == ScatterplotFacet.LOG) {
            y = Math.log10(relative_y + 1) * l / Math.log10(range_y + 1);
        } else {
            y = relative_y * l / range_y;
        }
        
        p.x = x;
        p.y = y;
        if (t != null) {
            t.transform(p, p);
        }
        
        return p;
    }
    
}
