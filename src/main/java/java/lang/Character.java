/* *****************************************************************
 *
 * Copyright 2022 Montera Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *******************************************************************/

package java.lang;

/**
 * @author Greg McCreath
 * @since 0.0.1
 */
public final class Character extends Object {

    public static final int MIN_RADIX = 2;
    public static final int MAX_RADIX = 36;
    public static final char MIN_VALUE = '\u0000';
    public static final char MAX_VALUE = '\uffff';

    private final char _value;

    public Character(char value) {
        this._value = value;
    }

    public char charValue() {
        return _value;
    }

    public static final Class<Character> TYPE = (Class<Character>) Class.getPrimitiveClass(Class.CHARACTER_CLASS);

    public int hashCode() {
        return (int)_value;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Character) {
            return _value == ((Character)obj)._value;
        }
        return false;
    }

    public String toString() {
        return Character.toString(_value);
    }

    public static String toString(char c) {
		char data[] = { c };
		return new String(data, 0, 1);
    }

    public static Character valueOf(char c) {
    	return new Character(c);
    }

    public static boolean isLowerCase(char ch) {
        return (ch >= 'a'  && ch <= 'z')
        || (ch >= 0xDF && ch <= 0xF6)
        || (ch >= 0xF8 && ch <= 0xFF);
    }

    public static boolean isUpperCase(char ch) {
        return (ch >= 'A'  && ch <= 'Z')
        || (ch >= 0xC0 && ch <= 0xD6)
        || (ch >= 0xD8 && ch <= 0xDE );
    }

    public static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isLetter(char ch) {
        return ( (ch >= 'a'  && ch <= 'z') ||
                 (ch >= 'A'  && ch <= 'Z') );
    }

    public static boolean isLetterOrDigit(char ch) {
        return ( isLetter(ch) || isDigit(ch) );
    }

    public static char toLowerCase(char ch) {
        if (isUpperCase(ch)) {
            if (ch <= 'Z') {
                return (char)(ch + ('a' - 'A'));
            } else {
                return (char)(ch + 0x20);
            }
        } else {
            return ch;
        }
    }

    public static char toUpperCase(char ch) {
        if (isLowerCase(ch)) {
            if (ch <= 'z') {
                return (char)(ch - ('a' - 'A'));
            } else {
                return (char)(ch - 0x20);
            }
        } else {
            return ch;
        }
    }

    public static int digit(char ch, int radix) {
        int value = -1;
        if (radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX) {
          if (isDigit(ch)) {
              value = ch - '0';
          }
          else if (isUpperCase(ch) || isLowerCase(ch)) {
              value = (ch & 0x1F) + 9;
          }
        }
        return (value < radix) ? value : -1;
    }

}

