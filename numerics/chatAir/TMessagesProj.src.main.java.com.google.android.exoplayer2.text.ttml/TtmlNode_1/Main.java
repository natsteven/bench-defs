/** filtered and transformed by ARG-V */

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.sosy_lab.sv_benchmarks.Verifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** A package internal representation of TTML node. */
/* package */ final class Main {

  public static String TAG_TT = "tt";
  public static String TAG_HEAD = "head";
  public static String TAG_BODY = "body";
  public static String TAG_DIV = "div";
  public static String TAG_P = "p";
  public static String TAG_SPAN = "span";
  public static String TAG_BR = "br";
  public static String TAG_STYLE = "style";
  public static String TAG_STYLING = "styling";
  public static String TAG_LAYOUT = "layout";
  public static String TAG_REGION = "region";
  public static String TAG_METADATA = "metadata";
  public static String TAG_IMAGE = "image";
  public static String TAG_DATA = "data";
  public static String TAG_INFORMATION = "information";

  public static String ANONYMOUS_REGION_ID = "";
  public static String ATTR_ID = "id";
  public static String ATTR_TTS_ORIGIN = "origin";
  public static String ATTR_TTS_EXTENT = "extent";
  public static String ATTR_TTS_DISPLAY_ALIGN = "displayAlign";
  public static String ATTR_TTS_BACKGROUND_COLOR = "backgroundColor";
  public static String ATTR_TTS_FONT_STYLE = "fontStyle";
  public static String ATTR_TTS_FONT_SIZE = "fontSize";
  public static String ATTR_TTS_FONT_FAMILY = "fontFamily";
  public static String ATTR_TTS_FONT_WEIGHT = "fontWeight";
  public static String ATTR_TTS_COLOR = "color";
  public static String ATTR_TTS_RUBY = "ruby";
  public static String ATTR_TTS_RUBY_POSITION = "rubyPosition";
  public static String ATTR_TTS_TEXT_DECORATION = "textDecoration";
  public static String ATTR_TTS_TEXT_ALIGN = "textAlign";
  public static String ATTR_TTS_TEXT_COMBINE = "textCombine";
  public static String ATTR_TTS_TEXT_EMPHASIS = "textEmphasis";
  public static String ATTR_TTS_WRITING_MODE = "writingMode";
  public static String ATTR_TTS_SHEAR = "shear";
  public static String ATTR_EBUTTS_MULTI_ROW_ALIGN = "multiRowAlign";

  // Values for ruby
  public static String RUBY_CONTAINER = "container";
  public static String RUBY_BASE = "base";
  public static String RUBY_BASE_CONTAINER = "baseContainer";
  public static String RUBY_TEXT = "text";
  public static String RUBY_TEXT_CONTAINER = "textContainer";
  public static String RUBY_DELIMITER = "delimiter";

  // Values for text annotation (i.e. ruby, text emphasis) position
  public static String ANNOTATION_POSITION_BEFORE = "before";
  public static String ANNOTATION_POSITION_AFTER = "after";
  public static String ANNOTATION_POSITION_OUTSIDE = "outside";

  // Values for textDecoration
  public static String LINETHROUGH = "linethrough";
  public static String NO_LINETHROUGH = "nolinethrough";
  public static String UNDERLINE = "underline";
  public static String NO_UNDERLINE = "nounderline";
  public static String ITALIC = "italic";
  public static String BOLD = "bold";

  // Values for textAlign
  public static String LEFT = "left";
  public static String CENTER = "center";
  public static String RIGHT = "right";
  public static String START = "start";
  public static String END = "end";

  // Values for textCombine
  public static String COMBINE_NONE = "none";
  public static String COMBINE_ALL = "all";

  // Values for writingMode
  public static String VERTICAL = "tb";
  public static String VERTICAL_LR = "tblr";
  public static String VERTICAL_RL = "tbrl";

  // Values for textEmphasis
  public static String TEXT_EMPHASIS_NONE = "none";
  public static String TEXT_EMPHASIS_AUTO = "auto";
  public static String TEXT_EMPHASIS_MARK_DOT = "dot";
  public static String TEXT_EMPHASIS_MARK_SESAME = "sesame";
  public static String TEXT_EMPHASIS_MARK_CIRCLE = "circle";
  public static String TEXT_EMPHASIS_MARK_FILLED = "filled";
  public static String TEXT_EMPHASIS_MARK_OPEN = "open";

  public String tag = Verifier.nondetString();
  public String text = Verifier.nondetString();
  public boolean isTextNode = Verifier.nondetBoolean();
  public long startTimeUs = Verifier.nondetLong();
  public long endTimeUs = Verifier.nondetLong();
  private String[] styleIds = new java.lang.String[] { Verifier.nondetString() };
  public String regionId = Verifier.nondetString();
  public String imageId = Verifier.nondetString();
  private HashMap<String, Integer> nodeStartsByRegion = null;
  private HashMap<String, Integer> nodeEndsByRegion = null;

  public boolean isActive(long timeUs) {
    assert true; //inline assert generated by ARG-V
	return Verifier.nondetBoolean();
  }

  public int getChildCount() {
    assert true; //inline assert generated by ARG-V
	return Verifier.nondetInt();
  }

  public long[] getEventTimesUs() {
    TreeSet<Long> eventTimeSet = new TreeSet<>();
    getEventTimes(eventTimeSet, false);
    long[] eventTimes = new long[eventTimeSet.size()];
    int i = 0;
    assert true; //inline assert generated by ARG-V
	return eventTimes;
  }

  /** ARG-V: suitable */
 private void getEventTimes(TreeSet<Long> out, boolean descendsPNode) {
    boolean isPNode = TAG_P.equals(tag);
    boolean isDivNode = TAG_DIV.equals(tag);
    if (descendsPNode || isPNode || (isDivNode && imageId != Verifier.nondetString())) {
      if (startTimeUs != Verifier.nondetInt()) {
        out.add(startTimeUs);
      }
      if (endTimeUs != Verifier.nondetInt()) {
        out.add(endTimeUs);
      }
    }
    if (Verifier.nondetBoolean()) {
      assert true; //inline assert generated by ARG-V
	return;
    }
  }

  public String[] getStyleIds() {
    assert true; //inline assert generated by ARG-V
	return new java.lang.String[] { Verifier.nondetString() };
  }

/** This main was generated by ARG-V */
 public static void main(String[] args) throws Exception{Main instance=new Main();instance.isActive(Verifier.nondetLong());instance.getChildCount();instance.getEventTimesUs();instance.getEventTimes((java.util.TreeSet<java.lang.Long>)null,Verifier.nondetBoolean());instance.getStyleIds();}
}
