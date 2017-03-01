EXIT MACRO
    MOV AH,4CH
    INT 21H
ENDM

PRINT MACRO CHAR
    PUSH AX
    PUSH DX
    MOV DL,CHAR
    MOV AH,02H
    INT 21H
    POP DX
    POP AX
ENDM


READ MACRO
     MOV AH,8
     INT 21H
ENDM

;prints string till the $ symbol
PRINT_STRING MACRO STRING_OFFSET
     PUSH AX
     PUSH DX
     MOV DX,OFFSET STRING_OFFSET
     MOV AH,09H
     INT 21H
     POP DX
     POP AX
ENDM


;exits the program
EXIT_PROGRAM MACRO
     MOV AH,4CH
     MOV AL,00H
     INT 21H
ENDM


;waits for a key to be pressed
;doesn't display it on screen
READ_KEY MACRO
     MOV AH,08H
     INT 21H
ENDM


;prints a character to screen
PRINT_CHAR MACRO CHAR
     PUSH DX
     PUSH AX
     MOV DL,CHAR
     MOV AH,02H
     INT 21H
     POP AX
     POP DX
ENDM
