package org.syntactic;

import java.util.ArrayList;
import java.util.List;
import org.token.Token;
import org.token.TokenType;

public class Syntactic {

    private final List<Token> tokens;
    private List<SyntacticError> syntacticErrors;
    private Token token;
    private int i;

    public Syntactic(List<Token> tokens){
        this.tokens = tokens;
        this.token = tokens.get(0);
        this.i = 0;
        this.syntacticErrors = new ArrayList<>();
    }

    public void PrintError(){
        SyntacticError newError = new SyntacticError(token, token.getLineNumber());
        System.err.println("Syntactic error. Token " + token.toString() + " not expected in input.");
        syntacticErrors.add(newError);
        i++;
        if (i < tokens.size()) {
            token = tokens.get(i);
        }
    }
    
    public boolean match(TokenType expectedType) {
        if (token.getType() == expectedType) {
            i++;
            if (i < tokens.size()) {
                token = tokens.get(i);
            }
            return true;
        } else {
            PrintError();
            return false;
        }
    }

    public List<SyntacticError> syntacticAnalysis() {
        Programa();
        return syntacticErrors;
    }

    private void Programa(){
        if(token.getType() == TokenType.FUNCTION){
            Funcao();
            FuncaoSeq();    
        }
        else{
            PrintError();
        }
    }

    private void FuncaoSeq(){
        if(token.getType() == TokenType.FUNCTION){
            Funcao();
            FuncaoSeq();    
        }
    }
    
    private void Funcao(){
        match(TokenType.FUNCTION);
        NomeFuncao();
        match(TokenType.LBRACKET);
        ListaParams();
        match(TokenType.RBRACKET);
        TipoRetornoFuncao();
        Bloco();
    }

    private void NomeFuncao(){
        if(token.getType() == TokenType.ID){
            match(TokenType.ID);
        } else if(token.getType() == TokenType.MAIN){
            match(TokenType.MAIN);
        } else{
            PrintError();
        }
    }

    private void ListaParams(){
        if(token.getType() == TokenType.ID){
            match(TokenType.ID);
            match(TokenType.COLON);
            Type();
            ListaParams2();
        }
    }

    private void ListaParams2(){
        if(token.getType() == TokenType.COMMA){
            match(TokenType.COMMA);
            match(TokenType.ID);
            match(TokenType.COLON);
            Type();
            ListaParams2();
        }
    }
    
    private void TipoRetornoFuncao(){
        if(token.getType() == TokenType.ARROW){
            match(TokenType.ARROW);
            Type();
        }
    }

    private void Bloco(){
        match(TokenType.LBRACE);
        Sequencia();
        match(TokenType.RBRACE);
    }

    private void Sequencia(){
        if(token.getType() == TokenType.LET){
            Declaracao();
            Sequencia();
        } else if(token.getType() == TokenType.ID || token.getType() == TokenType.IF || 
           token.getType() == TokenType.WHILE || token.getType() == TokenType.PRINTLN ||
           token.getType() == TokenType.RETURN){
            Comando();
            Sequencia();    
        }
    }

    private void Declaracao(){
        match(TokenType.LET);
        VarList();
        match(TokenType.COLON);
        Type();
        match(TokenType.SEMICOLON);
    }

    private void VarList(){
        match(TokenType.ID);
        VarList2();
    }

    private void VarList2(){
        if(token.getType() == TokenType.COMMA){
            match(TokenType.COMMA);
            match(TokenType.ID);
            VarList2();
        }
    }

    private void Type(){
        if(token.getType() == TokenType.INT){
            match(TokenType.INT);
        }
        else if( token.getType() == TokenType.FLOAT){
            match(TokenType.FLOAT);
        }
        else if(token.getType() == TokenType.CHAR){
            match(TokenType.CHAR);
        }
        else{
            PrintError();
        }
    }

    private void Comando() {
        switch (token.getType()) {
            case ID:
                match(TokenType.ID);
                AtribuicaoOuChamada();
                break;

            case IF:
                ComandoSe();
                break;

            case WHILE:
                match(TokenType.WHILE);
                Expr();
                Bloco();
                break;

            case PRINTLN:
                match(TokenType.PRINTLN);
                match(TokenType.LBRACE);
                match(TokenType.FMT_STRING);
                match(TokenType.COMMA);
                ListaArgs();
                match(TokenType.RBRACE);
                match(TokenType.SEMICOLON);
                break;

            case RETURN:
                match(TokenType.RETURN);
                Expr();
                match(TokenType.SEMICOLON);
                break;

            default:
                PrintError();
                break;
        }
    }

    private void AtribuicaoOuChamada(){
        if(token.getType() == TokenType.ASSIGN){
            match(TokenType.ASSIGN);
            Expr();
            match(TokenType.SEMICOLON);
        }
        else if( token.getType() == TokenType.LBRACKET){
            match(TokenType.LBRACKET);
            ListaArgs();
            match(TokenType.RBRACKET);
            match(TokenType.SEMICOLON);
        }
        else{
            PrintError();
        }
    }

    private void ComandoSe(){
        if(token.getType() == TokenType.IF){
            match(TokenType.IF);
            Expr();
            Bloco();
            ComandoSenao();
        }
        else if( token.getType() == TokenType.LBRACE){
            match(TokenType.LBRACE);
            Bloco();
        }
        else{
            PrintError();
        }
    }

    private void ComandoSenao(){
        if(token.getType() == TokenType.ELSE){
            match(TokenType.ELSE);
            ComandoSe();
        }
    }

    private void Expr(){
        if(token.getType() == TokenType.ID || token.getType() == TokenType.INT_CONST || token.getType() == TokenType.FLOAT_CONST || token.getType() == TokenType.CHAR_LITERAL || token.getType() == TokenType.LBRACKET){
            Rel();
            ExprOpc();
        }
        else{
            PrintError();
        }
    }
    
    private void ExprOpc(){
        if(token.getType() == TokenType.EQ){
            OpIgual();
            Rel();
            ExprOpc();
        }
    }

    private void OpIgual(){
        if(token.getType() == TokenType.EQ){
            match(TokenType.EQ);
        }
        else if(token.getType() == TokenType.NE){
            match(TokenType.NE);
        }
        else{
            PrintError();
        }
    }

    private void Rel(){
        if(token.getType() == TokenType.ID || token.getType() == TokenType.INT_CONST || token.getType() == TokenType.FLOAT_CONST || token.getType() == TokenType.CHAR_LITERAL || token.getType() == TokenType.LBRACKET){
            Adicao();
            RelOpc();
        }
        else{
            PrintError();
        }
    }

    private void RelOpc(){
        if(token.getType() == TokenType.LT){
            OpRel();
            Adicao();
            RelOpc();
        }
    }


    private void OpRel(){
        if(token.getType() == TokenType.LT){
            match(TokenType.LT);
        }
        else if(token.getType() == TokenType.LE){
            match(TokenType.LE);
        }
        else if(token.getType() == TokenType.GT){
            match(TokenType.GT);
        }
        else if(token.getType() == TokenType.GE){
            match(TokenType.GE);
        }
        else{
            PrintError();
        }
    }

    private void Adicao(){
        if(token.getType() == TokenType.ID || token.getType() == TokenType.INT_CONST || token.getType() == TokenType.FLOAT_CONST || token.getType() == TokenType.CHAR_LITERAL || token.getType() == TokenType.LBRACKET){
            Termo();
            AdicaoOpc();
        }
        else{
            PrintError();
        }
    }

    private void AdicaoOpc(){
        if(token.getType() == TokenType.PLUS){
            OpAdicao();
            Termo();
            AdicaoOpc();
        }
    }

    private void OpAdicao(){
        if(token.getType() == TokenType.PLUS){
            match(TokenType.PLUS);
        }
        else if(token.getType() == TokenType.MINUS){
            match(TokenType.MINUS);
        }
        else{
            PrintError();
        }
    }

    private void Termo(){
        if(token.getType() == TokenType.ID || token.getType() == TokenType.INT_CONST || token.getType() == TokenType.FLOAT_CONST || token.getType() == TokenType.CHAR_LITERAL || token.getType() == TokenType.LBRACKET){
            Fator();
            TermoOpc();
        }
        else{
            PrintError();
        }
    }

    private void TermoOpc(){
        if(token.getType() == TokenType.MULT || token.getType() == TokenType.DIV){
            OpMult();
            Fator();
            TermoOpc();
        }
    }

    private void OpMult(){
        if(token.getType() == TokenType.MULT){
            match(TokenType.MULT);
        }
        else if(token.getType() == TokenType.DIV){
            match(TokenType.DIV);
        }
        else{
            PrintError();
        }
    }

    private void Fator(){
        if(token.getType() == TokenType.ID){
            match(TokenType.ID);
            ChamadaFuncao();
        }
        else if(token.getType() == TokenType.INT_CONST){
            match(TokenType.INT_CONST);
        }
        else if(token.getType() == TokenType.FLOAT_CONST){
            match(TokenType.FLOAT_CONST);
        }
        else if(token.getType() == TokenType.CHAR_LITERAL){
            match(TokenType.CHAR_LITERAL);
        }
        else if(token.getType() == TokenType.LBRACKET){
            match(TokenType.LBRACKET);
            Expr();
            match(TokenType.RBRACKET);
        }
        else{
            PrintError();
        }
    }

    private void ChamadaFuncao(){
        if(token.getType() == TokenType.LBRACKET){
            match(TokenType.LBRACKET);
            ListaArgs();
            match(TokenType.RBRACKET);
        }
    }

    private void ListaArgs(){
        if(token.getType() == TokenType.ID || token.getType() == TokenType.INT_CONST || token.getType() == TokenType.FLOAT_CONST || token.getType() == TokenType.CHAR_LITERAL){
            Arg();
            ListaArgs2();
        }
    }

    private void ListaArgs2(){
        if(token.getType() == TokenType.COLON){
            match(TokenType.COLON);
            Arg();
            ListaArgs2();
        }
    }
    
    private void Arg(){
        if(token.getType() == TokenType.ID){
            match(TokenType.ID);
            ChamadaFuncao();
        }
        else if(token.getType() == TokenType.INT_CONST){
            match(TokenType.INT_CONST);
        }
        else if(token.getType() == TokenType.FLOAT_CONST){
            match(TokenType.FLOAT_CONST);
        }
        else if(token.getType() == TokenType.CHAR_LITERAL){
            match(TokenType.CHAR_LITERAL);
        }
        else{
            PrintError();
        }
    }
}