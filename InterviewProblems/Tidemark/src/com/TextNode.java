package com;

public class TextNode implements Comparable<TextNode> {
    String _word;
    int _count;

    public TextNode( String word, int count ) {
        _word = word;
        _count = count;
    }

    public String getWord() {
        return _word;
    }

    public int getCount() {
        return _count;
    }

    public int compareTo( TextNode that ) {
        String word2 = that.getWord();
        int count2 = that.getCount();
        if ( _word.length() != word2.length() ) {
            return _word.length() < word2.length() ? -1 :  1;
        }
        else if ( ! _word.equals( word2 ) ) {
            return _word.compareTo( word2 );
        }
        else {
            return _count < count2 ? -1 : _count > count2 ? 1 : 0;
        }
    }

    public String toString() {
        return "[ " + _word + ", " + _count + " ]";
    }
}
