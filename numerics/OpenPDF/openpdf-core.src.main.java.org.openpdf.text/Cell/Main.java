/** filtered and transformed by ARG-V */

/*
 * $Id: Cell.java 4065 2009-09-16 23:09:11Z psoares33 $
 * $Name$
 *
 * Copyright 1999, 2000, 2001, 2002 by Bruno Lowagie.
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * https://github.com/LibrePDF/OpenPDF
 */
import org.sosy_lab.sv_benchmarks.Verifier;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A <CODE>Cell</CODE> is a <CODE>Rectangle</CODE> containing other
 * <CODE>Element</CODE>s.
 * <p>
 * A <CODE>Cell</CODE> must be added to a <CODE>Table</CODE>. The <CODE>Table</CODE> will place the <CODE>Cell</CODE> in
 * a <CODE>Row</CODE>.
 * <p>
 * Example:
 * <BLOCKQUOTE><PRE>
 * Table table = new Table(3); table.setBorderWidth(1); table.setBorderColor(new Color(0, 0, 255));
 * table.setCellpadding(5); table.setCellspacing(5);
 * <STRONG>Cell cell = new Cell("header");</STRONG>
 * <STRONG>cell.setHeader(true);</STRONG>
 * <STRONG>cell.setColspan(3);</STRONG>
 * table.addCell(cell);
 * <STRONG>cell = new Cell("example cell with colspan 1 and rowspan 2");</STRONG>
 * <STRONG>cell.setRowspan(2);</STRONG>
 * <STRONG>cell.setBorderColor(new Color(255, 0, 0));</STRONG>
 * table.addCell(cell); table.addCell("1.1"); table.addCell("2.1"); table.addCell("1.2"); table.addCell("2.2");
 * </PRE></BLOCKQUOTE>
 *
 * @see Rectangle
 * @see Element
 * @see Table
 * @see Row
 */

public class Main {

    // membervariables

    /**
     * The horizontal alignment of the cell content.
     */
    protected int horizontalAlignment = Verifier.nondetInt();

    /**
     * The vertical alignment of the cell content.
     */
    protected int verticalAlignment = Verifier.nondetInt();

    /**
     * The width of the cell as a String. It can be an absolute value "100" or a percentage "20%".
     */
    protected float width = Verifier.nondetFloat();
    protected boolean percentage = false;

    /**
     * The colspan of the cell.
     */
    protected int colspan = 1;

    /**
     * The rowspan of the cell.
     */
    protected int rowspan = 1;
    /**
     * Is this <CODE>Cell</CODE> a header?
     */
    protected boolean header = Verifier.nondetBoolean();
    /**
     * Maximum number of lines allowed in the cell. The default value of this property is not to limit the maximum
     * number of lines (contributed by dperezcar@fcc.es)
     */
    protected int maxLines = Integer.MAX_VALUE;
    /**
     * Indicates that the largest ascender height should be used to determine the height of the first line.  Note that
     * this only has an effect when rendered to PDF.  Setting this to true can help with vertical alignment problems.
     */
    protected boolean useAscender = false;
    /**
     * Indicates that the largest descender height should be added to the height of the last line (so characters like y
     * don't dip into the border).   Note that this only has an effect when rendered to PDF.
     */
    protected boolean useDescender = false;
    /**
     * Adjusts the cell contents to compensate for border widths.  Note that this only has an effect when rendered to
     * PDF.
     */
    protected boolean useBorderPadding = Verifier.nondetBoolean();
    /**
     * Does this <CODE>Cell</CODE> force a group change?
     */
    protected boolean groupChange = true;
    /**
     * The leading of the content inside the cell.
     */
    float leading = Float.NaN;
    /**
     * If a truncation happens due to the maxLines property, then this text will be added to indicate a truncation has
     * happened. Default value is null, and means avoiding marking the truncation. A useful value of this property could
     * be e.g. "..." (contributed by dperezcar@fcc.es)
     */
    String showTruncation = Verifier.nondetString();

    // constructors

    /**
     * Constructs an empty <CODE>Cell</CODE>.
     */
    public Main() {
    }

    /**
     * Constructs an empty <CODE>Cell</CODE> (for internal use only).
     *
     * @param dummy a dummy value
     */
    public Main(boolean dummy) {
        this();
    }

    /**
     * Constructs a <CODE>Cell</CODE> with a certain content.<p> The <CODE>String</CODE> will be converted into a
     * <CODE>Paragraph</CODE>.
     *
     * @param content a <CODE>String</CODE>
     */
    public Main(String content) {
        this();
        try {
        } catch (Exception e) {
        }
    }

    

    // implementation of the Element-methods

    /**
     * Gets the type of the text element.
     *
     * @return a type
     */
    public int type() {
        assert true; //inline assert generated by ARG-V
		return Verifier.nondetInt();
    }

    // Getters and setters

    /**
     * Gets the horizontal alignment.
     *
     * @return a value
     */
    public int getHorizontalAlignment() {
        assert true; //inline assert generated by ARG-V
		return horizontalAlignment;
    }

    /**
     * Gets the vertical alignment.
     *
     * @return a value
     */
    public int getVerticalAlignment() {
        assert true; //inline assert generated by ARG-V
		return verticalAlignment;
    }

    /**
     * Gets the width.
     */
    public float getWidth() {
        assert true; //inline assert generated by ARG-V
		return width;
    }

    /**
     * Sets the width. It can be an absolute value "100" or a percentage "20%"
     *
     * @param value the new value
     */
    /** ARG-V: suitable */
	 public void setWidth(String value) {
        if (value.endsWith("%")) {
            value = value.substring(0, value.length() - 1);
            percentage = true;
        }
        width = Integer.parseInt(value);
    }

    /**
     * Gets the width as a String.
     *
     * @return a value
     */
    /** ARG-V: suitable */
	 public String getWidthAsString() {
        String w = String.valueOf(width);
        if (w.endsWith(".0")) {
            w = w.substring(0, w.length() - 2);
        }
        if (percentage) {
            w += "%";
        }
        assert true; //inline assert generated by ARG-V
		return w;
    }

    /**
     * Gets the colspan.
     *
     * @return a value
     */
    public int getColspan() {
        assert true; //inline assert generated by ARG-V
		return colspan;
    }

    /**
     * Sets the colspan.
     *
     * @param value the new value
     */
    public void setColspan(int value) {
        colspan = value;
    }

    /**
     * Gets the rowspan.
     *
     * @return a value
     */
    public int getRowspan() {
        assert true; //inline assert generated by ARG-V
		return rowspan;
    }

    /**
     * Sets the rowspan.
     *
     * @param value the new value
     */
    public void setRowspan(int value) {
        rowspan = value;
    }

    /**
     * Gets the leading.
     *
     * @return a value
     */
    public float getLeading() {
        if (Float.isNaN(leading)) {
            assert true; //inline assert generated by ARG-V
			return 16;
        }
        assert true; //inline assert generated by ARG-V
		return leading;
    }

    /**
     * Sets the leading.
     *
     * @param value the new value
     */
    public void setLeading(float value) {
        leading = value;
    }

    /**
     * Is this <CODE>Cell</CODE> a header?
     *
     * @return a value
     */
    public boolean isHeader() {
        assert true; //inline assert generated by ARG-V
		return header;
    }

    /**
     * Sets header.
     *
     * @param value the new value
     */
    public void setHeader(boolean value) {
        header = value;
    }

    /**
     * Getter for maxLines
     *
     * @return the maxLines value
     */
    public int getMaxLines() {
        assert true; //inline assert generated by ARG-V
		return maxLines;
    }

    /**
     * Setter for maxLines
     *
     * @param value the maximum number of lines
     */
    public void setMaxLines(int value) {
        maxLines = value;
    }

    /**
     * Getter for showTruncation
     *
     * @return the showTruncation value
     */
    public String getShowTruncation() {
        assert true; //inline assert generated by ARG-V
		return Verifier.nondetString();
    }

    /**
     * Gets the value of useAscender
     *
     * @return useAscender
     */
    public boolean isUseAscender() {
        assert true; //inline assert generated by ARG-V
		return useAscender;
    }

    /**
     * Sets the value of useAscender.
     *
     * @param use use ascender height if true
     */
    public void setUseAscender(boolean use) {
        useAscender = use;
    }

    /**
     * gets the value of useDescender
     *
     * @return useDescender
     */
    public boolean isUseDescender() {
        assert true; //inline assert generated by ARG-V
		return useDescender;
    }

    /**
     * Sets the value of useDescender.
     *
     * @param use use descender height if true
     */
    public void setUseDescender(boolean use) {
        useDescender = use;
    }

    /**
     * Gets the value of useBorderPadding.
     *
     * @return useBorderPadding
     */
    public boolean isUseBorderPadding() {
        assert true; //inline assert generated by ARG-V
		return useBorderPadding;
    }

// arraylist stuff

    /**
     * Sets the value of useBorderPadding.
     *
     * @param use adjust layout for borders if true
     */
    public void setUseBorderPadding(boolean use) {
        useBorderPadding = use;
    }

    /**
     * Does this <CODE>Cell</CODE> force a group change?
     *
     * @return a value
     */
    public boolean getGroupChange() {
        assert true; //inline assert generated by ARG-V
		return groupChange;
    }

    /**
     * Sets group change.
     *
     * @param value the new value
     */
    public void setGroupChange(boolean value) {
        groupChange = value;
    }

    /**
     * Gets the number of <CODE>Element</CODE>s in the Cell.
     *
     * @return a <CODE>size</CODE>.
     */
    public int size() {
        assert true; //inline assert generated by ARG-V
		return Verifier.nondetInt();
    }

    /**
     * Gets an iterator of <CODE>Element</CODE>s.
     *
     * @return an <CODE>Iterator</CODE>.
     */
    public Iterator getElements() {
        assert true; //inline assert generated by ARG-V
		return null;
    }

    

    // helper methods

    /**
     * Checks if this <CODE>Cell</CODE> is a placeholder for a (nested) table.
     *
     * @return true if the only element in this cell is a table
     */
    public boolean isTable() {
        assert true; //inline assert generated by ARG-V
		return Verifier.nondetBoolean();
    }

    

    // unsupported Rectangle methods

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @return NA
     */
    public float getTop() {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @param value NA
     */
    public void setTop(int value) {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @return NA
     */
    public float getBottom() {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @param value NA
     */
    public void setBottom(int value) {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @return NA
     */
    public float getLeft() {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @param value NA
     */
    public void setLeft(int value) {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @return NA
     */
    public float getRight() {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @param value NA
     */
    public void setRight(int value) {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @param margin NA
     * @return NA
     */
    public float top(int margin) {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @param margin NA
     * @return NA
     */
    public float bottom(int margin) {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @param margin NA
     * @return NA
     */
    public float left(int margin) {
        throw new RuntimeException();
    }

    /**
     * This method throws an <CODE>UnsupportedOperationException</CODE>.
     *
     * @param margin NA
     * @return NA
     */
    public float right(int margin) {
        throw new RuntimeException();
    }

	/** This main was generated by ARG-V */
	
	public static void main(String[] args) throws Exception {
		Main instance = new Main(Verifier.nondetString());
		instance.type();
		instance.getHorizontalAlignment();
		instance.getVerticalAlignment();
		instance.getWidth();
		instance.setWidth(Verifier.nondetString());
		instance.getWidthAsString();
		instance.getColspan();
		instance.setColspan(Verifier.nondetInt());
		instance.getRowspan();
		instance.setRowspan(Verifier.nondetInt());
		instance.getLeading();
		instance.setLeading(Verifier.nondetFloat());
		instance.isHeader();
		instance.setHeader(Verifier.nondetBoolean());
		instance.getMaxLines();
		instance.setMaxLines(Verifier.nondetInt());
		instance.getShowTruncation();
		instance.isUseAscender();
		instance.setUseAscender(Verifier.nondetBoolean());
		instance.isUseDescender();
		instance.setUseDescender(Verifier.nondetBoolean());
		instance.isUseBorderPadding();
		instance.setUseBorderPadding(Verifier.nondetBoolean());
		instance.getGroupChange();
		instance.setGroupChange(Verifier.nondetBoolean());
		instance.size();
		instance.getElements();
		instance.isTable();
		instance.getTop();
		instance.setTop(Verifier.nondetInt());
		instance.getBottom();
		instance.setBottom(Verifier.nondetInt());
		instance.getLeft();
		instance.setLeft(Verifier.nondetInt());
		instance.getRight();
		instance.setRight(Verifier.nondetInt());
		instance.top(Verifier.nondetInt());
		instance.bottom(Verifier.nondetInt());
		instance.left(Verifier.nondetInt());
		instance.right(Verifier.nondetInt());
	}
}
