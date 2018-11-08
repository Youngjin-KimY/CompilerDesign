package MiniC.Scanner;

import MiniC.Scanner.SourceFile;
import MiniC.Scanner.Token;



public final class Scanner {

    private SourceFile sourceFile;

    private char currentChar;
    private boolean verbose;
    private StringBuffer currentLexeme;
    private boolean currentlyScanningToken;
    private int currentLineNr;
    private int currentColNr;

    private char[] queue = new char[5];
    private int num_Queue = 0;
    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }
    private boolean chklast = true;

///////////////////////////////////////////////////////////////////////////////

    public Scanner(SourceFile source) {
        sourceFile = source;
        currentChar = sourceFile.readChar();
        verbose = false;
        currentLineNr = -1;
        currentColNr= -1;
    }

    public void enableDebugging() {
        verbose = true;
    }

    // takeIt appends the current character to the current token, and gets
    // the next character from the source program (or the to-be-implemented
    // "untake" buffer in case of look-ahead characters that got 'pushed back'
    // into the input stream).

    private void takeIt() {

        if (currentlyScanningToken)
        {
            currentLexeme.append(currentChar);
        }else{
            if(currentChar == '\n'){
                currentLineNr++;
            }
        }
        if(num_Queue == 0){
            currentChar = sourceFile.readChar();
        }else{
            currentChar = queue[0];
            queueUpt();
        }


    }
    private void queueUpt(){
        num_Queue--;
        for(int i=0;i<num_Queue;i++){
            queue[i] = queue[i+1];
        }
    }
    private void insertQueue(){
        queue[num_Queue] = currentChar;
        currentChar = sourceFile.readChar();
        num_Queue++;
    }
    boolean chk(char ch){
        if(ch == '1' || ch == '2' || ch == '3' || ch == '4' || ch == '5'
                || ch == '6' || ch == '7' || ch == '8' || ch == '9' || ch == '0'){
            return false;
        }
        return true;
    }
    int scaninnterToken(){
        boolean val = true,chkInt=true;
        switch (currentChar) {
            case '0':  case '1':  case '2':  case '3':  case '4':
            case '5':  case '6':  case '7':  case '8':  case '9': case '.':
                while(!(currentChar == '\u0000' || currentChar == ' ' || currentChar == ';'
                        || currentChar == '\n' || currentChar == '\r' || currentChar == '\t')){
                    if(!(isDigit(currentChar))){
                        if(!val){
                            if(chk(currentChar)){
                                break;
                            }else{
                                insertQueue();
                                if(chk(currentChar)){
                                    break;
                                }else{
                                    insertQueue();
                                    if(chk(currentChar)){
                                        queue[num_Queue] = currentChar;
                                        num_Queue++;
                                        currentChar = queue[0];
                                        queueUpt();
                                        break;
                                    }else{
                                        queue[num_Queue] = currentChar;
                                        num_Queue++;
                                        currentChar = queue[0];
                                        queueUpt();
                                        val = true;
                                    }
                                }
                            }
                        }
                        if((currentChar == 'e' || currentChar == 'E' || currentChar =='.'
                                || currentChar == '-'
                                || currentChar == '+')){
                            if(currentChar == '.'){
                                val = false;
                            }
                            takeIt();
                            chkInt = false;

                        }else { // id
                            break;
                        }
                    }else{
                        takeIt();
                    }
                }
                if(chkInt){
                    return Token.INTLITERAL;
                }else{
                    return Token.FLOATLITERAL;
                }
            case '+':
                takeIt();
                return Token.PLUS;
            case '\u0000': // sourceFile.eot:
                currentLexeme.append('$');
                return Token.EOF;
            // Add code here for the remaining MiniC tokens...

            case '=':
                takeIt();
                if(currentChar == '='){
                    takeIt();
                    return Token.EQ;
                }else{
                    return Token.ASSIGN;
                }
            case '|':
                takeIt();
                takeIt();
                return Token.OR;
            case '&':
                takeIt();
                takeIt();
                return Token.AND;
            case '!':
                takeIt();
                if(currentChar == '='){
                    takeIt();
                    return Token.NOTEQ;
                }else{
                    return Token.NOT;
                }
            case '<':
                takeIt();
                if(currentChar == '='){
                    takeIt();
                    return Token.LESSEQ;
                }else{
                    return Token.LESS;
                }
            case '>':
                takeIt();
                if(currentChar == '='){
                    takeIt();
                    return Token.GREATEREQ;
                }
                return Token.GREATER;
            case '-':
                takeIt();
                return Token.MINUS;
            case '*':
                takeIt();
                return Token.TIMES;
            case '/':
                boolean slashchk = false,asteriskshk = false;
                takeIt();
                if(currentChar !='*') {
                    if(currentChar == '/'){
                        while(currentChar != '\u0000'){
                            takeIt();
                            if(currentChar == '\n'){
                                currentLineNr++;
                            }
                        }
                        int endpos = currentLexeme.length();
                        currentLexeme.delete(0,endpos);
                        currentLexeme.append('$');
                        currentColNr=1;
                        currentLineNr++;
                        chklast=false;
                        return Token.EOF;
                    }else{
                        return Token.DIV;
                    }
                }
                else{
                    while(currentChar != '\u0000') {
                        takeIt();
                        if(currentChar == '\n'){
                            currentLineNr++;
                        }
                        if (currentChar == '*') {
                            takeIt();
                            if (currentChar == '\n') {
                                currentLineNr++;
                            }
                        }
                        if(currentChar == '/'){
                            takeIt();
                            if (currentChar == '\u0000') {
                                int endpos = currentLexeme.length();
                                currentLexeme.delete(0,endpos);
                                currentLexeme.append('$');
                                currentColNr=1;
                                currentLineNr++;
                                chklast=false;
                                return Token.EOF;
                            }else{
                                int endpos = currentLexeme.length();
                                currentColNr+=(endpos);
                                currentLexeme.delete(0,endpos);
                                if((currentChar == ' '
                                        || currentChar == '\f'
                                        || currentChar == '\n'
                                        || currentChar == '\r'
                                        || currentChar == '\t')){
                                    if(currentChar == '\n'){
                                        currentColNr =1;
                                        currentLineNr++;
                                    }
                                    currentlyScanningToken =false;
                                    takeIt();
                                    currentlyScanningToken=true;
                                }
                                return scaninnterToken();
                            }

                        }

                    }
                    System.out.println("ERROR: unterminated multi-line comment.");
                    int endpos =currentLexeme.length();
                    currentLexeme.delete(0,endpos);
                    currentLexeme.append('$');
                    chklast=false;
                    currentLineNr++;
                    return Token.EOF;
                }
            case '{':
                takeIt();
                return Token.LEFTBRACE;
            case '}':
                takeIt();
                return Token.RIGHTBRACE;
            case '[':
                takeIt();
                return Token.LEFTBRACKET;
            case ']':
                takeIt();
                return Token.RIGHTBRACKET;
            case '(':
                takeIt();
                return Token.LEFTPAREN;
            case ')':
                takeIt();
                return Token.RIGHTPAREN;
            case ',':
                takeIt();
                return Token.COMMA;
            case ';':
                takeIt();
                return Token.SEMICOLON;
            case '"':
                boolean thirdErrorChk = false;
                while(!(currentChar=='\u0000' || currentChar == '\n')){
                    takeIt();
                    if(currentChar == '\\') {
                        while (!(currentChar == '\u0000' || currentChar == '\n')) {
                            if (currentChar == '\\') {
                                takeIt();
                                if (currentChar != 'n') {
                                    takeIt();
                                    System.out.println("ERROR: illegal escape sequence");
                                } else {
                                    takeIt();
                                }
                            } else {
                                takeIt();
                            }

                            if(currentChar == '"'){
                                thirdErrorChk = true;
                            }
                        }
                    }
                    if(currentChar == '"'){
                        thirdErrorChk = true;
                        takeIt();
                        if(currentChar == '\n'){
                            currentLineNr++;
                        }
                        break;
                    }
                }
                if(!thirdErrorChk){
                    System.out.println("ERROR: unterminated string literal");
                }
                return Token.STRINGLITERAL;


            case '@':
                takeIt();
                return Token.ERROR;
            default:
                switch (currentChar){
                    case 'b':
                        takeIt();
                        if(currentChar == 'o'){
                            takeIt();
                            if(currentChar == 'o'){
                                takeIt();
                                if(currentChar == 'l'){
                                    takeIt();
                                    return lastresult(currentChar,Token.BOOL);
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'e':
                        takeIt();
                        if(currentChar == 'l'){
                            takeIt();
                            if(currentChar == 's'){
                                takeIt();
                                if(currentChar == 'e'){
                                    takeIt();
                                    return lastresult(currentChar,Token.ELSE);
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();

                        }
                    case 'f':
                        takeIt();
                        if(currentChar == 'o'){
                            takeIt();
                            if(currentChar == 'r'){
                                takeIt();
                                return lastresult(currentChar,Token.FOR);
                            }else{
                                return forloopingToken();
                            }
                        }else if(currentChar == 'l'){
                            takeIt();
                            if(currentChar == 'o'){
                                takeIt();
                                if(currentChar == 'a'){
                                    takeIt();
                                    if(currentChar == 't'){
                                        takeIt();
                                        return lastresult(currentChar,Token.FLOAT);
                                    }else{
                                        return forloopingToken();
                                    }
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else if(currentChar == 'a'){
                            takeIt();
                            if(currentChar == 'l'){
                                takeIt();
                                if(currentChar == 's'){
                                    takeIt();
                                    if(currentChar == 'e'){
                                        takeIt();
                                        return lastresult(currentChar,Token.BOOLLITERAL);
                                    }else{
                                        return forloopingToken();
                                    }
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'i':
                        takeIt();
                        if(currentChar == 'f'){
                            takeIt();
                            return lastresult(currentChar,Token.IF);
                        }else if(currentChar == 'n'){
                            takeIt();
                            if(currentChar == 't'){
                                takeIt();
                                return lastresult(currentChar,Token.INT);
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'r':
                        takeIt();
                        if(currentChar == 'e'){
                            takeIt();
                            if(currentChar == 't'){
                                takeIt();
                                if(currentChar == 'u'){
                                    takeIt();
                                    if(currentChar == 'r'){
                                        takeIt();
                                        if(currentChar == 'n'){
                                            takeIt();
                                            return lastresult(currentChar,Token.RETURN);
                                        }else{
                                            return forloopingToken();
                                        }
                                    }else{
                                        return forloopingToken();
                                    }
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'v':
                        takeIt();
                        if(currentChar == 'o'){
                            takeIt();
                            if(currentChar == 'i'){
                                takeIt();
                                if(currentChar == 'd'){
                                    takeIt();
                                    return lastresult(currentChar,Token.VOID);
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'w':
                        takeIt();
                        if(currentChar == 'h'){
                            takeIt();
                            if(currentChar == 'i'){
                                takeIt();
                                if(currentChar == 'l'){
                                    takeIt();
                                    if(currentChar == 'e'){
                                        takeIt();
                                        return lastresult(currentChar,Token.WHILE);
                                    }else{
                                        return forloopingToken();
                                    }

                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 't':
                        takeIt();
                        if(currentChar == 'r'){
                            takeIt();
                            if(currentChar == 'u'){
                                takeIt();
                                if(currentChar == 'e'){
                                    takeIt();
                                    return lastresult(currentChar,Token.BOOLLITERAL);
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    default:
                        return forloopingToken();

                }

        }
    }

    private int scanToken() {
        boolean val = true,chkInt=true;
        switch (currentChar) {
            case '0':  case '1':  case '2':  case '3':  case '4':
            case '5':  case '6':  case '7':  case '8':  case '9': case '.':
                while(!(currentChar == '\u0000' || currentChar == ' ' || currentChar == ';'
                        || currentChar == '\n' || currentChar == '\r' || currentChar == '\t')){
                    if(!(isDigit(currentChar))){
                        if(!val){
                            if(chk(currentChar)){
                                break;
                            }else{
                               insertQueue();
                               if(chk(currentChar)){
                                   break;
                               }else{
                                   insertQueue();
                                   if(chk(currentChar)){
                                       queue[num_Queue] = currentChar;
                                       num_Queue++;
                                       currentChar = queue[0];
                                       queueUpt();
                                       break;
                                   }else{
                                       queue[num_Queue] = currentChar;
                                       num_Queue++;
                                       currentChar = queue[0];
                                       queueUpt();
                                       val = true;
                                   }
                               }
                            }
                        }
                        if((currentChar == 'e' || currentChar == 'E' || currentChar =='.'
                                || currentChar == '-'
                                || currentChar == '+')){
                            if(currentChar == '.'){
                                val = false;
                            }
                            takeIt();
                            chkInt = false;

                        }else { // id
                            break;
                        }
                    }else{
                        takeIt();
                    }
                }
                if(chkInt){
                    return Token.INTLITERAL;
                }else{
                    return Token.FLOATLITERAL;
                }
            case '+':
                takeIt();
                return Token.PLUS;
            case '\u0000': // sourceFile.eot:
                currentLexeme.append('$');
                return Token.EOF;
            // Add code here for the remaining MiniC tokens...

            case '=':
                takeIt();
                if(currentChar == '='){
                    takeIt();
                    return Token.EQ;
                }else{
                    return Token.ASSIGN;
                }
            case '|':
                takeIt();
                takeIt();
                return Token.OR;
            case '&':
                takeIt();
                takeIt();
                return Token.AND;
            case '!':
                takeIt();
                if(currentChar == '='){
                    takeIt();
                    return Token.NOTEQ;
                }else{
                    return Token.NOT;
                }
            case '<':
                takeIt();
                if(currentChar == '='){
                    takeIt();
                    return Token.LESSEQ;
                }else{
                    return Token.LESS;
                }
            case '>':
                takeIt();
                if(currentChar == '='){
                    takeIt();
                    return Token.GREATEREQ;
                }
                return Token.GREATER;
            case '-':
                takeIt();
                return Token.MINUS;
            case '*':
                takeIt();
                return Token.TIMES;
            case '/':
                boolean slashchk = false,asteriskshk = false;
                takeIt();
                if(currentChar == '\n'){
                    currentLineNr++;
                }
                if(currentChar !='*') {
                    if(currentChar == '/'){
                        while(currentChar != '\u0000'){
                            if(currentChar == '\n'){
                                currentLineNr++;
                            }
                            takeIt();
                        }
                        int endpos = currentLexeme.length();
                        currentLexeme.delete(0,endpos);
                        currentLexeme.append('$');
                        currentColNr=1;
                        currentLineNr++;
                        chklast=false;
                        return Token.EOF;
                    }else{
                        return Token.DIV;
                    }
                }
                else{
                    while(currentChar != '\u0000') {
                        takeIt();
                        if(currentChar == '\n'){
                            currentLineNr++;
                        }
                        if (currentChar == '*') {
                            takeIt();
                            if (currentChar == '\n') {
                                currentLineNr++;
                            }
                        }
                        if(currentChar == '/'){
                            takeIt();
                            if (currentChar == '\u0000') {
                                int endpos = currentLexeme.length();
                                currentLexeme.delete(0,endpos);
                                currentLexeme.append('$');
                                currentColNr=1;
                                currentLineNr++;
                                chklast=false;
                                return Token.EOF;
                            }else{
                                int endpos = currentLexeme.length();
                                currentColNr+=(endpos);
                                currentLexeme.delete(0,endpos);
                                if((currentChar == ' '
                                        || currentChar == '\f'
                                        || currentChar == '\n'
                                        || currentChar == '\r'
                                        || currentChar == '\t')){
                                    if(currentChar == '\n'){
                                        currentColNr =1;
                                        currentLineNr++;
                                    }
                                    currentlyScanningToken =false;
                                    takeIt();
                                    currentlyScanningToken=true;
                                }else if(currentChar == '*'){
                                    takeIt();
                                }else{
                                    return scaninnterToken();
                                }

                            }

                        }

                    }
                    System.out.println("ERROR: unterminated multi-line comment.");
                    int endpos =currentLexeme.length();
                    currentLexeme.delete(0,endpos);
                    currentLexeme.append('$');
                    chklast=false;
                    currentLineNr++;
                    return Token.EOF;
                }
            case '{':
                takeIt();
                return Token.LEFTBRACE;
            case '}':
                takeIt();
                return Token.RIGHTBRACE;
            case '[':
                takeIt();
                return Token.LEFTBRACKET;
            case ']':
                takeIt();
                return Token.RIGHTBRACKET;
            case '(':
                takeIt();
                return Token.LEFTPAREN;
            case ')':
                takeIt();
                return Token.RIGHTPAREN;
            case ',':
                takeIt();
                return Token.COMMA;
            case ';':
                takeIt();
                return Token.SEMICOLON;
            case '"':
                boolean thirdErrorChk = false;
                while(!(currentChar=='\u0000' || currentChar == '\n')){
                    takeIt();
                    if(currentChar == '\n'){
                        currentLineNr++;
                    }
                    if(currentChar == '\\') {
                        while (!(currentChar == '\u0000' || currentChar == '\n')) {
                            if (currentChar == '\\') {
                                takeIt();
                                if (currentChar != '\n') {
                                    takeIt();
                                    System.out.println("ERROR: illegal escape sequence");
                                } else {
                                    takeIt();
                                }
                            } else {
                                takeIt();
                            }

                            if(currentChar == '"'){
                                thirdErrorChk = true;
                            }
                        }
                    }
                    if(currentChar == '"'){
                        thirdErrorChk = true;
                        takeIt();
                        if(currentChar == '\n'){
                            currentLineNr++;
                        }
                        break;
                    }
                }
                if(!thirdErrorChk){
                    System.out.println("ERROR: unterminated string literal");
                }
                return Token.STRINGLITERAL;


            case '@':
                takeIt();
                return Token.ERROR;
            default:
                switch (currentChar){
                    case 'b':
                        takeIt();
                        if(currentChar == 'o'){
                            takeIt();
                            if(currentChar == 'o'){
                                takeIt();
                                if(currentChar == 'l'){
                                    takeIt();
                                    return lastresult(currentChar,Token.BOOL);
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'e':
                        takeIt();
                        if(currentChar == 'l'){
                            takeIt();
                            if(currentChar == 's'){
                                takeIt();
                                if(currentChar == 'e'){
                                    takeIt();
                                    return lastresult(currentChar,Token.ELSE);
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                                return forloopingToken();

                        }
                    case 'f':
                        takeIt();
                        if(currentChar == 'o'){
                            takeIt();
                            if(currentChar == 'r'){
                                takeIt();
                                return lastresult(currentChar,Token.FOR);
                            }else{
                                return forloopingToken();
                            }
                        }else if(currentChar == 'l'){
                            takeIt();
                            if(currentChar == 'o'){
                                takeIt();
                                if(currentChar == 'a'){
                                    takeIt();
                                    if(currentChar == 't'){
                                        takeIt();
                                        return lastresult(currentChar,Token.FLOAT);
                                    }else{
                                        return forloopingToken();
                                    }
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else if(currentChar == 'a'){
                            takeIt();
                            if(currentChar == 'l'){
                                takeIt();
                                if(currentChar == 's'){
                                    takeIt();
                                    if(currentChar == 'e'){
                                        takeIt();
                                        return lastresult(currentChar,Token.BOOLLITERAL);
                                    }else{
                                        return forloopingToken();
                                    }
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'i':
                        takeIt();
                        if(currentChar == 'f'){
                            takeIt();
                            return lastresult(currentChar,Token.IF);
                        }else if(currentChar == 'n'){
                            takeIt();
                            if(currentChar == 't'){
                                takeIt();
                                return lastresult(currentChar,Token.INT);
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'r':
                        takeIt();
                        if(currentChar == 'e'){
                            takeIt();
                            if(currentChar == 't'){
                                takeIt();
                                if(currentChar == 'u'){
                                    takeIt();
                                    if(currentChar == 'r'){
                                        takeIt();
                                        if(currentChar == 'n'){
                                            takeIt();
                                            return lastresult(currentChar,Token.RETURN);
                                        }else{
                                            return forloopingToken();
                                        }
                                    }else{
                                        return forloopingToken();
                                    }
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'v':
                        takeIt();
                        if(currentChar == 'o'){
                            takeIt();
                            if(currentChar == 'i'){
                                takeIt();
                                if(currentChar == 'd'){
                                    takeIt();
                                    return lastresult(currentChar,Token.VOID);
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 'w':
                        takeIt();
                        if(currentChar == 'h'){
                            takeIt();
                            if(currentChar == 'i'){
                                takeIt();
                                if(currentChar == 'l'){
                                    takeIt();
                                    if(currentChar == 'e'){
                                        takeIt();
                                        return lastresult(currentChar,Token.WHILE);
                                    }else{
                                        return forloopingToken();
                                    }

                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    case 't':
                        takeIt();
                        if(currentChar == 'r'){
                            takeIt();
                            if(currentChar == 'u'){
                                takeIt();
                                if(currentChar == 'e'){
                                    takeIt();
                                    return lastresult(currentChar,Token.BOOLLITERAL);
                                }else{
                                    return forloopingToken();
                                }
                            }else{
                                return forloopingToken();
                            }
                        }else{
                            return forloopingToken();
                        }
                    default:
                            return forloopingToken();

                }

        }
    }
    int forloopingToken(){
        while(!(currentChar == '\u0000' || currentChar == ' ' || currentChar == ';'
                || currentChar == '\n' || currentChar == '\r' || currentChar == '\t')){
            if(!(currentChar == '+' || currentChar == '-' || currentChar =='.' || currentChar == '[' ||
                    currentChar == ']' || currentChar == '=' || currentChar == '<' || currentChar == '>' ||
                    currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}'))
                takeIt();
            else{
                break;
            }
        }
        return Token.ID;
    }
    int lastresult(char ch,int a){
        if(currentChar == '\u0000' || currentChar == ' ' || currentChar == ';'
                || currentChar == '\n' || currentChar == '\r' || currentChar == '\t'){
            return a;
        }else{
            return forloopingToken();
        }
    }

    boolean cotationchk (String s){{
        for(int i=0;i<s.length()-1;i++){
            if(s.charAt(i) == '\\'){
                if(s.charAt(i+1) == '"'){
                    return true;
                }
            }
        }
        return false;
    }

    }

    public Token scan () {
        Token currentToken;
        SourcePos pos;
        int kind;

        currentlyScanningToken = false;
        while (currentChar == ' '
                || currentChar == '\f'
                || currentChar == '\n'
                || currentChar == '\r'
                || currentChar == '\t')
        {
            if(currentChar == ' '){
                currentColNr++;
            }
            if(currentColNr == -1){
                //when the scanner start, col num =-1 and this is global.
                currentColNr = 1;
            }

            if(currentLineNr == -1){
                currentLineNr = 1;
            }
//            if(currentLineNr == 1 && currentChar == '\n'){
//                currentLineNr++;
//            }
            takeIt();

        }

        currentlyScanningToken = true;
        currentLexeme = new StringBuffer("");
        pos = new SourcePos();
        // Note: currentLineNr and currentColNr are not maintained yet!
        if(currentColNr == -1){
            //when the scanner start, col num =-1 and this is global.
            currentColNr = 1;
        }

        if(currentLineNr == -1){
            currentLineNr = 1;
        }

        kind = scanToken();

        pos.StartLine = currentLineNr;
        pos.EndLine = currentLineNr;
        pos.StartCol = currentColNr;

        currentColNr+=(currentLexeme.length()-1);

        String tmp = currentLexeme.toString();

        if(tmp.charAt(0) == '"'){
            tmp = tmp.substring(1);
            if(tmp.length()>1 && tmp.charAt(tmp.length()-1) == '"' && !cotationchk(tmp)){
                tmp = tmp.substring(0,tmp.length()-1);
            }
        }

        currentToken = new Token(kind, tmp, pos);


        pos.EndCol = currentColNr;

        if(currentChar == '\n' || currentChar == '\u0000'){
            if(chklast && currentChar == '\u0000'){
                currentLineNr++;
                chklast=false;
            }
            currentColNr = 1;
        }else{
            currentColNr++;
        }

        if (verbose)
            currentToken.print();
        return currentToken;
    }

}
