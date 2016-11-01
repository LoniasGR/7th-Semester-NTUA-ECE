;--------------------MACROS----------------------------;

INCLUDE MACROS.ASM

;----------------DECLARATIONS--------------------------; 
STACK_SEG SEGMENT STACK
    DW 128 DUP(?)
ENDS

DATA_SEG SEGMENT
    REQUEST DW "Give a 9-bit 2's complement number: $"
    DECIMAL DB "DECIMAL: $"  
    NEW_LINE DB 0AH,0DH,"$"
ENDS

CODE_SEG SEGMENT
    ASSUME CS:CODE_SEG,SS:STACK_SEG,DS:DATA_SEG,ES:DATA_SEG 


;--------------------CODE------------------------------;

    
MAIN PROC FAR
    ;SET SEGMENT REGISTERS
    MOV AX,DATA_SEG
    MOV DS,AX
    MOV ES,AX

START:
   PRINT_STRING REQUEST
   CALL GET_BINARY ;stores in DX the binary number we have
   PRINT_STRING NEW_LINE
   PRINT_STRING DECIMAL
   CALL BINARY_TO_DEC ;takes reg DX as input and turns it on dec.
   PRINT_STRING NEW_LINE
   JMP START

EXIT:
    EXIT_PROGRAM
MAIN ENDP


;GET_BINARY changes AX,CL,DL                
GET_BINARY PROC NEAR
    MOV CX,0009H                
    MOV DX,0000H
IGNORE:
     READ_KEY

IGNORE_2:
     CMP AL,62H
     JE TERMINATION_SEQUENCE
     CMP AL,42H
     JE TERMINATION_SEQUENCE        
     CMP AL,31H
     JG IGNORE
     CMP AL,30H
     JL IGNORE
     PRINT_CHAR AL
     CMP CX,03H
     JE PUT_DOT  

IGNORE_3:     
     SUB AL,30H
     MOV AH,00H
     ROL DX,01H
     ADD DX,AX
     LOOP IGNORE
     RET

TERMINATION_SEQUENCE:     
    READ_KEY
    CMP AL,30H
    JNE IGNORE_2
    READ_KEY
    CMP AL,34H
    JNE IGNORE_2
    JMP EXIT

GET_BINARY ENDP

PUT_DOT: 
    PRINT_CHAR 2EH
    JMP IGNORE_3

BINARY_TO_DEC PROC NEAR
    MOV AX,DX  
    MOV CX,AX
    CMP AX,0
    JE ZERO
    AND AX,0100H
    CMP AX,0100H
    JE NEGATIVE
    PRINT_CHAR 2BH
    MOV AX,CX 
    
BINARY_TO_DEC_2:    
    AND AX,0xFFFC
    ROR AX,02H
    MOV DX,0000H
    MOV BX,000AH
    DIV BX
    ADD AX,30H
    ADD DX,30H
    PRINT_CHAR AL
    PRINT_CHAR DL
    PRINT_CHAR 2EH
    JMP DECIMAL_PRINT

ZERO:
    PRINT_CHAR 30H
    PRINT_CHAR 2EH
    PRINT_CHAR 30H
    PRINT_CHAR 30H
    RET
     
NEGATIVE: 
    PRINT_CHAR 2DH
    MOV AX,CX
    OR AH,15
    NEG AX
    MOV CX,AX
    JMP BINARY_TO_DEC_2
    
DECIMAL_PRINT:
    MOV AX,CX
    AND AX,03H
    CMP AX,00H
    JE ZEROZERO
    CMP AX,01H
    JE ZEROONE
    CMP AX,02H
    JE ONEZERO
    CMP AX,03H
    JE ONEONE

ZEROZERO:
    PRINT_CHAR 30H
    PRINT_CHAR 30H
    RET

ZEROONE:
    PRINT_CHAR 32H
    PRINT_CHAR 35H
    RET
    
ONEZERO:
    PRINT_CHAR 35H
    PRINT_CHAR 30H
    RET

ONEONE:
    PRINT_CHAR 37H
    PRINT_CHAR 35H
    RET
        
BINARY_TO_DEC ENDP 

CODE_SEG ENDS
END MAIN