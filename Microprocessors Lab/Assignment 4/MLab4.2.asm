;---------MACROS----------------------------;

INCLUDE MACROS.ASM

;--------------DECLARATIONS----------------------------; 
STACK_SEG SEGMENT STACK
    DW 128 DUP(?)
ENDS

DATA_SEG SEGMENT
    REQUEST DW "Give a 3-bit hex number: $"
    DECIMAL DB "DECIMAL: $"  
    NEW_LINE DB 0AH,0DH,"$"
    RES  DB 10 DUP ('$')
ENDS

CODE_SEG SEGMENT
    ASSUME CS:CODE_SEG,SS:STACK_SEG,DS:DATA_SEG,ES:DATA_SEG 
    ASSUME DS:DATA,CS:CODE

;--------------------CODE-------------------------------;

    
MAIN PROC FAR
    ;SET SEGMENT REGISTERS
    MOV AX,DATA_SEG
    MOV DS,AX
    MOV ES,AX

START:
   PRINT_STRING REQUEST 
   CALL GET_HEX ; DX <- 3digit hex number    
   PUSH DX
   PRINT_STRING NEW_LINE
   PRINT_STRING DECIMAL  
   MOV AX,DX
   LEA SI,RES 
   POP DX
   CALL HEX2DEC              
   PRINT_STRING NEW_LINE
   JMP START
EXIT:
    EXIT_PROGRAM
MAIN ENDP
             

GET_HEX PROC NEAR
    MOV CX,0003H
    MOV DX,0000H
IGNORE1:
    READ_KEY
    CMP AL,55H ; uppercase U
    JE TERMINATION
    CMP AL,30H ; '0'
    JL IGNORE1 
    CMP AL,39H ; '9'
    JG CHAR_CHECK
IGNORE2:
    PRINT_CHAR AL
    ROL DX,04H
    MOV AH,00H  
    SUB AX,30H ; code - 48
    ADD DX,AX   
    SUB CL,01H
    CMP CX,0000H 
IGNORE3:
    JE WAIT_FOR_ENTER 
    JMP IGNORE1
RETURN:
    RET             

TERMINATION:
    JMP EXIT

CHAR_CHECK:
    CMP AL,41H ; 'A'
    JL IGNORE1
    CMP AL,47H ; 'F+1'
    JL UPPER_CASE
    CMP AL,61H ; 'a'
    JL IGNORE1
    CMP AL,67H ; 'f+1'
    JL LOWER_CASE   
    JMP IGNORE1
            
                                                                                                 
UPPER_CASE:               
    PRINT_CHAR AL
    ROL DX,04H
    MOV AH,00H  
    SUB AX,37H ; code - 56
    ADD DX,AX
    SUB CL,01H
    JMP IGNORE3

LOWER_CASE:    
    SUB AX,20H  ; makes it uppercase for printing
    PRINT_CHAR AL
    ROL DX,04H
    MOV AH,00H  
    SUB AX,37H ; code - 56
    ADD DX,AX
    SUB CL,01H
    JMP IGNORE3

WAIT_FOR_ENTER:
    PUSH AX
WFE:    
    READ_KEY     
    CMP AL,55H ; uppercase U
    JE TERMINATION
    CMP AL,0DH
    JNZ WFE   
    POP AX
    JMP RETURN
    
                

HEX2DEC PROC NEAR
    MOV CX,0
    MOV BX,10
   
LOOP1: 
    MOV DX,0
    DIV BX
    ADD DL,30H
    PUSH DX
    INC CX
    CMP AX,9
    JG LOOP1
  
    ADD AL,30H
;    PUSH AX      
    PRINT_CHAR AL
;    POP AX
    MOV [SI],AL
     
LOOP2:
    POP AX
    INC SI       
    CMP CL,03H
    JNE CNT
    PUSH AX
    MOV AL,2CH ; '
    PRINT_CHAR AL
    POP AX
    CNT:
    PRINT_CHAR AL
    MOV [SI],AL
    LOOP LOOP2
    RET
HEX2DEC ENDP           
